package model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * The request object sent by client containing information specified in the outline.
 */
public class Request extends Message {
    /**
     * Whether to read or write from the file.
     */
    private boolean read;
    /**
     * The file name to read/write.
     */
    private String filename;
    /**
     * The mode/encoding of the file.
     */
    private String mode;

    /**
     * The default response constructor.
     *
     * @param read     Whether to read or write from the file.
     * @param filename The file name to read/write.
     * @param mode     The mode/encoding of the file.
     */
    public Request(boolean read, String filename, String mode) {
        this.read = read;
        this.filename = filename;
        this.mode = mode;
    }

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.write(getEncoded());
    }

    @Serial
    private void readObject(ObjectInputStream in) throws Exception {
        Builder builder = fromEncoded(in.readAllBytes());
        this.read = builder.read;
        this.filename = builder.filename;
        this.mode = builder.mode;
    }


    /**
     * A secondary constructor to create a request object from request bytes.
     * See getEncoded for the encoding schema.
     *
     * @param bytes The byte encoded request object.
     * @return The request object constructed.
     * @throws Exception If bytes are not from a request object.
     */
    public static Builder fromEncoded(byte[] bytes) throws Exception {
        //create a builder for parsing byte encoded requests
        Builder builder = new Builder();
        for (byte b : bytes) {
            //feed the parser the bytes one by one
            builder.getState().handle(b);
        }
        return builder;
    }

    /**
     * Encode request object to bytes for transmission. The encoding is as follows:
     * |0 |1   |2 |3 - x-1 |x |x+2 - y-1|y |
     * +--+----+--+--------+--+---------+--+
     * |0 |type|0 |filename|0 |mode     |0 |
     * +--+----+--+--------+--+---------+--+
     * <p>
     * type is if the request is read(2) or write(1).
     * filename is a UTF-8 encoded string.
     * mode is a UTF-8 encoded string.
     * <p>
     * x is the separator between the filename and the mode and is not at a fixed position within the encoding.
     * y is the termination byte and can be at any point before the maxMessageSize.
     *
     * @return The byte encoded request object.
     */
    public byte[] getEncoded() {
        //the byteArray stream for concatenating the output
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(0); //separating byte
        output.write(read ? 0x2 : 0x1); //type byte, 2 for read, 1 for write
        output.writeBytes(filename.getBytes(StandardCharsets.UTF_8)); //UTF-8 encoded filename
        output.write(0); //separating byte
        output.writeBytes(mode.getBytes(StandardCharsets.UTF_8)); //UTF-8 encoded mode
        output.write(0); //termination byte
        return output.toByteArray();
    }

    /**
     * Get if the request is of type read.
     *
     * @return If the request is of type read.
     */
    public boolean isRead() {
        return read;
    }

    @Override
    public String toString() {
        return "model.Request{" +
                "read=" + read +
                ", filename='" + filename + '\'' +
                ", mode='" + mode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return read == request.read && Objects.equals(filename, request.filename) && Objects.equals(mode, request.mode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(read, filename, mode);
    }

    /**
     * The builder for request objects.
     */
    public static class Builder {
        /**
         * Whether to read or write from the file.
         */
        private boolean read;
        /**
         * The file name to read/write.
         */
        private String filename;
        /**
         * The mode/encoding of the file.
         */
        private String mode;
        /**
         * The state of the request parser.
         */
        private State state;

        /**
         * The default constructor for the request object builder.
         */
        public Builder() {
            state = new InitialState(this);
        }

        /**
         * Set the type of the request.
         *
         * @param read Whether to read or write from the file.
         */
        public void setRead(boolean read) {
            this.read = read;
        }

        /**
         * Set the filename of the request.
         *
         * @param filename The file name to read/write.
         */
        public void setFilename(String filename) {
            this.filename = filename;
        }

        /**
         * Set the mode of the request.
         *
         * @param mode The mode/encoding of the file.
         */
        public void setMode(String mode) {
            this.mode = mode;
        }

        /**
         * Build request object from the parameters of the builder.
         *
         * @return The request object built.
         * @throws Exception If not all parameters are set.
         */
        public Request build() throws Exception {
            if (filename == null || mode == null) {
                throw new Exception();
            }
            return new Request(read, filename, mode);
        }

        /**
         * Get the current state of the parser.
         *
         * @return The current state.
         */
        public State getState() {
            return state;
        }

        /**
         * Set the state of the parser.
         *
         * @param state The new state for the parser.
         */
        public void setState(State state) {
            this.state = state;
        }
    }
}

/**
 * Abstract state class for a object orientated parser.
 */
abstract class State {
    /**
     * The request builder.
     */
    protected final Request.Builder builder;

    /**
     * The default state constructor. This needs the builder so that it can store parameters across multiple states.
     *
     * @param builder The request builder.
     */
    protected State(Request.Builder builder) {
        this.builder = builder;
    }

    /**
     * Handle an individual byte from the byte encoded request object.
     *
     * @param b The byte to decode.
     * @throws Exception If byte does not align with the format of a byte encoded request object.
     */
    public abstract void handle(byte b) throws Exception;
}

/**
 * The initial state of the parser.
 */
class InitialState extends State {
    public InitialState(Request.Builder builder) {
        super(builder);
    }

    @Override
    public void handle(byte b) throws Exception {
        //if the first byte is not 0 throws Exception
        if (b != 0) {
            throw new Exception();
        } else {
            builder.setState(new TypeState(builder)
            );
        }
    }
}

/**
 * Decode if the request is read or write.
 */
class TypeState extends State {
    protected TypeState(Request.Builder builder) {
        super(builder);
    }

    @Override
    public void handle(byte b) throws Exception {
        //if the type byte is not a 1 or 2 throws Exception
        if (b != 1 && b != 2) {
            throw new Exception();
        }
        //set the read parameter in the builder
        builder.setRead(b == 2);
        //set the next state to decode filename
        builder.setState(new FilenameState(builder));
    }
}

/**
 * Abstract class for parsing a string ending a 0 byte.
 */
abstract class StringState extends State {
    /**
     * The temporary storage for the string while waiting for a 0 byte.
     */
    private final ByteArrayOutputStream output;

    protected StringState(Request.Builder builder) {
        super(builder);
        output = new ByteArrayOutputStream();
    }

    /**
     * Move to next state and set the parameter in the builder.
     *
     * @param output The string parsed by the StringState.
     */
    abstract protected void nextState(String output);

    @Override
    public void handle(byte b) {
        if (b == 0) {
            //pass bytes to concrete implementation
            nextState(output.toString(StandardCharsets.UTF_8));
        } else {
            //save byte
            output.write(b);
        }
    }
}

/**
 * The state for decoding the filename.
 */
class FilenameState extends StringState {
    protected FilenameState(Request.Builder builder) {
        super(builder);
    }

    @Override
    protected void nextState(String output) {
        //set the filename parameter in the builder
        builder.setFilename(output);
        //set the next state to decode mode
        builder.setState(new ModeState(builder));
    }
}

/**
 * The state for decoding the mode.
 */
class ModeState extends StringState {
    protected ModeState(Request.Builder builder) {
        super(builder);
    }

    @Override
    protected void nextState(String output) {
        //set the mode parameter in the builder
        builder.setMode(output);
        //set the next state to end
        builder.setState(new EndState(builder));
    }
}

/**
 * The state for when parsing is done, ignoring all remaining bytes.
 */
class EndState extends State {
    protected EndState(Request.Builder builder) {
        super(builder);
    }

    @Override
    public void handle(byte b) {
        //do nothing with byte
    }
}

