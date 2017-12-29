package com.shiweinan.keyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Weinan on 2017/12/27.
 */

public class TouchModel {
    HashMap<Character, TouchParams> param = new HashMap<>();
    public TouchModel(IMEService ims) {
        //load parameters
        double ppi = 432;
        String[] keyCenters = new String[] {
            "q,54,90","w,162,90","e,270,90","r,378,90","t,486,90","y,594,90","u,702,90","i,810,90","o,918,90","p,1026,90","a,108,270","s,216,270","d,324,270","f,432,270","g,540,270","h,648,270","j,756,270","k,864,270","l,972,270","z,216,450","x,324,450","c,432,450","v,540,450","b,648,450","n,756,450","m,864,450"
        };
        for (String str : keyCenters) {
            String[] split = str.split(",");
            param.put(split[0].charAt(0), new TouchParams(Double.valueOf(split[1]), Double.valueOf(split[2]),
                    1.4*108/6, 1.4*180/6));
                    //1.97*ppi/25.4, 1.88*ppi/25.4));
            //System.out.println(split[0].charAt(0)+ ":\t" + split[1] + ",\t"+split[2]);
        }
        /*for (Keyboard.Key k: ims.getKeys()) {
            if (k.label.length() == 1 && Character.isAlphabetic(k.label.charAt(0))) {
                param.put(k.label.charAt(0), new TouchParams(k.x+k.width/2,k.y+k.height/2,
                                                             1.97*ppi/25.4, 1.88*ppi/25.4));
                //System.out.println(k.label.charAt(0)+ ":\t" + (k.x+k.width/2) + ",\t"+(k.y+k.height/2));
                for (int x=0; x<1080; x++) {
                    for (int y=0; y<1000; y++) {
                        if (k.squaredDistanceFrom(x, y) == 0) {
                            System.out.println(k.label.charAt(0)+ ":\t" + x + ",\t"+y);
                        }

                    }
                }

            }
        }*/
    }
    List< HashMap<Character, Double>> logCache = new ArrayList<>();
    public void getLogCache(List<TouchPoint> points) {
        logCache.clear();
        for (int i=0; i<points.size(); i++) {
            logCache.add(new HashMap<Character, Double>());
            for (char ch = 'a'; ch <='z'; ch ++) {
                logCache.get(i).put(ch, getLogProbability(points.get(i), ch));
            }
        }
    }
    public double getCachedLogProbability(int pos, String target) {
        assert(pos+target.length() <= logCache.size());
        double ret = 0.0;
        for (int i=0; i<target.length(); i++) {
            ret += logCache.get(pos+i).get(target.charAt(i));
        }
        return ret / target.length();
    }
    public double getLogProbability(List<TouchPoint> points, String target) {
        if (points.size() != target.length()) return 0;
        double ret = 0.0;
        for (int i=0; i<target.length(); i++) {
            ret += getLogProbability(points.get(i), target.charAt(i));
        }
        return ret / target.length();
    }
    public double getLogProbability(TouchPoint point, char ch) {
        TouchParams p = param.get(ch);
        return p.logProb(point);
    }
}
