#!/bin/env bash
#
CLASSES_DIR=$(pwd)/build/classes/java/main

java -cp $CLASSES_DIR cs555.pastry.node.discovery.DiscoveryNode 50321 1328
