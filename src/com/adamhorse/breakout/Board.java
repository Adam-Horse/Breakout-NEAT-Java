package com.adamhorse.breakout;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.adamhorse.neat.Genome;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Board /*extends JPanel*/ {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean brickInput;
	private int points;
	private Genome player;
	//private Timer timer;
    private String message = "Game Over";
    private Ball ball;
    private Paddle paddle;
    private Brick[] bricks;
    private boolean inGame = true;
    
    //Utility methods for inputs for the NN
    public double getBallX() {
    	return ball.getX();
    }
    public double getBallY() {
    	return ball.getY();
    }
    public int getPoints() {
    	return points;
    }
    public int getPaddleX() {
    	return paddle.getX();
    }

    public Board(Genome player, boolean brickInput) {
    	this.brickInput = brickInput;
    	this.player = player;
        initBoard();
    }

    private void initBoard() {

//        addKeyListener(new TAdapter());
//        setFocusable(true);
//        setPreferredSize(new Dimension(Commons.WIDTH, Commons.HEIGHT));

        gameInit();
    }

    private void gameInit() {
    	points = 0;
        bricks = new Brick[Commons.N_OF_BRICKS];

        ball = new Ball();
        paddle = new Paddle();

        int k = 0;

        for (int i = 0; i < 5; i++) {

            for (int j = 0; j < 6; j++) {

                bricks[k] = new Brick(j * 40 + 30, i * 10 + 50);
                k++;
            }
        }
        
//        timer = new Timer(Commons.PERIOD, new GameCycle());
//        timer.start();
        
    }
    
    public void playerMove(boolean brickInput) {
    	double[] output = player.calculateOutput(this.extractInputFromGame(brickInput));
    	
    	//System.out.println(output[0]);
    	if (output[0] < 0.1) {
    		paddle.setDX(false);
    	} else if (output[0] > 0.4 && output[0] < 0.6) {
    		paddle.stopPaddle();
    	} else if (output[0] > 0.9) {
    		paddle.setDX(true);
    	}
    }
    
    /*
     * 0. xBallInput
     * 1. yBallInput
     * 2. xPaddleInput
     * 3 - 33. Brick status
     */
    public double[] extractInputFromGame(boolean brickInput) {
    	double[] networkInput;
    	if(brickInput) {
	    	networkInput = new double[Commons.INPUT_SIZE_WITH_BRICKS];
	    	//Normalize data
	    	networkInput[0] = getBallX() / Commons.WIDTH;
	    	networkInput[1] = getBallY() / Commons.HEIGHT;
	    	networkInput[2] = getPaddleX() / Commons.WIDTH;
	    	for (int i = 0; i < bricks.length; i++) {
	    		networkInput[i + 3] = bricks[i].isDestroyed() ? 1 : 0;
	    	}
	//    	for (double value : networkInput) {
	//    		System.out.print(value + ", ");
	//    	}
	    	//System.out.println();
	    	return networkInput;
    	} else {
    		networkInput = new double[Commons.INPUT_SIZE_NO_BRICKS];
    		networkInput[0] = getBallX() / Commons.WIDTH;
	    	networkInput[1] = getBallY() / Commons.HEIGHT;
	    	networkInput[2] = getPaddleX() / Commons.WIDTH;
	    	return networkInput;
    	}
    }
    
    public void reportScore() {
    	player.setFinalGameScore(points);
    	System.out.println("FINAL SCORE: " + getPoints() + "\tGAME OVER");
    }

//    @Override
//    public void paintComponent(Graphics g) {
//        super.paintComponent(g);
//
//        Graphics2D g2d = (Graphics2D) g;
//
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_ON);
//
//        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
//                RenderingHints.VALUE_RENDER_QUALITY);
//
//        if (inGame) {
//
//            drawObjects(g2d);
//        } else {
//
//            gameFinished(g2d);
//        }
//
//        Toolkit.getDefaultToolkit().sync();
//    }

//    private void drawObjects(Graphics2D g2d) {
//    	g2d.drawString("Points: " + points, 10, 15);
//        g2d.drawImage(ball.getImage(), ball.getX(), ball.getY(),
//                ball.getImageWidth(), ball.getImageHeight(), this);
//        g2d.drawImage(paddle.getImage(), paddle.getX(), paddle.getY(),
//                paddle.getImageWidth(), paddle.getImageHeight(), this);
//
//        for (int i = 0; i < Commons.N_OF_BRICKS; i++) {
//
//            if (!bricks[i].isDestroyed()) {
//
//                g2d.drawImage(bricks[i].getImage(), bricks[i].getX(),
//                        bricks[i].getY(), bricks[i].getImageWidth(),
//                        bricks[i].getImageHeight(), this);
//            }
//        }
//    }

