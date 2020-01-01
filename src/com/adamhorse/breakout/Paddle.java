package com.adamhorse.breakout;

import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Paddle extends Sprite  {

    private int dx;

    public Paddle() {
        
        initPaddle();        
    }
    
    private void initPaddle() {
    	imageWidth = Commons.PADDLE_WIDTH;
    	imageHeight = Commons.PADDLE_HEIGHT;

//        loadImage();
//        getImageDimensions();

        resetState();
    }
    
//    private void loadImage() {
//        
//        ImageIcon ii = new ImageIcon("src/resources/paddle.png");
//        image = ii.getImage();        
//    }    

    void move() {

        x += dx;

        if (x <= 0) {

            x = 0;
        }

        if (x >= Commons.WIDTH - imageWidth) {

            x = Commons.WIDTH - imageWidth;
        }
    }
    
    public void setDX(boolean right) {
    	dx = right ? Commons.VELOCITY : -Commons.VELOCITY;
    }
    public void stopPaddle() {
    	dx = 0;
    }

    void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {

            setDX(false);
        }

        if (key == KeyEvent.VK_RIGHT) {

            setDX(true);
        }
    }

    void keyReleased(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {

            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT) {

            dx = 0;
        }
    }

    private void resetState() {

        x = Commons.INIT_PADDLE_X;
        y = Commons.INIT_PADDLE_Y;
    }
}
