package actor.intermediate;

import model.AckMessage;
import model.Message;
import model.Request;
import model.Response;
import stub.StubServer;
import util.Config;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Map;
import java.util.Optional;

public class ClientSide extends Thread implements ClientSideApi {
    private final Intermediate intermediate;
    private final DatagramSocket socket;

    public ClientSide(Config config, Intermediate intermediate) throws SocketException {
        this.intermediate = intermediate;
        socket = new DatagramSocket(config.getIntProperty("intermediateClientSidePort"));
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
            StubServer.receiveAsync(socket, 1024, Map.of(
                    1, (input) -> {
                        send((Request) input.get(0));
                        return new AckMessage();
                    },
                    2, (input) -> send()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        socket.close();
    }
}
