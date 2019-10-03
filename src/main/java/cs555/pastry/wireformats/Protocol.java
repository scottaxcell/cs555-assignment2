package cs555.pastry.wireformats;

public class Protocol {
    public static final int REGISTER_REQUEST = 300;
    public static final int REGISTER_RESPONSE = 301;
    public static final int JOIN_REQUEST = 302;
    public static final int LOOKUP_REQUEST = 303;
    public static final int JOIN_RESPONSE = 304;
    public static final int LEAF_SET_UPDATE = 305;
    public static final int ROUTING_TABLE_UPDATE = 306;
    public static final int JOIN_COMPLETE = 307;
    public static final int LEAF_SET_REQUEST = 308;
    public static final int LEAF_SET_RESPONSE = 309;
    public static final int FORGET_ME = 310;
    public static final int LOOKUP_RESPONSE = 311;
    public static final int RANDOM_PEER_REQUEST = 312;
    public static final int RANDOM_PEER_RESPONSE = 313;
    public static final int STORE_FILE = 314;
    public static final int RETRIEVE_FILE_REQUEST = 315;
    public static final int RETRIEVE_FILE_RESPONSE = 316;
}
