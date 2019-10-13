# CS 555: Distributed Systems -- Assignment 2
Implementing The Pastry Peer To Peer Network

## TODO

### Miscellaneous
* handle file migration when new nodes join the network
* ~~use sha1 of timestamp to generate peer id~~
* ~~store file request message~~
* ~~retrieve file request message~~
* ~~retrieve file response message~~
* flesh out routing table row updates on join request
* don't add a peer to the routing table if it is already in the leafset
* ~~file read/write on client and peer nodes~~
* ~~add the peer that is traversed in the join request flow to the routing table~~
* ~~discovery node debug message to get all leafsets and print current network state~~
* ~~buffer register requests, send registration complete when node joins network~~
* ~~strip all address before storing or sending over the wire~~
* ~~rename Ip interfaces to Address and serverAddress variables to address~~
* ~~take another look at storing tcp connections in order to prevent creation of tons of tcp receiver threads~~

### Discovery node
* send alive heartbeats to connected peers, on disconnect update peer db
* ~~return a random node from the peer db~~
* ~~ensure a peer's id s unique on registration~~
* NOT NEEDED ANYMORE - ~~handle peer removal and print peer was removed~~
* ~~list-nodes: prints list of active peers~~

### Peer node
* ~~accept hex id from command line~~
* print the following on joining: DHT, route
* print DHT on update
* print when storing a file or sends to another peer
* print on lookupRequest/join queries: type of message, dest/file id, hop count, next hop
* ~~remove-node: remove self from network after contacting discovery node~~
* print-dht: print DHT
* list-files: prints files in storage
* give the poor fella a nickname!

### StoreData
* store/retrive file:
    * print random node it connects to
    * print id of data
    * print route of lookupRequest message
    * print success/fail of file operation

## Notes
* All peers are assumed to have the same port, this is vital.
* A client and a peer cannot run on the same machine since they use the same port.

### Join protocol
* new peer (X), get random peer (A) from discovery node
* X send join request to A with X set as destination
* A dispatches this join message with a node that is numerically the closest to X
    * A to Z (via B, C, ...)
* the first row of A's rt should be copied to the first row of X's rt
* the second row of B's rt should be copied to the second row of X's rt
* the third row of C's rt should be copied to the third row of X's rt
* Z's leafset should be copied to X's leafset
* Z sends it's contents (leafset and rt) to all peers in it's leafset and rt
* peers that receive this message, update their own tables to incorporate new peer X
### Message requirements
#### Register
* request: hex id and address
* response: success, assigned hex id, random peer id

#### Join
* requset: route and hop count, destination, RT
* response: route and hop count, destination, RT, leafset

#### Lookup
* request: route and hop count, destination
* response: hex address and address ?

#### Update DHT
* DHT

#### Store file
* request: file name, file data
* response: file name, file data, writeSuccess

#### Remove peer
* hex id





