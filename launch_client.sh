#!/bin/env bash
#
CLASSES_DIR=$(pwd)/build/classes/java/main

java -cp $CLASSES_DIR cs555.pastry.node.client.Client 1328 tokyo 50321
