package com.adamhorse.tests;

import java.awt.Point;
import java.util.HashMap;
import java.util.Random;

import com.adamhorse.neat.ConnectionGene;
import com.adamhorse.neat.Genome;
import com.adamhorse.neat.Innovation;
import com.adamhorse.neat.NodeGene;
import com.adamhorse.neat.NodeGene.TYPE;

public class TestGeneCounting {

	public static void main(String[] args) {
		
		HashMap<Integer, Point> nodeMap = new HashMap<Integer, Point>();
		
		Innovation nodeInnovator = new Innovation();
		Innovation connInnovator = new Innovation();

		NodeGene[] sharedNodes = new NodeGene[10];
		ConnectionGene[] sharedConnections = new ConnectionGene[sharedNodes.length - 1];
		
		sharedNodes[0] = new NodeGene(nodeInnovator.generateInnovation(), TYPE.INPUT);
		System.out.println("Current Node Innovation: " + nodeInnovator.currentInnovation());
		for (int i = 1; i < sharedNodes.length - 1; i++) {
			sharedNodes[i] = new NodeGene(nodeInnovator.generateInnovation(), TYPE.HIDDEN);
			System.out.println("Current Node Innovation: " + nodeInnovator.currentInnovation());
			sharedConnections[i - 1] = new ConnectionGene(connInnovator.generateInnovation(), 1.0, sharedNodes[i - 1].getInnovationNumber(), sharedNodes[i].getInnovationNumber(), true);
		}
		sharedNodes[sharedNodes.length - 1] = new NodeGene(nodeInnovator.generateInnovation(), TYPE.OUTPUT);
		System.out.println("Current Node Innovation: " + nodeInnovator.currentInnovation());
		sharedConnections[sharedConnections.length - 1] = new ConnectionGene(connInnovator.generateInnovation(), 1.0, sharedNodes[sharedNodes.length - 2].getInnovationNumber(), sharedNodes[sharedNodes.length - 1].getInnovationNumber(), true);
		
		Genome genome1 = new Genome(0, 0);
		Genome genome2 = new Genome(0, 0);

		for (NodeGene g : sharedNodes) {
			genome1.addNodeGene(g);
			genome2.addNodeGene(g.copy());
		}
		
		//TODO since we are using the same connection object, if we disable the connection in genome 1, it will ALSO DISABLE the connection in genome 2. Make copies of objects!!
		int counter = 0;
		for (ConnectionGene g : sharedConnections) {
			System.out.println("Current Out Node: " + g.getOutNode() + "; Current In Node: " + g.getInNode() + "; Connection Innovation: " + g.getInnovationNumber());
			genome1.addConnectionGene(g);
			genome2.addConnectionGene(g.copy());
			counter++;
		}
		
		PrintGenome.printGenome(genome1,  "./Test Images/Counting Test Genome 1.png", nodeMap);
		PrintGenome.printGenome(genome2,  "./Test Images/Counting Test Genome 2.png", nodeMap);
		
		System.out.println("Number of matching genes = " + Genome.countMatchingGenes(genome1, genome2) + "\t Correct answer = " + (sharedNodes.length + sharedConnections.length));
		System.out.println("Number of disjoint genes = " + Genome.countDisjointGenes(genome1, genome2) + "\t Correct answer = 0");
		System.out.println("Number of excess genes = " + Genome.countExcessGenes(genome1, genome2) + "\t Correct answer = 0");
		System.out.println("\n");
		
		
		//These Three mutations will add 3 new node genes and 6 new connection genes for a total of 9 excess genes
		genome1.addNodeMutation(new Random(), nodeInnovator, connInnovator);
		genome1.addNodeMutation(new Random(), nodeInnovator, connInnovator);
		genome1.addNodeMutation(new Random(), nodeInnovator, connInnovator);
		
		System.out.println("Current Node Innovation: " + nodeInnovator.currentInnovation());
		System.out.println("Current Connect Innovation: " + connInnovator.currentInnovation());
		System.out.println("Number of matching genes = " + Genome.countMatchingGenes(genome1, genome2) + "\t Correct answer = " + (sharedNodes.length + sharedConnections.length));
		System.out.println("Number of disjoint genes = " + Genome.countDisjointGenes(genome1, genome2) + "\t Correct answer = 0");
		System.out.println("Number of excess genes = " + Genome.countExcessGenes(genome1, genome2) + "\t Correct answer = 9");
		System.out.println("\n");
		
		PrintGenome.printGenome(genome1,  "./Test Images/Counting Excess Test Genome 1.png", nodeMap);
		PrintGenome.printGenome(genome2,  "./Test Images/Counting Excess Test Genome 2.png", nodeMap);

		
		//These Three mutations will add 3 new node genes and 6 new connection genes for a total of 9 excess genes
		//The 9 previous excess genes now become disjoint genes
		genome2.addNodeMutation(new Random(), nodeInnovator, connInnovator);
		genome2.addNodeMutation(new Random(), nodeInnovator, connInnovator);
		genome2.addNodeMutation(new Random(), nodeInnovator, connInnovator);

		System.out.println("Number of matching genes = " + Genome.countMatchingGenes(genome1, genome2) + "\t Correct answer = " + (sharedNodes.length + sharedConnections.length));
		System.out.println("Number of disjoint genes = " + Genome.countDisjointGenes(genome1, genome2) + "\t Correct answer = 9");
		System.out.println("Number of excess genes = " + Genome.countExcessGenes(genome1, genome2) + "\t Correct answer = 9");
		System.out.println("\n");
		
		PrintGenome.printGenome(genome1,  "./Test Images/Counting Disjoint Test Genome 1.png", nodeMap);
		PrintGenome.printGenome(genome2,  "./Test Images/Counting Disjoint Test Genome 2.png", nodeMap);

		
		
		
		System.out.println("Counting genes between same genomes, but with opposite parameters:");
		System.out.println("Number of matching genes = " + Genome.countMatchingGenes(genome2, genome1) + "\t Correct answer = " + (sharedNodes.length + sharedConnections.length));
		System.out.println("Number of disjoint genes = " + Genome.countDisjointGenes(genome2, genome1) + "\t Correct answer = 9");
		System.out.println("Number of excess genes = " + Genome.countExcessGenes(genome2, genome1) + "\t Correct answer = 9");
		System.out.println("\n");
		
		NodeGene finalMatchingNode = new NodeGene(nodeInnovator.generateInnovation(), TYPE.HIDDEN);
		ConnectionGene finalMatchingConn = new ConnectionGene(connInnovator.generateInnovation(), 1.0, 4, 7, true);
		
		genome1.addNodeGene(finalMatchingNode);
		genome2.addNodeGene(finalMatchingNode.copy());
		
		genome1.addConnectionGene(finalMatchingConn);
		genome2.addConnectionGene(finalMatchingConn.copy());
		
		System.out.println("Number of matching genes = " + Genome.countMatchingGenes(genome2, genome1) + "\t Correct answer = " + (sharedNodes.length + sharedConnections.length + 2));
		System.out.println("Number of disjoint genes = " + Genome.countDisjointGenes(genome1, genome2) + "\t Correct answer = 18");
		System.out.println("Number of excess genes = " + Genome.countExcessGenes(genome1, genome2) + "\t Correct answer = 0");
		
	}

}
