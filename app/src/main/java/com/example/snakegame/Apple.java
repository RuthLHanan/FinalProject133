package com.example.snakegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.Random;

class Apple {

    // The location of the apple on the grid
    // Not in pixels
    private Point location = new Point();

    // The range of values we can choose from
    // to spawn an apple
    private Point mSpawnRange;
    private int mSize;

    // This boolean determines if the apple is golden
    private boolean isGolden = false;

    // This int determines how many points the apple is worth
    private int scoreValue = 0;

    // An image to represent the apple
    private Bitmap mBitmapApple;

    // Get the isGolden value
    public boolean getAppleGolden(){
        return this.isGolden;
    }

    // Set the isGolden value
    public void setAppleGolden(boolean isAppleGold){
        this.isGolden = isAppleGold;
    }

    // Get the spawn range value
    public Point getSpawnRange(){
        return mSpawnRange;
    }

    // Get the size
    public int getSize(){
        return mSize;
    }

    // Get the score value
    public int getScoreValue(){ return this.scoreValue; }

    /// Set up the apple in the constructor
    private Apple(AppleBuilder builder) {
        this.isGolden=builder.isGolden;
        this.scoreValue=builder.scoreValue;
        // Make a note of the passed in spawn range
        this.mSpawnRange=builder.spawnRange;
        // Make a note of the size of an apple
        this.mSize=builder.size;
        // Hide the apple off-screen until the game starts
        location.x = -10;
        // Load the image to the bitmap
        // Load a different image if golden
        if( this.isGolden ) {
            mBitmapApple = BitmapFactory.decodeResource(builder.context.getResources(), R.drawable.applegolden);
        } else {
            mBitmapApple = BitmapFactory.decodeResource(builder.context.getResources(), R.drawable.apple);
        }
        // Resize the bitmap
        mBitmapApple = Bitmap.createScaledBitmap(mBitmapApple, builder.size, builder.size, false);
    }

    //Builder Class
    public static class AppleBuilder{

        // required parameters
        private boolean isGolden = false;
        private int size = 0;
        private int scoreValue = 0;
        private Point spawnRange;
        private Context context;
        public AppleBuilder(Context context, Point sr, int s, boolean isGoldenApple ){
            this.isGolden=isGoldenApple;
            if( isGoldenApple ) {
                this.scoreValue = 3;
            } else{
                this.scoreValue = 1;
            }
            this.spawnRange = sr;
            this.size = s;
            this.context = context;
        }

        public Apple build(){
            return new Apple(this);
        }

    }

    // This is called every time an apple is eaten
    void spawn(){
        // Choose two random values and place the apple
        Random random = new Random();

        location.x = random.nextInt(mSpawnRange.x) + 1;
        location.y = random.nextInt(mSpawnRange.y - 1) + 1;
    }

    // Let SnakeGame know where the apple is
    // SnakeGame can share this with the snake
    Point getLocation(){
        return location;
    }

    // Draw the apple
    void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(mBitmapApple,
                location.x * mSize, location.y * mSize, paint);

    }

}
