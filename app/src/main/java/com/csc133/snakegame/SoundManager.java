package com.gamecodeschool.snakegame;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import java.io.IOException;

public class SoundManager {
    // Declare variables
    private SoundPool mSP;
    private int mEatSoundId = -1;
    private int mCrashSoundId = -1;

    public SoundManager(Context context) {
        // Initialize the SoundPool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mSP = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Prepare the sounds in memory

            //playEatSound()
            descriptor = assetManager.openFd("get_apple.ogg");
            mEatSoundId = mSP.load(descriptor, 0);

            //playCrashSound()
            descriptor = assetManager.openFd("snake_death.ogg");
            mCrashSoundId = mSP.load(descriptor, 0);

        } catch (IOException e) {
            // Error
        }
    }
    public void playEatSound() {
        mSP.play(mEatSoundId, 1, 1, 0, 0, 1);
    }

    public void playCrashSound() {
        mSP.play(mCrashSoundId, 1, 1, 0, 0, 1);
    }

}
