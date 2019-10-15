#!/bin/env bash
#
CURRENT_WORKING_DIR=$(pwd)
CLASSES_DIR=$(pwd)/build/classes/java/main

START_PEER_NODE="cd $CURRENT_WORKING_DIR; java -cp $CLASSES_DIR cs555.pastry.node.peer.PeerNode 1328 tokyo 50321"

for peer_node in $(cat peer_nodes.txt)
do
  echo 'logging into '$peer_node
  COMMAND="xterm -geometry 200x48 -e 'ssh -t $peer_node \"$START_PEER_NODE\"'"
  echo $COMMAND
  eval $COMMAND &
done

echo

