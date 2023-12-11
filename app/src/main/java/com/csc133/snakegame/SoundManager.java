package com.csc133.snakegame;

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
    private SoundStrategy eatSoundStrategy;
    private SoundStrategy crashSoundStrategy;

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

        //default strategies for eating and crashing
        eatSoundStrategy = new DefaultSoundStrategy(mSP);
        crashSoundStrategy = new DefaultSoundStrategy(mSP);
    }

    //The setters can be used if we plan to change the sound strategies during runtime
    public void setEatSoundStrategy(SoundStrategy strategy) {
        eatSoundStrategy = strategy;
    }

    public void setCrashSoundStrategy(SoundStrategy strategy) {
        crashSoundStrategy = strategy;
    }

    // Plays when snake eats an apple
    public void playEatSound() {
        eatSoundStrategy.playSound(mEatSoundId);
    }

    //Plays when snake crashes into an obstacle or wall
    public void playCrashSound() {
        crashSoundStrategy.playSound(mCrashSoundId);
    }

}