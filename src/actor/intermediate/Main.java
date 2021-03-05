package actor.intermediate;

import util.Config;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Config config = new Config();
        Intermediate intermediate = new Intermediate(config);
        intermediate.runClientSide();
        intermediate.runServerSide();
    }
}
