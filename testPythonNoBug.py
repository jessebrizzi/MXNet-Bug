import mxnet as mx
print(mx.__version__)
import cv2
import numpy as np
from collections import namedtuple
import time

def get_image(fname):
    # download and show the image
    img = cv2.cvtColor(cv2.imread(fname), cv2.COLOR_BGR2RGB)
    if img is None:
         return None
    # convert into format (batch, RGB, width, height)
    img = cv2.resize(img, (224, 224))
    img = np.swapaxes(img, 0, 2)
    img = np.swapaxes(img, 1, 2)
    print(img.shape)
    return img

img = get_image('tabby.tiff')
Batch = namedtuple('Batch', ['data'])

def predict(fname, batchSize):
    batched_img = np.tile(img, (batchSize,1,1,1))
    # compute the predict probabilities
    mod.forward(Batch([mx.nd.array(batched_img)]))
    probs = mod.get_outputs()[0].asnumpy()
    
    for i in range(0,probs.shape[0]):
        prob = probs[i, :]
        assert(prob.shape[0] == 1000)
        j = np.argmax(prob)
        assert(j == 281 or j == 282) # its a cat

max_batch_size = 32
ctx = mx.gpu(0)

sym, arg_params, aux_params = mx.model.load_checkpoint('resnet-152', 0)
mod = mx.mod.Module(symbol=sym, context=ctx, label_names=None)
mod.bind(for_training=False, data_shapes=[('data', (max_batch_size,3,224,224))], 
         label_shapes=mod._label_shapes)
mod.set_params(arg_params, aux_params, allow_missing=True)

for i in range(1000):
    random_batch_size = np.random.randint(low=1,high=max_batch_size+1)
    start = time.time() * 1000
    predict('tabby.tiff', random_batch_size)
    if i % 1 == 0:
        print("Batch size {} Pred time {} ms".format(random_batch_size, (time.time() * 1000) - start))