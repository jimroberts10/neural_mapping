import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

//NeuralNet is a java class to replace the JSON data.
//NeuralNet contain an array of Skeletons
  //a Skeleton contains an array of Treenodes
    //a Treenode contains an id, a coordinate [x,y,z], a parent id, and a reference to a parent node
  //a Skeleton contains an array of Connectors
    //a Connector contains an id, a coordinate [x,y,x], presynaptic connections, and postsynaptic connections
public class NeuralNet {
	private Skeleton[] skeletons;
	//private Connector[] connectors; //connectors belong to the network, not necessarily individual neurons
	private int[] keys;
	//set calls
	public void setSkeletons(Skeleton[] skeletons){
		//for placing an array of skeletons
		this.skeletons = skeletons;
	}
	public void setSkeleton(int i, Skeleton skeleton){
		//for placing a skeleton individually
		skeletons[i] = skeleton; //possibly edit to extend length of array so that 'i' fits, with a warning about creating new arrays 
	}
	public void setKeys(int[] keys){
		this.keys = keys;
	}
	
	//get calls
	public Skeleton[] getSkeletons(){
		//for getting the array of skeletons
		return skeletons;
	}
	public Skeleton getSkeleton(int i){
		//for getting the skeleton individually
		return skeletons[i];
	}
	public Skeleton getSkeletonById(int id){
		//for getting the skeleton by its ID 
		int i = 0; 
		while(skeletons[i].getId()!=id){
			i++;
		}
		return skeletons[i];
	}
	public int[] getKeys(){
		return keys;
	}
	
	//CONSTRUCTOR CLASS (takes in data from JSON file)
	public NeuralNet(File file){
		//Read contents of file into a string
		String s = fileToString(file);
		
		//Convert string into Object
		Object obj = JSONValue.parse(s);
		
		//Convert Object into JSONObject
		JSONObject root = (JSONObject) obj;
		
		//Begin parsing Neuron("Skeleton") data from the neural network
		JSONObject skeleton_obj_list = (JSONObject)root.get("skeletons"); //first content is always skeletons 
		Object[] skeleton_keys = skeleton_obj_list.keySet().toArray(); //find index # of skeletons
		Skeleton[] skeletons = new Skeleton[skeleton_keys.length]; //a new skeleton for each key
		int[] keys = new int[skeleton_keys.length]; //a quick tool to see which keys are available
		
		//Loop through all neurons
		for(int i = 0; i < skeleton_keys.length; i++){
			String skel_id_as_string = skeleton_keys[i].toString();
			int skel_id = Integer.parseInt(skel_id_as_string); //get the JSON key as an integer; assign it as index number
			Skeleton skeleton = new Skeleton(skel_id); //create the corresponding skeleton object
			keys[i] = skel_id; //assign the key value to 'keys' as well
			
			//Select neuron [i] from the list of neurons
			JSONObject skeleton_obj = (JSONObject)skeleton_obj_list.get(skeleton_keys[i]); 
			
			//Begin parsing Connector data from each Neuron
			JSONObject connector_obj_list = (JSONObject)skeleton_obj.get("connectors");
			Connector[] connectors = build_connectors(connector_obj_list, skel_id);
			
			//Begin parsing Treenode data from each Neuron
			JSONObject treenode_obj_list = (JSONObject)skeleton_obj.get("treenodes");
			Treenode[] treenodes = build_treenodes(treenode_obj_list);
			
			//Assign Connectors and Treenodes to Skeleton
			skeleton.setConnectors(connectors);
			skeleton.setTreenodes(treenodes);
			
			//Assign this skeleton to the skeleton array
			skeletons[i] = skeleton; 
		}
		//set this skeleton array to the neural network
		this.skeletons = skeletons;
		this.keys = keys;
		System.out.println("data initialized successfully");
		//initialization is now complete!
	}
	
	public void printContents(){
		//PRINT CONTENTS OF NEURALNET
		System.out.print("this neural network consists of " + keys.length + " neurons:\n");
		if(keys.length>0){for(int i = 0; i<keys.length-1; i++){System.out.print(keys[i] + ", ");}System.out.print(keys[keys.length-1] + "\n");}
		System.out.print("\n");
	}
	
	private String fileToString(File file){
		//read a JSON file into JSON string
		try{
			String s = new String();
			BufferedReader reader = new BufferedReader( new FileReader (file));
			String         line = null;
			StringBuilder  stringBuilder = new StringBuilder();
			String         ls = System.getProperty("line.separator");
			
			try {
				while( ( line = reader.readLine() ) != null ) {
					stringBuilder.append( line );
					stringBuilder.append( ls );
				}
				s = stringBuilder.toString();
			} finally {
				reader.close();
			}
			return s;
		} catch (IOException e){
			e.printStackTrace();
			return null;
		}
	}
	
