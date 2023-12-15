package com.example.snakegame;

import android.media.SoundPool;

public class DefaultSoundStrategy implements SoundStrategy {
    private SoundPool soundPool;

    public DefaultSoundStrategy(SoundPool soundPool) {
        this.soundPool = soundPool;
    }
    @Override
    public void playSound(int soundId) {
        soundPool.play(soundId, 1, 1, 0, 0, 1);
    }
}
