package com.shiweinan.keyboard;

/**
 * Created by Weinan on 2017/12/27.
 */

public class TouchParams {
    double mux, muy, sigmax, sigmay;
    public TouchParams(double mux, double muy, double sigmax, double sigmay) {
        this.mux = mux;
        this.muy = muy;
        this.sigmax = sigmax;
        this.sigmay = sigmay;
    }
    public static double logGaussianProb(double x, double mu, double sigma) {
        double ret = - Math.log(Math.sqrt(2*Math.PI) * sigma) - (x - mu) * (x- mu) / (2 * sigma * sigma);
        return ret;
    }
    public double logProb(TouchPoint tp) {
        double ret = 0;
        ret += TouchParams.logGaussianProb(tp.getX(), mux, sigmax);
        ret += TouchParams.logGaussianProb(tp.getY(), muy, sigmay);
        return ret;
    }
}

