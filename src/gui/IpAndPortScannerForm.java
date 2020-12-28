package gui;

import logic.*;
import logic.entity.ScanResult;
import logic.util.ComboboxItems;
import logic.util.Filler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class IpAndPortScannerForm  extends JPanel {

    JTable table = null;

    String messageInfo = "<html><br>" +
            "Поле <i>IP адрес</i> - предназначено для ввода IP-адреса ресурса(-сов).<br>" +
            "<b>Варианты ввода</b>:<br>" +
            "<ul>" +
            "<li>127.0.0.1 - сканирование данного адреса;</li>" +
            "<li>127.0.0.1 - 127.0.0.255 - сканирование в диапазоне адресов;</li>" +
            "<li>127.0.0.1, 127.0.0.2, ... - сканирование перечисленных адресов.</li>" +
            "</ul>" +
            "<br>" +
            "Поле <i>Порт</i> - предназначено для ввода сканируемога порта ресурса(-сов).<br>" +
            "<b>Варианты ввода</b>:<br>" +
            "<ul>" +
            "<li>135 - сканирование данного порта;</li>" +
            "<li>1 - 1000 - сканирование в диапазоне портов;</li>" +
            "<li>1, 5, 10, ... - сканирование перечисленных портов.</li>" +
            "</ul>" +
            "Для сканирования доступны порты в диапазоне <b>0 - 65535</b>" +
            "<br>" +
            "<b>ВАЖНО:</b>  - Программа сканирует только в одном диапазоне. Либо IP-адресов, либо портов!<br>" +
            " " +
            "</html>";

    public  IpAndPortScannerForm() {

        setBackground(new Color(239, 222, 205));
        setLayout(null);

        JLabel ipLabel = new JLabel("IP адрес:");
        ipLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        ipLabel.setBounds(20, 15, 90, 50);
        add(ipLabel);

        String myIp = "127.0.0.1";

        try {
            myIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        JTextField ip = new JTextField(myIp);
        ip.setHorizontalAlignment(JTextField.CENTER);
        ip.setFont(new Font("Arial", Font.BOLD, 12));
        ip.setBounds(85, 25, 240, 30);
        add(ip);

        JButton infoLabel = new JButton("?");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoLabel.setBounds(335, 25, 30, 30);
        infoLabel.setBackground(new Color(209, 222, 205));
        infoLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        infoLabel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, messageInfo);
            }
        });
        infoLabel.setToolTipText(messageInfo);

        add(infoLabel);

        JLabel scanRangesPorts = new JLabel("Порт:");
        scanRangesPorts.setFont(new Font("Arial", Font.PLAIN, 14));
        scanRangesPorts.setBounds(20, 60, 150, 50);
        add(scanRangesPorts);

        JTextField port = new JTextField("0 - 65535");
        port.setHorizontalAlignment(JTextField.CENTER);
        port.setFont(new Font("Arial", Font.BOLD, 12));
        port.setBounds(85, 70, 240, 30);
        add(port);

        JLabel timeout = new JLabel("Задержка:");
        timeout.setFont(new Font("Arial", Font.PLAIN, 14));
        timeout.setBounds(390, 15, 100, 50);
        add(timeout);

        JComboBox comboBox = new JComboBox(ComboboxItems.items);
        comboBox.setFont(new Font("Arial", Font.BOLD, 12));
        comboBox.setSelectedIndex(5);
        comboBox.setBounds(465, 25, 150, 30);
        add(comboBox);

        table = new JTable();
        table.setModel(createModelTable((DefaultTableModel) table.getModel()));
        table.setBounds(20, 150, 590, 385);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 150, 590, 370);
        scrollPane.setPreferredSize(new Dimension(580, 390));
        add(scrollPane);

        JLabel status = new JLabel("Статус: ");
        status.setFont(new Font("Arial", Font.PLAIN, 14));
        status.setBounds(400, 110, 70, 30);
        add(status);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setBounds(460, 110, 150, 30);
        progressBar.setMinimum(0);
        progressBar.setMaximum(99);
        add(progressBar);

        JButton btn = new JButton("Сканировать");
        btn.setForeground(new Color(239, 222, 205));
        btn.setBackground(new Color(65, 145, 80));
        btn.setBounds(85, 110, 240, 30);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressBar.setValue(0);
                Filler filler = new Filler(ip.getText().trim(), port.getText().trim());

                List<String> ip = filler.getIp();
                List<Integer> port = filler.getPort();
                int timeout = Integer.parseInt(ComboboxItems.items[comboBox.getSelectedIndex()].split(" ")[0]);

                PortTester portTester = null;
                IpTester ipTester = null;

                if (ip.size() == 1 && port.size() >= 1) {
                    portTester = new PortTester(ip.get(0), port.get(0), port.get(port.size() - 1), timeout);
                    portTester.portTesting();
                }

                if (ip.size() >= 1 && port.size() == 1) {
                    ipTester = new IpTester(ip.get(0), ip.get(ip.size() - 1), port.get(0), timeout);
                    if (filler.isStatus()) {
                        ipTester.ipTesting();
                    } else {
                        ipTester.ipTesting(ip);
                    }
                }

                createModelTable((DefaultTableModel) table.getModel());

                List<ScanResult> list = new ArrayList<>();

                if (portTester != null) {
                    for (Future<ScanResult> f: portTester.getOpenPorts()) {
                        try {
                            progressBar.setValue((f.get().getPort() * 100) / port.get(port.size() - 1));
                            if (f.get().getPort() % 100 == 0) {
                                progressBar.update(progressBar.getGraphics());
                            }
                            if (f.get().isOpen()) {
                                list.add(f.get());
                            }
                        } catch (InterruptedException | ExecutionException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                if (ipTester != null) {
                    int size = ipTester.getList().size();
                    for (Future<ScanResult> f: ipTester.getList()) {
                        try {
                            int currentValue = ipTester.getList().indexOf(f);
                            progressBar.setValue((currentValue * 100) / size);
                            if (currentValue % 100 == 0) {
                                progressBar.update(progressBar.getGraphics());
                            }
                            list.add(f.get());
                        } catch (ExecutionException | InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                progressBar.setValue(100);
                addInfo((DefaultTableModel) table.getModel(), list);
            }
        });
        add(btn);

    }


    private DefaultTableModel createModelTable(DefaultTableModel model) {
        model.setRowCount(0);
        model.setColumnCount(0);
        model.addColumn("№");
        model.addColumn("IP-адрес");
        model.addColumn("Порт");
        model.addColumn("Статус");

        return model;
    }

    private void addInfo(DefaultTableModel model, List<ScanResult> scanResultList) {

        for (int i = 0; i < scanResultList.size(); i++) {
            String str = "Закрыт";
            if (scanResultList.get(i).isOpen()) {
                str = "Открыт";
            }
            model.addRow(new Object[]{i+1,scanResultList.get(i).getIpAddress() , scanResultList.get(i).getPort(), str});
        }

    }

}
