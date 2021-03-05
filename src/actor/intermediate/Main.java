package actor.intermediate;

import util.Config;

import java.io.IOException;

/**
 * A main class to run intermediate.
 */
public class Main {
    /**
     * A main function to run all components of the intermediate.
     *
     * @param args Unused arguments.
     * @throws IOException If it fails to parse the config file.
     */
    public static void main(String[] args) throws IOException {
        Config config = new Config();
        Intermediate intermediate = new Intermediate(config);
        intermediate.runClientSide();
        intermediate.runServerSide();
    }
}
