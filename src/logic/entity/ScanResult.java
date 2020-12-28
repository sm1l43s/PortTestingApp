package logic.entity;

public class ScanResult {

    private String ipAddress;
    private int port;
    private boolean isOpen;

    public ScanResult(int port, boolean isOpen) {
        this.port = port;
        this.isOpen = isOpen;
    }

    public ScanResult(String ipAddress, int port, boolean isOpen) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.isOpen = isOpen;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public String toString() {
        return "ScanResult{" +
                "ipAddress='" + ipAddress + '\'' +
                ", port=" + port +
                ", isOpen=" + isOpen +
                '}';
    }
}
