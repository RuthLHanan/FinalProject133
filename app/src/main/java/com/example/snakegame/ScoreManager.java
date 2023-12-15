package com.example.snakegame;

public class ScoreManager {
    private int mScore;

    public ScoreManager() {
        mScore = 0;
    }

    public int getScore(){
        return mScore;
    }

    public void increaseScore(int points) {
        mScore += points;
    }

    public void resetScore() {
        mScore = 0;
    }
}