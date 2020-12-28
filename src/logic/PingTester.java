package logic;

import logic.util.StreamGobbler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PingTester {

    private String ip;
    private int countPackage;
    private int sizePackage;

    public PingTester(String ip, int countPackage, int sizePackage) {
        this.ip = ip;
        this.countPackage = countPackage;
        this.sizePackage = sizePackage;

    }

    public List<String> getReachablebyPing() {
        List output = new ArrayList();
        try {
            String command;
            if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                // For Windows
                command = "ping -n " + countPackage + " -l " + sizePackage + " " + ip;
            } else {
                // For Linux and OSX
                command = "ping -c " + countPackage + " -s " + sizePackage + " " + ip;
            }

            Process proc = Runtime.getRuntime().exec(command);
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
            outputGobbler.start();

            proc.waitFor();
            output = outputGobbler.getOutputLines();

        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
        return output;
    }

}
