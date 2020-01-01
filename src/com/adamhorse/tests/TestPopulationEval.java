package com.adamhorse.tests;

import java.awt.Point;
import java.io.IOException;
import java.util.HashMap;

import com.adamhorse.neat.ConnectionGene;
import com.adamhorse.neat.Genome;
import com.adamhorse.neat.Innovation;
import com.adamhorse.neat.NodeGene;
import com.adamhorse.neat.PopulationEval;
import com.adamhorse.neat.NodeGene.TYPE;
import com.adamhorse.tests.PrintGenome;

public class TestPopulationEval {
	
	public static void main(String[] args) {
		HashMap<Integer, Point> nodeMap = new HashMap<Integer, Point>();
		Innovation nodeInnovation = new Innovation();
		Innovation connectionInnovation = new Innovation();
		
		final Genome genome = new Genome(2, 1);
		NodeGene n1 = new NodeGene(nodeInnovation.generateInnovation(), TYPE.INPUT);
		NodeGene n2 = new NodeGene(nodeInnovation.generateInnovation(), TYPE.INPUT);
		NodeGene n3 = new NodeGene(nodeInnovation.generateInnovation(), TYPE.OUTPUT);
		genome.addNodeGene(n1);
		genome.addNodeGene(n2);
		genome.addNodeGene(n3);
		
		ConnectionGene c1 = new ConnectionGene(connectionInnovation.generateInnovation(), 0.5, n1.getInnovationNumber(), n3.getInnovationNumber(), true);
		ConnectionGene c2 = new ConnectionGene(connectionInnovation.generateInnovation(), 0.5, n2.getInnovationNumber(), n3.getInnovationNumber(), true);
		genome.addConnectionGene(c1);
		genome.addConnectionGene(c2);
		
		PopulationEval eval = new PopulationEval(100, genome, nodeInnovation, connectionInnovation) {
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
