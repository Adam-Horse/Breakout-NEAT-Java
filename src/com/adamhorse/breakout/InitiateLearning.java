package com.adamhorse.breakout;

import java.awt.Point;
import java.io.IOException;
import java.util.HashMap;


import com.adamhorse.neat.Genome;
import com.adamhorse.neat.PopulationEval;
import com.adamhorse.tests.PrintGenome;



public class InitiateLearning {
	
	//Timer timer;
	
	InitialGenome initialGenome;
	Genome g;
	PopulationEval evaluator;
	
	int genCounter = 0;
	
	HashMap<Integer, Point> nodeMap = new HashMap<Integer, Point>();
	
	boolean allDone = false;
	boolean brickInput;
	
	public void initiateLearning(boolean brickInput) {
		this.brickInput = brickInput;
		
		initialGenome = new InitialGenome(this.brickInput);
		g = initialGenome.initializeGenome();
		
		
		
        evaluator = new PopulationEval(Commons.POPULATION_SIZE, g, initialGenome.getNodeCounter(), initialGenome.getConnCounter())  {
			@Override
			protected double evaluateGenome(Genome genome) {
				//System.out.println("Genome score: " + genome.getGameScore());
				return genome.getGameScore();
			}
		};
		
		for (int i = 0; i < Commons.MAX_GENERATIONS; i++) {
			playWholeGeneration();
		}
		
//		games = new GameThread[Commons.POPULATION_SIZE];
//		
//		//Create 100 different threads
//		for (int i = 0; i < Commons.POPULATION_SIZE; i++) {
//			//Shows first 5 games
//			games[i] = new GameThread(evaluator.getGenomes().get(i), brickInput, i < 5);
//		}
//		
//		start();
//		
//		timer = new Timer(Commons.CHECKING_PERIOD, new FinishCheck());
//        timer.start();
        
	}
	
	public void playWholeGeneration() {
		for (int i = 0; i < Commons.POPULATION_SIZE; i++) {
			playGame(new Breakout(evaluator.getGenomes().get(i), false));
		}
		try {
			evaluator.evaluate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print("Generation: " + genCounter);
		System.out.print("\tHighest fitness: " + evaluator.getHighestFitness());
		System.out.print("\tBest Species: " + evaluator.getBestSpecies().getID());
		System.out.print("\tAmount of species: " + evaluator.getSpeciesSize() + "\n");
		System.out.println("Size of genome list: " + evaluator.getPreviousGenomes().size());
		System.out.print("\n");
		PrintGenome.printGenome(evaluator.getBestGenome(), "./Test Images/" + "Best of generation " + genCounter + ".png", nodeMap);
		System.out.println("Previous genome size: " + evaluator.getPreviousGenomes().size());
		for (int i = 0; i < 5; i++) {
			PrintGenome.printGenome(evaluator.getTopFive().get(i), "./Test Images/" + "Gen " + genCounter + " of genome " + i + ".png", nodeMap, i);
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		genCounter++;
	}
	
	public void playGame(Breakout gameInstance) {
		//gameInstance.reset();
		int iteration = 0;
		
		while (iteration < Commons.MAX_ITERATIONS || !gameInstance.isFinished()) {
			//System.out.println("ITERATION: " + iteration);
			if (iteration > Commons.MAX_ITERATIONS) {
				break;
			}
			if (gameInstance.isFinished()) {
				break;
			}
			gameInstance.cycle();
			//System.out.println();
			iteration++;
			
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public void start() {
//		System.out.println("STARTING NEW GENERATION");
//		if (genCounter < 1) {
//			for (int i = 0; i < Commons.POPULATION_SIZE; i++) {
//				games[i].start();
//			}
//		} else {
//			System.out.println("INITIAL GENERATION IS DONE, THIS IS GENERATION " + genCounter);
//			for (int i = 0; i < Commons.POPULATION_SIZE; i++) {
//				games[i].replacePlayer(evaluator.getGenomes().get(i));
//			}
//		}
//	}
//	
//	public void resetAndStart() {
//		for (int i = 0; i < Commons.POPULATION_SIZE; i++) {
//			games[i].stopGame();
//		}
//		if (genCounter < maxGenerations) {
//			try {
//				evaluator.evaluate();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			//TODO I'm still printing the kids since the genome list switches at the end of evaluate
//			System.out.print("Generation: " + genCounter);
//			System.out.print("\tHighest fitness: " + evaluator.getHighestFitness());
//			System.out.print("\tBest Species: " + evaluator.getBestSpecies().getID());
//			System.out.print("\tAmount of species: " + evaluator.getSpeciesSize() + "\n");
//			System.out.println("Size of genome list: " + evaluator.getPreviousGenomes().size());
//			System.out.print("\n");
//			PrintGenome.printGenome(evaluator.getBestGenome(), "./Test Images/" + "Best of generation " + genCounter + ".png", nodeMap);
//			System.out.println("Previous genome size: " + evaluator.getPreviousGenomes().size());
//			for (int i = 0; i < Commons.POPULATION_SIZE; i++) {
//				PrintGenome.printGenome(evaluator.getPreviousGenomes().get(i), "./Test Images/" + "Gen " + genCounter + " of genome " + i + ".png", nodeMap, i);
//			}
//			genCounter++;
//			start();
//			allDone = false;
//			timer.start();
//		} else {
//			System.out.println("That was generation " + (maxGenerations - 1) + ", the LAST generation!");
//		}
//		
//	}
//	
//	private class FinishCheck implements ActionListener {
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//
//            allFinished();
//        }
//    }
//	
//	public void allFinished() {
//		System.out.println("CHECKING COMPLETION");
//		for (GameThread game : games) {
//			if (!game.isFinished()) {
//				allDone = false;
//				return;
//			}
//		}
//		System.out.println("The games have finished, time to evaluate");
//		System.out.println();
//		allDone = true;
//		timer.stop();
//		resetAndStart();
//	}
}
