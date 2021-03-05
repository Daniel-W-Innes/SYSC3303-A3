package actor.intermediate;

import model.Message;
import model.Request;

import java.io.IOException;

/**
 * Api for the client side of the intermediate.
 */
public interface ClientSideApi {
    /**
     * Send a request to the intermediate.
     *
     * @param request The request.
     * @throws IOException            Communication failure to the intermediate.
     * @throws ClassNotFoundException Serialization failure.
     */
    void send(Request request) throws IOException, ClassNotFoundException;

    /**
     * Try to get a response to the intermediate.
     *
     * @return Message if the the intermediate has a response then the Message is the request else it is a ack.
     * @throws IOException            Communication failure to the intermediate.
     * @throws ClassNotFoundException Serialization failure.
     */
    Message send() throws IOException, ClassNotFoundException;
}
