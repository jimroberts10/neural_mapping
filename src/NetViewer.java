import java.awt.Color;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.aqirna.d3obj.DDDTutorial;
import com.aqirna.d3obj.DObjectModifier;
import com.aqirna.d3obj.DPolygon;
import com.aqirna.d3obj.Screen;
import com.aqirna.d3obj.UserAction;

public class NetViewer extends JFrame{
	
	//static JFrame F = new NetViewer(null);
	Screen screenObject = new Screen();
	static ArrayList<DPolygon> linkArray = new ArrayList<DPolygon>();
	public NetViewer(NeuralNet network){
		add(screenObject);
		setUndecorated(true);
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setVisible(true);
		
		UserAction.setTVelocity(1000);
		
		loadNetwork(network.getSkeleton(0), Color.BLACK);
		loadNetwork(network.getSkeleton(1), Color.BLUE);
		loadNetwork(network.getSkeleton(2), Color.ORANGE);
		loadNetwork(network.getSkeleton(3), Color.GREEN);
		loadNetwork(network.getSkeleton(4), Color.RED);
		loadNetwork(network.getSkeleton(5), Color.YELLOW);
		loadNetwork(network.getSkeleton(6), Color.CYAN);

		for(int i = 0; i<linkArray.size(); i++){
			DObjectModifier.add(linkArray.get(i));
		}
	}
	
	public static void loadNetwork(Skeleton neuron, Color c){
		
		//find root node
		Treenode root = neuron.getTreenode(0);
		while(root.getParent()!=null){
			root = root.getParent();
		}
		
		//recursively link nodes to children
		linkChild(root, c);
		
	}
	
	public static void linkChild(Treenode node, Color c){
		if(node.getChild()==null){
			return; 
		} 
		ArrayList<Treenode> children = node.getChild();
		for(Treenode child : children){
			int[] d1 = node.getLocation();
			int[] d2 = child.getLocation();
			makeDPolygon(d1,d2,c);
			linkChild(child,c);
		}
	}
	
	public static void makeDPolygon(int[] d1, int[] d2,Color c){
		DPolygon myPoly = new DPolygon(new double[]{d1[0], d2[0]}, new double[]{d1[1], d2[1]}, new double[]{d1[2], d2[2]}, c);
		linkArray.add(myPoly);
	}
}
