package actor.intermediate;

import model.Request;
import model.Response;
import util.Config;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Intermediate {
    private final ConcurrentLinkedQueue<Request> requests;
    private final ConcurrentLinkedQueue<Response> responses;
    private final Config config;


    public Intermediate(Config config) {
        this.config = config;
        requests = new ConcurrentLinkedQueue<>();
        responses = new ConcurrentLinkedQueue<>();
    }

    public void addRequest(Request request) {
        requests.add(request);
    }

    public void addResponse(Response response) {
        responses.add(response);
    }

    public Optional<Request> getRequest() {
        return Optional.ofNullable(requests.poll());
    }

    public Optional<Response> getResponses() {
        return Optional.ofNullable(responses.poll());
    }

    public Thread runClientSide() {
        ClientSide clientSide = new ClientSide(config, this);
        Thread thread = new Thread(clientSide);
        thread.start();
        return thread;
    }

    public Thread runServerSide() {
        ServerSide serverSide = new ServerSide(config, this);
        Thread thread = new Thread(serverSide);
        thread.start();
        return thread;
    }
}
