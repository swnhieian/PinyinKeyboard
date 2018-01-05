package com.shiweinan.keyboard;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.List;

/**
 * Created by Weinan on 2018/1/3.
 */

public class CandidatePage extends PopupWindow {
    Context context;
    public CandidatePage(Context context) {
        super(context);
        init(context);
    }
    public CandidatePage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public CandidatePage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    private void init(Context context) {
        this.context = context;
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));
        super.setContentView(LayoutInflater.from(context).inflate(R.layout.candidate_page_view, null));
        this.getContentView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                CandidatePage.super.dismiss();
                return true;
            }
        });
    }
    private void selectCandidatePinyin(String pinyin) {
        System.out.println("SELECT:" + pinyin);

    }
    public void setPinyin(final List<String> suggests) {
        ListView listView = this.getContentView().findViewById(R.id.pinyinListView);
        listView.setAdapter(new ArrayAdapter<String>(context, R.layout.list_pinyin_item, suggests));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectCandidatePinyin(suggests.get(i));
            }
        });


    }


}
