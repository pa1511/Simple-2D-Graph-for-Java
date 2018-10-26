package main;

import java.awt.Color;
import java.util.Random;

import graph.SimpleGraph;

public class FunctionalityExampleMain {
	
	public static void main(String[] args) throws InterruptedException {
		
		//Create the graph object
		SimpleGraph graph = new SimpleGraph();
		
		//Launch a window showing the graph
		graph.display();
		Thread.sleep(1000);//so you can observe the changes in the graph

		//Utility
		Random random = new Random();
		
		//================================================================
		//Adding points to the graph
		
		//Add first set of points to the graph
		for(int i=0; i<10; i++) {
			double x = random.nextGaussian()*2;
			double y = random.nextGaussian()*2;
			graph.addPoint(x, y);//default color is RED
		}
		graph.repaint();
		Thread.sleep(1000);
		
		//Add second set of points to the graph
		for(int i=0; i<10; i++) {
			double x = random.nextGaussian()*2;
			double y = random.nextGaussian()*2;
			graph.addPoint(x, y,Color.GREEN);
		}
		graph.repaint();
		Thread.sleep(1000);
		
		//Remove all points from the graph
		graph.clearPoints();
		graph.repaint();
		Thread.sleep(1000);
		
		//================================================================
		//Plotting a function on the graph
		
		//Adding first function to the graph
		graph.addFunction(x->x);
		graph.repaint();
		Thread.sleep(1000);
		
		//Adding second function to the graph
		graph.addFunction(x->Math.sin(x),Color.ORANGE);
		graph.repaint();
		Thread.sleep(1000);
		
		//Removing all functions from the graph
		graph.clearFunctions();
		graph.repaint();
		Thread.sleep(1000);

		//================================================================
		//Adding shapes to the graph
		
		//Adding a line to the graph
		graph.addShape(new SimpleGraph.Line(0, 0, 5, 5, Color.BLUE));
		graph.repaint();
		Thread.sleep(1000);
		
		//Adding a circle to the graph
		graph.addShape(new SimpleGraph.Circle(0, 0, 5, Color.BLUE));
		graph.repaint();
		Thread.sleep(1000);
		
		//Adding a Polyline to the graph
		double[][] points = new double[][] {
			{0,0},
			{1,1},
			{2,2},
			{2,3},
			{1,3},
			{0,3}
		};
		graph.addShape(new SimpleGraph.PolyLine(Color.BLUE,points));
		graph.repaint();
		Thread.sleep(1000);

		//Removing all shapes from the graph
		graph.removeAllShapes();
		graph.repaint();
		Thread.sleep(1000);		
		
		//================================================================
		//Centering the graph around the coordinate system source
		graph.centralize();
		graph.repaint();
		Thread.sleep(1000);		
		
		//It is also possible to save the graph
		//File image = new File("graph.png");
		//graph.save(image);
	}

}
