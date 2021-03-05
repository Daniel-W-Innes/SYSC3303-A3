package model;

/**
 * A bad request object.
 */
public class BadRequest extends Request {

    /**
     * The default response constructor.
     *
     * @param read     Whether to read or write from the file.
     * @param filename The file name to read/write.
     * @param mode     The mode/encoding of the file.
     */
    public BadRequest(boolean read, String filename, String mode) {
        super(read, filename, mode);
    }

    /**
     * A broken implementation of the default method.
     *
     * @return An array of garbage bytes.
     */
    public byte[] getEncoded(int maxMessageSize) {
        return new byte[]{1};
    }
}