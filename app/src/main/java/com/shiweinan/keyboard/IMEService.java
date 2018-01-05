package com.shiweinan.keyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IMEService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    private StoneKeyboardView mKeyboardView;
    private Keyboard mKeyboard;

    private CandidatesContainer mCandidateContainer;
    private StringBuilder m_composeString = new StringBuilder(); // 保存写作串
    ArrayList<String> pinyinList = new ArrayList<String>();

    private List<TouchPoint> touchPoints = new ArrayList<>();
    private PredictAlgorithm predictAlgorithm;
    private TouchModel touchModel;

    @Override
    public View onCreateCandidatesView(){
        Log.d(this.getClass().toString(), "onCreateCandidatesView: ");
        //mCandidateContainer = new CandidateView(this);
        mCandidateContainer = new CandidatesContainer(this);
        //mCandidateContainer = new CandidateView(this);
        return mCandidateContainer;
    }
    @Override
    public void onStartInput(EditorInfo editorInfo, boolean restarting){
        super.onStartInput(editorInfo, restarting);
        Log.d(this.getClass().toString(), "onStartInput: ");
        m_composeString.setLength(0);
    }

    @Override
    public View onCreateInputView() {
        //mKeyboardView = (KeyboardView)getLayoutInflater().inflate(R.layout.keyboard,null);
        mKeyboardView = new StoneKeyboardView(getApplicationContext(), null, this);
        mKeyboard = new Keyboard(this, R.xml.qwerty);
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.setOnKeyboardActionListener(this);
        mKeyboardView.setPreviewEnabled(false);
        ///initialization
        if (touchModel == null) {
            touchModel = new TouchModel(this);
        }
        if (predictAlgorithm == null) {
            predictAlgorithm = new PredictAlgorithm(this, touchModel);
        }
        return mKeyboardView;
    }
    //temporary
    private char getRawInput(double x, double y) {
        for (Keyboard.Key key:mKeyboardView.getKeyboard().getKeys()) {
            if (key.isInside((int)(Math.round(x)), (int)(Math.round(y)))) {
                return key.label.charAt(0);
            }
        }
        return ' ';
    }
    private void updateCandidates() {
        String ret = "";
        List<String> seg = predictAlgorithm.predict(touchPoints);


        for (int i=0; i<touchPoints.size(); i++) {
            ret += getRawInput(touchPoints.get(i).x, touchPoints.get(i).y);
        }
        pinyinList.clear();
        getCurrentInputConnection().setComposingText(ret, 1);
        for (String s:seg) {
            pinyinList.add(s);
        }
        mCandidateContainer.setSuggestions(pinyinList);
        mCandidateContainer.setPinyinStr(ret);
        setCandidatesViewShown(true);
        if (Settings.getShowTouchPoints()) {
            mKeyboardView.drawPoints(touchPoints);
        }
    }
    private void delete() {
        if (touchPoints.size() > 0) {
            touchPoints.remove(touchPoints.size() - 1);
            updateCandidates();
        } else {
            getCurrentInputConnection().deleteSurroundingText(1, 0);
        }
        if (Settings.getShowTouchPoints()) {
            mKeyboardView.clearDrawPoints();
        }
    }
    private void enter() {
        if (touchPoints.size() > 0) {
            getCurrentInputConnection().finishComposingText();

            if (Settings.getShowPinyinSegmentation()) {

                List<PinyinSegmentation> pyseg = predictAlgorithm.getPinyinSegment(touchPoints, 0);
                //System.out.println("===========" + seg.size());
                getCurrentInputConnection().commitText("\n", 1);
                for (PinyinSegmentation s : pyseg) {
                    getCurrentInputConnection().commitText(s.getSegments() + " ", 1);
                    //s.showSegments();
                }
//            List<String> ret = new ArrayList<>();
//            for (PinyinSegmentation s : seg.subList(0, Math.min(5, seg.size()))) {
//                ret.add(s.getSegments());
//            }
            }
            
            touchPoints.clear();
            updateCandidates();

        } else {
            getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
        }
    }
    private void space() {
        if (touchPoints.size() > 0) {
            if (pinyinList.size() > 0) {
                getCurrentInputConnection().commitText(pinyinList.get(0) + " ", 1);
            }
            touchPoints.clear();
        } else {
            getCurrentInputConnection().commitText(" ", 1);
        }
        updateCandidates();
    }
    public void select(String str) {
        getCurrentInputConnection().commitText(str, 1);
        touchPoints.clear();
        updateCandidates();
    }

    public List<Keyboard.Key> getKeys() {
        return mKeyboard.getKeys();
    }
    public int getKeyboardHeight() {
        return mKeyboardView.getMeasuredHeight();
    }

    HashMap<Integer, ScheduledExecutorService> schedulers = new HashMap<>();



    public boolean onTouchEvent(final MotionEvent me) {
        //System.out.println("height!!!" + mKeyboardView.getMeasuredHeight());
        int actionId = me.getActionIndex();
        final float x = me.getX(actionId);
        final float y = me.getY(actionId);
        switch (me.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                /*if (schedulers.get(actionId) != null) {
                    schedulers.get(actionId).shutdownNow();
                    schedulers.put(actionId, null);
                } else {
                    ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();
                    s.scheduleWithFixedDelay(new Runnable() {
                        @Override
                        public void run() {
                            touchDown(x, y);
                        }
                    }, 0, 100, TimeUnit.MILLISECONDS);

                    schedulers.put(actionId, s);
                }*/
                touchDown(x, y);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (schedulers.get(actionId) != null) {
                    schedulers.get(actionId).shutdownNow();
                    schedulers.put(actionId, null);
                }
//                x = me.getX(action);
//                y = me.getY(action);
//                for (Keyboard.Key key:keys) {
//                    if (key.isInside(Math.round(x), Math.round(y))) {
//                        key.onReleased(true);
//                        //getCurrentInputConnection().commitText(key.label, 1);
//                        break;
//                    }
//                }
                break;
            default:
                break;
        }
        return true;
    }
    private void touchDown(float x, float y) {
        boolean isFunction = false;
        //touchPoints.add(new TouchPoint(x, y));
        //updateCandidates();
        //setCandidatesViewShown(true);
        List<Keyboard.Key> keys = mKeyboardView.getKeyboard().getKeys();
        for (Keyboard.Key key:keys) {
            if (key.isInside(Math.round(x), Math.round(y))) {
                if (key.codes[0] == Keyboard.KEYCODE_MODE_CHANGE) {
                    //InputMethodManager ime = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                    //ime.showInputMethodPicker();
                    //isFunction = true;
                    return;
                } else if (key.codes[0] == -5) { // delete
                    //delete();
                    //leave system handle it, for repeatable function
                    return;
                } else if (key.codes[0] == Keyboard.KEYCODE_DONE) {
                    enter();
                    isFunction = true;
                } else if (key.codes[0] == 32) { //space
                    space();
                    isFunction = true;
                }
                break;
            }
        }
        if (!isFunction) {
            touchPoints.add(new TouchPoint(x, y));
            updateCandidates();
        }
    }
    private void touchDown(MotionEvent me) {
        boolean isFunction = false;
        int action = me.getActionIndex();
        float x = me.getX(action);
        float y = me.getY(action);
        System.out.println("pos::::" + x + "," + y);
        //touchPoints.add(new TouchPoint(x, y));
        //updateCandidates();
        //setCandidatesViewShown(true);
        List<Keyboard.Key> keys = mKeyboardView.getKeyboard().getKeys();
        for (Keyboard.Key key:keys) {
            if (key.isInside(Math.round(x), Math.round(y))) {
                if (key.label.equals("?")) {
                    InputMethodManager ime = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                    ime.showInputMethodPicker();
                    isFunction = true;
                } else if (key.label.equals("DEL")) {
                    delete();
                    isFunction = true;
                } else if (key.codes[0] == Keyboard.KEYCODE_DONE) {
                    enter();
                    isFunction = true;
                } else if (key.label.equals(" ")) {
                    space();
                    isFunction = true;
                } else {
                    //getCurrentInputConnection().commitText(key.label, 1);
                    isFunction = false;
                }
                break;
            }
        }
        if (!isFunction) {
            touchPoints.add(new TouchPoint(x, y));
            updateCandidates();
        }
    }


    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic  = getCurrentInputConnection();
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                //ic.deleteSurroundingText(1, 0);
                delete();
                break;
            case Keyboard.KEYCODE_DONE:
                //ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                enter();
                break;
            case Keyboard.KEYCODE_MODE_CHANGE:
                InputMethodManager ime = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                ime.showInputMethodPicker();
                break;
            default:
                /*char code = (char)primaryCode;
                if(code == ' '){ // 如果收到的是空格
                    if(m_composeString.length() > 0) {  // 如果有写作串，则将首个候选提交上屏
                        ic.commitText(m_composeString, m_composeString.length());
                        m_composeString.setLength(0);
                    }else{                              // 如果没有写作串，则直接将空格上屏
                        ic.commitText(" ", 1);
                    }
                    //setCandidatesViewShown(false);
                }else {          // 否则，将字符计入写作串
                    m_composeString.append(code);
                    ic.setComposingText(m_composeString, 1);
                    if(mCandidateContainer != null){
                        ArrayList<String> list = new ArrayList<String>();
                        list.add(m_composeString.toString());
                        list.add(m_composeString.toString());
                        list.add(m_composeString.toString());
                        mCandidateContainer.setSuggestions(list);
                        setCandidatesViewShown(true);
                    }
                }*/
        }
    }

    @Override
    public void onPress(int primaryCode) {
    }
    @Override
    public void onRelease(int primaryCode) {
    }
    @Override
    public void onText(CharSequence text) {
    }
    @Override
    public void swipeDown() {
    }
    @Override
    public void swipeLeft() {
    }
    @Override
    public void swipeRight() {
    }
    @Override
    public void swipeUp() {
    }

}
