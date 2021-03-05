package actor.intermediate;

import model.Message;
import model.Response;

import java.io.IOException;

/**
 * Api for the server side of the intermediate.
 */
public interface ServerSideApi {
    /**
     * Send a response to the intermediate.
     *
     * @param response The response.
     * @throws IOException            Communication failure to the intermediate.
     * @throws ClassNotFoundException Serialization failure.
     */
    void send(Response response) throws IOException, ClassNotFoundException;

    /**
     * Try to get a request to the intermediate.
     *
     * @return Message if the the intermediate has a request then the Message is the request else it is a ack.
     * @throws IOException            Communication failure to the intermediate.
     * @throws ClassNotFoundException Serialization failure.
     */
    Message send() throws IOException, ClassNotFoundException;
}
