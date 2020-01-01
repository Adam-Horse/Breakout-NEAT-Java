package com.adamhorse.neat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.adamhorse.neat.NodeGene.TYPE;
import com.adamhorse.tests.PrintGenome;

public class Genome implements Comparable<Genome> {
	
	
	
	private double gameScore;
	private boolean finished = false;
	
	private final int inputSize;
	private final int outputSize;
	
	public int getInputSize() {
		return inputSize;
	}
	public int getOutputSize() {
		return outputSize;
	}
	
	private double[] inputs;
	
	private static final double PROBABILITY_PERTURBED = 0.9;
	
	private static List<Integer> tempList1 = new ArrayList<Integer>();
	private static List<Integer> tempList2 = new ArrayList<Integer>();
	
	private Map<Integer, ConnectionGene> connectionGenes;
	private Map<Integer, NodeGene> nodeGenes;
	private List<int[]> unconnectedNodes;
	
	private double fitness;
	private double adjustedFitness;
	
	private boolean deathMark = false;
	
	public Genome(int inputSize, int outputSize) {
		connectionGenes = new HashMap<Integer, ConnectionGene>();
		nodeGenes = new HashMap<Integer, NodeGene>();
		unconnectedNodes = new ArrayList<int[]>();
		this.inputSize = inputSize;
		this.outputSize = outputSize;
		this.inputs = new double[this.inputSize];
		checkIfValid();
	}
	
	public Genome(Genome g) {
		connectionGenes = new HashMap<Integer, ConnectionGene>();
		nodeGenes = new HashMap<Integer, NodeGene>();
		for (NodeGene node : g.getNodeGenes().values()) {
			this.addNodeGene(node.copy());
		}
		for (ConnectionGene conn : g.getConnectionGenes().values()) {
			this.addConnectionGene(conn.copy());
		}
		unconnectedNodes = g.getUnconnectedNodes();
		inputSize = g.getInputSize();
		outputSize = g.getOutputSize();
		
		checkIfValid();
	}
	
	//Copy genome with same fitness
	public Genome copy() {
		Genome copy = new Genome(this);
		copy.setFitness(this.getFitness());
		return copy;
	}
	
