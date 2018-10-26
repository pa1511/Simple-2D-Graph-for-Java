package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import graph.SimpleGraph;

public class GraphInSwingAppExampleMain {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(()->{
			
			//Utility
			Random random = new Random();
			
			//Define graph
			SimpleGraph graph = new SimpleGraph();
			graph.centralize();
			
			//Command buttons
			JButton button1 = new JButton(new AbstractAction("Sin") {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					double a = random.nextDouble();
					double b = random.nextDouble();
					graph.addFunction(x->a*Math.sin(x)+b,new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
					graph.repaint();
				}
			});
			JButton button2 = new JButton(new AbstractAction("Cos") {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					double a = random.nextDouble();
					double b = random.nextDouble();
					graph.addFunction(x->a*Math.cos(x)+b,new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
					graph.repaint();
				}
			});
			JButton button3 = new JButton(new AbstractAction("Clear") {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					graph.clearFunctions();
					graph.repaint();
				}
			});
			
			//Command panel
			JPanel commandPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			commandPanel.add(button1);
			commandPanel.add(button2);
			commandPanel.add(button3);
			
			//Application frame
			JFrame frame = new JFrame("My application");
			frame.setLayout(new BorderLayout());
			frame.add(graph,BorderLayout.CENTER);
			frame.add(commandPanel,BorderLayout.SOUTH);
			
			
			//Frame general settings
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setBounds(100, 100, 400, 400);
			
			//Make frame visible
			frame.setVisible(true);
		});
	}
	
}
