package stub.intermediate;

import actor.intermediate.ClientSideApi;
import model.Message;
import model.Request;
import stub.StubClient;
import util.Config;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Stub for the client side of the intermediate.
 */
public class ClientSide extends StubClient implements ClientSideApi {
    /**
     * The inetAddress for the client side of the intermediate.
     */
    private final InetAddress inetAddress;
    /**
     * The port for the client side of the intermediate.
     */
    private final int port;

    /**
     * The default client side stub constructor.
     *
     * @param config      The application configuration file loader.
     * @param inetAddress The inetAddress for the client side of the intermediate.
     * @param port        The port for the client side of the intermediate.
     */
    public ClientSide(Config config, InetAddress inetAddress, int port) {
        super(config);
        this.inetAddress = inetAddress;
        this.port = port;
    }

    @Override
    public void send(Request request) throws IOException, ClassNotFoundException {
        sendAndReceive(1, request, inetAddress, port);
    }

    @Override
    public Message send() throws IOException, ClassNotFoundException {
        return sendAndReceive(2, inetAddress, port);
    }
}
