package stub.intermediate;

import actor.intermediate.ClientSideApi;
import model.Message;
import model.Request;
import stub.StubClient;
import util.Config;

import java.io.IOException;
import java.net.InetAddress;

public class ClientSide extends StubClient implements ClientSideApi {
    private final InetAddress inetAddress;
    private final int port;

    /**
     * The default stub client constructor.
     *
     * @param config      The application configuration file loader.
     * @param inetAddress
     * @param port
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
