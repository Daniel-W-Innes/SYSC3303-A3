package model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;


/**
 * Representation of a function call to be use in stub.
 */
public class StubRequestMessage implements Serializable {
    /**
     * The number assigned to the function in order for the server to tell but function is being called.
     */
    private final int functionNumber;
    /**
     * The function arguments.
     */
    private final List<Serializable> arguments;

    /**
     * The default StubRequestMessage constructor.
     *
     * @param functionNumber The number assigned to the function in order for the server to tell but function is being called.
     * @param arguments      The function arguments.
     */

    public StubRequestMessage(int functionNumber, List<Serializable> arguments) {
        this.functionNumber = functionNumber;
        this.arguments = arguments;
    }

    /**
     * Get the function number.
     *
     * @return The function number.
     */
    public int getFunctionNumber() {
        return functionNumber;
    }

    /**
     * Get the function arguments.
     *
     * @return The function arguments.
     */
    public List<Serializable> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return "StubRequestMessage{" +
                "functionNumber=" + functionNumber +
                ", arguments=" + arguments +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StubRequestMessage that = (StubRequestMessage) o;
        return functionNumber == that.functionNumber && Objects.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functionNumber, arguments);
    }
}
