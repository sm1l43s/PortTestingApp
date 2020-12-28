package logic;

import logic.entity.ScanResult;
import logic.util.IpConvert;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class IpTester {

    private String startIp;
    private String finishIp;
    private int port;
    private int timeout;
    private List<Future<ScanResult>> list;

    public IpTester(String startIp, String finishIp, int port, int timeout) {
        this.startIp = startIp;
        this.finishIp = finishIp;
        this.port = port;
        this.timeout = timeout;
    }

    public void ipTesting(List ip) {
        ExecutorService es = Executors.newFixedThreadPool(256);
        List<Future<ScanResult>> futures = new ArrayList<>();
        for (int i = 0; i < ip.size(); i++) {
            futures.add(ipIsActive(es, (String) ip.get(i), port, timeout));
        }
        es.shutdown();
        this.list = futures;
    }

    public void ipTesting() {
        ExecutorService es = Executors.newFixedThreadPool(256);
        IpConvert ipConvert = new IpConvert();
        List<Future<ScanResult>> futures = new ArrayList<>();
        List ipList = ipConvert.getRangeIpAddress(startIp, finishIp);
        for (int i = 0; i < ipList.size(); i++) {
            futures.add(ipIsActive(es, (String) ipList.get(i), port, timeout));
        }
        es.shutdown();
        this.list = futures;
    }

    private Future<ScanResult> ipIsActive(ExecutorService es, String ip, int port, int timeout) {
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

    public List<Future<ScanResult>> getList() {
        return list;
    }
}
