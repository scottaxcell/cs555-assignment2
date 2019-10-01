package cs555.pastry.node.client;

import cs555.pastry.node.Node;
import cs555.pastry.transport.TcpServer;
import cs555.pastry.util.Utils;
import cs555.pastry.wireformats.Message;

import java.util.Scanner;
import java.util.regex.Pattern;

public class Client implements Node {
    private final TcpServer tcpServer;

    public Client() {
        tcpServer = new TcpServer(0, this);
    }

    public static void main(String[] args) {
        new Client().run();
    }

    private void run() {
        new Thread(tcpServer).start();
        Utils.sleep(500);

        handleCmdLineInput();
    }

    private void handleCmdLineInput() {
//        printMenu();

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
        }
    }

    private static void printHelpAndExit() {
        Utils.out("USAGE: java Client\n");
        System.exit(-1);
    }

    @Override
    public void onMessage(Message message) {

    }

    @Override
    public String getNodeTypeAsString() {
        return "Client (StoreData)";
    }
}
