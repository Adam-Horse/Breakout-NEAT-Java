package com.adamhorse.tests;

import java.awt.Point;
import java.util.HashMap;

import com.adamhorse.neat.ConnectionGene;
import com.adamhorse.neat.Genome;
import com.adamhorse.neat.Innovation;
import com.adamhorse.neat.NodeGene;
import com.adamhorse.neat.NodeGene.TYPE;

public class TestCalculation {
	
	public static final int INPUT_SIZE_1 = 3;
	public static final int OUTPUT_SIZE_1 = 1;
	

	public static void main(String[] args) {
		HashMap<Integer, Point> nodeMap = new HashMap<Integer, Point>();
		double[] inputs = new double[] {0.5, 0.6, 0.7};
		
		Innovation nodeCounter = new Innovation();
		
		Genome parent1 = new Genome(INPUT_SIZE_1, OUTPUT_SIZE_1);
		parent1.setFitness(0);
		
		NodeGene in1 = new NodeGene(nodeCounter.generateInnovation(), TYPE.INPUT);
		NodeGene in2 = new NodeGene(nodeCounter.generateInnovation(), TYPE.INPUT);
		NodeGene in3 = new NodeGene(nodeCounter.generateInnovation(), TYPE.INPUT);
		
		NodeGene out = new NodeGene(nodeCounter.generateInnovation(), TYPE.OUTPUT);
		
		NodeGene mid5 = new NodeGene(nodeCounter.generateInnovation(), TYPE.HIDDEN);
		
		parent1.addNodeGene(in1);
		parent1.addNodeGene(in2);
		parent1.addNodeGene(in3);
		parent1.addNodeGene(out);
		parent1.addNodeGene(mid5);
		
		ConnectionGene oneToOut = new ConnectionGene(1, 0.1, in1.getInnovationNumber(), out.getInnovationNumber(), true);
		ConnectionGene twoToOut = new ConnectionGene(2, 1, in2.getInnovationNumber(), out.getInnovationNumber(), false);
		ConnectionGene threeToOut = new ConnectionGene(3, 1.2, in3.getInnovationNumber(), out.getInnovationNumber(), true);
		ConnectionGene twoToMid5 = new ConnectionGene(4, -0.6, in2.getInnovationNumber(), mid5.getInnovationNumber(), true);
		ConnectionGene mid5ToOut = new ConnectionGene(5, 0.5, mid5.getInnovationNumber(), out.getInnovationNumber(), true);
		ConnectionGene oneToMid5 = new ConnectionGene(8, 0.3, in1.getInnovationNumber(), mid5.getInnovationNumber(), true);
		
		parent1.addConnectionGene(oneToOut);
		parent1.addConnectionGene(twoToOut);
		parent1.addConnectionGene(oneToMid5);
		parent1.addConnectionGene(twoToMid5);
		parent1.addConnectionGene(mid5ToOut);
		parent1.addConnectionGene(threeToOut);
		
		PrintGenome.printGenome(parent1, "./Test Images/Parent 1.png", nodeMap);
		double[] outputs = parent1.calculateOutput(inputs);
		System.out.println();
		System.out.println("Outputs:");
		for (double output : outputs) {
			System.out.println(output);
		}
		
	}

}
