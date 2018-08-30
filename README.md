# MXNet-Bug
Temp repository to share MXNet Bug.
Both bugs are present in MXNet release version v1.1.0 https://github.com/apache/incubator-mxnet/tree/v1.1.0
This repository demonstrates 2 bugs that I have observed in my use of MXNet with simple reproductions using one of the provided nets that can be resized (you need to swap out the SoftmaxOutput layers for Softmax layers or the Module resize call will fail)
- Squeezenet-v1.1
- Resnet-152 (you'll need to DL the params file `wget http://data.dmlc.ml/models/imagenet/resnet/152-layers/resnet-152-0000.params`)
- a small 2 layer MLP with random params 
This bug is related to resizing the Module (passing in a DataBatch that has a different shape than the initially binded shape) and is specific to the Scala API (A python version is included that is free of the issue)

To run the scala bug examples call `sbt run` from inside the `scala/` directory and select the test you want to run. 
To run examples in OSX instead of LINUX change the imports found in `scala/project/Dependencies.scala` from the Linux version to the OSX version. 

# TestBug.scala [issue link](https://github.com/apache/incubator-mxnet/issues/10867)
The resize call is leaking memory on the native side.

Create and bind a MXNet Module with batch size `N+1` and proceed to loop and pass DataBatches to it that require the Module to resize before performing the forward pass. Monitor the system resources (With htop, nvidia-smi, jvmtop) and you will notice the used system memory in htop will start to grow, but not the jvm heap size (the system memory usages grows beyond the set max JVM heap size) or GPU memory usage. This will continue until your system runs out of memory and there is a crash or the JVM is killed clearing all of the leaked used system memory with it.

# TestNoBug.scala
This just shows that when you when you create and bind a MXNet Module with batchsize `N` and proceed to loop and ONLY pass DataBatches of batch size `N` to it that DO NOT require resizing the memory leak is not observed. 

# TestPythonNoBug.py
This just shows that when you when you create and bind a MXNet Module with batchsize `N` and proceed to loop and pass DataBatches of batch size `1` through `N` to it that DO require resizing the memory leak is not observed. 
