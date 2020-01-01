package com.adamhorse.tests;

import java.awt.Point;
import java.util.HashMap;
import java.util.Random;

import com.adamhorse.neat.ConnectionGene;
import com.adamhorse.neat.Genome;
import com.adamhorse.neat.Innovation;
import com.adamhorse.neat.NodeGene;
import com.adamhorse.neat.NodeGene.TYPE;

public class TestAddConnectionMutation {

	public static void main(String[] args) {
		
		HashMap<Integer, Point> nodeMap = new HashMap<Integer, Point>();
		
		Innovation nodeInnovation = new Innovation();
		Innovation connectionInnovation = new Innovation();
		
		Genome genome = new Genome(2, 1);
		
		NodeGene input1 = new NodeGene(nodeInnovation.generateInnovation(), TYPE.INPUT);
		NodeGene input2 = new NodeGene(nodeInnovation.generateInnovation(), TYPE.INPUT);
		NodeGene output = new NodeGene(nodeInnovation.generateInnovation(), TYPE.OUTPUT);
		
		ConnectionGene oneToThree = new ConnectionGene(connectionInnovation.generateInnovation(), 0.5, input1.getInnovationNumber(), output.getInnovationNumber(), true);
		ConnectionGene twoToThree = new ConnectionGene(connectionInnovation.generateInnovation(), 0.5, input2.getInnovationNumber(), output.getInnovationNumber(), true);
		
		genome.addNodeGene(input1);
		genome.addNodeGene(input2);
		genome.addNodeGene(output);
		
		genome.addConnectionGene(oneToThree);
		genome.addConnectionGene(twoToThree);
		
		PrintGenome.printGenome(genome, "./Test Images/Unmutated Node Gene.png", nodeMap);
		
		genome.addNodeMutation(new Random(), nodeInnovation, connectionInnovation);

		PrintGenome.printGenome(genome, "./Test Images/Mutated Node Gene.png", nodeMap);
		
		genome.addConnectionMutation(new Random(), connectionInnovation);
		
		PrintGenome.printGenome(genome, "./Test Images/Mutated Connection Gene.png", nodeMap);
		
	}

}
