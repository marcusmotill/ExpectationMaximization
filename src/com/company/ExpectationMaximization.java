package com.company;


import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by marcusmotill on 2/24/16.
 */
public class ExpectationMaximization extends JFrame {

    private JPanel rootPanel;
    private JTextField tfMotifLength;
    private JLabel lMotif;
    private JButton bRun;
    private JLabel lseq;
    private JTextPane tpSeqs;
    private JTextArea taResults;

    int defaultMotifLen = 50;
    int defaultEMinterations = 500;
    String inputDilemeters = ">.*";
    int min = 0;

    public ExpectationMaximization() {
        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initView();

        setVisible(true);
        bRun.addActionListener(e -> {
            taResults.setText("");
            runAlgorithm();
        });
    }

    private void runAlgorithm() {
        List<String> input = getInput();
        ResidueMatrix residueMatrix = getAlignmentMatrix(input);
        residueMatrix = addPseudoCounts(residueMatrix);
        residueMatrix = getFrequencyMatrix(residueMatrix, input.size());
        residueMatrix = getLogOddsMatrix(residueMatrix);

        int motifLen = Integer.valueOf(tfMotifLength.getText());
        if (motifLen > min) {
            JOptionPane.showMessageDialog(rootPanel,
                    "Cannot process best sequence motif since motif length is longer than min sequence length",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            getBestSequenceMotif(residueMatrix, input);
        }

        if (motifLen > getInputSize(input)) {
            JOptionPane.showMessageDialog(rootPanel,
                    "Cannot process best alignment motif since motif length is longer than input",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            getBestAlignmentMotif(residueMatrix, input);
        }


    }

    private int getInputSize(List<String> input) {
        int sum = 0;
        for (String in : input) {
            sum += in.length();
        }
        return sum;
    }

    private void getBestAlignmentMotif(ResidueMatrix residueMatrix, List<String> input) {
        List<String> motifSites = getMotifSites(input);
        List<Double> scores = motifSites.stream().map(motifSite -> getMotifScore(residueMatrix, motifSite)).collect(Collectors.toList());
        String result = String.format("Best Motif for alignment with log odds score of %f: %s",
                Collections.max(scores), motifSites.get(scores.indexOf(Collections.max(scores))));
        String results = taResults.getText() + "\n" + result;
        taResults.setText(results);
    }

    private void getBestSequenceMotif(ResidueMatrix residueMatrix, List<String> input) {
        ListIterator<String> listIterator = input.listIterator();
        while (listIterator.hasNext()) {
            List<String> tempList = new ArrayList<>();
            tempList.add(listIterator.next());
            List<String> motifSites = getMotifSites(tempList);
            List<Double> scores = motifSites.stream().map(motifSite -> getMotifScore(residueMatrix, motifSite)).collect(Collectors.toList());
            String result = String.format("Best Motif for Sequence %d with log odds score of %f: %s",
                    listIterator.nextIndex(), Collections.max(scores),
                    motifSites.get(scores.indexOf(Collections.max(scores))));
            String results = taResults.getText() + "\n" + result;
            taResults.setText(results);
        }
    }

    private double getMotifScore(ResidueMatrix logOddsMatrix, String motifSite) {
        char[] motifSiteArray = motifSite.toCharArray();
        double motifScore = 0;
        for (int i = 0; i < motifSiteArray.length; i++) {
            char protein = motifSiteArray[i];
            if (protein == 'A') {
                motifScore += logOddsMatrix.getA().get(i);
            } else if (protein == 'C') {
                motifScore += logOddsMatrix.getC().get(i);
            } else if (protein == 'G') {
                motifScore += logOddsMatrix.getG().get(i);
            } else if (protein == 'T') {
                motifScore += logOddsMatrix.getT().get(i);
            }
        }

        return motifScore;
    }

    private List<String> getMotifSites(List<String> input) {
        List<String> motifSites = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        input.forEach(stringBuilder::append);
        stringBuilder.replace(0, stringBuilder.length(), stringBuilder.toString().replaceAll("X", ""));
        int motifEnd = Integer.valueOf(tfMotifLength.getText());
        for (int i = 0; motifEnd <= stringBuilder.length(); i++, motifEnd++) {
            motifSites.add(stringBuilder.substring(i, motifEnd));
        }

        return motifSites;
    }


    private List<String> getInput() {
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
        return input;


    }

    private ResidueMatrix getAlignmentMatrix(List<String> input) {
        int max = 0;
        min = input.get(0).length();
        for (String value : input) {
            if (value.length() > max) {
                max = value.length();
            }
            if (value.length() < min) {
                min = value.length();
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

        return new ResidueMatrix(A, C, G, T);

    }

    private ResidueMatrix addPseudoCounts(ResidueMatrix residueMatrix) {
        residueMatrix.setA(addPseudoCount(residueMatrix.getA()));
        residueMatrix.setC(addPseudoCount(residueMatrix.getC()));
        residueMatrix.setG(addPseudoCount(residueMatrix.getG()));
        residueMatrix.setT(addPseudoCount(residueMatrix.getT()));
        return residueMatrix;
    }

    private List<Double> addPseudoCount(List<Double> X) {
        ListIterator<Double> it = X.listIterator();
        while (it.hasNext()) {
            double value = it.next();
            it.set(value + 1);
        }
        return X;
    }

    private ResidueMatrix getFrequencyMatrix(ResidueMatrix residueMatrix, int size) {
        residueMatrix.setA(getFrequency(residueMatrix.getA(), size));
        residueMatrix.setC(getFrequency(residueMatrix.getC(), size));
        residueMatrix.setG(getFrequency(residueMatrix.getG(), size));
        residueMatrix.setT(getFrequency(residueMatrix.getT(), size));
        return residueMatrix;
    }

    private List<Double> getFrequency(List<Double> X, int seqs) {
        ListIterator<Double> it = X.listIterator();
        while (it.hasNext()) {
            double value = it.next();
            it.set(value / seqs);
        }
        return X;
    }

    private ResidueMatrix getLogOddsMatrix(ResidueMatrix residueMatrix) {
        residueMatrix.setA(getLogOdds(residueMatrix.getA()));
        residueMatrix.setC(getLogOdds(residueMatrix.getC()));
        residueMatrix.setG(getLogOdds(residueMatrix.getG()));
        residueMatrix.setT(getLogOdds(residueMatrix.getT()));
        return residueMatrix;
    }


    private List<Double> getLogOdds(List<Double> X) {
        ListIterator<Double> it = X.listIterator();
        while (it.hasNext()) {
            double value = it.next();
            it.set(Math.log10(value) / Math.log10(2));
        }
        return X;
    }

    private void printMatrix(ResidueMatrix residueMatrix) {
        System.out.println(residueMatrix.getA());
        System.out.println(residueMatrix.getC());
        System.out.println(residueMatrix.getG());
        System.out.println(residueMatrix.getT());
    }

    private void initView() {
        tfMotifLength.setText(String.valueOf(defaultMotifLen));
    }

}
