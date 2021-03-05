package actor;

import actor.intermediate.ServerSideApi;
import model.Message;
import model.Request;
import model.Response;
import stub.StubClient;
import stub.intermediate.ServerSide;
import util.Config;

import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Logger;

public class Server extends StubClient implements Runnable {
    /**
     * The application configuration file loader.
     */
    public final Config config;
    /**
     * The client's logger.
     */
    private final Logger logger;
    private final ServerSideApi serverSide;

    /**
     * The default constructor for the server.
     *
     * @param config     The application configuration file loader.
     * @param serverSide
     */
    public Server(Config config, ServerSideApi serverSide) {
        super(config);
        this.config = config;
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
        Server server = new Server(config, new ServerSide(config, InetAddress.getLocalHost(), config.getIntProperty("intermediateServerSidePort")));
        server.run();
    }


    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                Message message = serverSide.send();
                if (message instanceof Request) {
                    logger.info("Request: " + message);
                    serverSide.send(new Response(((Request) message).isRead()));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
