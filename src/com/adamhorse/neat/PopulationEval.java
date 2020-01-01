package com.adamhorse.neat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class PopulationEval {
	private Random r = new Random();
	private int populationSize;
	private double highestFitness = Double.MIN_VALUE;
	private Genome bestGenome = null;
	private Species mostPopulatedSpecies;
	private Species bestSpecies;
	
	private int genCounter = 0;
	
	private Innovation connectionInnovation;
	private Innovation nodeInnovation;
	
	private Innovation speciesCounter = new Innovation();
	
	private Map<Genome, Species> speciesMap;
	private List<Genome> genomes;
	private List<Species> speciesList;
	private List<Genome> nextGenGenomes;
	private List<Genome> previousGenomes;
	
	private List<Integer> genomesToDie;
	
	private List<Genome> topFiveGenomes;
	
	private final double MAX_DISTANCE = 15;
	private final double INTERSPECIES_MATING_RATE = 0.01;
	private final double WEIGHT_MUTATION_RATE = 0.8;
	private final double ADD_CONNECTION_RATE_LARGER_SPECIES = 0.3;
	private final double ADD_CONNECTION_RATE = 0.2;
	private final double ADD_NODE_RATE = 0.2;
	private final double C1 = 1.0;
	private final double C2 = 1.0;
	private final double C3 = 0.4;
	
	public PopulationEval(int populationSize, Genome startingGenome, Innovation nodeInnovation, Innovation connectionInnovation) {
		this.populationSize = populationSize;
		this.nodeInnovation = nodeInnovation;
		this.connectionInnovation = connectionInnovation;
		this.genomes = new ArrayList<Genome>(populationSize);
		bestGenome = new Genome(startingGenome);
		
		//myWriter.write("Same Object? " + startingGenome.equals(startingGenome));
		for (int i = 0; i < populationSize; i++) {
			this.genomes.add(new Genome(startingGenome));
			//myWriter.write("Added " + genomes.get(i));
		}
		this.speciesMap = new HashMap<Genome, Species>(populationSize);
		this.speciesList = new ArrayList<Species>();
		this.nextGenGenomes = new ArrayList<Genome>();
		this.genomesToDie = new ArrayList<Integer>();
		this.topFiveGenomes = new ArrayList<Genome>();
	}
	
	public List<Genome> getTopFive() {
		return topFiveGenomes;
	}
	
	public Species getBestSpecies() {
		return bestSpecies;
	}
	
	public int getSpeciesSize() {
		return speciesList.size();
	}
	
	public double getHighestFitness() {
		return highestFitness;
	}
	
	public Genome getBestGenome() {
		return bestGenome;
	}
	
	public List<Genome> getGenomes() {
		return genomes;
	}
	
	public List<Genome> getPreviousGenomes() {
		return previousGenomes;
	}
	
	abstract protected double evaluateGenome(Genome g);
	
	public void evaluate() throws IOException {
		
		previousGenomes = new ArrayList<Genome>();
		
		File myObj = new File("Generation " + genCounter + ".txt");
		myObj.createNewFile();
		FileWriter myWriter = new FileWriter("Generation " + genCounter + ".txt");
		
		for (Species s : speciesList) {
			s.reset(r);
		}
		myWriter.write("NEW GENERATION");
		myWriter.write("\n");
		
		speciesMap.clear();
		nextGenGenomes.clear();
		genomesToDie.clear();
		//Place genomes in species
		for (Genome g : genomes) {
			//g.checkIfValid();
			boolean foundSpecies = false;
			for (Species s : speciesMap.values()) {
				
				//check how close the genome is to each mascot until it finds one close enough to join the species
				if (Genome.compatabilityDistance(g, s.getMascot(), C1, C2, C3) < MAX_DISTANCE) {
					s.addMember(g);
					speciesMap.put(g, s);
					//It found the right species!
					foundSpecies = true;
					//myWriter.write("Genome added to species " + s.getID());
					break;
				}
				
			}
			//It didn't find any species to belong... So make a new one with this as its mascot!
			if (!foundSpecies) {
				Species newSpecies = new Species(g, speciesCounter.generateInnovation());
				speciesList.add(newSpecies);
				speciesMap.put(g, newSpecies);
				myWriter.write("A new species, species " + newSpecies.getID() + ",  has been added!\n");
			}
			
		}
		
		//Now give each genome a fitness
		for (Genome g : genomes) {
			double fitnessScore = evaluateGenome(g);
			g.setFitness(fitnessScore);
			if (fitnessScore > highestFitness) {
				highestFitness = fitnessScore;
				bestGenome = g;
				myWriter.write("Found a new best genome:\n");
				myWriter.write(g.toString() + "\n");
			}
		}
		
		//This loop does a LOT
		mostPopulatedSpecies = speciesList.get(0);
		bestSpecies = speciesList.get(0);
		for (Species s : speciesList) {
			//Now have each species evaluate the fitness of its members
			s.calculateAdjustedFitnesses();
			s.sortAndIdentifyBest();
			Genome bestInSpecies = s.getBestInSpecies();
			//Find most populated species
			if (s.getMembers().size() > mostPopulatedSpecies.getMembers().size()) {
				mostPopulatedSpecies = s;
			}
			if (s.getTotalAdjustedFitness() > bestSpecies.getTotalAdjustedFitness()) {
				bestSpecies = s;
			}
			myWriter.write("Species " + s.getID() + " size: " + s.getMembers().size());
			myWriter.write("\n");
			myWriter.write("Best in species " + s.getID() + ": \n" + bestInSpecies);
		}
		//We need to sort by adjusted fitness to punish similar solutions
		//TODO Just add rankings to the genome class
		Collections.sort(genomes);
		Collections.reverse(genomes);
		for (int i = 0; i < 5; i++) {
			topFiveGenomes.add(genomes.get(i));
		}
		
		//Place current genomes into history
		for (Genome g : genomes) {
			//COPY the genomes
			previousGenomes.add(g.copy());
		}
		
		//Now kill about half of the population. The probability of death should be a gradient based on fitness
		double increment = 1.0 / populationSize;
		for (int i = 0; i < populationSize; i++) {
			double probabilityOfSurvival = 1 - (increment * i);
			if (r.nextDouble() > probabilityOfSurvival) {
				genomes.get(i).mark();
			}
		}
		
		Iterator<Genome> iter = genomes.iterator();
		while(iter.hasNext()) {
			Genome g = iter.next();
			if (g.willDie()) {
				iter.remove();
			}
		}
		myWriter.write("Size after butchering: " + genomes.size());
		myWriter.write("\n");
		
		//Now reproduce
		//Depending on how well each SPECIES does, that should determine how much it repopulates
		//In the rare case that a species does NOT improve over twenty generations, only let the TOP TWO reproduce
		while (nextGenGenomes.size() < populationSize) {
//			myWriter.write("New child");
//			System.out.println("NEW CHILD");
			Species s = null;
			Genome p1;
			Genome p2;
			Genome child;
			
			//A 0.001 chance that species intermate
			if (r.nextDouble() < INTERSPECIES_MATING_RATE) {
				p1 = getRandomGenomeBasedOnFitness();
				p2 = getRandomGenomeBasedOnFitness();
			} else {
				s = getRandomSpeciesBasedOnFitness();
				p1 = getRandomGenomeBasedOnFitness(s);
				p2 = getRandomGenomeBasedOnFitness(s);
			}
			
			child = Genome.crossover(p1, p2, r);
			
			//Weight mutation
			if (r.nextDouble() < WEIGHT_MUTATION_RATE) {
				child.weightMutation(r);
				myWriter.write("Weight mutated in child");
				myWriter.write("\n");
			}
			//Connection Mutation
			if (s != null && s.getID() == mostPopulatedSpecies.getID()) {
				if (r.nextDouble() < ADD_CONNECTION_RATE_LARGER_SPECIES) {
					if (child.getUnconnectedNodes().size() > 0) {
						child.addConnectionMutation(r, connectionInnovation);
						myWriter.write("Connnection mutated in child in largest species");
						myWriter.write("\n");
					} else {
						myWriter.write("There were no unconnected nodes to connect in this network!");
						myWriter.write("\n");
					}
				}
			} else {
				if (child.getUnconnectedNodes().size() > 0) {
					if (r.nextDouble() < ADD_CONNECTION_RATE) {
						child.addConnectionMutation(r, connectionInnovation);
						myWriter.write("Connnection mutated in child in a smaller species");
						myWriter.write("\n");
					}
		 		} else {
					myWriter.write("There were no unconnected nodes to connect in this network!");
					myWriter.write("\n");
				}
			}
			//Node mutation - this mutation should be difficult, as new nodes 
			if (r.nextDouble() < ADD_NODE_RATE) {
				child.addNodeMutation(r, nodeInnovation, connectionInnovation);
				myWriter.write("Node mutated in child");
				myWriter.write("\n");
			}
			
			nextGenGenomes.add(child);
			myWriter.write("Index of new child: " + nextGenGenomes.indexOf(child));
			myWriter.write("\n");
			myWriter.write("\n");
			//myWriter.write();
			
		}
		
		myWriter.close();
		
		if (nextGenGenomes.size() == populationSize || nextGenGenomes.size() == 0) {
			genomes = nextGenGenomes;
			nextGenGenomes = new ArrayList<Genome>();
		} else {
			throw new RuntimeException("nextGenGenomes isn't the right size!!!");
		}
		genCounter++;
		
	}
	
	// sum of probabilities of selecting each genome - selection is more probable for genomes with higher fitness
	public Genome getRandomGenomeBasedOnFitness(Species s) {
		double completeWeight = 0.0;
		for (Genome g : s.getMembers()) {
			completeWeight += g.getFitness();
		}
        double r = Math.random() * completeWeight;
        double countWeight = 0.0;
        for (Genome g : s.getMembers()) {
            countWeight += g.getFitness();
            if (countWeight >= r) {
            	 return g;
            }
        }
        throw new RuntimeException("Couldn't find a genome... Number is genomes in selected species is " + s.getMembers().size() + ", and the total adjusted fitness is " + completeWeight);
	}
	
	//On the off chance that there is interspecies mating, just choose randomly from the population.
	public Genome getRandomGenomeBasedOnFitness() {
		double completeWeight = 0.0;
		for (Genome g : genomes) {
			completeWeight += g.getFitness();
		}
        double r = Math.random() * completeWeight;
        double countWeight = 0.0;
        for (Genome g : genomes) {
            countWeight += g.getFitness();
            if (countWeight >= r) {
            	 return g;
            }
        }
        throw new RuntimeException("Couldn't find a genome... Number is genomes in selected species is " + genomes.size() + ", and the total adjusted fitness is " + completeWeight);
	}
	
	// sum of probabilities of selecting each genome - selection is more probable for genomes with higher fitness
	public Species getRandomSpeciesBasedOnFitness() {
		double completeWeight = 0.0;
		for (Species s : speciesList) {
			completeWeight += s.getTotalAdjustedFitness();
		}
        double r = Math.random() * completeWeight;
        double countWeight = 0.0;
        for (Species s : speciesList) {
            countWeight += s.getTotalAdjustedFitness();
            if (countWeight >= r) {
            	 return s;
            }
        }
        throw new RuntimeException("Couldn't find a species... Number is species is " + speciesList.size() + ", and the total adjusted fitness is " + completeWeight);
	}
	
}
