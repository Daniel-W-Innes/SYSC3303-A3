package actor.intermediate;

import model.AckMessage;
import model.Message;
import model.Request;
import model.Response;
import stub.StubServer;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Map;
import java.util.Optional;

/**
 * The client side thread In charge of listening for and responding to request from the server.
 */
public class ClientSide extends Thread implements ClientSideApi {
    /**
     * The intermediate.
     */
    private final Intermediate intermediate;
    /**
     * The socket to receive with.
     */
    private final DatagramSocket socket;

    /**
     * The default intermediate client side constructor.
     *
     * @param port         The port for the client side.
     * @param intermediate The intermediate.
     * @throws SocketException If the the client side fails to bind the socket.
     */
    public ClientSide(int port, Intermediate intermediate) throws SocketException {
        this.intermediate = intermediate;
        socket = new DatagramSocket(port);
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
