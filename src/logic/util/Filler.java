package logic.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Filler {

    String ip = "";
    String port = "";
    boolean status = false;

    public Filler(String ip, String port) {
        this.ip = ip;
        this.port = port;
    }

    public List getIp() {
        List ip = new ArrayList();
        if (this.ip.contains("-")) {
            this.status = true;
            String[] ipRange = this.ip.split("-");
            ip = ipRange(ipRange[0].trim(), ipRange[1].trim());
        }

        if (this.ip.contains(",")) {
            this.status = false;
            ip = Arrays.asList(this.ip.split(","));
        }

        if (!this.ip.contains("-") && !this.ip.contains(",")) {
            ip.add(this.ip);
        }

        return ip;
    }

    private List ipRange(String ipStart, String ipFinish) {
        List ip = new ArrayList();
        IpConvert ipConvert = new IpConvert();
        long start = 0;
        long finish = 0;
        try {
            start = ipConvert.inetAddrToLong(InetAddress.getByName(ipStart));
            finish = ipConvert.inetAddrToLong(InetAddress.getByName(ipFinish));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        for (long i = start; i <= finish; i++) {
            ip.add(ipConvert.ipToStr(i));
        }

        return ip;
    }

    public List getPort() {
        List port = new ArrayList();

        if (this.port.contains("-")) {
            String[] portRange = this.port.split("-");
            port = portRange(portRange[0].trim(), portRange[1].trim());
        }

        if (this.port.contains(",")) {
            List list = new ArrayList();
            list = Arrays.asList(this.port.split(","));

            for (int i = 0; i < list.size(); i++) {
                String p = (String) list.get(i);
                port.add(Integer.parseInt(p.trim()));
            }
        }

        if (!this.port.contains("-") && !this.port.contains(",")) {
            port.add(Integer.parseInt(this.port));
        }

        return port;
    }

    private List portRange(String portStart, String portFinish) {
        List port = new ArrayList();
        IpConvert ipConvert = new IpConvert();
        int start = Integer.parseInt(portStart);
        int finish = Integer.parseInt(portFinish);

        for (int i = start; i <= finish; i++) {
            port.add(i);
        }
        return port;
    }

    public boolean isStatus() {
        return status;
    }
}
