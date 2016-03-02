package com.company;


import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.*;

/**
 * Created by marcusmotill on 2/24/16.
 */
public class ExpectationMaximization extends JFrame {

    private JPanel rootPanel;
    private JTextField tfMotifLength;
    private JLabel lMotif;
    private JTextField tfEMIterations;
    private JLabel lEM;
    private JButton bRun;
    private JLabel lseq;
    private JTextPane tpResults;
    private JTextPane tpSeqs;

    int defaultMotifLen = 50;
    int defaultEMinterations = 500;
    String inputDilemeters = ">.*";

    public ExpectationMaximization() {
        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initView();

        setVisible(true);
        bRun.addActionListener(e -> {
            getInput();
        });
    }

    private void getInput() {
        List<String> input = new ArrayList<>(Arrays.asList(tpSeqs.getText().split(inputDilemeters)));
        ListIterator<String> it = input.listIterator();
        while (it.hasNext()) {
            String value = it.next();
            if (value.trim().length() == 0) {
                it.remove();
            } else {
                it.set(value.trim());
            }

        }

        getAlignmentMatrix(input);

    }

    private void getAlignmentMatrix(List<String> input) {
        int max = 0;
        for (String value : input) {
            if (value.length() > max) {
                max = value.length();
            }
        }

        ListIterator<String> it = input.listIterator();
        while (it.hasNext()) {
            String value = it.next();
            int diff = max - value.length();
            StringBuilder buffer = new StringBuilder();
            buffer.append(value);
            while (diff != 0) {
                buffer.append("X");
                diff--;
            }
            it.set(buffer.toString());
        }


        List<Double> A = new ArrayList<>();
        List<Double> C = new ArrayList<>();
        List<Double> G = new ArrayList<>();
        List<Double> T = new ArrayList<>();


        List<char[]> seqs = new ArrayList<>();
        for (String seq : input) {
            seqs.add(seq.toCharArray());
        }

        for (int i = 0; i < seqs.get(0).length; i++) {
            StringBuilder col = new StringBuilder();
            for (char[] chars : seqs) {
                col.append(chars[i]);
            }
            int aCount = StringUtils.countMatches(col.toString(), "A");
            int cCount = StringUtils.countMatches(col.toString(), "C");
            int gCount = StringUtils.countMatches(col.toString(), "G");
            int tCount = StringUtils.countMatches(col.toString(), "T");
            A.add((double) aCount);
            C.add((double) cCount);
            G.add((double) gCount);
            T.add((double) tCount);
        }

        System.out.println(A);
        System.out.println(C);
        System.out.println(G);
        System.out.println(T);

        A = getFrequency(A, seqs.size());
        C = getFrequency(C, seqs.size());
        G = getFrequency(G, seqs.size());
        T = getFrequency(T, seqs.size());

        System.out.println(A);
        System.out.println(C);
        System.out.println(G);
        System.out.println(T);

        A = getLogOdds(A);
        C = getLogOdds(C);
        G = getLogOdds(G);
        T = getLogOdds(T);

        System.out.println(A);
        System.out.println(C);
        System.out.println(G);
        System.out.println(T);

    }


    private List<Double> getFrequency(List<Double> X, int seqs) {
        ListIterator<Double> it = X.listIterator();
        while (it.hasNext()) {
            double value = it.next();
            it.set(value / seqs);
        }
        return X;
    }

    private List<Double> getLogOdds(List<Double> X) {
        ListIterator<Double> it = X.listIterator();
        while (it.hasNext()) {
            double value = it.next();
            it.set(Math.log10(value) / Math.log10(2));
        }
        return X;
    }

    private void initView() {
        tfMotifLength.setText(String.valueOf(defaultMotifLen));
        tfEMIterations.setText(String.valueOf(defaultEMinterations));
    }

}
