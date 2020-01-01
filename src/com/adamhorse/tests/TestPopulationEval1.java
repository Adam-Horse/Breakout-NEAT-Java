package com.adamhorse.tests;

import java.awt.Point;
import java.io.IOException;
import java.util.HashMap;

import com.adamhorse.breakout.Commons;
import com.adamhorse.neat.ConnectionGene;
import com.adamhorse.neat.Genome;
import com.adamhorse.neat.Innovation;
import com.adamhorse.neat.NodeGene;
import com.adamhorse.neat.PopulationEval;
import com.adamhorse.neat.NodeGene.FUNCTION;
import com.adamhorse.neat.NodeGene.TYPE;

public class TestPopulationEval1 {

	public static void main(String[] args) {
		
		HashMap<Integer, Point> nodeMap = new HashMap<Integer, Point>();
		
		Innovation nodeCounter = new Innovation();
		Innovation connectionCounter = new Innovation();
		
		Genome initialGenome = new Genome(Commons.INPUT_SIZE_WITH_BRICKS, Commons.OUTPUT_SIZE);
		
		NodeGene xBallInput = new NodeGene(nodeCounter.generateInnovation(), TYPE.INPUT);
		NodeGene yBallInput = new NodeGene(nodeCounter.generateInnovation(), TYPE.INPUT);
		NodeGene xPaddleInput = new NodeGene(nodeCounter.generateInnovation(), TYPE.INPUT);
		NodeGene[] brickInputs = new NodeGene[Commons.N_OF_BRICKS];
		NodeGene paddleOutput;
		
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
		//PrintGenome.printGenome(initialGenome, "./Test Images/Initial Genome.png", nodeMap);
		
		PopulationEval eval = new PopulationEval(100, initialGenome, nodeCounter, connectionCounter) {
			@Override
			protected double evaluateGenome(Genome genome) {
				//This includes disabled connections
				int totalActiveConns = 0;
				for (ConnectionGene conn : genome.getConnectionGenes().values()) {
					totalActiveConns++;
				}
				return totalActiveConns;
			}
		};
		for (int i = 0; i < 100; i++) {
			try {
				eval.evaluate();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.print("Generation: " + i);
			System.out.print("\tHighest fitness: " + eval.getHighestFitness());
			System.out.print("\tBest Species: " + eval.getBestSpecies().getID());
			System.out.print("\tAmount of species: " + eval.getSpeciesSize() + "\n");
			System.out.println("Size of genome list: " + eval.getGenomes().size());
			System.out.print("\n");
			PrintGenome.printGenome(eval.getBestGenome(), "./Test Images/" + "Best of generation " + i + ".png", nodeMap);
			
			//Right now I am printing the children. I am not printing the genes from the 0th generation
			//TODO STOP PRINTING KIDS
			//TODO GENERATION SHOULD HAVE EXACTALLY ZERO MUTATIONS
//			if (i == 1) {
//				for (int j = 0; j < eval.getGenomes().size(); j++) {
//					System.out.println(eval.getGenomes().get(j).getConnectionGenes().values());
//					//It seems i'm printing the list of genomes AFTER I switch the genomes to the new generation
//					PrintGenome.printGenome(eval.getGenomes().get(j), "./Test Images/" + j + ".png", nodeMap);
//					
//				}
//			}
		}
	}

}
