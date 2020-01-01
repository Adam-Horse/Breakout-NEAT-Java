package com.adamhorse.neat;

import java.text.DecimalFormat;

public class ConnectionGene {
	
	private int innovationNumber;
	private double weight;
	private int outNodeID;
	private int inNodeID;
	private boolean expressed;
	
	private DecimalFormat df = new DecimalFormat("#.###");
	
	public ConnectionGene(int innovationNumber,
						  double weight,
						  int outNodeID,
						  int inNodeID,
						  boolean expressed) {
		
		this.innovationNumber = innovationNumber;
		this.weight = weight;
		this.outNodeID = outNodeID;
		this.inNodeID = inNodeID;
		this.expressed = expressed;
		
	}
	
	public int getInnovationNumber() {
		return innovationNumber;
	}

	public double getWeight() {
		return weight;
	}
	
	public void setWeight(double newWeight) {
		this.weight = newWeight;
	}

	public int getOutNode() {
		return outNodeID;
	}

	public int getInNode() {
		return inNodeID;
	}

	public boolean isExpressed() {
		return expressed;
	}
	
	public void disable() {
		expressed = false;
	}
	
	public void enable() {
		expressed = true;
	}
	
	public ConnectionGene copy() {
		return new ConnectionGene(innovationNumber, weight, outNodeID, inNodeID, expressed);
	}
	
	@Override
	public String toString() {
		if (expressed) {
			return "(" + innovationNumber + ", " + df.format(getWeight()) + ")";
		} else if (!expressed) {
			return "(" + innovationNumber + ", " + df.format(getWeight()) + ", DISABLED)";
		}
		return "SOMETHING WENT WRONG";
	}
	
}
