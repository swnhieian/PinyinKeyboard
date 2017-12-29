package com.shiweinan.keyboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * Created by Weinan on 2017/12/28.
 */

public class CandidatesContainer extends RelativeLayout {
    CandidateView candidateView;
    Button moreButton;
    public CandidatesContainer(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.candidate_view,null);
        addView(view);
        moreButton = findViewById(R.id.moreButton);
        moreButton.setVisibility(INVISIBLE);
        candidateView = new CandidateView(context);
        candidateView.setVisibility(VISIBLE);
        candidateView.setService((IMEService)context);

        LayoutParams layout = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layout.addRule(RelativeLayout.BELOW, moreButton.getId());
        addView(candidateView, layout);
    }
    public void setSuggestions(List<String> suggestion) {
        candidateView.setSuggestions(suggestion);
    }

}
