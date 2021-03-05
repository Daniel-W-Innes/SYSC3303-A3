package stub;

import model.StubRequestMessage;
import util.Config;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

/**
 * Abstract stub client for rpc.
 */
public abstract class StubClient {
    /**
     * The application configuration file loader.
     */
    protected final Config config;

    /**
     * The default stub client constructor.
     *
     * @param config The application configuration file loader.
     */
    protected StubClient(Config config) {
        this.config = config;
    }

    /**
     * sendAndReceive creates a StubRequestMessage then calls another version of sendAndReceive to send it and receive the response.
     *
     * @param functionNumber The number assigned to the function in order for the server to tell but function is being called.
     * @param inetAddress    The IP address to send the stubRequestMessage to.
     * @param port           The port to send the stubRequestMessage to.
     * @param <T>            The type of the response must implement serializable.
     * @return The object receive on the socket.
     * @throws IOException            IOException is thrown if the server fails to send or receive to the port.
     * @throws ClassNotFoundException ClassNotFoundException is thrown if serialization failed.
     */
    protected <T> T sendAndReceive(int functionNumber, InetAddress inetAddress, int port) throws IOException, ClassNotFoundException {
        return sendAndReceive(new StubRequestMessage(functionNumber, List.of()), inetAddress, port);
    }

    /**
     * sendAndReceive creates a StubRequestMessage then calls another version of sendAndReceive to send it and receive the response.
     *
     * @param functionNumber The number assigned to the function in order for the server to tell but function is being called.
     * @param argument       The argument for the function.
     * @param inetAddress    The IP address to send the stubRequestMessage to.
     * @param port           The port to send the stubRequestMessage to.
     * @param <T>            The type of the response must implement serializable.
     * @return The object receive on the socket.
     * @throws IOException            IOException is thrown if the server fails to send or receive to the port.
     * @throws ClassNotFoundException ClassNotFoundException is thrown if serialization failed.
     */
    protected <T> T sendAndReceive(int functionNumber, Serializable argument, InetAddress inetAddress, int port) throws IOException, ClassNotFoundException {
        return sendAndReceive(new StubRequestMessage(functionNumber, List.of(argument)), inetAddress, port);
    }

    /**
     * sendAndReceive creates a StubRequestMessage then calls another version of sendAndReceive to send it and receive the response.
     *
     * @param functionNumber The number assigned to the function in order for the server to tell but function is being called.
     * @param arguments      The function arguments.
     * @param inetAddress    The IP address to send the stubRequestMessage to.
     * @param port           The port to send the stubRequestMessage to.
     * @param <T>            The type of the response must implement serializable.
     * @return The object receive on the socket.
     * @throws IOException            IOException is thrown if the server fails to send or receive to the port.
     * @throws ClassNotFoundException ClassNotFoundException is thrown if serialization failed.
     */
    protected <T> T sendAndReceive(int functionNumber, List<Serializable> arguments, InetAddress inetAddress, int port) throws IOException, ClassNotFoundException {
        return sendAndReceive(new StubRequestMessage(functionNumber, arguments), inetAddress, port);
    }


    /**
     * sendAndReceive creates a socket, sends a stubRequestMessage, wait for the response, and deserialize response.
     *
     * @param stubRequestMessage The stubRequestMessage to send.
     * @param inetAddress        The IP address to send the stubRequestMessage to.
     * @param port               The port to send the stubRequestMessage to.
     * @param <T>                The type of the response must implement serializable.
     * @return The object receive on the socket.
     * @throws IOException            IOException is thrown if the server fails to send or receive to the port.
     * @throws ClassNotFoundException ClassNotFoundException is thrown if serialization failed.
     */
    protected <T> T sendAndReceive(StubRequestMessage stubRequestMessage, InetAddress inetAddress, int port) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream;
        ObjectInputStream objectInputStream;

        //serialize request
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(stubRequestMessage);
        objectOutputStream.flush();

        //send request and wait for response
        byte[] response = sendAndReceive(byteArrayOutputStream.toByteArray(), inetAddress, port);

        //deserialize response
        byteArrayInputStream = new ByteArrayInputStream(response);
        objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return (T) objectInputStream.readObject();
    }

    /**
     * sendAndReceive creates a socket, sends bytes on it, and wait for the response.
     *
     * @param data        The bytes to send.
     * @param inetAddress The IP address to send the bytes to.
     * @param port        The port to send the bytes to.
     * @return The bytes receive on the socket.
     * @throws IOException IOException is thrown if the server fails to send or receive to the port.
     */
    protected byte[] sendAndReceive(byte[] data, InetAddress inetAddress, int port) throws IOException {
        //using try-with-resources to close the datagram socket
        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            return sendAndReceive(datagramSocket, data, inetAddress, port);
        }
    }

    /**
     * sendAndReceive sends bytes on a given socket and wait for the response.
     *
     * @param datagramSocket The socket to send and receive bytes.
     * @param data           The bytes to send.
     * @param inetAddress    The IP address to send the bytes to.
     * @param port           The port to send the bytes to.
     * @return The bytes receive on the socket.
     * @throws IOException IOException is thrown if the server fails to send or receive to the port.
     */
    protected byte[] sendAndReceive(DatagramSocket datagramSocket, byte[] data, InetAddress inetAddress, int port) throws IOException {
        //set socket to time
        datagramSocket.setSoTimeout(config.getIntProperty("timeout"));

        //response buff
        byte[] buff = new byte[config.getIntProperty("maxMessageSize")];
        DatagramPacket datagramPacket = new DatagramPacket(buff, buff.length);

        //send request
        datagramSocket.send(new DatagramPacket(data, data.length, inetAddress, port));

        //receive response
        datagramSocket.receive(datagramPacket);
        return buff;
    }
}
