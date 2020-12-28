package gui;

import javax.swing.*;
import java.awt.*;

public class MainForm extends JFrame {

    public MainForm() {
        setTitle("Сканер сети");
        setSize(new Dimension(640, 600));
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 16));
        tabbedPane.setBounds(0, 0, 640, 600);
        tabbedPane.add("Сканирование сети", new IpAndPortScannerForm());
        tabbedPane.add("Утилита PING", new PingForm());
        getContentPane().add(tabbedPane);

        setVisible(true);
    }



}
