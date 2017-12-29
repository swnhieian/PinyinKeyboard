package com.shiweinan.keyboard;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Weinan on 2017/12/27.
 */

public class PinyinSegmentation {
    double score;
    List<String> segments;
    public PinyinSegmentation() {
        score = 0.0;
        segments = new ArrayList<>();
    }
    public void insertBefore(Pair<String, Double> pair) {
        segments.add(0, pair.first);
        score += pair.second;
    }
    public double getScore() {
        return score;
    }
    public String getFirstSegment() {
        if (segments.size() > 0) {
            return segments.get(0);
        }
        return "";
    }
    public void showSegments() {
        System.out.print("in:show:segments::::::");
        for (String str:segments) {
            System.out.print(str + " ");
        }
        System.out.print(score + ";;;;;;" + getScore());
        System.out.println();
    }
    public String getSegments() {
        String ret = "";
        for (String str:segments) {
            ret += (str + "'");
        }
        if (ret.endsWith("'")) {
            ret = ret.substring(0, ret.length() - 1);
        }
        return ret;
    }
}
