package com.adamhorse.breakout;

public interface Commons {
    
    final int WIDTH = 300;
    final int HEIGHT = 400;
    final int BOTTOM_EDGE = 390;
    final int N_OF_BRICKS = 30;
    final int INIT_PADDLE_X = 200;
    final int INIT_PADDLE_Y = 360;
    final int INIT_BALL_X = 230;
    final int INIT_BALL_Y = 355;    
    final int PERIOD = 50;
    final int CHECKING_PERIOD = 1000;
    final int VELOCITY = 5;
	public static final int INPUT_SIZE_WITH_BRICKS = 3 + N_OF_BRICKS;
	public static final int INPUT_SIZE_NO_BRICKS = 3;
	public static final int OUTPUT_SIZE = 1;
	final int POPULATION_SIZE = 100;
	final int MAX_ITERATIONS = 1000;
	final int MAX_GENERATIONS = 15;
	final int BALL_WIDTH = 5;
	final int BALL_HEIGHT = 5;
	final int PADDLE_WIDTH = 40;
	final int PADDLE_HEIGHT = 10;
	final int BRICK_WIDTH = 40;
	final int BRICK_HEIGHT = 10;
}
