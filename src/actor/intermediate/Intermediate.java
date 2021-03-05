package actor.intermediate;

import model.Request;
import model.Response;
import util.Config;

import java.net.SocketException;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A buffer between the server and client.
 */
public class Intermediate {
    /**
     * Non-blocking thread safe queue for storing requests.
     */
    private final ConcurrentLinkedQueue<Request> requests;
    /**
     * Non-blocking thread safe queue for storing responses.
     */
    private final ConcurrentLinkedQueue<Response> responses;
    /**
     * The application configuration file loader.
     */
    private final Config config;


    /**
     * The default intermediate constructor.
     *
     * @param config The application configuration file loader.
     */
    public Intermediate(Config config) {
        this.config = config;
        requests = new ConcurrentLinkedQueue<>();
        responses = new ConcurrentLinkedQueue<>();
    }

    /**
     * Add request to the queue.
     *
     * @param request The new request.
     */
    public void addRequest(Request request) {
        requests.add(request);
    }

    /**
     * Add response to the queue.
     *
     * @param response The new response.
     */
    public void addResponse(Response response) {
        responses.add(response);
    }

    /**
     * Try and get a request from the queue.
     *
     * @return A optional Potentially containing a Request.
     */
    public Optional<Request> getRequest() {
        return Optional.ofNullable(requests.poll());
    }

    /**
     * Try and get a response from the queue.
     *
     * @return A optional Potentially containing a response.
     */
    public Optional<Response> getResponses() {
        return Optional.ofNullable(responses.poll());
    }

    /**
     * Run the intermediate client side.
     *
     * @return The ClientSide in order to interrupt the thread if needed.
     * @throws SocketException If the the client side fails to bind the socket.
     */
    public ClientSide runClientSide() throws SocketException {
        ClientSide clientSide = new ClientSide(config.getIntProperty("intermediateClientSidePort"), this);
        clientSide.start();
        return clientSide;
    }

    /**
     * Run the intermediate server side.
     *
     * @return The ServerSide in order to interrupt the thread if needed.
     * @throws SocketException If the the server side fails to bind the socket.
     */
    public ServerSide runServerSide() throws SocketException {
        ServerSide serverSide = new ServerSide(config.getIntProperty("intermediateServerSidePort"), this);
        serverSide.start();
        return serverSide;
    }
}
