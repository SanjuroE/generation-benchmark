package dev.sanjuroe.generation;

public interface Unmarshaller {

    default void init() throws Exception {
    }

    Employee readEmployee(Parser parser) throws Throwable;
}
