package com.adamhorse.neat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Species {
	
	private List<Genome> members;
	private Genome mascot;
	private Genome bestInSpecies;
	private int ID;
	double totalAdjustedFitness;
	
	public Species(Genome mascot, int ID) {
		this.mascot = mascot;
		members = new ArrayList<Genome>();
		members.add(this.mascot);
		this.ID = ID;
	}
	
	public int getID() {
		return ID;
	}
	
	public Genome getMascot() {
		return mascot;
	}
	
	public Genome getBestInSpecies() {
		if (bestInSpecies != null) {
			return bestInSpecies;
		} else {
			throw new RuntimeException("You haven't run the sorting method to find the best species yet!");
		}
	}
	
	public void addMember(Genome newMember) {
		members.add(newMember);
	}
	
	public List<Genome> getMembers() {
		return members;
	}
	
	public void calculateAdjustedFitnesses() {
		totalAdjustedFitness = 0;
		for (Genome member : members) {
			member.setAdjustedFitness(member.getFitness() / (double) members.size());
			totalAdjustedFitness += member.getAdjustedFitness();
		}
	}
	
	public void sortAndIdentifyBest() {
		Collections.sort(members);
		Collections.reverse(members);
		bestInSpecies = members.get(0);
	}
	
	public double getTotalAdjustedFitness() {
		return totalAdjustedFitness;
	}
	
	public void reset(Random r) {
		int newMascotIndex = r.nextInt(members.size());
		this.mascot = members.get(newMascotIndex);
		members.clear();
		members.add(this.mascot);
		//System.out.println("Species reset");
		totalAdjustedFitness = 0;
	}
	
}
