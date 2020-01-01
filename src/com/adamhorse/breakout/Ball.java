package com.adamhorse.breakout;

import javax.swing.ImageIcon;

public class Ball extends Sprite {

    private int xdir;
    private int ydir;

    public Ball() {

        initBall();
    }

    private void initBall() {
    	
    	imageWidth = Commons.BALL_WIDTH;
    	imageHeight = Commons.BALL_HEIGHT;
        
        xdir = Commons.VELOCITY;
        ydir = -Commons.VELOCITY;

//        loadImage();
//        getImageDimensions();
        resetState();
    }

//    private void loadImage() {
//
//        ImageIcon ii = new ImageIcon("src/resources/ball.png");
//        image = ii.getImage();
//    }

    void move() {

        x += xdir;
        y += ydir;

        if (x <= 0) {

            setXDir(Commons.VELOCITY);
            //System.out.println("HIT LEFT WALL");
        }

        if (x >= Commons.WIDTH - imageWidth) {

            
            setXDir(-Commons.VELOCITY);
            //System.out.println("HIT RIGHT WALL");
        }
        
        if (y == 0) {

            setYDir(Commons.VELOCITY);
            //System.out.println("HIT TOP WALL");
        }
    }

    private void resetState() {

        x = Commons.INIT_BALL_X;
        y = Commons.INIT_BALL_Y;
    }

    void setXDir(int x) {

        xdir = x;
        
    }

    void setYDir(int y) {

        ydir = y;
    }

    int getYDir() {

        return ydir;
    }
}
