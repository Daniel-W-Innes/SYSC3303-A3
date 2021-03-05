package stub.intermediate;

import actor.intermediate.ServerSideApi;
import model.Message;
import model.Response;
import stub.StubClient;
import util.Config;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Stub for the server side of the intermediate.
 */
public class ServerSide extends StubClient implements ServerSideApi {
    /**
     * The inetAddress for the server side of the intermediate.
     */
    private final InetAddress inetAddress;
    /**
     * The port for the server side of the intermediate.
     */
    private final int port;

    /**
     * The default server side stub constructor.
     *
     * @param config      The application configuration file loader.
     * @param inetAddress The inetAddress for the server side of the intermediate.
     * @param port        The port for the server side of the intermediate.
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
