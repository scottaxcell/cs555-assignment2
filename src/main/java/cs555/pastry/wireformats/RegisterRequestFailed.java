package cs555.pastry.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegisterRequestFailed implements Message {
    @Override
    public String toString() {
        return "RegisterRequestFailed";
    }

    @Override
    public int getProtocol() {
        return Protocol.REGISTER_REQUEST_FAILED;
    }

    @Override
    public byte[] getBytes() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));

            WireformatUtils.serializeInt(dataOutputStream, getProtocol());

            dataOutputStream.flush();

            byte[] data = byteArrayOutputStream.toByteArray();

            byteArrayOutputStream.close();
            dataOutputStream.close();

            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
