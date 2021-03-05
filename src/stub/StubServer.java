package stub;


import model.StubRequestMessage;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * Abstract stub server for rpc.
 */
public class StubServer {

    /**
     * receiveAsync binds to a socket and then uses callbacks to handle requests
     * <p>
     * The expected implementation of this method is to use the callbacks to specify the server's response to requests.
     * The key of callbacks is the function number and specifies which function was requested.
     * The value of callbacks is the function to be called.
     * For a function with one argument, the callback will be called with a list of length one.
     * For void functions, the callback should return AckMessage.
     *
     * @param datagramSocket The port number to bind to.
     * @param maxMessageSize The maximum number of bytes for a message.
     * @param callbacks      The function number mapped to the sever callback function.
     * @throws IOException IOException is thrown if the server fails to receive or bind to the port.
     * @see model.AckMessage
     */
    public static void receiveAsync(DatagramSocket datagramSocket, int maxMessageSize, Map<Integer, Function<List<Serializable>, Serializable>> callbacks) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        try {
            while (!Thread.interrupted()) { //loop until the thread is interrupted
                //reset buff between requests
                byte[] buff = new byte[maxMessageSize];
                DatagramPacket datagramPacket = new DatagramPacket(buff, buff.length);

                //receive request
                datagramSocket.receive(datagramPacket);

                //handle request
                executorService.submit(() -> handleMessage(datagramPacket.getAddress(), datagramPacket.getPort(), datagramPacket.getData(), callbacks));
            }
        } catch (SocketException e) {
            if (!Thread.interrupted()){
                throw e;
            }
        } finally {
            executorService.shutdown();
        }
    }

    private static void handleMessage(InetAddress inetAddress, int port, byte[] buff, Map<Integer, Function<List<Serializable>, Serializable>> callbacks) {
        //deserialize request
        StubRequestMessage stubRequestMessage;
        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            stubRequestMessage = (StubRequestMessage) (new ObjectInputStream(new ByteArrayInputStream(buff))).readObject();


            //run the appropriate callback to get response
            Serializable response = callbacks.get(stubRequestMessage.getFunctionNumber()).apply(stubRequestMessage.getArguments());

            //serialize response
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(response);
            objectOutputStream.flush();
            buff = byteArrayOutputStream.toByteArray();

            //send response
            datagramSocket.send(new DatagramPacket(buff, buff.length, inetAddress, port));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException();
        }
    }
}
