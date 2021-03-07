package stub;


import model.StubRequestMessage;

import java.io.*;
import java.lang.reflect.UndeclaredThrowableException;
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
     * receiveAsync uses a datagramSocket to receiving message then callbacks to handle requests
     * <p>
     * The expected implementation of this method is to use the callbacks to specify the server's response to requests.
     * The key of callbacks is the function number and specifies which function was requested.
     * The value of callbacks is the function to be called.
     * For a function with one argument, the callback will be called with a list of length one.
     * For void functions, the callback should return AckMessage.
     *
     * @param datagramSocket The socket used for receiving.
     * @param maxMessageSize The maximum number of bytes for a message.
     * @param callbacks      The function number mapped to the sever callback function.
     * @throws IOException IOException is thrown if the server fails to receive or bind to the port.
     * @see model.AckMessage
     */
    public static void receiveAsync(DatagramSocket datagramSocket, int numHandlerThreads, int maxMessageSize, Map<Integer, Function<List<Serializable>, Serializable>> callbacks) throws IOException {
        //use a thread pool to handle request and avoid blocking receive
        ExecutorService executorService = Executors.newFixedThreadPool(numHandlerThreads);

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
            if (!Thread.interrupted()) {
                throw e;
            }
        } finally {
            executorService.shutdown();
        }
    }

    /**
     * handleMessage deserialize the request then uses the callback to determine the response.
     * The responses is response a sent back on a different socket.
     *
     * @param inetAddress The inetAddress of the request sender.
     * @param port        The port of the request sender.
     * @param data        The data from the request.
     * @param callbacks   The callback functions.
     */
    private static void handleMessage(InetAddress inetAddress, int port, byte[] data, Map<Integer, Function<List<Serializable>, Serializable>> callbacks) {

        StubRequestMessage stubRequestMessage;
        try {
            //deserialize request
            stubRequestMessage = (StubRequestMessage) (new ObjectInputStream(new ByteArrayInputStream(data))).readObject();
        } catch (IOException | ClassNotFoundException e) {
            return; //fail to deserialize request is http this would be a 400 error
        }
        //using try-with-resources to close the datagram socket
        try (DatagramSocket datagramSocket = new DatagramSocket()) {

            //run the appropriate callback to get response
            Serializable response = callbacks.get(stubRequestMessage.getFunctionNumber()).apply(stubRequestMessage.getArguments());

            //serialize response
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(response);
            objectOutputStream.flush();
            data = byteArrayOutputStream.toByteArray();

            //send response
            datagramSocket.send(new DatagramPacket(data, data.length, inetAddress, port));
        } catch (IOException e) {
            //fail to serialize or send response is http this would be a 500 error
            //using run time exception to avoid error handling inside lambdas
            throw new UndeclaredThrowableException(e);
        }
    }
}
