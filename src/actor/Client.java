package actor;

import actor.intermediate.ClientSideApi;
import model.BadRequest;
import model.Message;
import model.Request;
import model.Response;
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

/**
 * Sends requests and prints the response.
 */
public class Client implements Runnable {
    /**
     * The client's logger.
     */
    private final Logger logger;
    /**
     * The queue of requests to be sent on run.
     */
    private final Queue<Request> requests;
    /**
     * The client side of the intermediate.
     */
    private final ClientSideApi clientSide;
    /**
     * The application configuration file loader.
     */
    private final Config config;

    /**
     * Create a client to send requests from requests queue.
     *
     * @param config     The application configuration file loader.
     * @param clientSide The client side of the intermediate.
     * @param requests   The requests to send.
     */
    public Client(Config config, ClientSideApi clientSide, Queue<Request> requests) {
        logger = Logger.getLogger(this.getClass().getName());
        this.requests = requests;
        this.clientSide = clientSide;
        this.config = config;
    }

    /**
     * Create a client to send default requests.
     *
     * @param config     The application configuration.
     * @param clientSide The client side of the intermediate.
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
        for (int i = 0; i <= 5; i++) {
            requests.add(new Request(true, "filename " + i, "mode"));
            requests.add(new Request(false, "filename " + i, "mode"));
        }
        //add a bad request to the end of the queue
        requests.add(new BadRequest(true, "badFilename", "badMode"));
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
                    Message message = clientSide.send();  //ask intermediate for response
                    if (message instanceof Response) {
                        logger.info("Response: " + message);
                        countDownLatch.countDown(); //track response is received
                        if (countDownLatch.getCount() == 0) {
                            break; //Stop listening for more responses after enough have been received
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            for (Request request : requests) {
                //Handle interrupt by interrupting the receiving thread and stop sending requests
                if (Thread.interrupted()) {
                    thread.interrupt();
                    break;
                } else {
                    logger.info("sending: " + request);
                    //send request
                    clientSide.send(request);
                }
            }
            // wait to receive responses
            if (!countDownLatch.await(config.getIntProperty("timeout"), TimeUnit.MILLISECONDS)) {
                //using run time exception to avoid error handling inside runnable
                throw new UndeclaredThrowableException(new TimeoutException());
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
