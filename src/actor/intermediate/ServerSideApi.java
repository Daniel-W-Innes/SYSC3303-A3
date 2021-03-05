package actor.intermediate;

import model.Message;
import model.Response;

import java.io.IOException;

public interface ServerSideApi {
    void send(Response response) throws IOException, ClassNotFoundException;

    Message send() throws IOException, ClassNotFoundException;

}
