package gui;

import logic.util.ComboboxItems;
import logic.PortTester;
import logic.entity.ScanResult;

import javax.swing.*;
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

public class PortScannerForm extends JPanel {

    JTable table = null;

    public PortScannerForm() {

        setBackground(new Color(239, 222, 205));
        setLayout(null);

        JLabel scanIpAddressLabel = new JLabel("IP-адрес:");
        scanIpAddressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        scanIpAddressLabel.setBounds(20, 15, 100, 50);
        add(scanIpAddressLabel);

        String myIp = null;
        try {
            myIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            myIp = "127.0.0.1";
            e.printStackTrace();
        }

        JTextField ipAddress = new JTextField(myIp);
        ipAddress.setHorizontalAlignment(JTextField.CENTER);
        ipAddress.setFont(new Font("Arial", Font.BOLD, 12));
        ipAddress.setBounds(85, 25, 170, 30);
        add(ipAddress);

        JLabel scanRangesPorts = new JLabel("Порт от:");
        scanRangesPorts.setFont(new Font("Arial", Font.PLAIN, 14));
        scanRangesPorts.setBounds(20, 60, 150, 50);
        add(scanRangesPorts);

        JTextField startPort = new JTextField("0");
        startPort.setHorizontalAlignment(JTextField.CENTER);
        startPort.setFont(new Font("Arial", Font.BOLD, 12));
        startPort.setBounds(85, 70, 60, 30);
        add(startPort);

        JLabel finiishPortLabel = new JLabel("до");
        finiishPortLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        finiishPortLabel.setBounds(155, 60, 50, 50);
        add(finiishPortLabel);

        JTextField finishPort = new JTextField("65535");
        finishPort.setHorizontalAlignment(JTextField.CENTER);
        finishPort.setFont(new Font("Arial", Font.BOLD, 12));
        finishPort.setBounds(180, 70, 75, 30);
        add(finishPort);

        JLabel timeout = new JLabel("Задержка:");
        timeout.setFont(new Font("Arial", Font.PLAIN, 14));
        timeout.setBounds(320, 15, 100, 50);
        add(timeout);

        JComboBox comboBox = new JComboBox(ComboboxItems.items);
        comboBox.setFont(new Font("Arial", Font.BOLD, 12));
        comboBox.setSelectedIndex(5);
        comboBox.setBounds(395, 25, 210, 30);
        add(comboBox);

        table = new JTable();
        table.setModel(createModelTable((DefaultTableModel) table.getModel()));
        table.setBounds(20, 150, 590, 385);
        table.setPreferredSize(new Dimension(590, 385));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 150, 590, 370);
        scrollPane.setPreferredSize(new Dimension(580, 370));
        add(scrollPane);

        JLabel status = new JLabel("Статус: ");
        status.setFont(new Font("Arial", Font.PLAIN, 14));
        status.setBounds(170, 110, 100, 30);
        add(status);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setBounds(230, 110, 200, 30);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        add(progressBar);

        JButton btn = new JButton("Сканировать");
        btn.setForeground(new Color(239, 222, 205));
        btn.setBackground(new Color(65, 145, 80));
        btn.setBounds(370, 65, 200, 30);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressBar.setValue(0);
                try {
                    Integer.parseInt(startPort.getText());
                    Integer.parseInt(finishPort.getText());

                    int timeout = Integer.parseInt(ComboboxItems.items[comboBox.getSelectedIndex()].split(" ")[0]);

                    PortTester tester = new PortTester(ipAddress.getText(), Integer.parseInt(startPort.getText()),
                            Integer.parseInt(finishPort.getText()), timeout);
                    tester.portTesting();

                    createModelTable((DefaultTableModel) table.getModel());

                    List<ScanResult> list = new ArrayList<>();
                    for (Future<ScanResult> f: tester.getOpenPorts()) {
                        try {
                            progressBar.setValue((f.get().getPort() * 100) / Integer.parseInt(finishPort.getText()));

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

                    addInfo((DefaultTableModel) table.getModel(), list);

                } catch (NumberFormatException exp) {
                    JOptionPane.showMessageDialog(null, "Неверный формат ввода!!! Диапозон портов с 0 по 65535.");
                }
            }
        });
        add(btn);


    }

    private DefaultTableModel createModelTable(DefaultTableModel model) {
        model.setRowCount(0);
        model.setColumnCount(0);
        model.addColumn("№");
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
            model.addRow(new Object[]{i+1, scanResultList.get(i).getPort(), str});
        }

    }

}
