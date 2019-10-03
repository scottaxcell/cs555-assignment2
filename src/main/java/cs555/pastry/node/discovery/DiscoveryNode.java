package cs555.pastry.node.discovery;

import cs555.pastry.node.Node;
import cs555.pastry.routing.Peer;
import cs555.pastry.transport.TcpSender;
import cs555.pastry.transport.TcpServer;
import cs555.pastry.util.Utils;
import cs555.pastry.wireformats.*;
import cs555.pastry.wireformats.debug.LeafSetResponse;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class DiscoveryNode implements Node {
    private final TcpServer tcpServer;
    private final Peers peers = new Peers();
    private final List<RegisterRequest> registerRequests = new ArrayList<>();
    private boolean isRegisterRequestInProgress;
    private String registeringPeerIp;
    private final NetworkPrinter networkPrinter;

    private DiscoveryNode(int port, int peerPort) {
        tcpServer = new TcpServer(port, this);
        networkPrinter = new NetworkPrinter(peerPort);
    }

    @Override
    public synchronized void onMessage(Message message) {
        int protocol = message.getProtocol();
        switch (protocol) {
            case Protocol.REGISTER_REQUEST:
                handleRegisterRequest((RegisterRequest) message);
                break;
            case Protocol.JOIN_COMPLETE:
                handleJoinComplete((JoinComplete) message);
                break;
            case Protocol.LEAF_SET_RESPONSE:
                handleLeafSetResponse((LeafSetResponse) message);
                break;
            case Protocol.FORGET_ME:
                handleForgetMe((ForgetMe) message);
                break;
            case Protocol.RANDOM_PEER_REQUEST:
                handleRandomPeerRequest((RandomPeerRequest) message);
                break;
            default:
                throw new RuntimeException(String.format("received an unknown message with protocol %d", protocol));
        }
    }

    private void handleRandomPeerRequest(RandomPeerRequest request) {
        Utils.debug("received: " + request);

        Peer randomPeer = peers.getRandomPeer("");
        if (randomPeer != null) {
            RandomPeerResponse response = new RandomPeerResponse(randomPeer);
            Utils.debug("sending: " + response);
            TcpSender tcpSender = new TcpSender(request.getSocket());
            tcpSender.send(response.getBytes());
        }
    }

    private void handleForgetMe(ForgetMe message) {
        Utils.debug("received: " + message);
        peers.removePeer(message.getPeer());
    }

    private void handleLeafSetResponse(LeafSetResponse response) {
        Utils.debug("received: " + response);
        networkPrinter.handleLeafSetResponse(response);
    }

    private void handleJoinComplete(JoinComplete message) {
        Utils.debug("received: " + message);
        isRegisterRequestInProgress = false;
        if (registerRequests.size() > 0)
            handleRegisterRequest(registerRequests.remove(0));
    }

    private void handleRegisterRequest(RegisterRequest request) {
        Utils.debug("received: " + request);

        RegisterResponse registerResponse = new RegisterResponse();
        if (isRegisterRequestInProgress && !request.getPeer().getAddress().equals(registeringPeerIp)) {
            registerRequests.add(request);
            return;
        }

        if (!peers.registerPeer(request.getPeer())) {
            registerResponse.setRegistrationSuccess(false);
            registeringPeerIp = request.getPeer().getAddress();
        }
        else {
            registerResponse.setRegistrationSuccess(true);
            registerResponse.setAssignedId(request.getPeer().getId());
            Peer randomPeer = peers.getRandomPeer(request.getPeer().getId());
            if (randomPeer != null) {
                registerResponse.setRandomPeerId(randomPeer.getId());
                registerResponse.setRandomPeerAddress(randomPeer.getAddress());
            }
            isRegisterRequestInProgress = true;
        }
        Socket socket = request.getSocket();
        TcpSender tcpSender = new TcpSender(socket);
        tcpSender.send(registerResponse.getBytes());
    }

    @Override
    public String getNodeTypeAsString() {
        return "DiscoveryNode";
    }

    public static void main(String[] args) {
        if (args.length != 2)
            printHelpAndExit();

        int port = Integer.parseInt(args[0]);
        int peerPort = Integer.parseInt(args[1]);

        new DiscoveryNode(port, peerPort).run();
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
            else if (input.startsWith("h")) {
                printMenu();
            }
            else if (input.startsWith("pnt")) {
                printNetworkTopology();
            }
            else if (input.startsWith("lan")) {
                printActiveNodes();
            }
        }
    }

    private void printActiveNodes() {
        Utils.info("Active Nodes");
        Utils.out("      ============\n");
        List<Peer> peers = this.peers.getPeers();
        if (peers.isEmpty())
            Utils.out("      No known active nodes\n");
        else
            peers.stream()
            .sorted((p1, p2) -> Utils.getHexIdDecimalDifference(p1.getId(), p2.getId()) > 0 ? 1 : Utils.getHexIdDecimalDifference(p2.getId(), p1.getId()) > 0 ? -1 : 0)
            .forEach(p -> {
                Utils.out("      " + p.getId() + " @ " + p.getAddress() + "\n");
            });
    }

    private void printNetworkTopology() {
        networkPrinter.print(peers.getPeers());
    }

    private static void printMenu() {
        Utils.out("\n*****************************\n");
        Utils.out("h   -- print this menu\n");
        Utils.out("pnt -- print network topology\n");
        Utils.out("lan -- list active nodes\n");
        Utils.out("*****************************\n");
    }

    private static void printHelpAndExit() {
        Utils.out("USAGE: java DiscoveryNode <port> <peer-port>\n");
        System.exit(-1);
    }
}
