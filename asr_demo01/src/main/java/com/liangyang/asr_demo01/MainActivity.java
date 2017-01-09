package com.liangyang.asr_demo01;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView mInputTv;
    private ImageButton mVoiceBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //在代码中初始化SDK
        SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID + "=5872ea13");//申请的Appid
        //初始化视图
        mInputTv = (TextView) findViewById(R.id.input_tv);
    }

    /**
     * Button的监听事件
     * @param view
     */
    public void open(View view) {
        //在代码中封装语音识别实体，然后封装个方法使用
        initSpeech(this);

    }

    /**
     * 初始化语音识别
     *
     * @param context
     */
    private void initSpeech(Context context) {
        //1.创建RecognizerDialog对象
        RecognizerDialog recognizerDialog = new RecognizerDialog(context, null);
        //2.设置accent、language等参数
        recognizerDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        recognizerDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //3.设置回调接口
        recognizerDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                if (!isLast) {
                    /**
                     * 解析语音
                     * 由于语音识别返回的是个JSON数据，所以这里我们使用Gson这个包进行解析
                     * 需要在dependencies中添加
                     */
                    String result = parseVoice(recognizerResult.getResultString());
                    //显示语音识别
                    mInputTv.setText(result);
                }
            }

            @Override
            public void onError(SpeechError speechError) {

            }
        });
        //4.显示dialog，接收语音输入
        recognizerDialog.show();

    }

    /**
     * 解析语音Json
     *
     * @param resultString
     * @return
     */
    private String parseVoice(String resultString) {
        Gson gson = new Gson();
        Voice voiceBean = gson.fromJson(resultString, Voice.class);

        StringBuffer sb = new StringBuffer();
        ArrayList<Voice.WSBean> ws = voiceBean.ws;
        for (Voice.WSBean wsBean : ws) {
            String word = wsBean.cw.get(0).w;
            sb.append(word);
        }
        return sb.toString();
    }


    /**
     * 语音对象封装
     */
    public class Voice {

        public ArrayList<WSBean> ws;

        public class WSBean {
            public ArrayList<CWBean> cw;
        }

        public class CWBean {
            public String w;
        }
    }
}
