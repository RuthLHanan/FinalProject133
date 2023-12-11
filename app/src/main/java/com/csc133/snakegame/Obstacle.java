package com.csc133.snakegame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.content.Context;

import java.util.Random;

public class Obstacle {
    private Point position;
    private Bitmap obstacleBitmap;

    public Obstacle(Context context, int drawableId, int size) {
        this.position = new Point(-1, -1); // Initialize to an off-screen position
        this.obstacleBitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        this.obstacleBitmap = Bitmap.createScaledBitmap(obstacleBitmap, size, size, false);
    }

    public void generateRandomPosition(int maxX, int maxY) {
        // Generate a random position within the screen boundaries
        position.x = (int) (Math.random() * maxX);
        position.y = (int) (Math.random() * maxY);
    }

    public Point getPosition() {
        return position;
    }

    public void draw(Canvas canvas, int segmentSize) {
        // Draw the obstacle bitmap at the specified position
        canvas.drawBitmap(obstacleBitmap, position.x * segmentSize, position.y * segmentSize, null);
    }

    // This is called every time an apple is eaten
    void spawn(Point mSpawnRange){
        // Choose two random values and place the apple
        Random random = new Random();

        position.x = random.nextInt(mSpawnRange.x) + 1;
        position.y = random.nextInt(mSpawnRange.y - 1) + 1;
    }
}
