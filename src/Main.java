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
		
		//Test creating neuron-network data structure using kc_pn_tree_geometry.json 
		NeuralNet testnet = new NeuralNet(jsonData4);
		System.out.println("");
		System.out.println("Neurons by ID:");
		int[] skeleton_keys = testnet.getKeys();
		for(int i = 0; i < skeleton_keys.length; i++){
			System.out.println(skeleton_keys[i]);
		}
		System.out.println("");
		
		//Test that connections made created
		Skeleton neuron2 = testnet.getSkeleton(3);
		int[] connectorKeys = neuron2.getConnectorKeys();
		int[] treenodeKeys = neuron2.getTreenodeKeys();
		System.out.println("Closer look at Neuron #" +neuron2.getId());
		System.out.println("Connections:");
		for(int i = 0; i < connectorKeys.length; i++){
			System.out.println(connectorKeys[i]);
		}
		System.out.println("");
		
		//Test that treenodes are made
		System.out.println("Treenodes:");
		for(int i = 0; i < treenodeKeys.length; i++){
			System.out.println(treenodeKeys[i]);
		}
		
	}
}
