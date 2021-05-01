package dev.sanjuroe.generation;

public interface Unmarshaller<T> {

    default void init() throws Exception {
    }

    T read(Parser parser) throws Throwable;
}
