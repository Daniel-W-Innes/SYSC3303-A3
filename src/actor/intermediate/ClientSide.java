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

public class ClientSide implements Runnable, ClientSideApi {
    private final Intermediate intermediate;
    private final Config config;

    public ClientSide(Config config, Intermediate intermediate) {
        this.config = config;
        this.intermediate = intermediate;
    }

    @Override
    public void send(Request request) {
        intermediate.addRequest(request);
    }

    @Override
    public Message send() {
        Optional<Response> response = intermediate.getResponses();
        if (response.isPresent()) {
            return response.get();
        } else {
            return new AckMessage();
        }
    }

    @Override
    public void run() {
        try {
            StubServer.receiveAsync(config.getIntProperty("intermediateClientSidePort"), 1024, Map.of(
                    1, (input) -> {
                        send((Request) input.get(0));
                        return new AckMessage();
                    },
                    2, (input) -> send()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
