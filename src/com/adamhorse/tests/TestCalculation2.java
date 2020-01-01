package com.adamhorse.tests;

import java.awt.Point;
import java.util.HashMap;

import com.adamhorse.neat.ConnectionGene;
import com.adamhorse.neat.Genome;
import com.adamhorse.neat.Innovation;
import com.adamhorse.neat.NodeGene;
import com.adamhorse.neat.NodeGene.TYPE;

public class TestCalculation2 {

	public static final int INPUT_SIZE_2 = 4;
	public static final int OUTPUT_SIZE_2 = 2;
	
	public static void main(String[] args) {
		HashMap<Integer, Point> nodeMap = new HashMap<Integer, Point>();
		double[] inputs = new double[] {0.5, 0.6, 0.7, 0.8};
		
		Innovation nodeCounter = new Innovation();
		
		Genome parent1 = new Genome(INPUT_SIZE_2, OUTPUT_SIZE_2);
		parent1.setFitness(0);
		
		NodeGene in1 = new NodeGene(nodeCounter.generateInnovation(), TYPE.INPUT);
		NodeGene in2 = new NodeGene(nodeCounter.generateInnovation(), TYPE.INPUT);
		NodeGene in3 = new NodeGene(nodeCounter.generateInnovation(), TYPE.INPUT);
		NodeGene in4 = new NodeGene(nodeCounter.generateInnovation(), TYPE.INPUT);
		
		NodeGene out5 = new NodeGene(nodeCounter.generateInnovation(), TYPE.OUTPUT);
		NodeGene out6 = new NodeGene(nodeCounter.generateInnovation(), TYPE.OUTPUT);
		
		NodeGene mid7 = new NodeGene(nodeCounter.generateInnovation(), TYPE.HIDDEN);
		NodeGene mid8 = new NodeGene(nodeCounter.generateInnovation(), TYPE.HIDDEN);
		NodeGene mid9 = new NodeGene(nodeCounter.generateInnovation(), TYPE.HIDDEN);
		NodeGene mid10 = new NodeGene(nodeCounter.generateInnovation(), TYPE.HIDDEN);
		
		parent1.addNodeGene(in1);
		parent1.addNodeGene(in2);
		parent1.addNodeGene(in3);
		parent1.addNodeGene(in4);
		parent1.addNodeGene(out5);
		parent1.addNodeGene(out6);
		parent1.addNodeGene(mid7);
		parent1.addNodeGene(mid8);
		parent1.addNodeGene(mid9);
		parent1.addNodeGene(mid10);
		
		ConnectionGene in1ToMid7 = new ConnectionGene(1, -0.27, in1.getInnovationNumber(), mid7.getInnovationNumber(), true);
		ConnectionGene in2ToMid7 = new ConnectionGene(2, 0.39, in2.getInnovationNumber(), mid7.getInnovationNumber(), true);
		ConnectionGene mid7ToMid10 = new ConnectionGene(3, 1.26, mid7.getInnovationNumber(), mid10.getInnovationNumber(), true);
		ConnectionGene mid9ToMid10 = new ConnectionGene(4, -1.44, mid9.getInnovationNumber(), mid10.getInnovationNumber(), true);
		ConnectionGene mid10ToOut5 = new ConnectionGene(5, 0.35, mid10.getInnovationNumber(), out5.getInnovationNumber(), true);
		ConnectionGene mid9ToOut5 = new ConnectionGene(6, -0.94, mid9.getInnovationNumber(), out5.getInnovationNumber(), true);
		ConnectionGene mid9ToOut6 = new ConnectionGene(7, 0.123, mid9.getInnovationNumber(), out6.getInnovationNumber(), true);
		ConnectionGene mid8ToOut6 = new ConnectionGene(8, 0.456, mid8.getInnovationNumber(), out6.getInnovationNumber(), true);
		ConnectionGene in4ToMid8 = new ConnectionGene(9, 0.78, in4.getInnovationNumber(), mid8.getInnovationNumber(), true);
		ConnectionGene in4ToMid9 = new ConnectionGene(10, -0.116, in4.getInnovationNumber(), mid9.getInnovationNumber(), true);
		ConnectionGene in3ToMid9 = new ConnectionGene(11, -2.0, in3.getInnovationNumber(), mid9.getInnovationNumber(), true);
		
		parent1.addConnectionGene(in1ToMid7);
		parent1.addConnectionGene(in2ToMid7);
		parent1.addConnectionGene(mid7ToMid10);
		parent1.addConnectionGene(mid9ToMid10);
		parent1.addConnectionGene(mid10ToOut5);
		parent1.addConnectionGene(mid9ToOut5);
		parent1.addConnectionGene(mid9ToOut6);
		parent1.addConnectionGene(mid8ToOut6);
		parent1.addConnectionGene(in4ToMid8);
		parent1.addConnectionGene(in4ToMid9);
		parent1.addConnectionGene(in3ToMid9);
		
		PrintGenome.printGenome(parent1, "./Test Images/Parent 1.png", nodeMap);
		double[] outputs = parent1.calculateOutput(inputs);
//		System.out.println();
		System.out.println("Outputs:");
		for (double output : outputs) {
			System.out.println(output);
		}

	}

}
