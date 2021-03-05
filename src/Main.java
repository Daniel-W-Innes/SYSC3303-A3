import actor.Client;
import actor.Server;
import actor.intermediate.Intermediate;
import stub.intermediate.ClientSide;
import stub.intermediate.ServerSide;
import util.Config;

import java.io.IOException;
import java.net.InetAddress;

/**
 * A main class to run all the actors.
 */
public class Main {
    /**
     * A main function to run all the actors in their own thread.
     *
     * @param args Unused arguments.
     * @throws IOException If it fails to parse the config file.
     */
    public static void main(String[] args) throws IOException {
        Config config = new Config();

        //run intermediate
        Intermediate intermediate = new Intermediate(config);
        Thread intermediateServerSideThread = intermediate.runServerSide();
        Thread intermediateClientSideThread = intermediate.runClientSide();

        //create client and server
        Thread serverThread = new Thread(new Server(new ServerSide(config, InetAddress.getLocalHost(), config.getIntProperty("intermediateServerSidePort"))));
        Thread clientThread = new Thread(new Client(config, new ClientSide(config, InetAddress.getLocalHost(), config.getIntProperty("intermediateClientSidePort"))));

        //run server
        serverThread.start();

        //run client
        clientThread.start();

        try {
            //wait client thread end
            clientThread.join();
        } catch (InterruptedException ignored) {
            //interrupt client thread if the main is interrupted
            clientThread.interrupt();
        }
        //sleep to give messages time to move through the system
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }

        //Interrupt all remaining threads
        intermediateClientSideThread.interrupt();
        serverThread.interrupt();
        intermediateServerSideThread.interrupt();
    }
}
