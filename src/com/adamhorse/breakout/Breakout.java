package com.adamhorse.breakout;

import javax.swing.JFrame;

import com.adamhorse.neat.Genome;

import java.awt.Component;
import java.awt.EventQueue;

public class Breakout /* extends JFrame */ {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Board board;
	Genome player;
	boolean brickInput;
	
	public Breakout(Genome player, boolean brickInput) {
		this.brickInput = brickInput;
		this.player = player;
        initUI(player, this.brickInput);
    }
	
    private void initUI(Genome player, boolean brickInput) {
    	
    	board = new Board(player, brickInput);
//        add(board);
//        setTitle("Breakout");
//        
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
//        setResizable(false);
//        pack();
        //setVisible(true);
    }
    
    public boolean isFinished() {
    	return !board.isInGame();
    }
    
    public void cycle() {
    	board.doGameCycle();
    }
    
//    public void stop() {
//    	remove(board);
//    	System.out.println("THE UI HAS BEEN REMOVED");
//    }
//    
//    public void reset(Genome player) {
//    	this.player = player;
//    	board = new Board(player, brickInput);
//        add(board);
//        System.out.println("THE NEW GAME HAS BEEN ADDED");
//    }
    
}
