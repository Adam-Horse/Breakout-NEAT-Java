package com.adamhorse.tests;

import java.awt.Point;
import java.util.HashMap;
import java.util.Random;

import com.adamhorse.neat.ConnectionGene;
import com.adamhorse.neat.Genome;
import com.adamhorse.neat.Innovation;
import com.adamhorse.neat.NodeGene;
import com.adamhorse.neat.NodeGene.TYPE;

public class TestPaperExample {

	public static void main(String[] args) {
		
		HashMap<Integer, Point> nodeMap = new HashMap<Integer, Point>();
		
		Innovation nodeCounter = new Innovation();
		
		Genome parent1 = new Genome(3, 1);
		parent1.setFitness(0);
		Genome parent2 = new Genome(3, 1);
		parent2.setFitness(0);
		
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
		
		ConnectionGene oneToOut = new ConnectionGene(1, 0.5, in1.getInnovationNumber(), out.getInnovationNumber(), true);
		ConnectionGene twoToOut = new ConnectionGene(2, 0.5, in2.getInnovationNumber(), out.getInnovationNumber(), false);
		ConnectionGene threeToOut = new ConnectionGene(3, 0.5, in3.getInnovationNumber(), out.getInnovationNumber(), true);
		ConnectionGene twoToMid5 = new ConnectionGene(4, 0.5, in2.getInnovationNumber(), mid5.getInnovationNumber(), true);
		ConnectionGene mid5ToOut = new ConnectionGene(5, 0.5, mid5.getInnovationNumber(), out.getInnovationNumber(), true);
		ConnectionGene oneToMid5 = new ConnectionGene(8, 0.5, in1.getInnovationNumber(), mid5.getInnovationNumber(), true);
		
		parent1.addConnectionGene(oneToOut);
		parent1.addConnectionGene(twoToOut);
		parent1.addConnectionGene(oneToMid5);
		parent1.addConnectionGene(twoToMid5);
		parent1.addConnectionGene(mid5ToOut);
		parent1.addConnectionGene(threeToOut);
		
		PrintGenome.printGenome(parent1, "./Test Images/Parent 1.png", nodeMap);
		
		//And now for the second parent
		NodeGene mid6 = new NodeGene(nodeCounter.generateInnovation(), TYPE.HIDDEN);
		
		parent2.addNodeGene(in1.copy());
		parent2.addNodeGene(in2.copy());
		parent2.addNodeGene(in3.copy());
		parent2.addNodeGene(out.copy());
		parent2.addNodeGene(mid5.copy());
		parent2.addNodeGene(mid6);
		
		ConnectionGene mid5ToOutDisabled = new ConnectionGene(5, 0.5, mid5.getInnovationNumber(), out.getInnovationNumber(), false);
		ConnectionGene mid5ToMid6 = new ConnectionGene(6, 0.5, mid5.getInnovationNumber(), mid6.getInnovationNumber(), true);
		ConnectionGene mid6ToOut = new ConnectionGene(7, 0.5, mid6.getInnovationNumber(), out.getInnovationNumber(), true);
		ConnectionGene threeToMid5 = new ConnectionGene(9, 0.5, in3.getInnovationNumber(), mid5.getInnovationNumber(), true);
		ConnectionGene oneToMid6 = new ConnectionGene(10, 0.5, in1.getInnovationNumber(), mid6.getInnovationNumber(), true);
		
		parent2.addConnectionGene(oneToOut.copy());
		parent2.addConnectionGene(twoToOut.copy());
		parent2.addConnectionGene(twoToMid5.copy());
		parent2.addConnectionGene(mid5ToOutDisabled);
		parent2.addConnectionGene(threeToOut.copy());
		parent2.addConnectionGene(mid5ToMid6);
		parent2.addConnectionGene(mid6ToOut);
		parent2.addConnectionGene(threeToMid5);
		parent2.addConnectionGene(oneToMid6);
		
		PrintGenome.printGenome(parent2, "./Test Images/Parent 2.png", nodeMap);
		
		Genome child = Genome.crossover(parent1, parent2, new Random());
		PrintGenome.printGenome(child, "./Test Images/child.png", nodeMap);
	}

}
