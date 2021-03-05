package actor;

import actor.intermediate.ClientSideApi;
import model.Message;
import model.Request;
import model.Response;
import stub.StubClient;
import stub.intermediate.ClientSide;
import util.Config;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class Client extends StubClient implements Runnable {
    /**
     * The client's logger.
     */
    private final Logger logger;
    /**
     * The queue of requests to be sent on run.
     */
    private final Queue<Request> requests;

    private final ClientSideApi clientSide;

    /**
     * Create a client to send requests from requests queue.
     *
     * @param config   The application configuration file loader.
     * @param requests The requests to send.
     */
    public Client(Config config, ClientSideApi clientSide, Queue<Request> requests) {
        super(config);
        logger = Logger.getLogger(this.getClass().getName());
        this.requests = requests;
        this.clientSide = clientSide;
    }

    /**
     * Create a client to send default requests.
     *
     * @param config The application configuration.
     */
    public Client(Config config, ClientSideApi clientSide) {
        this(config, clientSide, getDefaultRequests());
    }


    /**
     * Generate a queue containing the default requests specified in the outline.
     *
     * @return The request queue.
     */
    public static Queue<Request> getDefaultRequests() {
        Queue<Request> requests = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            requests.add(new Request(true, "filename " + i, "mode"));
            requests.add(new Request(false, "filename " + i, "mode"));
        }
        //add a bad request to the end of the queue
        requests.add(new Request(true, "badFilename", "badMode"));
        return requests;
    }


    /**
     * Run the server in the main thread.
     *
     * @param args Unused arguments.
     * @throws IOException If it fails to parse the config file.
     */
    public static void main(String[] args) throws IOException {
        Config config = new Config();
        Client client = new Client(config, new ClientSide(config, InetAddress.getLocalHost(), config.getIntProperty("intermediateClientSidePort")));
        client.run();
    }


    @Override
    public void run() {
        CountDownLatch countDownLatch = new CountDownLatch(requests.size());
        Thread thread = new Thread(() -> {
            try {
                while (!Thread.interrupted()) {
                    Message message = clientSide.send();
                    if (message instanceof Response) {
                        logger.info("Response: " + message);
                        countDownLatch.countDown();
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            for (Request request : requests) {
                if (Thread.interrupted()) {
                    thread.interrupt();
                    break;
                } else {
                    logger.info("sending: " + request);
                    clientSide.send(request);
                }
            }
            if (!countDownLatch.await(config.getIntProperty("timeout"), TimeUnit.SECONDS)) {
                throw new UndeclaredThrowableException(new TimeoutException());
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
