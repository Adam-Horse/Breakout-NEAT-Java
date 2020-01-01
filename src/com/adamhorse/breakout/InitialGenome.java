package com.adamhorse.breakout;

import java.awt.Point;
import java.util.HashMap;

import com.adamhorse.neat.ConnectionGene;
import com.adamhorse.neat.Genome;
import com.adamhorse.neat.Innovation;
import com.adamhorse.neat.NodeGene;
import com.adamhorse.neat.NodeGene.FUNCTION;
import com.adamhorse.neat.NodeGene.TYPE;
import com.adamhorse.tests.PrintGenome;

public class InitialGenome {
	
	HashMap<Integer, Point> nodeMap = new HashMap<Integer, Point>();
	
	Innovation nodeCounter = new Innovation();
	Innovation connectionCounter = new Innovation();
	
	Genome initialGenome;
	
	NodeGene xBallInput = new NodeGene(nodeCounter.generateInnovation(), TYPE.INPUT);
	NodeGene yBallInput = new NodeGene(nodeCounter.generateInnovation(), TYPE.INPUT);
	NodeGene xPaddleInput = new NodeGene(nodeCounter.generateInnovation(), TYPE.INPUT);
	NodeGene[] brickInputs = new NodeGene[Commons.N_OF_BRICKS];
	NodeGene paddleOutput;
	
	public Innovation getNodeCounter() {
		return nodeCounter;
	}
	public Innovation getConnCounter() {
		return connectionCounter;
	}
	
	public InitialGenome(boolean withBricks) {
		if (withBricks) {
			learningInitWithBricks();
		} else if (!withBricks) {
			learningInitNoBricks();
		}
	}
	
	public void learningInitNoBricks() {
		initialGenome = new Genome(Commons.INPUT_SIZE_NO_BRICKS, Commons.OUTPUT_SIZE);
		initialGenome.addNodeGene(xBallInput);
		initialGenome.addNodeGene(yBallInput);
		initialGenome.addNodeGene(xPaddleInput);
		//This node determines left or right
		paddleOutput = new NodeGene(nodeCounter.generateInnovation(), TYPE.OUTPUT, FUNCTION.BINARY_STEP);
		initialGenome.addNodeGene(paddleOutput);
		
		//Now to add the connection genes
		ConnectionGene xBallInputInitialConn = new ConnectionGene(connectionCounter.generateInnovation(), 0.5, xBallInput.getInnovationNumber(), paddleOutput.getInnovationNumber(), true);
		ConnectionGene yBallInputInitialConn = new ConnectionGene(connectionCounter.generateInnovation(), 0.5, yBallInput.getInnovationNumber(), paddleOutput.getInnovationNumber(), true);
		ConnectionGene xPaddleInputInitialConn = new ConnectionGene(connectionCounter.generateInnovation(), 0.5, xPaddleInput.getInnovationNumber(), paddleOutput.getInnovationNumber(), true);
		initialGenome.addConnectionGene(xBallInputInitialConn);
		initialGenome.addConnectionGene(yBallInputInitialConn);
		initialGenome.addConnectionGene(xPaddleInputInitialConn);
		PrintGenome.printGenome(initialGenome, "./Test Images/Initial Genome.png", nodeMap);
	}
	
	public void learningInitWithBricks() {
		initialGenome = new Genome(Commons.INPUT_SIZE_WITH_BRICKS, Commons.OUTPUT_SIZE);
		initialGenome.addNodeGene(xBallInput);
		initialGenome.addNodeGene(yBallInput);
		initialGenome.addNodeGene(xPaddleInput);
		for (int i = 0; i < brickInputs.length; i++) {
			brickInputs[i] = new NodeGene(nodeCounter.generateInnovation(), TYPE.INPUT);
			initialGenome.addNodeGene(brickInputs[i]);
		}
		//This node determines left or right
		paddleOutput = new NodeGene(nodeCounter.generateInnovation(), TYPE.OUTPUT, FUNCTION.BINARY_STEP);
		initialGenome.addNodeGene(paddleOutput);
		
		//Now to add the connection genes
		ConnectionGene xBallInputInitialConn = new ConnectionGene(connectionCounter.generateInnovation(), 0.5, xBallInput.getInnovationNumber(), paddleOutput.getInnovationNumber(), true);
		ConnectionGene yBallInputInitialConn = new ConnectionGene(connectionCounter.generateInnovation(), 0.5, yBallInput.getInnovationNumber(), paddleOutput.getInnovationNumber(), true);
		ConnectionGene xPaddleInputInitialConn = new ConnectionGene(connectionCounter.generateInnovation(), 0.5, xPaddleInput.getInnovationNumber(), paddleOutput.getInnovationNumber(), true);
		initialGenome.addConnectionGene(xBallInputInitialConn);
		initialGenome.addConnectionGene(yBallInputInitialConn);
		initialGenome.addConnectionGene(xPaddleInputInitialConn);
		for (int i = 0; i < Commons.N_OF_BRICKS; i++) {
			ConnectionGene newConn = new ConnectionGene(connectionCounter.generateInnovation(), 0.5, brickInputs[i].getInnovationNumber(), paddleOutput.getInnovationNumber(), true);
			initialGenome.addConnectionGene(newConn);
		}
		PrintGenome.printGenome(initialGenome, "./Test Images/Initial Genome.png", nodeMap);
	}
	
	public Genome initializeGenome() {
		return initialGenome;
	}
	
	
}
