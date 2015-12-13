package greedy;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This program demonstrates how to work with JFrame in Swing.
 * @author www.codejava.net
 *
 */
public class TSPFrame extends JFrame implements ActionListener {
	private File tspFile = null;
	private Map map = null;
	private JButton start = null;
	private JTextArea status;
	private Point search = null;
	TSP t = new TSP();
	public double minPath = 0.0;
	public ArrayList<Point> minPathList;

	
	public void actionPerformed(ActionEvent e) {
	    System.out.println("Selected: " + e.getActionCommand());
	    switch(e.getActionCommand()){
	    	case "Exit":
	    		int reply = JOptionPane.showConfirmDialog(TSPFrame.this,
						"Are you sure you want to quit?",
						"Exit",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (reply == JOptionPane.YES_OPTION) {
					dispose();
				} else {
					return;
				}
	    		break;
	    	case "Open TSP":
	    		JFileChooser fileChooser = new JFileChooser();
	    		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
	    		FileFilter filter = new FileNameExtensionFilter("TSP File","tsp");
	    		fileChooser.setFileFilter(filter);
	    		int result = fileChooser.showOpenDialog(this);
	    		if(result == JFileChooser.APPROVE_OPTION){
	    			File fileSelected = fileChooser.getSelectedFile();
	    			tspFile = fileSelected;
	    			status.setText("Press Start");
	    			status.update(getGraphics());
	    			t.readPointsFromFile(tspFile);
	    		}
	    		break;
	    	case "Start":
	    		if(t.points.size() > 0){
					minPathList = new ArrayList<>();
					minPath = 0.0;
					status.setText("Processing...");
					this.update(getGraphics());
	
					minPathList = (ArrayList<Point>) findShortestPathInsertion(t.points);
					minPath = totalDistance(minPathList);
					String path = "";
					for (int i = 0; i < minPathList.size(); i++) {
						path += "(" + minPathList.get(i).x + ","
								+ minPathList.get(i).y + ")";
					}
					status.setText("The min path is:" + path
							+ "with a distance of " + minPath);
					status.validate();
					this.update(getGraphics());
	    		}
    			break;
	    		
	    	default :
	    		System.out.println("Invalid option");
	    		break;
	    }

	  }
	
	public List<Point> findShortestPathInsertion(List<Point> points){
		double dist = 0;
		double min = 0;
		int rindex = 0;
		int pindex =0;
		Point p = null;
		if(points.isEmpty()){
			return null;
		}
		//points.
		List<Point>route = new ArrayList<Point>();
		route.add(points.get(0)); //make first point starting point 
		for(int i = 0; i < points.size(); i++){
			min = distance(points.get(0),points.get(i));//add a second point that is furthest away from the initail point
			if(dist == 0 | (min > dist && min >0) ){ // didn't feel like creating a new variable 
				pindex = i;
				dist = min;
			}
		}
		p = points.remove(pindex);
		route.add(p);
		p = points.remove(0);
		route.add(p);
		dist = 0;
		min =0;
		while(!points.isEmpty()){ // repeat until all points are visited
			for(int i = 0; i< route.size() -1 ;i++){ // go through each edge
				for(int j = 0; j < points.size(); j++){ //find point with min distance from an edge
					min = pointToLine(route.get(i),route.get(i+1),points.get(j));
					//System.out.println(min+","+ i + "," +(i+1) +"");
					if(dist == 0 | (min < dist && min > 0)){
						dist = min;
						pindex = j;
						rindex = i+1;
					}
				}
			}
			dist = 0;
			p = points.remove(pindex);
			route.add(rindex, p);
			map.setRoute(route);
			map.update(map.getGraphics());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return route;
	}
	
	public void findShortestPathList(List<Point> route,List<Point> notInroute, Point q){
		List<Point> p = new ArrayList<Point>(); 
		List<Point> r = new ArrayList<Point>();
		r.addAll(route); 
		p.addAll(notInroute); 
		//we have to make copies or else we'll lose reference to points
		
		if(q != null){// only null when we start out, did this to create a starting position
			p.remove(q);
			r.add(q);
		}
		map.setRoute(r);
		map.update(map.getGraphics());
		if(	q != null && q.equals(search)){ // if all points have been visited,
			//r.add(r.get(0)); // the problem requires us to come back to where we started
			//map.setRoute(r);
			//map.update(map.getGraphics());
			double dist = totalDistance(r);
			if(minPath == 0 || dist < minPath){
				minPath = dist;
				minPathList.clear();
				minPathList.addAll(r);
			}
		}else{
			if(q == null){
				for(int i = 0; i < p.get(0).p.size(); i++){
					findShortestPathList(r,p.get(0).p,p.get(0));
				}
			}else{
				for(int i = 0; i < p.size(); i++){
					findShortestPathList(r,p.get(i).p,p.get(i)); // recursively call function on each Point in point of the list
				}
			}
		}
	}
	
	public double distance(Point a, Point b){
		return Math.sqrt(Math.pow((b.x - a.x),2) + Math.pow((b.y - a.y),2));
	}
	
	public double slope(Point a, Point b){
		return (b.y - a.y)/(b.x - a.x);
	}
	
	public double intercept(Point a, Point b){
		return a.y - slope(a,b)* a.x;
	}
	
	public double pointToLineDist(Point a, Point b, Point point){
		double sp = slope(a,b);
		double inter = intercept(a,b);
		return Math.abs(slope(a,b) - 1 + intercept(a,b))/Math.sqrt(Math.pow(sp,2 )+1);
	}
	
	public double pointToLine(Point a, Point b, Point point){
		double dist = distance(a,b);
		double t = ((point.x -a.x)*(b.x - a.x) +(point.y - a.y)*(b.y - a.y))/Math.pow(dist,2);
		if(t <0){
			return distance(point, a);
		} 
		if(t > 1){
			return distance(point, b);
		}
		Point p = new Point(a.x + t * (b.x - a.x), a.y + t * (b.y - a.y));
		return distance(point, p);
 		//return Math.abs((b.x -a.x)*(a.y - point.y) -(a.x - point.x)*(b.y - a.y))/Math.sqrt(Math.pow(b.y - a.y, 2)+Math.pow(b.x -a.x, 2));
	}
	
	public double totalDistance(List<Point> p){
		double dist = 0.0;
		for(int i = 0 ; i < p.size() -1; i++){
			dist += distance(p.get(i),p.get(i+1)); 
		}
		return dist;
	}
	

	public TSPFrame() {
		super("TSP GUI");
		map = new Map();
		minPathList = new ArrayList<>();
		status = new JTextArea("Select a tsp file");
		
		status.setLineWrap(true);
		status.setEditable(false);
		status.setWrapStyleWord(true);
		setLayout(new BorderLayout());
		add(map,"Center");
		add(status,"South");
		
		JPanel panel = new JPanel();
		start = new JButton("Start");
		start.addActionListener(this);
		panel.add(start);
		add(panel,"North");
	
		// adds menu bar
		JMenuBar menuBar = new JMenuBar();
		JMenu menuFile = new JMenu("File");
		JMenuItem menuItemExit = new JMenuItem("Exit");
		menuItemExit.addActionListener(this);
		menuFile.add(menuItemExit);
		JMenuItem menuItemOpen = new JMenuItem("Open TSP");
		menuItemOpen.addActionListener(this);
		menuFile.add(menuItemOpen);

		menuBar.add(menuFile);
		
		// adds menu bar to the frame
		setJMenuBar(menuBar);

		// adds window event listener
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				int reply = JOptionPane.showConfirmDialog(TSPFrame.this,
						"Are you sure you want to quit?",
						"Exit",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (reply == JOptionPane.YES_OPTION) {
					dispose();
				} else {
					return;
				}
			}
		});

		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setVisible(true);
		
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new TSPFrame();
			}
		});
	}
}
/*
19.74386607515605,0,1
26.454070817003945,0,1
19.65228241407048,0,1
17.438243045158014,0,1
12.327957697524148,0,1
10.330508206666945,1,2
16.589367428833114,1,2
11.239032407500437,1,2
7.557252014410333,1,2
13.277263272540011,1,2
10.330508206666945,2,3
16.589367428833114,2,3
12.693720925987659,2,3
7.557252014410333,2,3
13.277263272540011,2,3
20.091104749103394,3,4
20.6844226598377,3,4
13.889365661696871,3,4
13.084250761941432,3,4
20.265637774678837,3,4
25.562499969253132,4,5
21.742394807108994,4,5
13.889365661696871,4,5
16.736156285496925,4,5
27.89096833737296,4,5
*/
