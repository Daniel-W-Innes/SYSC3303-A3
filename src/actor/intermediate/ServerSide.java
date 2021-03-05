package actor.intermediate;

import model.AckMessage;
import model.Message;
import model.Request;
import model.Response;
import stub.StubServer;
import util.Config;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class ServerSide implements Runnable, ServerSideApi {
    private final Intermediate intermediate;
    private final Config config;

    public ServerSide(Config config, Intermediate intermediate) {
        this.config = config;
        this.intermediate = intermediate;
    }

    @Override
    public Message send() {
        Optional<Request> request = intermediate.getRequest();
        if (request.isPresent()) {
            return request.get();
        } else {
            return new AckMessage();
        }
    }

    @Override
    public void send(Response response) {
        intermediate.addResponse(response);
    }

    @Override
    public void run() {
        try {
            StubServer.receiveAsync(config.getIntProperty("intermediateServerSidePort"), 1024, Map.of(
                    1, (input) -> {
                        send((Response) input.get(0));
                        return new AckMessage();
                    },
                    2, (input) -> send()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
