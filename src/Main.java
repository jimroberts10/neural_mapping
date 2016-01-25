import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class Main {
	public static void main (String args[]){
		
		//Load all four files from repository
		File jsonData1 = new File("src\\res\\example.json");
		File jsonData2 = new File("src\\res\\simple_tree_geometry.json");
		File jsonData3 = new File("src\\res\\kc_tree_geometry.json");
		File jsonData4 = new File("src\\res\\kc_pn_tree_geometry.json");
		
		//Test creating neuron-network data structure
		NeuralNet net1 = new NeuralNet(jsonData1);
		NeuralNet net2 = new NeuralNet(jsonData2);
		NeuralNet net3 = new NeuralNet(jsonData3);
		NeuralNet net4 = new NeuralNet(jsonData4);
		
		net1.printContents();
		net2.printContents();
		net3.printContents();
		net4.printContents();
		
		//Sanity test that everything is connected
		//Pick a random node, check find parent nodes until you reach the root, then find child nodes, jumping from one skeleton to another when a connector has both a presynaptic and postsynaptic connection
		//Print contents the whole way to check that parent and child nodes are connected, and connected to what they should be
		connectionSanityTest1(net3);
		int[] dump1 = calculateBoundingBox(net3.getSkeleton(0));
		double dump2 = calculateTotalCableLength(net3.getSkeleton(0));
		int dump3 = calculateConnectionsBetweenPairs(net3);
	}
	public static void connectionSanityTest1(NeuralNet network){
		Treenode ref = network.getSkeleton(1).getTreenode(0);
		Connector con;
		while(ref.getParent()!=null){
			ref = ref.getParent();
		}
		
		System.out.print("\n================================\n================================\n");
		while(ref.getChild()!=null){
			System.out.println("\nNext Child Node: ");
			ref = ref.getChild(0);
			ref.printContents();
			if(ref.getPresynaptic_connector()!=null){
				con = ref.getPresynaptic_connector();
				if(con.getPostsynaptic_node()!=null){
					int skel_id = ref.getSkeleton_id();
					ref = con.getPostsynaptic_node(0);
					System.out.println("JUMPING FROM NEURON #" + skel_id + " TO NEURON #" + ref.getSkeleton_id());
					con.printContents();
					System.out.print("\n");
					ref.printContents();
					System.out.print("\n");
				}
			}
		}
	}
	
	public static int[] calculateBoundingBox(Skeleton neuron){
		//Takes in a neuron and calculates its bounding box
		//Returns a vector in the from [minX, minY, minZ, maxX, maxY, maxZ]
		ArrayList<Treenode> nodes = neuron.getTreenode();
		int minX = neuron.getTreenode(0).getLocation()[0];
		int minY = neuron.getTreenode(0).getLocation()[1];
		int minZ = neuron.getTreenode(0).getLocation()[2];
		int maxX = neuron.getTreenode(0).getLocation()[0];
		int maxY = neuron.getTreenode(0).getLocation()[1];
		int maxZ = neuron.getTreenode(0).getLocation()[2];
		
		for(Treenode node: nodes){
			if(node.getLocation()[0]<minX) minX = node.getLocation()[0];
			if(node.getLocation()[1]<minY) minY = node.getLocation()[1];
			if(node.getLocation()[2]<minZ) minZ = node.getLocation()[2];
			if(node.getLocation()[0]>maxX) maxX = node.getLocation()[0];
			if(node.getLocation()[1]>maxY) maxY = node.getLocation()[1];
			if(node.getLocation()[2]>maxZ) maxZ = node.getLocation()[2];
		}
		
		int[] boundingBox = new int[]{minX, minY, minZ, maxX, maxY, maxZ};
		System.out.print("Bounding Box for neuron " + neuron.getId() +": \nmin- {" + minX +"," + minY+"," + minZ +"}\nmax- {" + maxX+"," + maxY+"," + maxZ + "}\n");
		return boundingBox;
	}

	public static double calculateTotalCableLength(Skeleton neuron){
		//find parent node
		Treenode root = neuron.getTreenode(0);
		while(root.getParent()!=null){
			root = root.getParent();
		}
		
		//recursively calculate cable length
		double cableLength = 0;
		cableLength += calculateCableLengthChildren(root);
		System.out.print("\nTotal length of cables: \n" + cableLength +"nm \n");
		return cableLength;
	}
	
	public static double calculateCableLengthChildren(Treenode node){
		if(node.getChild()==null){
			return 0; 
		}
		double cableLength = 0; 
		ArrayList<Treenode> children = node.getChild();
		for(Treenode child : children){
			int[] d1 = node.getLocation();
			int[] d2 = child.getLocation();
			cableLength += distanceFormula(d1, d2);
			cableLength += calculateCableLengthChildren(child);
		}
		return cableLength;
	}
	
	public static double distanceFormula(int[] d1, int[] d2){
		double distance = Math.sqrt(
				((d1[0]-d2[0])*(d1[0]-d2[0]))
				+ ((d1[1]-d2[1])*(d1[1]-d2[1]))
				+ ((d1[2]-d2[2])*(d1[2]-d2[2])));
		return distance;
	}

	public static int calculateConnectionsBetweenPairs(NeuralNet network){
		ArrayList<Connector> connectors = network.getConnectors();
		ArrayList<Connector> connectorsBetweenPairs = new ArrayList<Connector>();

		for(Connector con : connectors){
			if(con.getPresynaptic_node()!=null && con.getPostsynaptic_node()!=null){
				connectorsBetweenPairs.add(con);
			}
		}
		System.out.print("\nThere are " + connectorsBetweenPairs.size() + " connectors which link between neurons:\n");
		for(Connector con : connectorsBetweenPairs){
			System.out.print(con.getId() + ",");
		}System.out.print("\n");
		return connectorsBetweenPairs.size();
	}
}
