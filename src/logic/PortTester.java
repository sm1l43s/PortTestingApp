package logic;

import logic.entity.ScanResult;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PortTester{

    private String ipAddress;
    private int startPort;
    private int finishPort;
    private int timeout;
    private List<Future<ScanResult>> openPorts;

    public PortTester(String ipAddress, int startPort, int finishPort, int timeout) {
        this.ipAddress = ipAddress;
        this.startPort = startPort;
        this.finishPort = finishPort;
        this.timeout = timeout;
    }

    public void portTesting() {
        ExecutorService es = Executors.newFixedThreadPool(256);
        List<Future<ScanResult>> futures = new ArrayList<>();
        for (int port = startPort; port <= finishPort; port++) {
            futures.add(portIsOpen(es, ipAddress, port, timeout));
        }
        es.shutdown();
        this.openPorts = futures;
    }

    private Future<ScanResult> portIsOpen(ExecutorService es, String ip, int port, int timeout) {
        return es.submit(new Callable<ScanResult>() {
            @Override public ScanResult call() {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(ip, port), timeout);
                    return new ScanResult(ip, port, true);
                } catch (Exception ex) {
                    return new ScanResult(ip, port, false);
                }
            }
        });
    }

    public List<Future<ScanResult>> getOpenPorts() {
        return (List<Future<ScanResult>>) openPorts;
    }
}
