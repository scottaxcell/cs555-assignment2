package cs555.pastry.node.client;

import cs555.pastry.node.Node;
import cs555.pastry.transport.TcpConnection;
import cs555.pastry.transport.TcpServer;
import cs555.pastry.util.Utils;
import cs555.pastry.wireformats.*;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Client implements Node {
    private final TcpServer tcpServer;
    private TcpConnection discoveryNodeTcpConnection;
    private final StoreData storeData = new StoreData(this);
    private final int peerPort;

    public Client(int peerPort, String discoveryNodeIp, int discoveryNodePort) {
        this.peerPort = peerPort;
        tcpServer = new TcpServer(peerPort, this);
        try {
            Socket socket = new Socket(discoveryNodeIp, discoveryNodePort);
            discoveryNodeTcpConnection = new TcpConnection(socket, this);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        if (args.length != 3)
            printHelpAndExit();

        int port = Integer.parseInt(args[0]);
        String discoveryNodeIp = args[1];
        int discoveryNodePort = Integer.parseInt(args[2]);

        new Client(port, discoveryNodeIp, discoveryNodePort).run();
    }

    private void run() {
        new Thread(tcpServer).start();
        Utils.sleep(500);

        handleCmdLineInput();
    }

    private void handleCmdLineInput() {
        printMenu();

        String input;
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter(Pattern.compile("[\\r\\n;]+"));

        while (true) {
            Utils.out("\n");

            input = scanner.next();
            if (input.startsWith("e")) {
                Utils.info("Auf Wiedersehen");
                System.exit(0);
            }
            else if (input.startsWith("sf")) {
//                Utils.out("file:\n");
//                String fileName = scanner.next();
                String fileName = "/s/chopin/a/grad/sgaxcell/cs555-assignment2/files/Scott-Axcell-HW3-WC.pdf";
                Path path = Paths.get(fileName);
                if (!path.toFile().exists()) {
                    Utils.error("file does not exist: " + path);
                    continue;
                }
                storeFile(path);
                printProgressBar();
                Utils.info("Stored " + path);
            }
            else if (input.startsWith("rf")) {
//                Utils.out("file:\n");
//                String fileName = scanner.next();
                String fileName = "/s/chopin/a/grad/sgaxcell/cs555-assignment2/files/Scott-Axcell-HW3-WC.pdf";
                Path path = Paths.get(fileName);
                retrieveFile(path);
                printProgressBar();
            }
        }
    }

    private void retrieveFile(Path path) {
        Utils.info("Retrieving " + path + " ...", false);
        storeData.retrieveFile(path);
    }

    private void storeFile(Path path) {
        Utils.info("Storing " + path + " ...", false);
        storeData.storeFile(path);
    }

    private void printProgressBar() {
        try {
            while (storeData.isRunning()) {
                Thread.sleep(500);
                Utils.out(".");
            }
            Utils.out("\n");
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void printMenu() {
        Utils.out("\n*********************\n");
        Utils.out("h  -- print this menu\n");
        Utils.out("sf -- store file\n");
        Utils.out("rf -- retreive file\n");
        Utils.out("*********************\n");
    }

    private static void printHelpAndExit() {
        Utils.out("USAGE: java Client <peer-port> <discovery-node-host> <discovery-node-port>\n");
        System.exit(-1);
    }

    @Override
    public synchronized void onMessage(Message message) {
        int protocol = message.getProtocol();
        switch (protocol) {
            case Protocol.RANDOM_PEER_RESPONSE:
                handleRandomPeerResponse((RandomPeerResponse) message);
                break;
            case Protocol.LOOKUP_RESPONSE:
                handleLookupResponse((LookupResponse) message);
                break;
            case Protocol.RETRIEVE_FILE_RESPONSE:
                handleRetrieveFileResponse((RetrieveFileResponse) message);
                break;
            default:
                throw new RuntimeException(String.format("received an unknown message with protocol %d", protocol));
        }
    }

    private void handleRetrieveFileResponse(RetrieveFileResponse response) {
        Utils.debug("received: " + response);
        storeData.handleRetrieveFileResponse(response);
    }

    private void handleLookupResponse(LookupResponse response) {
        Utils.debug("received: " + response);
        storeData.handleLookupResponse(response);
    }

    private void handleRandomPeerResponse(RandomPeerResponse response) {
        Utils.debug("received: " + response);
        storeData.handleRandomPeerResponse(response);
    }

    @Override
    public String getNodeTypeAsString() {
        return "Client (StoreData)";
    }

    public String getIp() {
        return Utils.getIpFromAddress(discoveryNodeTcpConnection.getLocalSocketAddress());
    }

    public void sendDiscoveryNodeMessage(Message message) {
        discoveryNodeTcpConnection.send(message.getBytes());
    }

    public int getPeerPort() {
        return peerPort;
    }
}
