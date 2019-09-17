package cs555.pastry.node;

import cs555.pastry.wireformats.Message;

public interface Node {
    void onMessage(Message message);

    String getNodeTypeAsString();
}
