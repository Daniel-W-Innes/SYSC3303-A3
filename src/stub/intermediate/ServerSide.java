package stub.intermediate;

import actor.intermediate.ServerSideApi;
import model.Message;
import model.Response;
import stub.StubClient;
import util.Config;

import java.io.IOException;
import java.net.InetAddress;

public class ServerSide extends StubClient implements ServerSideApi {
    private final InetAddress inetAddress;
    private final int port;

    /**
     * The default stub client constructor.
     *
     * @param config      The application configuration file loader.
     * @param inetAddress
     * @param port
     */
    public ServerSide(Config config, InetAddress inetAddress, int port) {
        super(config);
        this.inetAddress = inetAddress;
        this.port = port;
    }


    @Override
    public void send(Response response) throws IOException, ClassNotFoundException {
        sendAndReceive(1, response, inetAddress, port);
    }


    @Override
    public Message send() throws IOException, ClassNotFoundException {
        return sendAndReceive(2, inetAddress, port);
    }
}
