package com.adamhorse.tests;

import java.awt.Point;
import java.io.IOException;
import java.util.HashMap;

import com.adamhorse.breakout.InitialGenome;
import com.adamhorse.neat.ConnectionGene;
import com.adamhorse.neat.Genome;
import com.adamhorse.neat.PopulationEval;

public class TestPopulation2 {

	public static void main(String[] args) {
		
		HashMap<Integer, Point> nodeMap = new HashMap<Integer, Point>();
		
		InitialGenome ig = new InitialGenome(false);
		
		Genome g = ig.initializeGenome();		
		PopulationEval eval = new PopulationEval(100, g, ig.getNodeCounter(), ig.getConnCounter()) {
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
