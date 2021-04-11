package dev.sanjuroe.generation;

import java.io.IOException;

public interface Unmarshaller {

    default void init() {
    }

    Employee readEmployee(Parser parser) throws IOException;
}
