package cs555.pastry.wireformats;

import java.io.*;

public class RegisterResponse implements Message {
    private boolean registrationSuccess = false;
    private String assignedId = "";
    private String randomPeerId = "";
    private String randomPeerAddress = "";

    public RegisterResponse() {
    }

    @Override
    public String toString() {
        return "RegisterResponse{" +
            "registrationSuccess=" + registrationSuccess +
            ", assignedId='" + assignedId + '\'' +
            ", randomPeerId='" + randomPeerId + '\'' +
            ", randomPeerAddress='" + randomPeerAddress + '\'' +
            '}';
    }

    @Override
    public int getProtocol() {
        return Protocol.REGISTER_RESPONSE;
    }

    @Override
    public byte[] getBytes() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));

            WireformatUtils.serializeInt(dataOutputStream, getProtocol());
            WireformatUtils.serializeBoolean(dataOutputStream, registrationSuccess);
            WireformatUtils.serializeString(dataOutputStream, assignedId);
            WireformatUtils.serializeString(dataOutputStream, randomPeerId);
            WireformatUtils.serializeString(dataOutputStream, randomPeerAddress);

            dataOutputStream.flush();

            byte[] data = byteArrayOutputStream.toByteArray();

            byteArrayOutputStream.close();
            dataOutputStream.close();

            return data;
        }
        catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public RegisterResponse(byte[] bytes) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(byteArrayInputStream));

            int protocol = WireformatUtils.deserializeInt(dataInputStream);
            registrationSuccess = WireformatUtils.deserializeBoolean(dataInputStream);
            assignedId = WireformatUtils.deserializeString(dataInputStream);
            randomPeerId = WireformatUtils.deserializeString(dataInputStream);
            randomPeerAddress = WireformatUtils.deserializeString(dataInputStream);

            byteArrayInputStream.close();
            dataInputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isRegistrationSuccess() {
        return registrationSuccess;
    }

    public void setRegistrationSuccess(boolean registrationSuccess) {
        this.registrationSuccess = registrationSuccess;
    }

    public String getAssignedId() {
        return assignedId;
    }

    public void setAssignedId(String assignedId) {
        this.assignedId = assignedId;
    }

    public String getRandomPeerId() {
        return randomPeerId;
    }

    public void setRandomPeerId(String randomPeerId) {
        this.randomPeerId = randomPeerId;
    }

    public String getRandomPeerAddress() {
        return randomPeerAddress;
    }

    public void setRandomPeerAddress(String randomPeerAddress) {
        this.randomPeerAddress = randomPeerAddress;
    }
}
