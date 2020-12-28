package gui;

import logic.PingTester;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PingForm extends JPanel {

    public PingForm() {

        setBackground(new Color(239, 222, 205));
        setLayout(null);

        JLabel ipLabel = new JLabel("Адрес ресурса:");
        ipLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        ipLabel.setBounds(20, 0, 120, 50);
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
        ip.setBounds(130, 10, 200, 30);
        add(ip);

        JLabel packagesLabel = new JLabel("Кол-во пакетов:");
        packagesLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        packagesLabel.setBounds( 360, 0, 120, 50);
        add(packagesLabel);

        JTextField packageCount = new JTextField("4");
        packageCount.setHorizontalAlignment(JTextField.CENTER);
        packageCount.setFont(new Font("Arial", Font.BOLD, 12));
        packageCount.setBounds(470, 10, 140, 30);
        add(packageCount);

        JLabel sizePackageLabel = new JLabel("Размер пакета (байт):");
        sizePackageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        sizePackageLabel.setBounds(20, 40, 150, 50);
        add(sizePackageLabel);

        JTextField packageSize = new JTextField("32");
        packageSize.setHorizontalAlignment(JTextField.CENTER);
        packageSize.setFont(new Font("Arial", Font.BOLD, 12));
        packageSize.setBounds(170, 50, 70, 30);
        add(packageSize);

        JTextArea outputField = new JTextArea();
        outputField.setBorder(new EmptyBorder(10, 30, 10, 30));
        outputField.setFont(new Font("Arial", Font.PLAIN, 12));
        outputField.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(outputField);
        scrollPane.setBounds(20, 120, 590, 400);
        add(scrollPane);

        JButton button = new JButton("Тестировать");
        button.setForeground(new Color(239, 222, 205));
        button.setBackground(new Color(65, 145, 80));
        button.setBounds(425, 50, 180, 30);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outputField.setText(" ");

                String _ip = ip.getText().trim();
                int _countPackage = Integer.parseInt(packageCount.getText());
                int _sizePackage = Integer.parseInt(packageSize.getText());


                PingTester pingTester = new PingTester(_ip, _countPackage, _sizePackage);

                for (String output: pingTester.getReachablebyPing()) {
                    outputField.append(output + "\n");
                }

            }
        });
        add(button);



    }

}
