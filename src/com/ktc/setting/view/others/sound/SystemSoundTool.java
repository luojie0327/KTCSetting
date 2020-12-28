package com.ktc.setting.view.others.sound;

import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;

public class SystemSoundTool {

    public static void setSoundEffectsEnabled(Context context, boolean flag) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (flag) {
            mAudioManager.loadSoundEffects();
        } else {
            mAudioManager.unloadSoundEffects();
        }
        Settings.System.putInt(context.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, flag ? 1 : 0);
    }

    public static boolean getSoundEffectsEnabled(Context context) {
        return Settings.System.getInt(context.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 1)
                != 0;
    }
}
