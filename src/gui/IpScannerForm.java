package gui;

import logic.util.ComboboxItems;
import logic.IpTester;
import logic.entity.ScanResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class IpScannerForm extends JPanel {

    JTable table = null;

    public IpScannerForm() {
        setBackground(new Color(239, 222, 205));
        setLayout(null);

        JLabel startIpLabel = new JLabel("Диапазон IP:");
        startIpLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        startIpLabel.setBounds(20, 15, 100, 50);
        add(startIpLabel);

        JTextField startIp = new JTextField("127.0.0.1");
        startIp.setHorizontalAlignment(JTextField.CENTER);
        startIp.setFont(new Font("Arial", Font.BOLD, 12));
        startIp.setBounds(120, 25, 150, 30);
        add(startIp);

        JLabel finishIpLabel = new JLabel("до:");
        finishIpLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        finishIpLabel.setBounds(280, 15, 30, 50);
        add(finishIpLabel);

        JTextField finishIp = new JTextField("127.0.0.255");
        finishIp.setHorizontalAlignment(JTextField.CENTER);
        finishIp.setFont(new Font("Arial", Font.BOLD, 12));
        finishIp.setBounds(310, 25, 150, 30);
        add(finishIp);

        JLabel portLabel = new JLabel("Номер порта:");
        portLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        portLabel.setBounds(20, 55, 100, 50);
        add(portLabel);

        JTextField portField = new JTextField("22");
        portField.setHorizontalAlignment(JTextField.CENTER);
        portField.setFont(new Font("Arial", Font.BOLD, 12));
        portField.setBounds(120, 65, 70, 30);
        add(portField);

        JLabel timeout = new JLabel("Задержка:");
        timeout.setFont(new Font("Arial", Font.PLAIN, 14));
        timeout.setBounds(200, 55, 70, 50);
        add(timeout);

        JComboBox comboBox = new JComboBox(ComboboxItems.items);
        comboBox.setFont(new Font("Arial", Font.BOLD, 12));
        comboBox.setSelectedIndex(5);
        comboBox.setBounds(280, 65, 180, 30);
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
        btn.setBounds(20, 110, 200, 30);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressBar.setValue(0);
                int timeout = Integer.parseInt(ComboboxItems.items[comboBox.getSelectedIndex()].split(" ")[0]);
                int port = Integer.parseInt(portField.getText());

                IpTester ipTester = new IpTester(startIp.getText(), finishIp.getText(), port, timeout);
                ipTester.ipTesting();

                createModelTable((DefaultTableModel) table.getModel());

                List<ScanResult> list = new ArrayList<>();

                int size = ipTester.getList().size();
                for (Future<ScanResult> f: ipTester.getList()) {
                    try {
                        int currentValue = ipTester.getList().indexOf(f);

                        progressBar.setValue((currentValue * 100) / size);

                        if (currentValue % 500 == 0) {
                            progressBar.update(progressBar.getGraphics());
                        }

                        list.add(f.get());

                    } catch (ExecutionException | InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }

                addInfo((DefaultTableModel) table.getModel(), list);

            }
        });
        add(btn);

    }

    private DefaultTableModel createModelTable(DefaultTableModel model) {
        model.setRowCount(0);
        model.setColumnCount(0);
        model.addColumn("№");
        model.addColumn("Ip-адрес");
        model.addColumn("Порт");
        model.addColumn("Статус порта");

        return model;
    }

    private void addInfo(DefaultTableModel model, List<ScanResult> scanResultList) {
        for (int i = 0; i < scanResultList.size(); i++) {
            String portStatus = "Закрыт";
            if (scanResultList.get(i).isOpen()) {
                portStatus = "Открыт";
            }

            model.addRow(new Object[]{i+1, scanResultList.get(i).getIpAddress() ,scanResultList.get(i).getPort(), portStatus});
        }

    }

}
