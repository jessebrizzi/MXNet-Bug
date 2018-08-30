package test.bug

import com.sksamuel.scrimage.{Image, ImageMetadata, ScaleMethod}
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import org.apache.mxnet.module.Module
import org.apache.mxnet.{Context, DataBatch, DataDesc, NDArray, Shape, Symbol, module, Model}
import scala.collection.mutable

object TestBug {
  val WIDTH = 224
  val HEIGHT = 224
  val CHANNELS = 3
  val MAX_BATCH_SIZE = 32
  val CONTEXT = Context.gpu(0)

  def loadImageIntoImageBuffer(filename: String): BufferedImage = {
    val img = ImageIO.read(new File(filename))
    val resized = new Image(img, ImageMetadata.empty).scaleTo(WIDTH, HEIGHT, ScaleMethod.Bicubic).awt

    resized
  }

  def imageBufferToFloatArray(img: BufferedImage): Array[Float] = {
    val bytes = img.getData.getDataElements(0, 0, img.getWidth, img.getHeight, null).asInstanceOf[Array[Byte]]

    val numPixels = bytes.length / CHANNELS

    // pull out each channel individually
    val res = Array.fill(CHANNELS)(Array.fill(numPixels)(0f))
    for {
      p <- 0 until numPixels
      c <- 0 until CHANNELS
    } res(c)(p) = (bytes(p * CHANNELS + c) & 0xFF).toFloat
    res.reduceLeft(_ ++ _)
  }

  /**
   * @param model  The loaded Module model
   * @param floats  The list of input images to predict
   * @return       The raw results for each input image from the model.
   */
  def predict(model: Module, input: NDArray, batchSize: Int): Array[Float] = {

    val batch = new DataBatch(
      IndexedSeq(input),
      IndexedSeq.empty[NDArray],
      IndexedSeq.fill(batchSize)(0L),
      pad = 0
    )

    // call the model and extract the results
    val prediction = model.predict(batch)

    assert((prediction.head.size == batchSize*1000), // Imagenet
      s"The correct number of class prediction results is returned. ${prediction.head.size} returned expected ${batchSize*1000}")

    val expectedSize = prediction.head.size / batchSize
    val reshapeNDarray = prediction.head.reshape(Shape(batchSize, expectedSize))
    val predNDarray = NDArray.argmax(reshapeNDarray, 1)
    val out = predNDarray.toArray

    // Should clear the used GPU mem
    prediction.foreach(_.dispose())
    reshapeNDarray.dispose()
    predNDarray.dispose()
    input.dispose()

    out
  }

  def main(args: Array[String]): Unit = {

    val r = new scala.util.Random(8675309)

    // make net
    val (symbol, argParams, auxParams) = Model.loadCheckpoint("../squeezenet-v1.1", 0)
    val model = new module.Module(symbolVar=symbol, labelNames=IndexedSeq.empty[String], contexts=CONTEXT)
    val dataShapes = IndexedSeq(DataDesc(
      name = "data",
      shape = Shape(MAX_BATCH_SIZE, CHANNELS, HEIGHT, WIDTH)
    ))
    model.bind(dataShapes = dataShapes, forTraining = false)
    model.setParams(argParams, auxParams, allowExtra = false, allowMissing = false)

    val file = "../tabby.tiff"
    val img = loadImageIntoImageBuffer(file)
    // convert the images to float arrays
    val imgFloat = imageBufferToFloatArray(img)
    val imgNDArray = NDArray.array(imgFloat, Shape(1, CHANNELS, HEIGHT, WIDTH), CONTEXT)

    println("Starting test")

    (1 to 10000).toList.foreach(i => {
      val randomBatchSize = r.nextInt(MAX_BATCH_SIZE) + 1

      val t0 = System.currentTimeMillis()

      val input = NDArray.tile(imgNDArray, Shape(randomBatchSize, 1, 1, 1))

      val results = predict(model, input, randomBatchSize)

      print (s"$i: Batch Size $randomBatchSize ")
      assert(results.size == randomBatchSize, "The correct number of feature vectors are returned.")

      results.foreach(result => {
        assert((result == 281 || result == 282), s"The image is classified correctly. $result returned") // its a cat
      })

      val t1 = System.currentTimeMillis()
      println(s"pred time ${t1 - t0} ms")
    })

    println("Pausing execution so you can view memory usage")
    val a=scala.io.StdIn.readChar()
  }
}
