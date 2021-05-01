package dev.sanjuroe.generation;

import java.io.IOException;

public interface Parser {

    boolean readBoolean() throws IOException;

    int readInteger() throws IOException;

    String readString() throws IOException;
}
