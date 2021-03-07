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


/**
 * The server side thread In charge of listening for and responding to request from the server.
 */
public class ServerSide extends Thread implements ServerSideApi {
    /**
     * The intermediate.
     */
    private final Intermediate intermediate;
    /**
     * The application configuration file loader.
     */
    private final Config config;
    /**
     * The socket to receive with.
     */
    private final DatagramSocket socket;

    /**
     * The default intermediate server side constructor.
     *
     * @param config       The application configuration file loader.
     * @param intermediate The intermediate.
     * @throws SocketException If the the server side fails to bind the socket.
     */
    public ServerSide(Config config, Intermediate intermediate) throws SocketException {
        this.config = config;
        this.intermediate = intermediate;
        socket = new DatagramSocket(this.config.getIntProperty("intermediateServerSidePort"));
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
            StubServer.receiveAsync(socket, config.getIntProperty("numHandlerThreads"), config.getIntProperty("maxMessageSize"), Map.of(
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
        // close socket to interrupt receive
        socket.close();
    }
}
