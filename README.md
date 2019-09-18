# CS 555: Distributed Systems -- Assignment 2
Implementing The Pastry Peer To Peer Network

## TODO
### Discovery node
* send alive heartbeats to connected peers, on disconnect update peer db
* return a random node from the peer db
* ensure a peer's id s unique on registration

### Peer node
* must accept hex id from command line

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





