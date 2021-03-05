import actor.Client;
import actor.Server;
import actor.intermediate.Intermediate;
import stub.intermediate.ClientSide;
import stub.intermediate.ServerSide;
import util.Config;

import java.io.IOException;
import java.net.InetAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        Config config = new Config();
        Intermediate intermediate = new Intermediate(config);
        Thread intermediateServerSideThread = intermediate.runServerSide();
        Thread intermediateClientSideThread = intermediate.runClientSide();
        Thread serverThread = new Thread(new Server(config, new ServerSide(config, InetAddress.getLocalHost(), config.getIntProperty("intermediateServerSidePort"))));
        Thread clientThread = new Thread(new Client(config, new ClientSide(config, InetAddress.getLocalHost(), config.getIntProperty("intermediateClientSidePort"))));
        serverThread.start();
        clientThread.start();
        try {
            clientThread.join();
        } catch (InterruptedException ignored) {
        }
        intermediateClientSideThread.interrupt();
        serverThread.interrupt();
        intermediateServerSideThread.interrupt();
    }
}
