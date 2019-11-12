package graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleUnaryOperator;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import dataModels.DPoint;
import dataModels.Pair;
import utility.ResourceLoader;
import utility.CommonUIActions;

/**
 * A simple interactive 2D graph implementation. 
 * @author paf
 */
public class SimpleGraph extends JPanel {
	
	private double shiftX = 0.025;
	private double shiftY = 0.025;
	//
	private double precision = 0.001;
	//
	//
	private double maxValueX;
	private double maxValueY;
	
	private boolean showGrid = true;
	private double gridSpreadX;
	private double gridSpreadY;
	//
	private int pointSize = 10;
	//
	private boolean showTicks = true;
	private int tickSize = 8;	
	//
	private boolean showAxis = true;
	//
	private final Map<Color,List<DPoint>> points = new HashMap<>();
	private final List<Pair<DoubleUnaryOperator, Color>> functions = new ArrayList<>();
	private final List<IGraphShape> shapes = new ArrayList<>();
	
	//
	private final DecimalFormat decimalFormat = new DecimalFormat("#.##");
	//

	private static Icon checkIcon;
	private static Icon crossIcon;
	static {
		try {
			checkIcon = ResourceLoader.loadImageIcon(SimpleGraph.class, "check.png",16,16);
			crossIcon = ResourceLoader.loadImageIcon(SimpleGraph.class, "cross.png",16,16);
		} catch (IOException e) {
			checkIcon = null;
			crossIcon = null;
		}
	}
	//
	private boolean interactiveMode = false;
	
	private boolean canDrag = true;
	
	private boolean canZoomInOut = true;

	/**
	 * Creates a simple graphs where the step on each axis is 1
	 * The maximum visible span of the x axis is set to 10. 
	 * The maximum visible span of the y axis is set to 10. 
	 */
	public SimpleGraph() {
		this(10,10);
	}	

	/**
	 * Creates a simple graphs where the step on each axis is 1
	 * @param maxValueX -  maximum visible span of the x axis 
	 * @param maxValueY -  maximum visible span of the y axis
	 */
	public SimpleGraph(double maxValueX, double maxValueY) {
		this(maxValueX,maxValueY,1.0,1.0);
	}	
	