	public double getGameScore() {
		return gameScore;
	}
	public void setFinalGameScore(double gameScore) {
		this.finished = true;
		this.gameScore = gameScore;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public void mark() {
		deathMark = true;
	}
	
	public boolean willDie() {
		return deathMark;
	}
	
	public void setAdjustedFitness(double adjFit) {
		this.adjustedFitness = adjFit;
	}
	
	public double getAdjustedFitness() {
		return adjustedFitness;
	}
	
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	public double getFitness() {
		return this.fitness;
	}
	
	public Map<Integer, ConnectionGene> getConnectionGenes() {
		return connectionGenes;
	}

	public Map<Integer, NodeGene> getNodeGenes() {
		return nodeGenes;
	}

	public List<int[]> getUnconnectedNodes() {
		return unconnectedNodes;
	}
	
	public void addUnconnectedSet(List<int[]> unconnectedPairs) {
		for (int[] pair : unconnectedPairs) {
			unconnectedNodes.add(pair);
		}
	}

	public void addNodeGene(NodeGene node) {
		nodeGenes.put(node.getInnovationNumber(), node);
	}
	
	//TODO add logger to debug later
	public void addConnectionGene(ConnectionGene con) {
		if (con == null) {
			throw new RuntimeException("Can't add a null connection!!");
		}
		if (nodeGenes.get(con.getInNode()) == null) {
			throw new RuntimeException("Connection " + con.getInnovationNumber() + " isn't going into anything!");
		} else if (nodeGenes.get(con.getInNode()) == null) {
			throw new RuntimeException("Connection " + con.getInnovationNumber() + " isn't going out of anything!");
		}
		connectionGenes.put(con.getInnovationNumber(), con);
	}
	
	public void addUnconnectedPair(NodeGene node1, NodeGene node2) {
		unconnectedNodes.add(new int[] {node1.getInnovationNumber(), node2.getInnovationNumber()});
	}
	
	
	public void resetValues() {
		for (NodeGene node : getNodeGenes().values()) {
			node.reset();
		}
		finished = false;
	}
	
	public double[] calculateOutput(double[] inputArray) {
// 		System.out.println("Number of inputs: " + inputs.length);
		resetValues();
		for (NodeGene node : getNodeGenes().values()) {
			for (ConnectionGene conn : getConnectionGenes().values()) {
				if (conn.getInNode() == node.getInnovationNumber() && conn.isExpressed()) {
					node.addInputConn(conn);
				}
			}
//			System.out.println("ALL THE INPUT CONNS FOR NODE " + node.getInnovationNumber() + ":");
//			for (ConnectionGene conn : node.getInputConns()) {
//				System.out.println(conn);
//			}
		}
//		System.out.println();
		
		this.inputs = inputArray;
		double[] outputs = new double[outputSize];
		for (int i = 1; i < inputs.length + 1; i++) {
//			System.out.println();
			double input = inputs[i - 1];
			NodeGene inputNode = getNodeGenes().get(i);
//			System.out.println("Now on input node " + inputNode.getInnovationNumber());
			inputNode.setInputValue(input);
			
			for (ConnectionGene conn : getConnectionGenes().values()) {
				if (conn.getOutNode() == inputNode.getInnovationNumber() && conn.isExpressed()) {
					NodeGene nextNode = getNodeGenes().get(conn.getInNode());
					nextNode.addInputValue(inputNode.getOutputValue(conn), conn);
					
				}
			}
		}
//		System.out.println();
//		System.out.println();
//		System.out.println("Moving on from the inputs...");
		//Keep looping though all the connections, if the out node ready to fire, send the data through the connection
		boolean allFired = false;
		while (!allFired) {
			//Loop through all the hidden nodes and fire them
			for (NodeGene node : getNodeGenes().values()) {
				
				if (node.getType() == TYPE.HIDDEN) {
					
					for (ConnectionGene conn : getConnectionGenes().values()) {
						
						if (conn.getOutNode() == node.getInnovationNumber() && conn.isExpressed()) {
							
							NodeGene outNode = node;
							NodeGene inNode = getNodeGenes().get(conn.getInNode());
							
							if (outNode.isReadyToFire()) {
								inNode.addInputValue(outNode.getOutputValue(conn), conn);
							}
							
						}
					}
				}
			}
			
			//Check if all the nodes have fired except for the output node
			for (NodeGene node : getNodeGenes().values()) {
				if (node.getType() != TYPE.OUTPUT) {
					if (node.isFired() == false) {
						allFired = false;
						break;
					}
					allFired = true;
				}
			}
			
		}
		//Place outputs in array
		int outputCounter = 0;
		for (NodeGene outputNode : getNodeGenes().values()) {
			if (outputNode.getType() == TYPE.OUTPUT) {
				if (outputNode.isReadyToFire()) {
					outputs[outputCounter] = outputNode.getOutputValue();
					outputCounter++;
				}
			}
		}
		return outputs;
	}
	
	public static int countMatchingGenes(Genome genome1, Genome genome2) {
		int matchingGenes = 0;
		
		List<Integer> nodeKeys1 = asSortedList(genome1.getNodeGenes().keySet(), tempList1);
		List<Integer> nodeKeys2 = asSortedList(genome2.getNodeGenes().keySet(), tempList2);
		
		int highestInnovation1 = nodeKeys1.get(nodeKeys1.size() - 1);
		int highestInnovation2 = nodeKeys2.get(nodeKeys2.size() - 1);
		int indices = Math.max(highestInnovation1, highestInnovation2);
		
		for (int i = 0; i <= indices; i++) { 					// loop through genes -> i is innovation numbers
			NodeGene node1 = genome1.getNodeGenes().get(i);
			NodeGene node2 = genome2.getNodeGenes().get(i);
			if (node1 != null && node2 != null) { 
				// both genomes has the gene w/ this innovation number
				matchingGenes++;
			}
		}
		
		List<Integer> conKeys1 = asSortedList(genome1.getConnectionGenes().keySet(), tempList1);
		List<Integer> conKeys2 = asSortedList(genome2.getConnectionGenes().keySet(), tempList2);
		
		highestInnovation1 = conKeys1.get(conKeys1.size() - 1);
		highestInnovation2 = conKeys2.get(conKeys2.size() - 1);
		
		indices = Math.max(highestInnovation1, highestInnovation2);
		for (int i = 0; i <= indices; i++) { 					// loop through genes -> i is innovation numbers
			ConnectionGene connection1 = genome1.getConnectionGenes().get(i);
			ConnectionGene connection2 = genome2.getConnectionGenes().get(i);
			if (connection1 != null && connection2 != null) { 
				// both genomes has the gene w/ this innovation number
				matchingGenes++;
			}
		}
		
		return matchingGenes;
	}
	
	/**
	 * Counts number of excess genes between genome 1 and genome 2
	 * @param genome1
	 * @param genome2
	 * @return The count of excess genes
	 */
	public static int countExcessGenes(Genome genome1, Genome genome2) {
		int excessGenes = 0;
		
		//Count excess nodes
		List<Integer> nodeKeys1 = asSortedList(genome1.getNodeGenes().keySet(), tempList1);
		List<Integer> nodeKeys2 = asSortedList(genome2.getNodeGenes().keySet(), tempList2);
		
		int highestInnovation1 = nodeKeys1.get(nodeKeys1.size() - 1);
		int highestInnovation2 = nodeKeys2.get(nodeKeys2.size() - 1);
		
		//If genome 1 has a higher innovation
		if (highestInnovation1 > highestInnovation2) {
			//Find the index of the highest innovation in genome 2 in genome 1
			//and count how many genes exist AFTER that index
			int lastIndex = nodeKeys1.indexOf(highestInnovation2);
			if (lastIndex == -1) {
				for (int i = 0; i < nodeKeys1.size(); i++) {
					if (nodeKeys1.get(i) > highestInnovation2) {
						lastIndex = i - 1;
						break;
					}
				}
			}
			excessGenes += nodeKeys1.size() - lastIndex - 1;
			//TODO What if the last gene is a disjoint gene that doesn't exist in the other genome???
			//TODO Find the closest innovation number and it's index
//			System.out.println("-------------------------------------");
//			System.out.println("Genome 2 node list size: " + nodeKeys1.size());
//			System.out.println("Genome 1 largest Innovation: " + highestInnovation2);
//			System.out.println("Index, or closest index, of largest innovation from genome 1: " + lastIndex);
//			System.out.println("-------------------------------------");
		} else if (highestInnovation1 < highestInnovation2) {
			int lastIndex = nodeKeys2.indexOf(highestInnovation1);
			if (lastIndex == -1) {
				for (int i = 0; i < nodeKeys2.size(); i++) {
					if (nodeKeys2.get(i) > highestInnovation1) {
						lastIndex = i - 1;
						break;
					}
				}
			}
			excessGenes += nodeKeys2.size() - lastIndex - 1;
//			System.out.println("-------------------------------------");
//			System.out.println("Genome 1 node list size: " + nodeKeys1.size());
//			System.out.println("Genome 2 largest Innovation: " + highestInnovation2);
//			System.out.println("Index, or closest index, of largest innovation from genome 2: " + lastIndex);
//			System.out.println("-------------------------------------");
		}
		
		//Count excess connections
		List<Integer> connectionKeys1 = asSortedList(genome1.getConnectionGenes().keySet(), tempList1);
		List<Integer> connectionKeys2 = asSortedList(genome2.getConnectionGenes().keySet(), tempList2);
		
		highestInnovation1 = connectionKeys1.get(connectionKeys1.size() - 1);
		highestInnovation2 = connectionKeys2.get(connectionKeys2.size() - 1);
		
		if (highestInnovation1 > highestInnovation2) {
			//Find the index of the highest innovation in genome 2 in genome one
			//and count how many genes exist AFTER that index
			int lastIndex = connectionKeys1.indexOf(highestInnovation2);
			if (lastIndex == -1) {
				for (int i = 0; i < connectionKeys1.size(); i++) {
					if (connectionKeys1.get(i) > highestInnovation2) {
						lastIndex = i - 1;
						break;
					}
				}
			}
			excessGenes += connectionKeys1.size() - lastIndex - 1;
//			System.out.println("-------------------------------------");
//			System.out.println("Genome 2 connection list size: " + connectionKeys1.size());
//			System.out.println("Genome 1 largest Innovation: " + highestInnovation2);
//			System.out.println("Index, or closest index, of largest innovation from genome 1: " + lastIndex);
//			System.out.println("-------------------------------------");
		} else if (highestInnovation1 < highestInnovation2) {
			int lastIndex = connectionKeys2.indexOf(highestInnovation1);
			if (lastIndex == -1) {
				for (int i = 0; i < connectionKeys2.size(); i++) {
					if (connectionKeys2.get(i) > highestInnovation1) {
						lastIndex = i - 1;
						break;
					}
				}
			}
			excessGenes += connectionKeys2.size() - lastIndex - 1;
//			System.out.println("-------------------------------------");
//			System.out.println("Genome 1 connection list size: " + connectionKeys1.size());
//			System.out.println("Genome 2 largest Innovation: " + highestInnovation2);
//			System.out.println("Index, or closest index, of largest innovation from genome 2: " + lastIndex);
//			System.out.println("-------------------------------------");
		}
		
		
		return excessGenes;
	}
	
	/**
	 * Counts only the number of disjoint genes between 2 genomes
	 * @param genome1
	 * @param genome2
	 * @return the number of disjoint genes
	 */
	public static int countDisjointGenes(Genome genome1, Genome genome2) {
		
		int disjointGenes = 0;
		
		List<Integer> nodeKeys1 = asSortedList(genome1.getNodeGenes().keySet(), tempList1);
		List<Integer> nodeKeys2 = asSortedList(genome2.getNodeGenes().keySet(), tempList2);
		
		int highestInnovation1 = nodeKeys1.get(nodeKeys1.size() - 1);
		int highestInnovation2 = nodeKeys2.get(nodeKeys2.size() - 1);
		int highest = Math.max(highestInnovation1, highestInnovation2);
		
		for (int i = 0; i <= highest; i++) {
			NodeGene node1 = genome1.getNodeGenes().get(i);
			NodeGene node2 = genome2.getNodeGenes().get(i);
			if (node1 == null && highestInnovation1 > i && node2 != null) {
				disjointGenes++;
			} else if (node2 == null && highestInnovation2 > i && node1 != null) {
				disjointGenes++;
			}
		}
		
		List<Integer> conKeys1 = asSortedList(genome1.getConnectionGenes().keySet(), tempList1);
		List<Integer> conKeys2 = asSortedList(genome2.getConnectionGenes().keySet(), tempList2);
		
		highestInnovation1 = conKeys1.get(conKeys1.size() - 1);
		highestInnovation2 = conKeys2.get(conKeys2.size() - 1);
		highest = Math.max(highestInnovation1, highestInnovation2);
		
		for (int i = 0; i <= highest; i++) {
			ConnectionGene con1 = genome1.getConnectionGenes().get(i);
			ConnectionGene con2 = genome2.getConnectionGenes().get(i);
			if (con1 == null && highestInnovation1 > i && con2 != null) {
				disjointGenes++;
			} else if (con2 == null && highestInnovation2 > i && con1 != null) {
				disjointGenes++;
			}
		}
		
		return disjointGenes;
		
	}
	
	/**
	 * 
	 * @param genome1
	 * @param genome2
	 * @return Average Weight Difference between the 2 genomes
	 */
	public static double avgWeightDiff(Genome genome1, Genome genome2) {
		int matchingGenes = 0;
		double weightDiffSum = 0;
		List<Integer> conKeys1 = asSortedList(genome1.getConnectionGenes().keySet(), tempList1);
		List<Integer> conKeys2 = asSortedList(genome2.getConnectionGenes().keySet(), tempList2);
		
		int highestInnovation1 = conKeys1.get(conKeys1.size() - 1);
		int highestInnovation2 = conKeys2.get(conKeys2.size() - 1);
		int highest = Math.max(highestInnovation1, highestInnovation2);
		
		for (int i = 0; i <= highest; i++) {
			ConnectionGene con1 = genome1.getConnectionGenes().get(i);
			ConnectionGene con2 = genome2.getConnectionGenes().get(i);
			if (con1 != null && con2 != null) {
				matchingGenes++;
				weightDiffSum += Math.abs(con1.getWeight() - con2.getWeight());
			}
		}
		return weightDiffSum / matchingGenes;
		
	}
	
	/**
	 * Computes how different the two genomes are based on excess genes, disjoint genes, and weight differences.
	 * The distances are separated for speciation.
	 * @param genome1
	 * @param genome2
	 * @param c1
	 * @param c2
	 * @param c3
	 * @return The compatibility distance
	 */
	public static double compatabilityDistance(Genome genome1, Genome genome2, double c1, double c2, double c3) {
		double distance = 0;
		double n = 1;
		int g1Size = genome1.getNodeGenes().size() + genome1.getConnectionGenes().size();
		int g2Size = genome2.getNodeGenes().size() + genome2.getConnectionGenes().size();
		if (g1Size >= 20 || g2Size >= 20) {
			n = Math.max(g1Size, g2Size);
		}
		
		distance += (countExcessGenes(genome1, genome2) * c1) / n;
		distance += (countDisjointGenes(genome1, genome2) * c2) / n;
		distance += avgWeightDiff(genome1, genome2) * c3;
		return distance;
	}
	
	public void weightMutation(Random r) {
		for (ConnectionGene con : getConnectionGenes().values()) {
			if (r.nextDouble() < PROBABILITY_PERTURBED) {
				//The weight can change by a maximum of 20%
				//Perturbed means to change by a SMALL amount
				con.setWeight(con.getWeight() + (con.getWeight() * (r.nextDouble() * 0.4 - 0.2)));
			} else {
				//New random weight between 2 and -2
				con.setWeight(r.nextDouble() * 4.0 - 2.0);
			}
		}
	}
	
	/**
	 * Splits an existing enabled connection and places a new node in between.
	 * This node will ALWAYS be HIDDEN
	 * @param Random r
	 * @param nodeInnovation
	 * @param connectionInnovation
	 */
	public void addNodeMutation(Random r, Innovation nodeInnovation, Innovation connectionInnovation) {
		
		NodeGene newNode = new NodeGene(nodeInnovation.generateInnovation(), TYPE.HIDDEN);
		addNodeGene(newNode);
		
		//randomly selects a connection to shove a node between
		ConnectionGene oldConn = (ConnectionGene) connectionGenes.values().toArray()[r.nextInt(connectionGenes.size() - 1)];
		//Creates the two new connections of the new node as referenced in page 108
		ConnectionGene newInConn = new ConnectionGene(connectionInnovation.generateInnovation(), 1.0, oldConn.getOutNode(), newNode.getInnovationNumber(), true);
		ConnectionGene newOutConn = new ConnectionGene(connectionInnovation.generateInnovation(), oldConn.getWeight(), newNode.getInnovationNumber(), oldConn.getInNode(), true);
		addConnectionGene(newInConn);
		addConnectionGene(newOutConn);
		
		//Add all pairs of unconnected nodes except for the three nodes with new connections
		//TODO Optimize this, looping isn't efficient
		//TODO Maybe only choose 10 random unconnected pairs to store in the list
		//TODO Maybe write the pairs to a file instead of an object
		for (NodeGene node : nodeGenes.values()) {
			if (!(node.equals(nodeGenes.get(oldConn.getOutNode())) ||
				  node.equals(nodeGenes.get(oldConn.getInNode())) ||
				  node.equals(newNode))) {
				
				addUnconnectedPair(newNode, node);
				//System.out.println("Unconnected: " + newNode + ", " + node);
			}
		}
		oldConn.disable();
		checkIfValid();
	}
	
	public void checkIfValid() {
		for (NodeGene node : getNodeGenes().values()) {
			for (ConnectionGene conn : getConnectionGenes().values()) {
				if (conn.getOutNode() == node.getInnovationNumber()) {
					nodeGenes.put(node.getInnovationNumber(), node);
					return;
				}
				//throw new RuntimeException("Node " + node.getInnovationNumber() + " doesn't have any out connections!!!");
			}
			System.out.println("Node " + node.getInnovationNumber() + " doesn't have any out connections!!!");
		}
	}
	
	/**
	 * Just connects two previously unconnected nodes
	 * @param r
	 * @param connectionInnovation
	 */
	public void addConnectionMutation(Random r, Innovation connectionInnovation) {
		boolean success = false;
		int tries = 0;
		//Randomly selects an unconnected pair
		while (!success && tries < getUnconnectedNodes().size() * 2) {
			int[] unconnectedPair = getUnconnectedNodes().get(r.nextInt(getUnconnectedNodes().size()));
			NodeGene node1 = this.getNodeGenes().get(unconnectedPair[0]);
			NodeGene node2 = this.getNodeGenes().get(unconnectedPair[1]);
			
			if (node1 == null || node2 == null) {
				continue;
			}
			
			//TODO optimize this... looping through everything is NOT efficient
			boolean connectionExists = false;
			for (ConnectionGene con : getConnectionGenes().values()) {
				if (con.getInNode() ==node1.getInnovationNumber() && con.getOutNode() == node2.getInnovationNumber()) { // existing connection
					connectionExists = true;
					break;
				} else if (con.getInNode() == node2.getInnovationNumber() && con.getOutNode() == node1.getInnovationNumber()) { // existing connection
					connectionExists = true;
					break;
				}
			}
			if (connectionExists) {
				tries++;
				continue;
			}
			success = true;
			double weight = r.nextDouble() * 2 - 1;
			
			boolean reversed = false;
			if (node1.getType() == TYPE.OUTPUT) {
				if (node2.getType() == TYPE.INPUT) {
					reversed = true;
				} else if (node2.getType() == TYPE.HIDDEN) {
					reversed = true;
				}
			} else if (node1.getType() == TYPE.HIDDEN) {
				if (node2.getType() == TYPE.INPUT) {
					reversed = true;
				}
			}
			
			ConnectionGene newConn;
			
			if (reversed) {
				newConn = new ConnectionGene(connectionInnovation.generateInnovation(), weight, node2.getInnovationNumber(), node1.getInnovationNumber(), true);
			} else {
				newConn = new ConnectionGene(connectionInnovation.generateInnovation(), weight, node1.getInnovationNumber(), node2.getInnovationNumber(), true);
			}
			
			addConnectionGene(newConn);
		}
		if (tries == getUnconnectedNodes().size() * 2) {
			System.out.println("CONNECTION COULD NOT BE FOUND");
		}
		checkIfValid();
	}
	
	//The connection genes are crossed, then the nodes are added, depending on what connection genes are given.
	public static Genome crossover(Genome parent1, Genome parent2, Random r) {
		int inSize = parent1.getInputSize();
		int outSize = parent1.getOutputSize();
		Genome child = new Genome(inSize, outSize);
		Genome moreFitParent = null;
		Genome lessFitParent = null;
		
		//If parent 1 is more fit than parent 2
		if (parent1.getFitness() > parent2.getFitness()) {
			moreFitParent = parent1;
			lessFitParent = parent2;
		} else if (parent1.getFitness() < parent2.getFitness()) {
			moreFitParent = parent2;
			lessFitParent = parent1;
		}
		
		//If fitness IS NOT equal
		if (moreFitParent != null || lessFitParent != null) {
			child.addUnconnectedSet(moreFitParent.getUnconnectedNodes());
			for (ConnectionGene moreFitConn : moreFitParent.getConnectionGenes().values()) {
				//If lessFitParent also has the gene, meaning that the genes are matching
				//Then randomly select one of the matching genes
				if (lessFitParent.getConnectionGenes().containsKey(moreFitConn.getInnovationNumber())) {
					if (r.nextBoolean()) {
						ConnectionGene newConn = moreFitConn.copy();
						NodeGene newOutNode = moreFitParent.getNodeGenes().get(newConn.getOutNode()).copy();
						NodeGene newInNode = moreFitParent.getNodeGenes().get(newConn.getInNode()).copy();
						child.addNodeGene(newOutNode);
						child.addNodeGene(newInNode);
						child.addConnectionGene(newConn);
						
					} else {
						ConnectionGene newConn = lessFitParent.getConnectionGenes().get(moreFitConn.getInnovationNumber()).copy();
						NodeGene newOutNode = lessFitParent.getNodeGenes().get(newConn.getOutNode()).copy();
						NodeGene newInNode = lessFitParent.getNodeGenes().get(newConn.getInNode()).copy();
						child.addNodeGene(newOutNode);
						child.addNodeGene(newInNode);
						child.addConnectionGene(newConn);
						
					}
				} else {
					//But if the gene is excess or disjointed, then grab them from moreFitParent
					ConnectionGene newConn = moreFitConn.copy();
					NodeGene newOutNode = moreFitParent.getNodeGenes().get(newConn.getOutNode()).copy();
					NodeGene newInNode = moreFitParent.getNodeGenes().get(newConn.getInNode()).copy();
					child.addNodeGene(newOutNode);
					child.addNodeGene(newInNode);
					child.addConnectionGene(newConn);
					
				}
			}
		//If fitness IS equal
		} else {
			child.addUnconnectedSet(parent1.getUnconnectedNodes());
			child.addUnconnectedSet(parent2.getUnconnectedNodes());
			//System.out.println("I'm crossing over now!");
			for (ConnectionGene p1Conn : parent1.getConnectionGenes().values()) {
				//If parent 2 also has the gene, meaning that the genes are matching
				//Then randomly select one of the matching genes
				if (parent2.getConnectionGenes().containsKey(p1Conn.getInnovationNumber())) {
					if (r.nextBoolean()) {
						ConnectionGene newConn = p1Conn.copy();
						NodeGene newOutNode = parent1.getNodeGenes().get(newConn.getOutNode()).copy();
						NodeGene newInNode = parent1.getNodeGenes().get(newConn.getInNode()).copy();
						child.addNodeGene(newOutNode);
						child.addNodeGene(newInNode);
						child.addConnectionGene(newConn);
						//System.out.println("Pulled the matching " + p1Conn.getInnovationNumber() + " connection gene from parent 1!");
					} else {
						ConnectionGene newConn = parent2.getConnectionGenes().get(p1Conn.getInnovationNumber()).copy();
						NodeGene newOutNode = parent2.getNodeGenes().get(newConn.getOutNode()).copy();
						NodeGene newInNode = parent2.getNodeGenes().get(newConn.getInNode()).copy();
						child.addNodeGene(newOutNode);
						child.addNodeGene(newInNode);
						child.addConnectionGene(newConn);
						//System.out.println("Pulled the matching " + p1Conn.getInnovationNumber() + " connection gene from parent 2!");
					}
				} else {
					
					//But if the gene is excess or disjointed, then grab them from both parents
					//TODO MAKE THIS RANDOM, NOT 100% INHERENTANCE
					if (r.nextBoolean()) {
						ConnectionGene newConn = p1Conn.copy();
						NodeGene newOutNode = parent1.getNodeGenes().get(newConn.getOutNode()).copy();
						NodeGene newInNode = parent1.getNodeGenes().get(newConn.getInNode()).copy();
						child.addNodeGene(newOutNode);
						child.addNodeGene(newInNode);
						child.addConnectionGene(newConn);
					}
					//System.out.println("Pulled the NOT matching " + p1Conn.getInnovationNumber() + " connection gene from parent 1!");
				}
			}
			for (ConnectionGene p2Conn : parent2.getConnectionGenes().values()) {
				if (!parent1.getConnectionGenes().containsKey(p2Conn.getInnovationNumber())) {
					//TODO MAKE THIS RANDOM, NOT 100% INHERENTANCE
					if (r.nextBoolean()) {
						ConnectionGene newConn = p2Conn.copy();
						NodeGene newOutNode = parent2.getNodeGenes().get(newConn.getOutNode()).copy();
						NodeGene newInNode = parent2.getNodeGenes().get(newConn.getInNode()).copy();
						child.addNodeGene(newOutNode);
						child.addNodeGene(newInNode);
						child.addConnectionGene(newConn);
						//System.out.println("Pulled the NOT matching " + p2Conn.getInnovationNumber() + " connection gene from parent 2!");
					}
				}
			}
		}
		//System.out.println();
		child.checkIfValid();
		return child;
	}
	
	private static List<Integer> asSortedList(Collection<Integer> c, List<Integer> list) {
		list.clear();
		list.addAll(c);
		java.util.Collections.sort(list);
		return list;
	}
	
	public static int countNodesByType(TYPE type, Genome genome) {
		int count = 0;
		for (NodeGene node : genome.getNodeGenes().values()) {
			if (node.getType() == type) {
				count++;
			}
		}
		return count;
	}
	
	public int countNodesByType(TYPE type) {
		int count = 0;
		for (NodeGene node : getNodeGenes().values()) {
			if (node.getType() == type) {
				count++;
			}
		}
		return count;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (ConnectionGene con : connectionGenes.values()) {
			if (nodeGenes.get(con.getOutNode()) == null) {
				throw new RuntimeException("The out node of connection " + con.getInnovationNumber() + " is null!");
			} else if (nodeGenes.get(con.getInNode()) == null) {
				throw new RuntimeException("The in node of connection " + con.getInnovationNumber() + " is null!");
			}
			sb.append(nodeGenes.get(con.getOutNode()).toString() + " -> " + con.toString() + " -> " + nodeGenes.get(con.getInNode()).toString() + "\n");
		}
		String s = sb.toString();
		return s;
	}
	
	/**
	 * @param Genome g
	 * 
	 * @return 1 if this is getter than Genome g
	 * 		   -1 if g is better than this
	 * 		   0 if this and g are equal in fitness
	 */
	@Override
	public int compareTo(Genome g) {
		// TODO Auto-generated method stub
		if (this.getAdjustedFitness() > g.getAdjustedFitness()) {
			return 1;
		} else if (this.getAdjustedFitness() < g.getAdjustedFitness()) {
			return -1;
		} else {
			return 0;
		}
	}
	
}
