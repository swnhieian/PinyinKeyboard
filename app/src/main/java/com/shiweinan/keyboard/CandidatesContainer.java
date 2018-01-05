package com.shiweinan.keyboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Weinan on 2017/12/28.
 */

public class CandidatesContainer extends RelativeLayout {
    CandidateView candidateView;
    CandidatePage candidatePage;
    TextView pinyinView;
    public CandidatesContainer(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.candidate_view,null);
        addView(view);
        //moreButton = findViewById(R.id.moreButton);
        //moreButton.setVisibility(INVISIBLE);
        //candidateView = new CandidateView(context);
        candidateView = findViewById(R.id.candidates);
        candidateView.setVisibility(VISIBLE);
        candidateView.setService((IMEService)context);
        pinyinView = findViewById(R.id.pinyinStr);
        //candidatePage = findViewById(R.id.candidatePage);
        //candidatePage.setVisibility(INVISIBLE);
    }
    public void setSuggestions(List<String> suggestion) {
        candidateView.setSuggestions(suggestion);
    }
    public void setPinyinStr(String pinyin) {
        pinyinView.setText(pinyin);
        pinyinView.setVisibility(VISIBLE);
    }


}