//    private void gameFinished(Graphics2D g2d) {
//
//        Font font = new Font("Verdana", Font.BOLD, 18);
//        FontMetrics fontMetrics = this.getFontMetrics(font);
//
//        g2d.setColor(Color.BLACK);
//        g2d.setFont(font);
//        g2d.drawString(message,
//                (Commons.WIDTH - fontMetrics.stringWidth(message)) / 2,
//                Commons.WIDTH / 2);
//    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {

            paddle.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {

            paddle.keyPressed(e);
        }
    }

    private class GameCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            doGameCycle();
            
        }
    }
    
    //Only implement the game cycle when a HUMAN is playing!
    public void doGameCycle() {
    	playerMove(brickInput);
        ball.move();
        paddle.move();
        checkCollision();
        //System.out.println("BALL: " + "(" + ball.getX() + ", " + ball.getY() + ")");
//        System.out.println("PADDLE: " + "(" + paddle.getX() + ", " + paddle.getY() + ")");
//        repaint();
    }

    private void stopGame() {
    	reportScore();
        inGame = false;
//        if (timer != null) {
//        	timer.stop();
//        }
        System.out.println();
    }
    
    public boolean isInGame() {
    	return inGame;
    }

    private void checkCollision() {

        if (ball.getRect().getMaxY() > Commons.BOTTOM_EDGE) {
        	System.out.println("THE BALL HIT THE BOTTOM EDGE: (" + ball.getX() + ", " + ball.getY() + ")");
            stopGame();
            
        }

        for (int i = 0, j = 0; i < Commons.N_OF_BRICKS; i++) {

            if (bricks[i].isDestroyed()) {

                j++;
            }

            if (j == Commons.N_OF_BRICKS) {

                message = "Victory";
                stopGame();
                System.out.println("ALL BRICKS DESTROYED");
            }
        }

        if ((ball.getRect()).intersects(paddle.getRect())) {

            int paddleLPos = (int) paddle.getRect().getMinX();
            int ballLPos = (int) ball.getRect().getMinX();

            int first = paddleLPos + 8;
            int second = paddleLPos + 16;
            int third = paddleLPos + 24;
            int fourth = paddleLPos + 32;

            if (ballLPos < first) {

                ball.setXDir(-Commons.VELOCITY);
                ball.setYDir(-Commons.VELOCITY);
            }

            if (ballLPos >= first && ballLPos < second) {

                ball.setXDir(-Commons.VELOCITY);
                ball.setYDir(-1 * ball.getYDir());
            }

            if (ballLPos >= second && ballLPos < third) {

                ball.setXDir(0);
                ball.setYDir(-Commons.VELOCITY);
            }

            if (ballLPos >= third && ballLPos < fourth) {

                ball.setXDir(Commons.VELOCITY);
                ball.setYDir(-1 * ball.getYDir());
            }

            if (ballLPos > fourth) {

                ball.setXDir(Commons.VELOCITY);
                ball.setYDir(-Commons.VELOCITY);
            }
        }

        for (int i = 0; i < Commons.N_OF_BRICKS; i++) {

            if ((ball.getRect()).intersects(bricks[i].getRect())) {
            	
            	//System.out.println("BRICK " + i + " DESTROYED");

                int ballLeft = (int) ball.getRect().getMinX();
                int ballHeight = (int) ball.getRect().getHeight();
                int ballWidth = (int) ball.getRect().getWidth();
                int ballTop = (int) ball.getRect().getMinY();

                Point pointRight = new Point(ballLeft + ballWidth + 1, ballTop);
                Point pointLeft = new Point(ballLeft - 1, ballTop);
                Point pointTop = new Point(ballLeft, ballTop - 1);
                Point pointBottom = new Point(ballLeft, ballTop + ballHeight + 1);

                if (!bricks[i].isDestroyed()) {

                    if (bricks[i].getRect().contains(pointRight)) {

                        ball.setXDir(-Commons.VELOCITY);
                    } else if (bricks[i].getRect().contains(pointLeft)) {

                        ball.setXDir(Commons.VELOCITY);
                    }

                    if (bricks[i].getRect().contains(pointTop)) {

                        ball.setYDir(Commons.VELOCITY);
                    } else if (bricks[i].getRect().contains(pointBottom)) {

                        ball.setYDir(-Commons.VELOCITY);
                    }

                    bricks[i].setDestroyed(true);
                    points += 5;
                }
            }
        }
    }
}
