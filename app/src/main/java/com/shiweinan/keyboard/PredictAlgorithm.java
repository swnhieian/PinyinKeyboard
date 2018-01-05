package com.shiweinan.keyboard;


import android.content.res.AssetManager;
import android.inputmethodservice.InputMethodService;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Weinan on 2017/12/27.
 */

public class PredictAlgorithm {
    HashMap<String, Double> pinyinMap = new HashMap<>();
    HashMap<String, HashMap<String, Double>> bgmPinyinMap = new HashMap<>();
    TouchModel touchModel;
    public PredictAlgorithm(InputMethodService ims, TouchModel tm) {
        //get all pinyin map
        String str = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(ims.getAssets().open("pinyinMap.txt")));
            while ((str = br.readLine()) != null) {
                String[] line = str.split(" ");
//                if (line[1].equals("i")) {
//                    line[1] = "ch";
//                } else if (line[1].equals("u")) {
//                    line[1] = "sh";
//                } else if (line[1].equals("v")) {
//                    line[1] = "zh";
//                }
                pinyinMap.put(line[1].toLowerCase(), Double.valueOf(line[2]));

            }
            br.close();
            br = new BufferedReader(new InputStreamReader(ims.getAssets().open("bigramPinyin.txt")));
            while ((str = br.readLine()) != null) {
                String[] line = str.split(" ");
                if (!bgmPinyinMap.containsKey(line[0].toLowerCase())) {
                    bgmPinyinMap.put(line[0].toLowerCase(), new HashMap<String, Double>());
                }
                bgmPinyinMap.get(line[0].toLowerCase()).put(line[1].toLowerCase(), Double.valueOf(line[2]));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        touchModel = tm;
        loadChineseFreqDict(ims);
    }
    HashMap<String, List<ChineseLemma>> ChineseLM = new HashMap<>();
    private void loadChineseFreqDict(InputMethodService ims) {
        String str = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(ims.getAssets().open("rawdict_utf8_65105_logfreq.txt")));
            while ((str = br.readLine()) != null) {
                String[] line = str.split(" ");
                assert(line.length >= 4);
                ChineseLemma temp = new ChineseLemma(line);
                String first = temp.getFirstPinyin();
                if (!ChineseLM.keySet().contains(first)) {
                    ChineseLM.put(first, new ArrayList<ChineseLemma>());
                }
                ChineseLM.get(first).add(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> predict(List<TouchPoint> points) {
        //touchModel.getLogCache(points);





        return predictWithChinese(points);
        //return ret;
    }
    public List<String> predictWithChinese(List<TouchPoint> points) {

        List<Pair<String, Double>> split = new ArrayList<>();
        for (String pinyin: pinyinMap.keySet()) {
            if (pinyin.length() > points.size()) continue;
            List<TouchPoint> tp = points.subList(0, pinyin.length());
            double prob = touchModel.getLogProbability(tp, pinyin) + pinyinMap.get(pinyin);
            split.add(new Pair<>(pinyin, prob));
        }
        Collections.sort(split, new Comparator<Pair<String, Double>>() {
            @Override
            public int compare(Pair<String, Double> stringDoublePair, Pair<String, Double> t1) {
                return t1.second.compareTo(stringDoublePair.second);
            }
        });

        List<Pair<ChineseLemma, Double>> chSplit = new ArrayList<>();
        int prefixSize = Math.min(10, split.size());
        for (int i=0; i<prefixSize; i++) {
            if (!ChineseLM.keySet().contains(split.get(i).first)) continue;
            for (ChineseLemma cl: ChineseLM.get(split.get(i).first)) {
                if (cl.getPointSize() > points.size()) continue;
                List<TouchPoint> tp = points.subList(0, cl.getPointSize());
                double prob = touchModel.getLogProbability(tp, cl.getPinyinString()) + cl.getLogProb() / cl.getCharacterLength();
                if (cl.getPointSize() < points.size()) {
                    prob *= 2;
                }
                if (prob > -20) {
                    chSplit.add(new Pair<>(cl, prob));
                }
            }
        }
        System.out.println("chinese length:" + chSplit.size());
        Collections.sort(chSplit, new Comparator<Pair<ChineseLemma, Double>>() {
            @Override
            public int compare(Pair<ChineseLemma, Double> chineseLemmaDoublePair, Pair<ChineseLemma, Double> t1) {
                return t1.second.compareTo(chineseLemmaDoublePair.second);
            }
        });
        List<String> ret = new ArrayList<>();
        for (int i=0; i<Math.min(10, chSplit.size()); i++) {
            System.out.println(chSplit.get(i).first.getCharacter() + " " +
                               chSplit.get(i).first.getLogProb() + " " +
                               chSplit.get(i).second);
        }
        if (chSplit.size() == 0) return ret;
        ChineseLemma first = chSplit.get(0).first;
        List<TouchPoint> remainingPoints = points.subList(first.getPointSize(), points.size());
        List<PinyinSegmentation> pinyinSeg = getPinyinSegment(remainingPoints, 0);
        for (PinyinSegmentation s:pinyinSeg) {
            s.showSegments();
        }
        ret.add(first.getCharacter() + pinyinSeg.get(0).getSegments());
        for (int i=1; i<Math.min(10, chSplit.size()); i++) {
            ret.add(chSplit.get(i).first.getCharacter());
        }
        return ret;
    }
    public List<PinyinSegmentation> getPinyinSegment(List<TouchPoint> points, int calculated) {
        if (points.size() == 0) {
            List<PinyinSegmentation> ret = new ArrayList<>();
            ret.add(new PinyinSegmentation());
            return ret;
        };
        List<Pair<String, Double>> split = new ArrayList<>();
        for (String pinyin : pinyinMap.keySet()) {
            if (pinyin.length() > points.size()) continue;
            List<TouchPoint> p = points.subList(0, pinyin.length());
            double prob = touchModel.getLogProbability(p, pinyin) + pinyinMap.get(pinyin);
            //double prob = touchModel.getCachedLogProbability(calculated, pinyin) + pinyinMap.get(pinyin);
            split.add(new Pair<>(pinyin, prob));
        }
        Collections.sort(split, new Comparator<Pair<String, Double>>() {
            @Override
            public int compare(Pair<String, Double> stringDoublePair, Pair<String, Double> t1) {
                return t1.second.compareTo(stringDoublePair.second);
            }
        });
        List<PinyinSegmentation> ret = new ArrayList<>();
        int matchSize = 5;
        /*if (calculated <= 6) {
            matchSize = 3;
        }
        if (calculated <= 9) {
            matchSize = 4;
        }*/
        if (calculated <=5 && calculated+points.size() >=12) {
            matchSize = 2;
        }
        if (calculated <=7 && calculated+points.size() >=12) {
           matchSize = 3;
        }
        if (calculated+points.size() >=14) {
            matchSize = 2;
        }
        for (int i=0; i<Math.min(split.size(), matchSize); i++) {
            Pair<String, Double> pair = split.get(i);
            List<PinyinSegmentation> res = getPinyinSegment(points.subList(pair.first.length(), points.size()),
                                                            calculated+pair.first.length());
            for (PinyinSegmentation pySeg: res) {
                String nextSeg = pySeg.getFirstSegment();
                if (Settings.getUseBigram() && nextSeg.length()>0) {
                    Pair<String, Double> newPair = new Pair<>(pair.first, pair.second + (bgmPinyinMap.get(pair.first).get(nextSeg)));
                    pySeg.insertBefore(newPair);
                } else {
                    pySeg.insertBefore(pair);
                }
            }
            ret.addAll(res);
        }
        Collections.sort(ret, new Comparator<PinyinSegmentation>() {
            @Override
            public int compare(PinyinSegmentation pinyinSegmentation, PinyinSegmentation t1) {
                return ((Double) t1.getScore()).compareTo(pinyinSegmentation.getScore());
            }
        });
        int returnSize = 5;
      /*  if (calculated <=6) {
            returnSize = 3;
        }
        if (calculated <= 9) {
            returnSize = 4;
        }*/
        if (calculated <=5 && calculated+points.size() >=12) {
            returnSize = 3;
        }
        if (calculated <=7 && calculated+points.size() >=12) {
            returnSize = 4;
        }
        if (calculated+points.size() >= 16) {
            returnSize = 2;
        }

        return ret.subList(0, Math.min(returnSize, ret.size()));
    }
}
