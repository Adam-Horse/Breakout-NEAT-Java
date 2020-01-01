package com.adamhorse.tests;

import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import com.adamhorse.neat.ConnectionGene;
import com.adamhorse.neat.Genome;
import com.adamhorse.neat.NodeGene;
import com.adamhorse.neat.NodeGene.TYPE;

public class PrintGenome {
	
	public static void printGenome(Genome genome, String path, HashMap<Integer, Point> nodeGenePositions) {
		//double currentInYPosition;
		System.out.println("Printing genome with fitness: " + genome.getFitness());
		System.out.println();
		Random r = new Random();
		
		int nodeSize = 40;
		int imageHeight = 1024;
		int imageWidth = 1440;
		
		
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = image.getGraphics();
		Font myFont = new Font("Dialog", Font.PLAIN, 25);
		g.setFont(myFont);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, imageWidth, imageHeight);
		
		//Place inputs on left
		//Place outputs on right
		//Place hidden somewhere in middle
		Object[] nodeArray = genome.getNodeGenes().values().toArray();
		int totalInNodeCount = Genome.countNodesByType(TYPE.INPUT, genome);
		int totalOutNodeCount = Genome.countNodesByType(TYPE.OUTPUT, genome);
		//int totalHiddenNodeCount = countNodesByType(TYPE.HIDDEN, genome);
		
		int outNodeCounter = 0;
		
		for (int i = 0; i < nodeArray.length; i++) {
			//System.out.println(i + 1);
			NodeGene node = (NodeGene) nodeArray[i];
			g.setColor(Color.BLACK);
			//TODO innovation now includes 0, so nodes are not spreading correctly
			if (node.getType() == TYPE.INPUT) {
				
				double x =  nodeSize / 2.0;
				double y = (double) node.getInnovationNumber() / (double) (totalInNodeCount + 1) * imageHeight;
				g.fillOval((int) Math.round(x) - nodeSize / 2, (int) Math.round(y) - nodeSize / 2, nodeSize, nodeSize);
				nodeGenePositions.put(node.getInnovationNumber(), new Point((int) x, (int) y));
				
			} else if (node.getType() == TYPE.HIDDEN) {
				
				//Make this hidden node in between the connected nodes.
				if (!nodeGenePositions.containsKey(node.getInnovationNumber())) {
					int x = r.nextInt(imageWidth - nodeSize * 2) + nodeSize;
					int y = r.nextInt(imageHeight - nodeSize * 3) + (int) (nodeSize * 1.5);
					g.fillOval(x - nodeSize / 2, y - nodeSize / 2, nodeSize, nodeSize);
					nodeGenePositions.put(node.getInnovationNumber(), new Point(x, y));
				} else {
					g.fillOval((int) Math.round(nodeGenePositions.get(node.getInnovationNumber()).getX() - nodeSize / 2),
							   (int) Math.round(nodeGenePositions.get(node.getInnovationNumber()).getY() - nodeSize / 2), nodeSize, nodeSize);
				}
				
			} else if (node.getType() == TYPE.OUTPUT) {
				//TODO Make a node map OUTSIDE of this class so hidden node locations can be held constant
				double x = imageWidth - nodeSize;
				double y = (double) (outNodeCounter + 1) / (double) (totalOutNodeCount + 1) * imageHeight;
				//System.out.println(y);
				g.fillOval((int) Math.round(x) - nodeSize / 2, (int) Math.round(y) - nodeSize / 2, nodeSize, nodeSize);
				nodeGenePositions.put(node.getInnovationNumber(), new Point((int) x, (int) y));
				outNodeCounter++;
			}
			//Place label on nodes
			g.setColor(Color.WHITE);
			g.drawString("" + node.getInnovationNumber(), (int) nodeGenePositions.get(node.getInnovationNumber()).getX() - 3,
										    (int) nodeGenePositions.get(node.getInnovationNumber()).getY() + 4);
			
		}
		
