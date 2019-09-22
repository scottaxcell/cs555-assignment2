# CS 555: Distributed Systems -- Assignment 2
Implementing The Pastry Peer To Peer Network

## TODO

### Miscellaneous
* file read/write on client and peer nodes


### Discovery node
* send alive heartbeats to connected peers, on disconnect update peer db
* ~~return a random node from the peer db~~
* ~~ensure a peer's id s unique on registration~~
* handle peer removal and print peer was removed
* list-nodes: prints list of active peers

### Peer node
* accept hex id from command line
* print the following on joining: DHT, route
* print DHT on update
* print when storing a file or sends to another peer
* print on lookup/join queries: type of message, dest/file id, hop count, next hop
* remove-node: remove self from network after contacting discovery node
* print-dht: print DHT
* list-files: prints files in storage

### StoreData
* store/retrive file:
    * print random node it connects to
    * print id of data
    * print route of lookup message
    * print success/fail of file operation

## Notes
* store connected peer tcp connections using the registration id
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
* request: hex id and ip
* response: success, assigned hex id, random peer id

#### Join
* requset: route and hop count, destination, DHT

#### Lookup
* request: route and hop count, destination
* response: hex ip and ip ?

#### Update DHT
* DHT

#### Store file
* request: file name, file data
* response: file name, file data, writeSuccess

#### Remove peer
* hex id





