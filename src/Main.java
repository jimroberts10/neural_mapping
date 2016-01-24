import java.io.File;
import java.io.IOException;

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
		
		Treenode ref = net3.getSkeleton(1).getTreenode(0);
		Connector jim;
		while(ref.getParent()!=null){
			ref = ref.getParent();
		}
		System.out.print("\n================================\n================================\n");
		while(ref.getChild()!=null){
			System.out.println("\nNext Child Node: ");
			ref = ref.getChild(0);
			ref.printContents();
			if(ref.getPresynaptic_connector()!=null){
				jim = ref.getPresynaptic_connector();
				if(jim.getPostsynaptic_node()!=null){
					int skel_id = ref.getSkeleton_id();
					ref = jim.getPostsynaptic_node(0);
					System.out.println("JUMPING FROM NEURON #" + skel_id + " TO NEURON #" + ref.getSkeleton_id());
					jim.printContents();
					System.out.print("\n");
					ref.printContents();
					System.out.print("\n");
				}
			}
		}
	}
}