	private Connector[] build_connectors(JSONObject connector_obj_list, int skeleton_id){
		Object[] connector_keys = connector_obj_list.keySet().toArray();
		Connector[] connectors = new Connector[connector_keys.length];
		int[] connectorKeys = new int[connector_keys.length]; //a quick tool to see which keys are available
		//Loop through all connectors
		for(int j = 0; j < connector_keys.length; j++){
			//initialize the connector, assign it an ID
			String conn_id_as_string = connector_keys[j].toString();
			int conn_id = Integer.parseInt(conn_id_as_string);
			Connector connector = new Connector(conn_id); 
			connectorKeys[j] = conn_id;
			
			//Load and set data from the connector [j] JSON Object
			JSONObject connector_obj = (JSONObject)connector_obj_list.get(connector_keys[j]);
			String location_str = connector_obj.get("location").toString();
			String presynaptic_to_str = connector_obj.get("presynaptic_to").toString();
			String postsynaptic_to_str = connector_obj.get("postsynaptic_to").toString();
			
			//Loads values for the connector
			load_connector_defaults(connector, skeleton_id, location_str, presynaptic_to_str, postsynaptic_to_str);
			
			//assign this connector to the array
			connectors[j] = connector;
		}
		return connectors;

	}
	
	private void load_connector_defaults(Connector connector, int skeleton_id, String location_str, String presynaptic_to_str, String postsynaptic_to_str){
		//load location data
		int[] location = stringToArray(location_str); //[x,y,z]
		connector.setLocation(location);
		
		//load presynapse data
		//[parent_neuron_id  ] (row 0)
		//[parent_node_id] (row 1)
		//null if none exist
		presynaptic_to_str = presynaptic_to_str.replaceAll("\\[", "").replaceAll("\\]", "");
		if(!presynaptic_to_str.equals("")){
			int pre_neuron_id = skeleton_id;
			int pre_node_id = Integer.parseInt(presynaptic_to_str);
			int[] presynaptic_to = {pre_neuron_id,pre_node_id};
			connector.setPresynaptic_to(presynaptic_to);
		}
		else{
			int[] presynaptic_to = {-1,-1};
			connector.setPresynaptic_to(presynaptic_to);
		}
		
		//load postsynpase data
		//[child_neuron_id 1] [neuron 2] ... [neuron n] (row 0)
		//[child_node_id   1] [node   2] ... [node   n] (row 1)
		//on this initialization, however, only (column 0) will be occupied
		//null if none exist
		int[] p_to_a = stringToArray(postsynaptic_to_str); 
		if(p_to_a!=null){
			int[][] postsynaptic_to = new int[p_to_a.length][2];
			for(int i = 0; i<p_to_a.length; i++){
				postsynaptic_to[i][0] = skeleton_id;
				postsynaptic_to[i][1] = p_to_a[i];
			}
			connector.setPostsynaptic_to(postsynaptic_to);
		}else{int[][] postsynaptic_to =new int[0][2];
			connector.setPostsynaptic_to(postsynaptic_to);	
		}
	}

	private Treenode[] build_treenodes(JSONObject treenode_obj_list){
		Object[] treenode_keys = treenode_obj_list.keySet().toArray();
		Treenode[] treenodes = new Treenode[treenode_keys.length];
		int[] treenodeKeys = new int[treenode_keys.length]; //a quick tool to see which keys are available
		
		//Loop through all Treenodes
		for(int j = 0; j < treenode_keys.length; j++){
			//initialize the connector, assign it an ID
			String tree_id_as_string = treenode_keys[j].toString();
			int tree_id = Integer.parseInt(tree_id_as_string);
			Treenode treenode = new Treenode(tree_id); 
			treenodeKeys[j] = tree_id;
			
			//Load and set data from the connector [j] JSON Object
			JSONObject treenode_obj = (JSONObject)treenode_obj_list.get(treenode_keys[j]);
			String location_str = treenode_obj.get("location").toString();
			int[] location = stringToArray(location_str);
			treenode.setLocation(location);
			
			//Special case: parent_id is null
			int parent_id;
			Object parent_id_obj = treenode_obj.get("parent_id");
			if (parent_id_obj == null){
				parent_id = -1; //assign -1 if parent id is null to avoid errors
			} 
			else{
				String parent_id_str = parent_id_obj.toString();
				parent_id = Integer.parseInt(parent_id_str);
			}
			
			treenode.setParent_id(parent_id);
			
			//Assign this treenode to the array
			treenodes[j] = treenode;
		}
		return treenodes;

	}
	
	private int[] stringToArray(String arr){
		//convert string to a 1-D array
			String[] items = arr.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
			
			int[] results = new int[items.length];
			
			for (int i = 0; i < items.length; i++) {
				try {
					results[i] = Integer.parseInt(items[i]);
				} catch (NumberFormatException nfe) {return null;};
			}
			return results;
		}
}
