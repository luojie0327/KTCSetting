package com.ktc.setting.view.others.tts;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TtsEngines;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TTSManager {

    private static final String TAG = TTSManager.class.getSimpleName();

    private Context mContext;
    private TextToSpeech mTts;
    private TtsEngines mEnginesHelper;
    private String mVoiceCheckEngine;
    private String mCurrentEngine;
    private static final int VOICE_DATA_INTEGRITY_CHECK = 1977;
    private Intent mVoiceCheckData;

    public TTSManager(Context context) {
        mContext = context;
        mTts = new TextToSpeech(context, mInitListener);
        mEnginesHelper = new TtsEngines(mContext);
        mCurrentEngine = mTts.getDefaultEngine();
        checkVoiceData(mCurrentEngine);
    }

    /**
     * 获取TTS语言种类
     */
    public List<Locale> getTTSLanguageList() {
        List<Locale> ttsLanguageList = new ArrayList<>();
        if (mVoiceCheckData != null) {
            final ArrayList<String> available = mVoiceCheckData.getStringArrayListExtra(
                    TextToSpeech.Engine.EXTRA_AVAILABLE_VOICES);
            for (int i = 0; i < available.size(); i++) {
                final Locale locale = mEnginesHelper.parseLocaleString(available.get(i));
                ttsLanguageList.add(locale);
            }
        }
        return ttsLanguageList;
    }

    /**
     * 获取当前TTS语言
     */
    public Locale getCurrentTTSLanguage() {
        if (mVoiceCheckData == null) {
            return null;
        }
        List<Locale> locales = getTTSLanguageList();
        Locale currentLocale = mEnginesHelper.getLocalePrefForEngine(mCurrentEngine);
        for (Locale locale : locales) {
            if (locale.getCountry().equals(currentLocale.getCountry()) && locale.getLanguage().equals(currentLocale.getLanguage())) {
                return locale;
            }
        }
        return null;
    }

    /**
     * 切换TTS语言
     */
    public void updateLanguageTo(Locale locale) {
        mEnginesHelper.updateLocalePrefForEngine(mCurrentEngine, locale);
        if (mCurrentEngine.equals(mTts.getCurrentEngine())) {
            // Null locale means "use system default"
            mTts.setLanguage((locale != null) ? locale : Locale.getDefault());
        }
    }

    private synchronized void checkVoiceData(String engine) {
        if (TextUtils.equals(mVoiceCheckEngine, engine)) {
            return;
        }
        mVoiceCheckEngine = engine;
        Intent intent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        intent.setPackage(engine);
        try {
            ((Activity) mContext).startActivityForResult(intent, VOICE_DATA_INTEGRITY_CHECK);
        } catch (ActivityNotFoundException ex) {
            Log.e(TAG, "Failed to check TTS data, no activity found for " + intent + ")");
        }
    }

    private TextToSpeech.OnInitListener mInitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                Log.i(TAG, "tts engine init success");
            } else {
                Log.i(TAG, "tts engine init fail");
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_DATA_INTEGRITY_CHECK) {
            mVoiceCheckData = data;
        }
    }

    public void release(){
        mTts.shutdown();
    }
}
