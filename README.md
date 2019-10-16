# CS 555: Distributed Systems -- Assignment 2

Implementing The Pastry Peer To Peer Networw

## Build

`$ gradle assemble`

## Run

### Automated Scripts

In the first terminal, launch the Discovery Node.

`$ ./launch_discovery_node.sh`

<em>**Note** This script should be launched from `tokyo.cs.colostate.edu`.</em>

In the second terminal, launch 10 Peers and a Client.

`$ ./launch_peer_nodes_and_client.sh`

<em>**Note** This script assumes that the Discovery has been launched on `tokyo.cs.colostate.edu:50321`.</em>

### Manual

Discovery Node

`$ java -cp build/classes/java/main cs555.pastry.node.discovery.DiscoveryNode <port> <peer-port>`

Client

`$ java -cp build/classes/java/main cs555.pastry.node.client.Client <peer-port> <discovery-node-host> <discovery-node-port>`

Peer Node

`$ java -cp build/classes/java/main cs555.pastry.node.peer.PeerNode <port> <discovery-node-host> <discovery-node-port> [hex-id]`

## Notes

- All peers are assumed to have the same port, this is vital.
- A client and a peer cannot run on the same machine since they use the same port.
