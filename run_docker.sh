#!/bin/sh

docker build -t test-img . && \
nvidia-docker run -it \
    --rm \
    -p 5005:5005 \
    -v $(pwd):/root/build \
    -w=/root/build/scala \
    test-img \
    sh -c "exec >/dev/tty 2>/dev/tty </dev/tty && /usr/bin/screen -s /bin/bash"