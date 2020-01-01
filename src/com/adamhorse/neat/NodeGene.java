package com.adamhorse.neat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeGene {
	
	public static enum TYPE {
		INPUT,
		HIDDEN,
		OUTPUT
	}
	
	public static enum FUNCTION {
		SIGMOID,
		TANH,
		RELU,
		LINEAR,
		BINARY_STEP,
		TERTIARY_STEP
	}
	
	private int innovationNumber;
	private TYPE type;
	private FUNCTION function;
	private double inputValue;
	private double outputValue;
	private double slope;
	
	private boolean readyToFire = false;
	private boolean fired = false;
	private List<ConnectionGene> connInputs;
	private Map<ConnectionGene, Boolean> recievedInputs;
	
	public NodeGene(int innovationNumber, TYPE type) {
		this.innovationNumber = innovationNumber;
		this.type = type;
		connInputs = new ArrayList<ConnectionGene>();
		recievedInputs = new HashMap<ConnectionGene, Boolean>();
		setFunction();
		
	}
	
	public NodeGene(int innovationNumber, TYPE type, FUNCTION function) {
		this.innovationNumber = innovationNumber;
		this.type = type;
		this.function = function;
		connInputs = new ArrayList<ConnectionGene>();
		recievedInputs = new HashMap<ConnectionGene, Boolean>();
		setFunction();
	}
	
	public NodeGene(int innovationNumber, TYPE type, FUNCTION function, double slope) {
		this.innovationNumber = innovationNumber;
		this.type = type;
		this.function = function;
		this.slope = slope;
		connInputs = new ArrayList<ConnectionGene>();
		recievedInputs = new HashMap<ConnectionGene, Boolean>();
		setFunction();
	}
	
	public void reset() {
		inputValue = 0;
		outputValue = 0;
	}
	
	public void setFunction() {
		switch (type) {
			case INPUT:
				function = null;
				readyToFire = true;
				break;
			case HIDDEN:
				function = FUNCTION.SIGMOID;
				break;
			case OUTPUT:
				function = FUNCTION.TERTIARY_STEP;
		}
	}
	
	public boolean isReadyToFire() {
		return readyToFire;
	}
	public boolean isFired() {
		return fired;
	}
	
	public List<ConnectionGene> getInputConns() {
		return connInputs;
	}
	
	public void addInputConn(ConnectionGene conn) {
		connInputs.add(conn);
		recievedInputs.put(conn, false);
	}
	
	public int getInnovationNumber() {
		return innovationNumber;
	}
	
	public double getOutputValue(ConnectionGene connection) {
		if (!readyToFire) {
			System.out.println("I STILL HAVEN'T RECIEVED INPUT FROM:");
			for (ConnectionGene conn : recievedInputs.keySet()) {
				if (!recievedInputs.get(conn)) {
					System.out.println(conn);
				}
			}
			throw new RuntimeException("WHY DID I NOT GET THOSE INPUTS?");
			
		}
		fired = true;
//		System.out.println("Node " + getInnovationNumber() + " Has FIRED into connection " + connection.getInnovationNumber());
		return outputValue;
	}
	public double getOutputValue() {
		if (!readyToFire) {
			System.out.println("I STILL HAVEN'T RECIEVED INPUT FROM:");
			for (ConnectionGene conn : recievedInputs.keySet()) {
				if (!recievedInputs.get(conn)) {
					System.out.println(conn);
				}
			}
			throw new RuntimeException("WHY DID I NOT GET THOSE INPUTS?");
			
		}
		fired = true;
//		System.out.println("Node " + getInnovationNumber() + " Has FIRED a value of " + outputValue + "!");
		return outputValue;
	}
	public void setInputValue(double inputValue) {
		this.inputValue = inputValue;
		calculateOutputValue();
		readyToFire = true;
	}
	
	public void addInputValue(double newInput, ConnectionGene conn) {
		inputValue += newInput * conn.getWeight();
		recievedInputs.replace(conn, false, true);
//		System.out.println("Status of node " + getInnovationNumber() + ": " + recievedInputs + "; Current Value: " + inputValue);
		boolean complete = true;
		for (Boolean recieved : recievedInputs.values()) {
			if (!recieved) {
				complete = false;
			}
		}
		if (complete) {
			calculateOutputValue();
			readyToFire = true;
		}
	}
	
	public void calculateOutputValue() {
//		System.out.println("Node " + getInnovationNumber() + " Has calculated the output and is READY TO FIRE!");
		if (function != null) {
			switch (function) {
				case SIGMOID:
					outputValue = (1.0 / (1 + Math.pow(Math.E, -inputValue)));
					break;
				case TANH:
					outputValue = Math.tanh(inputValue);
					break;
				case RELU:
					outputValue = inputValue < 0 ? 0 : inputValue;
					break;
				case LINEAR:
					outputValue = inputValue * slope;
					break;
				case BINARY_STEP:
					outputValue = inputValue < 0 ? 0 : 1;
					break;
				case TERTIARY_STEP:
					if (inputValue < -0.25) {
						outputValue = 0;
					} else if (inputValue >= -0.25 && inputValue < 0.25) {
						outputValue = 0.5;
					} else if (inputValue >= 0.25) {
						outputValue = 1;
					}
				default:
					outputValue = inputValue;
					break;
			}
		} else {
			outputValue = inputValue;
		}
//		System.out.println("Output of node " + getInnovationNumber() + ": " + outputValue);
	}
	
	public TYPE getType() {
		return type;
	}
	
	/**
	 * To make sure we don't reuse the same object
	 * @return a COPY of the NodeGene object with the same fields
	 */
	public NodeGene copy() {
		return new NodeGene(innovationNumber, type);
	}
	
	@Override
	public String toString() {
		return "(" + innovationNumber + ", " + type + ")";
	}
	
}
