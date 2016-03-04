package com.company;

import java.util.List;

/**
 * Created by marcusmotill on 3/4/16.
 */
public class ResidueMatrix {
    private List<Double> A;
    private List<Double> G;
    private List<Double> C;
    private List<Double> T;

    public ResidueMatrix(List<Double> A, List<Double> C, List<Double> G, List<Double> T) {
        this.A = A;
        this.G = G;
        this.C = C;
        this.T = T;
    }


    public List<Double> getA() {
        return A;
    }

    public void setA(List<Double> a) {
        A = a;
    }

    public List<Double> getG() {
        return G;
    }

    public void setG(List<Double> g) {
        G = g;
    }

    public List<Double> getC() {
        return C;
    }

    public void setC(List<Double> c) {
        C = c;
    }

    public List<Double> getT() {
        return T;
    }

    public void setT(List<Double> t) {
        T = t;
    }
}
