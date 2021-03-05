package actor.intermediate;

import model.Message;
import model.Request;

import java.io.IOException;

public interface ClientSideApi {
    void send(Request request) throws IOException, ClassNotFoundException;

    Message send() throws IOException, ClassNotFoundException;
}