	/**
	 * Indicate should the grid be shown
	 * @param showGrid
	 */
	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
		repaint();
	}
	
	/**
	 * Indicate should the thick on the axis be shown
	 * @param showTicks
	 */
	public void setShowTicks(boolean showTicks) {
		this.showTicks = showTicks;
		repaint();
	}
	
	/**
	 * Indicate should the axis be show
	 * @param showAxis
	 */
	public void setShowAxis(boolean showAxis) {
		this.showAxis = showAxis;
		repaint();
	}
	
	/**
	 * Indicate the activation of interactive mode
	 * @param interactiveMode
	 */
	public void setInteractiveMode(boolean interactiveMode) {
		this.interactiveMode = interactiveMode;
	}
	
	/**
	 * Indicate can the graph be draged
	 */
	public void setCanDrag(boolean canDrag) {
		this.canDrag = canDrag;
	}
	
	/**
	 * Indicate can the graph be zoomed in/out
	 */
	public void setCanZoomInOut(boolean canZoomInOut) {
		this.canZoomInOut = canZoomInOut;
	}
	
	/**
	 * 
	 * @param maxValueX -  maximum visible span of the x axis 
	 * @param maxValueY -  maximum visible span of the y axis
	 * @param gridSpreadX - step for the x axis
	 * @param gridSpreadY - step for the y axis
	 */
	public SimpleGraph(double maxValueX, double maxValueY, double gridSpreadX, double gridSpreadY) {
		this.maxValueX = maxValueX;
		this.maxValueY = maxValueY;
				
		this.gridSpreadX = gridSpreadX;
		this.gridSpreadY = gridSpreadY;
		
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(700, 700));
		
		JPopupMenu popupMenu = new JPopupMenu("Menu");
		JMenuItem showGridMenuItem = popupMenu.add(new JMenuItem(new AbstractAction("Show grid") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showGrid = !showGrid;
				repaint();
			}
		}));
		
		JMenuItem showTicksMenuItem = popupMenu.add(new JMenuItem(new AbstractAction("Show ticks") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showTicks = !showTicks;
				repaint();
			}
		}));

		JMenuItem showAxisMenuItem = popupMenu.add(new JMenuItem(new AbstractAction("Show axis") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showAxis = !showAxis;
				repaint();
			}
		}));
		
		JMenuItem canDragMenuItem = popupMenu.add(new JMenuItem(new AbstractAction("Can drag") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				canDrag = !canDrag;
			}
		}));

		JMenuItem canZoomInOutMenuItem = popupMenu.add(new JMenuItem(new AbstractAction("Can zoom in/out") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				canZoomInOut = !canZoomInOut;
			}
		}));
		
		JMenuItem interactiveModeMenuItem = popupMenu.add(new JMenuItem(new AbstractAction("Enable adding points") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				interactiveMode = !interactiveMode;
				repaint();
			}
		}));

		
		popupMenu.add(new JMenuItem(new AbstractAction("Centralize") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				centralize();
				repaint();
			}
		}));		
		popupMenu.add(new JMenuItem(new AbstractAction("Graph settings") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
								
				SettingsPanel settingsPanel = new SettingsPanel();
				
				int option = JOptionPane.showConfirmDialog(SimpleGraph.this, settingsPanel, "Graph settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if(option==JOptionPane.OK_OPTION){
					settingsPanel.updateGraph();
				}
				
			}
		}));
		popupMenu.add(new JMenuItem(new CommonUIActions.SelectFile("Save as PNG",System.getProperty("user.home"),true) {		
			@Override
			public void doWithSelectedDirectory(File selectedFile) {
				try {
					if(!selectedFile.getName().endsWith(".png"))
						selectedFile = new File(selectedFile.getAbsolutePath()+".png");
					save(selectedFile);
					JOptionPane.showMessageDialog(SimpleGraph.this, "Image successfully saved", "Information", JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(SimpleGraph.this, "An error occured during saving.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}));
		
		popupMenu.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				showGridMenuItem.setIcon(showGrid ?  crossIcon : checkIcon);
				showTicksMenuItem.setIcon(showTicks ? crossIcon : checkIcon);
				showAxisMenuItem.setIcon(showAxis ? crossIcon : checkIcon );
				canDragMenuItem.setIcon(canDrag ? crossIcon : checkIcon );
				canZoomInOutMenuItem.setIcon(canZoomInOut ? crossIcon : checkIcon );
				interactiveModeMenuItem.setIcon(interactiveMode ? crossIcon : checkIcon);
				
				showGridMenuItem.setText(showGrid ? "Hide grid" : "Show grid");
				showTicksMenuItem.setText(showTicks ? "Hide ticks" : "Show ticks");
				showAxisMenuItem.setText(showAxis ? "Hide Axis" : "Show axis" );
				canDragMenuItem.setText(canDrag ? "Disable drag" : "Enable drag");
				canZoomInOutMenuItem.setText(canZoomInOut ? "Disable zoom" : "Enable zoom");
				interactiveModeMenuItem.setText(interactiveMode ? "Disable adding points" : "Enable adding points");
			}
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				//
			}
			
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				//
			}
		});

		setComponentPopupMenu(popupMenu);
		
		//TODO: this could be changed to a state pattern to easily expand graph functionality 
		MouseAdapter mouseInputHandler = new MouseAdapter() {
			
			private double startShiftX;
			private double startShiftY;
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.isPopupTrigger()){
					Point p = e.getPoint();
					popupMenu.show(SimpleGraph.this, p.x, p.y);
				}
				else {
					//TODO: add other interaction possibilities 
					if(interactiveMode) {
						Point p = e.getPoint();
						
						double width = getWidth();
						double height = getHeight();
						
						double b = p.getY()/height;
						double d = p.getX()/width;
						
						double startX = getXValueFor(d);
						double startY = getYValueFor(b);
						
						addPoint(startX, startY);
						
						repaint();
						//TODO: allow subscription to this kind of event
					}
				}
				super.mouseClicked(e);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(canDrag) {
					if(SwingUtilities.isLeftMouseButton(e)){
						int width = getWidth();
						int height = getHeight();
						
						Point point = e.getPoint();
						
						startShiftX = point.getX()/width - shiftX;
						startShiftY = 1.0-point.getY()/height - shiftY;
	
						
						repaint();
					}
				}
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if(canDrag) {
					if(SwingUtilities.isLeftMouseButton(e)){
						
						int width = getWidth();
						int height = getHeight();
						
						Point point = e.getPoint();
						
						shiftX = point.getX()/width-startShiftX;
						shiftY = 1.0-point.getY()/height-startShiftY;
											
						repaint();
					}
				}
			}
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {				
				if(!canZoomInOut)
					return;
				
				int rotation = e.getWheelRotation();

				Point point = e.getPoint();
				double width = getWidth();
				double height = getHeight();
				
				double b = point.getY()/height;
				double d = point.getX()/width;
				
				double startX = getXValueFor(d);
				double startY = getYValueFor(b);
								
				//
				double moveX = rotation*SimpleGraph.this.maxValueX*0.1;
				SimpleGraph.this.maxValueX += moveX;
				double moveY = rotation*SimpleGraph.this.maxValueY*0.1;
				SimpleGraph.this.maxValueY += moveY;
				//
				
				shiftX = d-startX/SimpleGraph.this.maxValueX;
				shiftY = 1.0-b-startY/SimpleGraph.this.maxValueY;
				
				repaint();
			}

		};
		addMouseListener(mouseInputHandler);
		addMouseMotionListener(mouseInputHandler);
		addMouseWheelListener(mouseInputHandler);		
	}

	/**
	 * Removes all functions from the graph. <br/>
	 * The graph <b> WILL NOT </b> automatically repaint. 
	 */
	public void clearFunctions(){
		functions.clear();
	}	
	
	/**
	 * Removes all points from the graph. <br/>
	 * The graph <b> WILL NOT </b> automatically repaint. 
	 */
	public void clearPoints() {
		points.clear();
	}

	/**
	 * Adds the given function to the graph to be plotted. <br/>
	 * The function will be displayed in blue color. <br/>
	 * The graph <b> WILL NOT </b> automatically repaint. 
	 */
	public void addFunction(@Nonnull DoubleUnaryOperator function){
		addFunction(function, Color.BLUE);
	}

	/**
	 * Adds the given function to the graph to be plotted. <br/>
	 * The function will be displayed in the specified color. <br/>
	 * The graph <b> WILL NOT </b> automatically repaint. 
	 */
	public void addFunction(@Nonnull DoubleUnaryOperator function,@Nonnull Color color){
		functions.add(Pair.of(function, color));
	}

	/**
	 * Adds the given point to the graph. <br/>
	 * This array needs to have a length of 2 or more otherwise adding the point will cause an exception. <br/>
	 * If the length is larger then 2 then only the 2 first elements are considered. <br/>
	 * They are considered the x and y coordinate of the point. <br/>
	 * The point is displayed in red color. <br/>
	 * The graph <b> WILL NOT </b> automatically repaint. 
	 */
	public void addPoint(double[] point) {
		addPoint(point[0],point[1]);
	}
	
	/**
	 * Adds the given point to the graph. <br/>
	 * The point is displayed in red color. <br/>
	 * The graph <b> WILL NOT </b> automatically repaint. 
	 */
	public void addPoint(double x,double y){
		addPoint(x,y,Color.RED);
	}

	/**
	 * Adds the given point to the graph. <br/>
	 * The point is displayed in the specified color. <br/>
	 * The graph <b> WILL NOT </b> automatically repaint. 
	 */
	public void addPoint(double x,double y,@Nonnull Color color){
		DPoint point = new DPoint(x, y);
		addPoint(point, color);
	}

	/**
	 * Adds the given point to the graph. <br/>
	 * The point dimension should be at least 2. <br/>
	 * The point is displayed in the specified color. <br/>
	 * The graph <b> WILL NOT </b> automatically repaint. 
	 */
	public void addPoint(DPoint point, Color color){		
		List<DPoint> groupPoints = points.get(color);
		if(groupPoints==null){
			groupPoints = new ArrayList<>();
			points.put(color, groupPoints);
		}
		
		groupPoints.add(point);
	}
	
	/**
	 * Adds the given points and shows them in the specified color. 
	 * The graph <b> WILL NOT </b> automatically repaint. 
	 */
	public void addPointGroup(List<DPoint> group, Color groupColor){
		List<DPoint> existingGroupPoints = points.get(groupColor);
		if(existingGroupPoints==null){
			points.put(groupColor, group);
		}
		else{
			existingGroupPoints.addAll(group);
		}
	}
	
	/**
	 * Returns all the points on the graph
	 */
	public Map<Color, List<DPoint>> getPoints() {
		return points;
	}
	
	/**
	 * Adds the given shape to the graph. <br/>
	 * The graph <b> WILL NOT </b> automatically repaint. 
	 */
	public void addShape(IGraphShape graphShape) {
		shapes.add(graphShape);
	}
	
	/**
	 * Removes the given shape from the graph. <br/>
	 * The graph <b> WILL NOT </b> automatically repaint. 
	 */
	public void removeShape(IGraphShape graphShape) {
		shapes.remove(graphShape);
	}
	
	/**
	 * Removes all shapes from the graph. <br/>
	 * The graph <b> WILL NOT </b> automatically repaint. 
	 */
	public void removeAllShapes() {
		shapes.clear();
	}

	/**
	 * Centers the graph around the coordinate system center. <br/>
	 * The graph <b> WILL NOT </b> automatically repaint. 
	 */
	public void centralize() {
		shiftX = 0.5;
		shiftY = 0.5;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Color c = g.getColor();
		int width = getWidth();
		int height = getHeight();
		
		int xCoordinateHeight = (int)((1.0-shiftY)*height);
		int yCoordinateWidth = (int)(shiftX*width);
		int yCoordinateStartHeight = (int)(0.5*shiftY*height);

		double upperXLimit = (1.0-shiftX)*maxValueX;
		double upperYLimit = (1.0-shiftY)*maxValueY;

		//=================================================================================
		// creating grid 
		if(showGrid){
			g.setColor(Color.LIGHT_GRAY);
			
			double start1 = (int)((upperXLimit-maxValueX)/gridSpreadX)*gridSpreadX;
			for(double xValue=start1;xValue<=upperXLimit;xValue+=gridSpreadX){
				int x = calculateX(yCoordinateWidth, xValue/maxValueX);
				g.drawLine(x, 0, x, height);
			}
			
			double start3 = (int)((upperYLimit-maxValueY)/gridSpreadY)*gridSpreadY;
			for(double yValue=start3;yValue<=upperYLimit;yValue+=gridSpreadY){
				int y = calculateY(yCoordinateStartHeight, yValue/maxValueY);
				g.drawLine(0, y, width, y);
			}
			
			g.setColor(c);
		}

		//=================================================================================
		// creating the coordinates 
		if(showAxis) {
			g.setColor(Color.BLACK);
			
			//X axis
			if(xCoordinateHeight>0 && xCoordinateHeight<height)
				g.drawLine(0, xCoordinateHeight, width, xCoordinateHeight);

			//Y axis
			if(yCoordinateWidth>0 && yCoordinateWidth<width)
				g.drawLine(yCoordinateWidth, 0, yCoordinateWidth, height);
		}
		
		if(showTicks){

			//X axis ticks
			double start1 = (int)((upperXLimit-maxValueX)/gridSpreadX)*gridSpreadX;
			for(double p=start1;p<=upperXLimit;p+=gridSpreadX){
				int x = calculateX(yCoordinateWidth, p/maxValueX);
				g.drawLine(x, xCoordinateHeight-tickSize/2, x, xCoordinateHeight+tickSize/2);
				g.drawString(decimalFormat.format(p), x-tickSize, xCoordinateHeight-tickSize);
			}
			//Y axis ticks
			double start3 = (int)((upperYLimit-maxValueY)/gridSpreadY)*gridSpreadY;
			for(double p=start3;p<=upperYLimit;p+=gridSpreadY){
				int y = calculateY(yCoordinateStartHeight, p/maxValueY);
				g.drawLine(yCoordinateWidth-tickSize/2, y, yCoordinateWidth+tickSize/2, y);
				g.drawString(decimalFormat.format(p), yCoordinateWidth+tickSize, y+tickSize/2);
			}
		}
		
		g.setColor(c);
		
		//=================================================================================
		//Plotting points 
		for(Map.Entry<Color, List<DPoint>> pointGroup:points.entrySet()){
			g.setColor(pointGroup.getKey());
			for(DPoint point:pointGroup.getValue()){
				
				double px = point.getActualCoordinates()[0];
				double py = point.getActualCoordinates()[1];
				
				int x = calculateX(yCoordinateWidth, px/maxValueX);
				//small optimization so that we don't plot point which are not visible
				if(x<0 || x>width)
					continue;
				
				int y = calculateY(yCoordinateStartHeight, py/maxValueY);
				//small optimization so that we don't plot point which are not visible
				if(y<0 || y>height)
					continue;
				
				g.fillOval(x-pointSize/2, y-pointSize/2, pointSize, pointSize);
			}
		}

		//=================================================================================
		//Plotting function
		for(Pair<DoubleUnaryOperator,Color> functionPair:functions){
			g.setColor(functionPair.right());
			DoubleUnaryOperator operator = functionPair.left();

			double start = -1.0-shiftX;
			double end = 1.0-shiftX;

			int[] xPoints = new int[(int)((end-start)/precision)];
			int[] yPoints = new int[xPoints.length];
			int i=0;
			for(double p=start;p<=end && i<xPoints.length;p+=precision){
				int x = calculateX(yCoordinateWidth, p);
				if(x<-1 || x>width+1)//-1 and +1 so the function line is not visibly connected 
					continue;
				
				double relativeY = operator.applyAsDouble(p*maxValueX)/maxValueY;
				int y = calculateY(yCoordinateStartHeight, relativeY);
				if(y<-1 || y>height+1)//-1 and +1 so the function line is not visibly connected at the top/bottom of the graph
					continue;
				
				xPoints[i] = x;
				yPoints[i] = y;
				i++;
			}
			
			g.drawPolyline(xPoints, yPoints, i);
			
		}

		//=================================================================================
		//Plotting shapes
		
		for(IGraphShape shape:shapes) {
			shape.draw(g, x->calculateX(yCoordinateWidth, x/maxValueX), y->calculateY(yCoordinateStartHeight, y/maxValueY));
		}
		
		//=================================================================================

		g.setColor(c);
	}
	
	private double getYValueFor(double percentageOfHeight) {
		return (1.0-shiftY-percentageOfHeight)*SimpleGraph.this.maxValueY;
	}

	private double getXValueFor(double percentageOfWidth) {
		return (percentageOfWidth-shiftX)*SimpleGraph.this.maxValueX;
	}

	private int calculateY(int yCoordinateStartHeight, double value) {
		int height = getHeight();
		return (int)((1.0-value)*height)-2*yCoordinateStartHeight;
	}

	private int calculateX(int yCoordinateWidth, double value) {
		int width = getWidth();
		return (int)(value*width)+yCoordinateWidth;
	}
		
	//=============================================================================================================
	//PNG save support
	
    /**
     * Saves the state of the graph to the given image file. <br/>
     */
    public void save(File image) throws IOException{
        BufferedImage paintImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = paintImage.getGraphics();
        this.paintComponent(g);
        ImageIO.write(paintImage, "PNG", image);
    }
	
	//=============================================================================================================
	private class SettingsPanel extends JPanel{
		
		private final @Nonnull JSpinner maxXSpinner;
		private final @Nonnull JSpinner maxYSpinner;
		private final @Nonnull JSpinner stepXSpinner;
		private final @Nonnull JSpinner stepYSpinner;
		private final @Nonnull JSpinner pointSizeSpinner;


		public SettingsPanel() {
			setLayout(new GridLayout(0, 2));
			maxXSpinner = new JSpinner(new SpinnerNumberModel(SimpleGraph.this.maxValueX, 0.01, 1e6, 0.001));
			maxYSpinner = new JSpinner(new SpinnerNumberModel(SimpleGraph.this.maxValueY, 0.01, 1e6, 0.001));
			stepXSpinner = new JSpinner(new SpinnerNumberModel(SimpleGraph.this.gridSpreadX, 0.001, 1e3, 0.001));
			stepYSpinner = new JSpinner(new SpinnerNumberModel(SimpleGraph.this.gridSpreadY, 0.001, 1e3, 0.001));
			pointSizeSpinner = new JSpinner(new SpinnerNumberModel(SimpleGraph.this.pointSize, 2, 20, 1));
			
			add(new JLabel("Max X value: "));
			add(maxXSpinner);
			add(new JLabel("Max Y value: "));
			add(maxYSpinner);
			add(new JLabel("Step X: "));
			add(stepXSpinner);
			add(new JLabel("Step Y: "));
			add(stepYSpinner);
			add(new JLabel("Point size: "));
			add(pointSizeSpinner);
		}
		
		public void updateGraph(){
			SimpleGraph.this.maxValueX = ((Double) maxXSpinner.getValue()).doubleValue();
			SimpleGraph.this.maxValueY = ((Double) maxYSpinner.getValue()).doubleValue();
			SimpleGraph.this.gridSpreadX = ((Double) stepXSpinner.getValue()).doubleValue();
			SimpleGraph.this.gridSpreadY = ((Double) stepYSpinner.getValue()).doubleValue();
			SimpleGraph.this.pointSize = ((Integer) pointSizeSpinner.getValue()).intValue();
			
			SimpleGraph.this.repaint();
		}
		
	}
	
	//=============================================================================================================
	
	/**
	 * Launches a window showing the graph. <br/> 
	 */
	public void display() {
		display(500, 150, 800, 800);
	}
	
	/**
	 * Launches a window showing the graph. <br/> 
	 * @param x window coordinate
	 * @param y window coordinate
	 * @param width of the window
	 * @param height of thw window
	 */
	public void display(int x, int y, int width, int height) {
		SwingUtilities.invokeLater(()->{
			
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {} 
			
			JFrame frame = new JFrame();
			frame.setLayout(new BorderLayout());
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setBounds(500, 150, 800, 800);
			
			frame.add(new JScrollPane(this),BorderLayout.CENTER);
			frame.setMinimumSize(new Dimension(300, 300));
			
			frame.setVisible(true);
		});
	}

	/**
	 * Sets the size of displayed points. <br/>
	 */
	public void setPointSize(@Nonnegative int pointSize) {
		this.pointSize = pointSize;
	}
	
	/**
	 * Sets the x axis step. <br/>
	 */
	public void setGridSpreadX(@Nonnegative double gridSpreadX) {
		this.gridSpreadX = gridSpreadX;
	}
	
	/**
	 * Sets the y axis step. <br/>
	 */
	public void setGridSpreadY(@Nonnegative double gridSpreadY) {
		this.gridSpreadY = gridSpreadY;
	}
	
	//=============================================================================================================
	/**
	 * This interface needs to be implemented by a shape which can be plotted on the graph. <br/>
	 * It makes no restrictions on the type of shape only requesting that the draw method is implemented. <br/>
	 */
	public static interface IGraphShape {		
		/**
		 * 
		 * @param g - graphics object used for painting the object 
		 * @param xValueToScreenPosition - function which translates the x coordinate value to the position which needs to be given to the graphics object
		 * @param yValueToScreenPosition - function which translates the y coordinate value to the position which needs to be given to the graphics object
		 */
		public void draw(Graphics g, DoubleToIntFunction xValueToScreenPosition, DoubleToIntFunction yValueToScreenPosition);
	}

	/**
	 * This class implements a line to be shown on the graph. <br/>
	 */
	public static class Line implements IGraphShape{

		private double startX;
		private double startY;
		private double endX;
		private double endY;
		private Color color;

		
		public Line(double startX, double startY, double endX, double endY, Color color) {
			this.startX = startX;
			this.startY = startY;
			this.endX = endX;
			this.endY = endY;
			this.color = color;
		}


		public Line(double[] start, double[] end, Color color) {
			this(start[0],start[1],end[0],end[1],color);
		}
		
		
		@Override
		public void draw(Graphics g, DoubleToIntFunction xValueToScreenPosition,
				DoubleToIntFunction yValueToScreenPosition) {
			
			int x1 = xValueToScreenPosition.applyAsInt(startX);
			int y1 = yValueToScreenPosition.applyAsInt(startY);
			
			int x2 = xValueToScreenPosition.applyAsInt((endX));
			int y2 = yValueToScreenPosition.applyAsInt((endY));
			
			g.setColor(color);
			g.drawLine(x1, y1, x2, y2);
		}
		
	}

	/**
	 * This class implements a polyline line to be shown on the graph. <br/>
	 */
	public static class PolyLine implements IGraphShape{

		private Color color;
		private double[][] points;

		public PolyLine(Color color, double[]... points) {
			this.points = points;
			this.color = color;
		}
		
		@Override
		public void draw(Graphics g, DoubleToIntFunction xValueToScreenPosition,
				DoubleToIntFunction yValueToScreenPosition) {
			
			int[] xPoints = new int[points.length];
			int[] yPoints = new int[points.length]; 
 
			for(int i=0; i<points.length; i++) {
				double[] point = points[i];
				xPoints[i] = xValueToScreenPosition.applyAsInt(point[0]);
				yPoints[i] = yValueToScreenPosition.applyAsInt(point[1]);
			}
			
			g.setColor(color);
			g.drawPolygon(xPoints, yPoints, points.length);
		}
		
	}

	/**
	 * This class implements a circle to be shown on the graph. <br/>
	 */
	public static class Circle implements IGraphShape{

		private Color color;
		private double[] upperLeftPoint;
		private double diameter;
		
		public Circle(double[] center, double radius, Color color) {
			upperLeftPoint = center;
			upperLeftPoint[0]-=radius;
			upperLeftPoint[1]+=radius;
			
			diameter = radius*2;
			this.color = color;
		}
		
		public Circle(double centerX, double centerY, double radius, Color color) {
			this(new double[] {centerX,centerY},radius,color);			
		}
		
		@Override
		public void draw(Graphics g, DoubleToIntFunction xValueToScreenPosition,
				DoubleToIntFunction yValueToScreenPosition) {
			
			int x = xValueToScreenPosition.applyAsInt(upperLeftPoint[0]);
			int y = yValueToScreenPosition.applyAsInt(upperLeftPoint[1]);

			int width = xValueToScreenPosition.applyAsInt(upperLeftPoint[0]+diameter)-x;
			int height = yValueToScreenPosition.applyAsInt(upperLeftPoint[1]-diameter)-y;
			
			g.setColor(color);
			g.drawOval(x, y, width, height);
		}
		
	}


}
