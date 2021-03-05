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

public class ServerSide extends Thread implements ServerSideApi {
    private final Intermediate intermediate;
    private final DatagramSocket socket;

    public ServerSide(Config config, Intermediate intermediate) throws SocketException {
        this.intermediate = intermediate;
        socket = new DatagramSocket(config.getIntProperty("intermediateServerSidePort"));
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
            StubServer.receiveAsync(socket, 1024, Map.of(
                    1, (input) -> {
                        send((Response) input.get(0));
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
