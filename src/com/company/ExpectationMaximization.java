package com.company;

import javax.swing.*;

/**
 * Created by marcusmotill on 2/24/16.
 */
public class ExpectationMaximization extends JFrame {

    private JPanel rootPanel;
    private JTextField tfMotifLength;
    private JLabel lMotif;
    private JTextField tfEMIterations;
    private JLabel lEM;
    private JTextArea taSequence;
    private JButton bRun;
    private JLabel lseq;
    private JTextPane tpResults;

    int defaultMotifLen = 50;
    int defaultEMinterations = 500;

    public ExpectationMaximization() {
        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initView();

        setVisible(true);
    }

    private void initView() {
        tfMotifLength.setText(String.valueOf(defaultMotifLen));
        tfEMIterations.setText(String.valueOf(defaultEMinterations));
    }

}
