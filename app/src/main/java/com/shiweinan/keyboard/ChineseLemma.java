package com.shiweinan.keyboard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Weinan on 2018/1/2.
 */

public class ChineseLemma {
    private double logProb;
    private String character;
    private String pinyinString;
    List<String> pinyinList;
    public ChineseLemma(String[] line) {
        character = line[0];
        logProb = Double.valueOf(line[1]);
        pinyinList = new ArrayList<>();
        pinyinString = "";
        for (int i=3; i<line.length; i++) {
            pinyinList.add(line[i]);
            pinyinString += line[i];
        }
    }
    public String getFirstPinyin() {
        return pinyinList.get(0);
    }
    public int getPointSize() {
        return pinyinString.length();
    }
    public String getPinyinString() {
        return pinyinString;
    }
    public double getLogProb() {
        return logProb;
    }
    public String getCharacter() {
        return character;
    }
    public int getCharacterLength() {
        return pinyinList.size();
    }
}
