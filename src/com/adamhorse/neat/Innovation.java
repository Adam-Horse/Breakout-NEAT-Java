package com.adamhorse.neat;

public class Innovation {
	
	private int counter;
	
	public Innovation() {
		counter = 0;
	}
	
	/**
	 * Counts up the innovation and returns the new current innovation
	 * @return the new current innovation
	 */
	public int generateInnovation() {
		this.counter++;
		return this.counter;
	}
	
	/**
	 * @return the current innovation WITHOUT increasing it
	 */
	public int currentInnovation() {
		return this.counter;
	}
	
}
