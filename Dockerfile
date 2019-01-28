# for MXNet 1.5.0-SNAPSHOT change FROM image to 'nvidia/cuda:9.2-cudnn7-runtime-ubuntu16.04' and use OpenCV 2.4
# for MXNet 1.4.0-SNAPSHOT change FROM image to 'nvidia/cuda:9.0-cudnn7-runtime-ubuntu16.04' and use OpenCV 2.4
# for MXNet 1.3.1 change FROM image to 'nvidia/cuda:9.0-cudnn7-runtime-ubuntu16.04' and use OpenCV 3.4
FROM 'nvidia/cuda:9.0-cudnn7-runtime-ubuntu16.04'

# set default java environment variable
ENV JAVA_VERSION_MAJOR=8 \
    JAVA_VERSION_MINOR=181 \
    JAVA_HOME=/usr/lib/jvm/default-jvm \
    PATH=${PATH}:/usr/lib/jvm/default-jvm/bin/

# RUN EVERYTHING
RUN apt-get update && \
    # Install the software we'll need for installing repositories
    apt-get install -y software-properties-common curl git wget screen htop nano libopenblas-dev && \
    # Install the OpenJDK repository
    add-apt-repository ppa:openjdk-r/ppa -y && \
    # Install sbt repository 
    echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list && \
    apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823 && \
    # Update data from repositories
    apt-get update && \
    # Install JDK
    apt-get install -y --no-install-recommends openjdk-8-jdk && \
    # Fix default setting
    ln -s java-8-openjdk-amd64  /usr/lib/jvm/default-jvm && \
    # Install SBT
    apt-get install -y sbt && \
    # Install JVMTop
    cd ~ && \
    wget https://github.com/patric-r/jvmtop/releases/download/0.8.0/jvmtop-0.8.0.tar.gz && \
    tar -zxvf jvmtop-0.8.0.tar.gz && \
    chmod +x jvmtop.sh && \
    rm jvmtop-0.8.0.tar.gz && \
    #######################
    ## Install OpenCV 2.4 #
    #######################
    apt-get install -y libopencv-dev && \
    #######################
    ## Install OpenCV 3.4 #
    #######################
    # apt-get install -y --no-install-recommends build-essential cmake pkg-config libjpeg8-dev libtiff5-dev libjasper-dev libpng12-dev libgtk2.0-dev libavcodec-dev libavformat-dev libswscale-dev libv4l-dev libatlas-base-dev gfortran libavresample-dev libgphoto2-dev libgstreamer-plugins-base1.0-dev libdc1394-22-dev && \
    # cd /opt && \
    # git clone https://github.com/opencv/opencv_contrib.git && \
    # cd opencv_contrib && \
    # git checkout 3.4.0 && \
    # cd /opt && \
    # git clone https://github.com/opencv/opencv.git && \
    # cd opencv && \
    # git checkout 3.4.0 && \
    # mkdir build && \
    # cd build && \
    # cmake -D CMAKE_BUILD_TYPE=RELEASE \
    # -D BUILD_NEW_PYTHON_SUPPORT=ON \
    # -D CMAKE_INSTALL_PREFIX=/usr/local \
    # -D INSTALL_C_EXAMPLES=OFF \
    # -D INSTALL_PYTHON_EXAMPLES=OFF \
    # -D OPENCV_EXTRA_MODULES_PATH=/opt/opencv_contrib/modules \
    # -D PYTHON_EXECUTABLE=/usr/bin/python2.7 \
    # -D BUILD_EXAMPLES=OFF /opt/opencv && \
    # make -j $(nproc) && \
    # make install && \
    # ldconfig && \
    # rm -rf /opt/opencv* && \
    # Clean up apt now that we are done installing 
    apt-get clean && \
    rm -r /var/lib/apt/lists/*