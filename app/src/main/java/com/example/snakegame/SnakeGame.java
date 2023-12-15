package com.example.snakegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.Random;

class SnakeGame extends SurfaceView implements Runnable{

    // Objects for the game loop/thread
    private Thread mThread = null;
    // Control pausing between updates
    private long mNextFrameTime;
    // Is the game currently playing and or paused?
    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;
    private volatile boolean mMainMenu = true;
    private volatile boolean mGameOver = false;

    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 40;
    private int mNumBlocksHigh;

    // Objects for drawing
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;

    // A snake ssss
    private Snake mSnake;
    // And an apple
    private Apple mApple;

    private Context thisContext;

    private Bitmap playButtonBitmap;
    //Initialize the Sound Manager
    private Bitmap pauseButtonBitmap;

    //Initialize the Sound Manager
    private SoundManager mSP;

    //Initialize the Score Manager
    private ScoreManager mScoreManager;
    private Obstacle mObstacle;

    // This is the constructor method that gets called
    // from SnakeActivity
    public SnakeGame(Context context, Point size) {
        super(context);

        thisContext = context;

        //Initialize the SoundManger
        mSP = new SoundManager(context);

        //Initialize the ScoreManager
        mScoreManager = new ScoreManager();

        // Work out how many pixels each block is
        int blockSize = size.x / NUM_BLOCKS_WIDE;

        //button images
        playButtonBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.play_button);
        pauseButtonBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pause_button);

        mObstacle = new Obstacle(context, R.drawable.skull, blockSize);

        // How many blocks of the same size will fit into the height
        mNumBlocksHigh = size.y / blockSize;

        // Initialize the drawing objects
        mSurfaceHolder = getHolder();
        mPaint = new Paint();

        // Call the constructors of our two game objects
        mApple = new Apple.AppleBuilder(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize, false).build();

        mSnake = new Snake(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);

    }

    // Called to start a new game
    public void newGame() {

        // reset the snake
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);

        // Get the apple ready for dinner
        mApple.spawn();

        // Reset the mScore
        mScoreManager.resetScore();

        // Setup mNextFrameTime so an update can triggered
        mNextFrameTime = System.currentTimeMillis();
    }


    // Handles the game loop
    @Override
    public void run() {
        while (mPlaying) {
            if(!mPaused && !mMainMenu) {
                // Update 10 times a second
                if (updateRequired()) {
                    update();
                }
            }

            draw();
            if (mPaused) {
                // Sleep for a short period to reduce CPU usage when the game is paused
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // Handle interruption
                }
            }
        }
    }


    // Check to see if it is time for an update
    public boolean updateRequired() {

        // Run at 10 frames per second
        final long TARGET_FPS = 10;
        // There are 1000 milliseconds in a second
        final long MILLIS_PER_SECOND = 1000;

        // Are we due to update the frame
        if(mNextFrameTime <= System.currentTimeMillis()){
            // Tenth of a second has passed

            // Setup when the next update will be triggered
            mNextFrameTime =System.currentTimeMillis()
                    + MILLIS_PER_SECOND / TARGET_FPS;

            // Return true so that the update and draw
            // methods are executed
            return true;
        }

        return false;
    }


    // Update all the game objects
    public void update() {

        // Move the snake
        mSnake.move();

        // Did the head of the snake eat the apple?
        if(mSnake.checkDinner(mApple.getLocation())){
            // This reminds me of Edge of Tomorrow.
            // One day the apple will be ready!

            // Add to  mScore
            // Golden apples are worth 3 points
            mScoreManager.increaseScore(mApple.getScoreValue());

            // Determine if the next apple is a Golden Apple - 10% chance
            Random random = new Random();
            int rngAppleValue = random.nextInt(10);
            boolean appleIsGolden = (9 == rngAppleValue);

            // Create a new Apple object based on the apple type and the old parameters
            mApple = new Apple.AppleBuilder(thisContext, mApple.getSpawnRange(), mApple.getSize(), appleIsGolden).build();

            mApple.spawn();

            mObstacle.spawn(mApple.getSpawnRange());

            // Play a sound
            mSP.playEatSound();
        }

        // Did the snake die?
        if (mSnake.detectDeath()|| mSnake.detectObstacleCollision(mObstacle)) {
            // Pause the game ready to start again
            mSP.playCrashSound();
            mPaused =true;
            mGameOver=true;
        }

    }


    // Do all the drawing
    public void draw() {
        // Get a lock on the mCanvas
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();

            // Fill the screen with a color
            mCanvas.drawColor(Color.argb(255, 26, 128, 182));

            // Set the size and color of the mPaint for the text
            mPaint.setColor(Color.argb(255, 255, 255, 255));
            mPaint.setTextSize(120);

            // Draw the score if the game is not over
            if( !mGameOver && !mPaused && !mMainMenu) {
                mCanvas.drawText("" + mScoreManager.getScore(), 20, 120, mPaint);
            }

            // Draw some text if the game is over
            if( mGameOver ){
                // Set the size and color of the mPaint for the game over screen
                mPaint.setColor(Color.argb(255, 255, 255, 255));
                mPaint.setTextSize(150);

                // Draw the Game Over message
                mCanvas.drawText(getResources().
                                getString(R.string.game_over),
                        200, 120, mPaint);
                mPaint.setTextSize(100);
                mCanvas.drawText(getResources().
                                getString(R.string.final_score) + " " + mScoreManager.getScore(),
                        300, 270, mPaint);

                // Draw buttons to play again or go to the main menu
                mPaint.setTextSize(70);
                mCanvas.drawText(getResources().
                                getString(R.string.main_menu),
                        0, 700, mPaint);
                mCanvas.drawText(getResources().
                                getString(R.string.play_again),
                        850, 700, mPaint);
            }
            // Draw some text for the main menu
            else if(mPaused && mMainMenu){

                // Set the size and color of the mPaint for the text
                mPaint.setColor(Color.argb(255, 255, 255, 255));
                mPaint.setTextSize(150);

                // Draw the message
                // We will give this an international upgrade soon
                //mCanvas.drawText("Tap To Play!", 200, 700, mPaint);
                mCanvas.drawText(getResources().
                                getString(R.string.tap_to_play),
                        200, 700, mPaint);
            } // Draw some text while paused
            else if(mPaused && !mMainMenu && !mGameOver){

                // Set the size and color of the mPaint for the text
                mPaint.setColor(Color.argb(255, 255, 255, 255));
                mPaint.setTextSize(150);

                mCanvas.drawText(getResources().
                                getString(R.string.game_paused),
                        125, 120, mPaint);

                // Draw the play button
                Bitmap buttonBitmap = playButtonBitmap;
                int buttonX = 0;
                int buttonY = 600;
                mCanvas.drawBitmap(buttonBitmap, buttonX, buttonY, mPaint);
            } else{
                // Resume play, draw everything else for the game
                // Draw obstacle, apple and the snake
                mApple.draw(mCanvas, mPaint);
                mSnake.draw(mCanvas, mPaint);

                // Draw the pause button
                Bitmap buttonBitmap = pauseButtonBitmap;
                int buttonX = 0;
                int buttonY = 600;
                mCanvas.drawBitmap(buttonBitmap, buttonX, buttonY, mPaint);
            }

            // Unlock the mCanvas and reveal the graphics for this frame
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                float touchX = motionEvent.getX();
                float touchY = motionEvent.getY();
                // Game Over: Only accept inputs for the buttons
                if( mGameOver ){
                    if ((touchX >= 850) && (touchY >= 600)) {
                        // Play again is clicked, start a new game
                        mPaused = false;
                        mGameOver = false;
                        mMainMenu = false;
                        newGame();

                        // Don't want to process snake direction for this tap
                        return true;
                    } else if ((touchX <= 325) && (touchY >= 600)) {
                        // Main Menu is clicked, display Tap to Start
                        mPaused = true;
                        mGameOver = false;
                        mMainMenu = true;

                        // Don't want to process snake direction for this tap
                        return true;
                    }else{
                        return true;
                    }
                }
                // Main Menu: any click will start a new game
                if (mPaused && mMainMenu) {
                    // Start a new game from the Main Menu
                    mPaused = false;
                    mGameOver = false;
                    mMainMenu = false;
                    newGame();

                    // Don't want to process snake direction for this tap
                    return true;
                }

                // Game is Paused: Only accept inputs for the play button
                if( mPaused ) {
                    // If the game is paused and the region for the pause button is clicked
                    if ((touchX <= 140) && (touchY >= 600)) {
                        // unpause the game
                        mPaused = false;

                        // Don't want to process snake direction for this tap
                        return true;
                    }
                } else {
                    // Game is playing: Change the snake motion or pause the game
                    if ((touchX <= 140) && (touchY >= 600)) {
                        // Pause the game
                        mPaused = true;

                        // Don't want to process snake direction for this tap
                        return true;
                    }
                    // Let the Snake class handle the input
                    mSnake.switchHeading(motionEvent);
                    break;
                }

            default:
                break;

        }
        return true;
    }

    // Stop the thread
    public void pause() {
        mPlaying = false;
        try {
            mThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }


    // Start the thread
    public void resume() {
        mPlaying = true;
        mThread = new Thread(this);
        mThread.start();
    }
}
