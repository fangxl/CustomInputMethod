package com.binglen.androidxxime;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import java.util.List;

/**
 * Created by palance on 16/1/11.
 */
public class AndroidXXIME extends InputMethodService{
    public static final String TAG = "AndroidXXIME";
    private KeyboardView keyboardView; // 对应keyboard.xml中定义的KeyboardView
    private Keyboard keyboard;  // 对应qwerty.xml中定义的Keyboard

    public static Keyboard abcKeyboard;// 字母键盘
    public static Keyboard symbolKeyboard;// 字母键盘
    public static Keyboard numKeyboard;// 数字键盘

    // 开始输入的键盘状态设置
    public static int inputType = 1;// 默认
    public static final int INPUTTYPE_NUM = 1; // 数字，右下角 为空
    public static final int INPUTTYPE_NUM_FINISH = 2;// 数字，右下角 完成
    public static final int INPUTTYPE_NUM_POINT = 3; // 数字，右下角 为点
    public static final int INPUTTYPE_NUM_X = 4; // 数字，右下角 为X
    public static final int INPUTTYPE_NUM_NEXT = 5; // 数字，右下角 为下一个

    public static final int INPUTTYPE_ABC = 6;// 一般的abc
    public static final int INPUTTYPE_SYMBOL = 7;// 标点键盘
    public static final int INPUTTYPE_NUM_ABC = 8; // 数字，右下角 为下一个

    public static final int KEYBOARD_SHOW = 1;
    public static final int KEYBOARD_HIDE = 2;

    public boolean isUpper = false;// 是否大写

    @Override
    public boolean onEvaluateFullscreenMode() {
        boolean b = super.onEvaluateFullscreenMode();
        Log.e(TAG, "onEvaluateFullscreenMode() = " + b);
        return false;
    }

    @Override
    public boolean onEvaluateInputViewShown() {
        boolean b = super.onEvaluateInputViewShown();
        Log.e(TAG, "onEvaluateInputViewShown() = " + b);

        return true;
    }

    @Override
    public View onCreateInputView() {
        // keyboard被创建后，将调用onCreateInputView函数
        keyboardView = (KeyboardView)getLayoutInflater().inflate(R.layout.keyboard, null);  // 此处使用了keyboard.xml
        keyboard = new Keyboard(this, R.xml.keyboard_abc);  // 此处使用了qwerty.xml
        abcKeyboard = new Keyboard(this, R.xml.keyboard_abc);
        numKeyboard = new Keyboard(this, R.xml.keyboard_number);
        symbolKeyboard = new Keyboard(this, R.xml.keyboard_symbol);
        keyboardView.setKeyboard(keyboard);
        //设置了preview需要在键上设置字符，不然会报空指针
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(listener);
        return keyboardView;
    }

    private void initKeyBoard(int keyBoardViewID) {
        keyboardView = (KeyboardView)getLayoutInflater().inflate(keyBoardViewID,null);
        keyboardView.setEnabled(true);
        keyboardView.setOnKeyboardActionListener(listener);
        keyboardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true;
                }
                return false;
            }
        });
    }

    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        //字符键盘会走这个方法
        @Override
        public void onText(CharSequence text) {
//            if (ed == null)
//                return;
//            Editable editable = ed.getText();
////			if (editable.length()>=20)
////				return;
//            int start = ed.getSelectionStart();
//            int end = ed.getSelectionEnd();
//            String temp = editable.subSequence(0, start) + text.toString() + editable.subSequence(start, editable.length());
//            ed.setText(temp);
//            Editable etext = ed.getText();
//            Selection.setSelection(etext, start + 1);
        }

        @Override
        public void onRelease(int primaryCode) {
//            if (inputType != KeyboardUtil.INPUTTYPE_NUM_ABC
//                    && (primaryCode == Keyboard.KEYCODE_SHIFT)) {
//                keyboardView.setPreviewEnabled(true);
//            }
        }

        @Override
        public void onPress(int primaryCode) {

        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            InputConnection ic = getCurrentInputConnection();

            Log.e("onKey","primaryCode" + primaryCode);
            if (primaryCode == Keyboard.KEYCODE_SHIFT) {// 大小写切换
                changeKey();
                keyboardView.setKeyboard(keyboard);

            } else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
                ic.deleteSurroundingText(1, 0);
            }else if (primaryCode == -2) {//

            }else if (primaryCode == 32) {//回车

            }else if (primaryCode == 123123) {//转换数字键盘
                isUpper = false;
                changeKeyboard(INPUTTYPE_NUM);
            } else if (primaryCode == 456456) {//转换字母键盘
                isUpper = false;
                changeKeyboard(INPUTTYPE_ABC);
            } else if (primaryCode == 789789) {//转换字符键盘
                isUpper = false;
                changeKeyboard(INPUTTYPE_SYMBOL);
            } else {
                char code = (char)primaryCode;
                ic.commitText(String.valueOf(code), 1);
            }
        }
    };

    /**
     * 键盘大小写切换
     */
    private void changeKey() {
        List<Keyboard.Key> keylist = keyboard.getKeys();
        if (isUpper) {// 大写切小写
            isUpper = false;
            for (Keyboard.Key key : keylist) {
                if (key.label != null && isword(key.label.toString())) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0] + 32;
                }
            }
        } else {// 小写切大写
            isUpper = true;
            for (Keyboard.Key key : keylist) {
                if (key.label != null && isword(key.label.toString())) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0] - 32;
                }
            }
        }
    }

    private void changeKeyboard(int inputType) {
        if(inputType == INPUTTYPE_NUM){
            keyboard = numKeyboard;
        }else if(inputType == INPUTTYPE_ABC){
            keyboard = abcKeyboard;
        }else if(inputType == INPUTTYPE_SYMBOL){
            keyboard = symbolKeyboard;
        }

        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(listener);
        keyboardView.invalidate();
    }


    private boolean isword(String str) {
        String wordstr = "abcdefghijklmnopqrstuvwxyz";
        if (wordstr.indexOf(str.toLowerCase()) > -1) {
            return true;
        }
        return false;
    }
}
