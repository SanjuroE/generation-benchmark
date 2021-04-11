package dev.sanjuroe.generation;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Data {

    public static byte[] generateEmployee() throws IOException {
        var baos = new ByteArrayOutputStream();
        var dos = new DataOutputStream(baos);
        dos.writeInt(101);
        dos.writeBoolean(true);
        dos.writeUTF("Peter");
        dos.writeUTF("Johnson");
        dos.writeInt(2007);
        dos.writeUTF("Engineer");
        dos.close();
        return baos.toByteArray();
    }
}
