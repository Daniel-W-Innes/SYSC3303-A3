package actor;

import actor.intermediate.ServerSideApi;
import model.Message;
import model.Request;
import model.Response;
import stub.intermediate.ServerSide;
import util.Config;

import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Logger;

/**
 * Receives requests and responses to them.
 */
public class Server implements Runnable {
    /**
     * The client's logger.
     */
    private final Logger logger;
    /**
     * The server side of the intermediate.
     */
    private final ServerSideApi serverSide;

    /**
     * The default constructor for the server.
     *
     * @param serverSide The server side of the intermediate.
     */
    public Server(ServerSideApi serverSide) {
        this.serverSide = serverSide;
        logger = Logger.getLogger(this.getClass().getName());
    }

    /**
     * Run the server in the main thread.
     *
     * @param args Unused arguments.
     * @throws IOException If it fails to parse the config file.
     */
    public static void main(String[] args) throws IOException {
        Config config = new Config();
        Server server = new Server(new ServerSide(config, InetAddress.getLocalHost(), config.getIntProperty("intermediateServerSidePort")));
        server.run();
    }


    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                Message message = serverSide.send(); //ask intermediate for request
                if (message instanceof Request) {
                    logger.info("Request: " + message);
                    serverSide.send(new Response(((Request) message).isRead())); //respond to request
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
