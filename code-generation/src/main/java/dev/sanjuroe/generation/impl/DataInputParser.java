package dev.sanjuroe.generation.impl;

import dev.sanjuroe.generation.Parser;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DataInputParser implements Parser {

    private DataInput input;

    public DataInputParser(InputStream is) {
        this.input = new DataInputStream(is);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return input.readBoolean();
    }

    @Override
    public int readInteger() throws IOException {
        return input.readInt();
    }

    @Override
    public String readString() throws IOException {
        return input.readUTF();
    }
}