		g.setColor(Color.BLUE);
		for (ConnectionGene con : genome.getConnectionGenes().values()) {
			//TODO Goes through list of connections, connections might be disabled in one genome, but must be enabled in the other genome
			if (!con.isExpressed()) {
				continue;
			}
			
			// nodeGenePositions.get(1) is null
			Point outNode = nodeGenePositions.get(con.getOutNode());
			Point inNode = nodeGenePositions.get(con.getInNode());
			
			//System.out.println(genome.getNodes().get(con.getOutNode()) + " -> " + genome.getNodes().get(con.getInNode()));
			//System.out.println("(" + outNode.getX() + ", " + outNode.getY()+ ")" + " -> " + "(" + inNode.getX() + ", " + inNode.getY() + ")\n");
			
			Point newInPosition = new Point((int) (((inNode.getX() - outNode.getX()) * 0.95) + outNode.getX()), (int) (((inNode.getY() - outNode.getY()) * 0.95) + outNode.getY()));
			
			Arrow.drawArrow((Graphics2D) g, outNode, newInPosition, 25);
			DecimalFormat df = new DecimalFormat("#.###");
			g.drawString("" + con.getInnovationNumber() + ", " + df.format(con.getWeight()), (int) (Math.min(outNode.getX(), inNode.getX()) + Math.abs(outNode.getX() - inNode.getX()) * 0.5 + 5),
											   (int) (Math.min(outNode.getY(), inNode.getY()) + Math.abs(outNode.getY() - inNode.getY()) * 0.5 + 5));
			
		}
		try {
			ImageIO.write(image, "PNG", new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	
	
	
	public static void printGenome(Genome genome, String path, HashMap<Integer, Point> nodeGenePositions, int Id) {
		//double currentInYPosition;
		System.out.println("Printing genome "  + Id);
		System.out.println("Fitness: " + genome.getFitness());
		System.out.println();
		Random r = new Random();
		
		int nodeSize = 40;
		int imageHeight = 1024;
		int imageWidth = 1440;
		
		
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = image.getGraphics();
		Font myFont = new Font("Dialog", Font.PLAIN, 25);
		g.setFont(myFont);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, imageWidth, imageHeight);
		
		//Place inputs on left
		//Place outputs on right
		//Place hidden somewhere in middle
		Object[] nodeArray = genome.getNodeGenes().values().toArray();
		int totalInNodeCount = Genome.countNodesByType(TYPE.INPUT, genome);
		int totalOutNodeCount = Genome.countNodesByType(TYPE.OUTPUT, genome);
		//int totalHiddenNodeCount = countNodesByType(TYPE.HIDDEN, genome);
		
		int outNodeCounter = 0;
		
		for (int i = 0; i < nodeArray.length; i++) {
			//System.out.println(i + 1);
			NodeGene node = (NodeGene) nodeArray[i];
			g.setColor(Color.BLACK);
			//TODO innovation now includes 0, so nodes are not spreading correctly
			if (node.getType() == TYPE.INPUT) {
				
				double x =  nodeSize / 2.0;
				double y = (double) node.getInnovationNumber() / (double) (totalInNodeCount + 1) * imageHeight;
				g.fillOval((int) Math.round(x) - nodeSize / 2, (int) Math.round(y) - nodeSize / 2, nodeSize, nodeSize);
				nodeGenePositions.put(node.getInnovationNumber(), new Point((int) x, (int) y));
				
			} else if (node.getType() == TYPE.HIDDEN) {
				
				//Make this hidden node in between the connected nodes.
				if (!nodeGenePositions.containsKey(node.getInnovationNumber())) {
					int x = r.nextInt(imageWidth - nodeSize * 2) + nodeSize;
					int y = r.nextInt(imageHeight - nodeSize * 3) + (int) (nodeSize * 1.5);
					g.fillOval(x - nodeSize / 2, y - nodeSize / 2, nodeSize, nodeSize);
					nodeGenePositions.put(node.getInnovationNumber(), new Point(x, y));
				} else {
					g.fillOval((int) Math.round(nodeGenePositions.get(node.getInnovationNumber()).getX() - nodeSize / 2),
							   (int) Math.round(nodeGenePositions.get(node.getInnovationNumber()).getY() - nodeSize / 2), nodeSize, nodeSize);
				}
				
			} else if (node.getType() == TYPE.OUTPUT) {
				//TODO Make a node map OUTSIDE of this class so hidden node locations can be held constant
				double x = imageWidth - nodeSize;
				double y = (double) (outNodeCounter + 1) / (double) (totalOutNodeCount + 1) * imageHeight;
				//System.out.println(y);
				g.fillOval((int) Math.round(x) - nodeSize / 2, (int) Math.round(y) - nodeSize / 2, nodeSize, nodeSize);
				nodeGenePositions.put(node.getInnovationNumber(), new Point((int) x, (int) y));
				outNodeCounter++;
			}
			//Place label on nodes
			g.setColor(Color.WHITE);
			g.drawString("" + node.getInnovationNumber(), (int) nodeGenePositions.get(node.getInnovationNumber()).getX() - 3,
										    (int) nodeGenePositions.get(node.getInnovationNumber()).getY() + 4);
			
		}
		
		g.setColor(Color.BLUE);
		for (ConnectionGene con : genome.getConnectionGenes().values()) {
			//TODO Goes through list of connections, connections might be disabled in one genome, but must be enabled in the other genome
			if (!con.isExpressed()) {
				continue;
			}
			
			// nodeGenePositions.get(1) is null
			Point outNode = nodeGenePositions.get(con.getOutNode());
			Point inNode = nodeGenePositions.get(con.getInNode());
			
			//System.out.println(genome.getNodes().get(con.getOutNode()) + " -> " + genome.getNodes().get(con.getInNode()));
			//System.out.println("(" + outNode.getX() + ", " + outNode.getY()+ ")" + " -> " + "(" + inNode.getX() + ", " + inNode.getY() + ")\n");
			
			Point newInPosition = new Point((int) (((inNode.getX() - outNode.getX()) * 0.95) + outNode.getX()), (int) (((inNode.getY() - outNode.getY()) * 0.95) + outNode.getY()));
			
			Arrow.drawArrow((Graphics2D) g, outNode, newInPosition, 25);
			DecimalFormat df = new DecimalFormat("#.###");
			g.drawString("" + con.getInnovationNumber() + ", " + df.format(con.getWeight()), (int) (Math.min(outNode.getX(), inNode.getX()) + Math.abs(outNode.getX() - inNode.getX()) * 0.5 + 5),
											   (int) (Math.min(outNode.getY(), inNode.getY()) + Math.abs(outNode.getY() - inNode.getY()) * 0.5 + 5));
			
		}
		try {
			ImageIO.write(image, "PNG", new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
}
