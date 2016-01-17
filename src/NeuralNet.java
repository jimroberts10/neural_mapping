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
				int[] location = stringToArray(location_str);
				int presynaptic_to = -1;
				presynaptic_to_str = presynaptic_to_str.replaceAll("\\[", "").replaceAll("\\]", "");
				if(!presynaptic_to_str.equals("")){presynaptic_to = Integer.parseInt(presynaptic_to_str);} //equal -1 if not yet connected
				int[] postsynaptic_to = stringToArray(postsynaptic_to_str); //null if not yet connected?
				connector.setLocation(location);
				connector.setPostsynaptic_to(postsynaptic_to);
				connector.setPresynaptic_to(presynaptic_to);
				
				//assign this connector to the array
				connectors[j] = connector;
			}
			
			//Begin parsing Treenode data from each Neuron
			JSONObject treenode_obj_list = (JSONObject)skeleton_obj.get("treenodes");
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
			
			//Assign Connectors and Treenodes to Skeleton
			skeleton.setConnectors(connectors);
			skeleton.setTreenodes(treenodes);
			skeleton.setConnectorKeys(connectorKeys);
			skeleton.setTreenodeKeys(treenodeKeys);
			
			//Assign this skeleton to the skeleton array
			skeletons[i] = skeleton; 
		}
		//set this skeleton array to the neural network
		this.skeletons = skeletons;
		this.keys = keys;
		System.out.println("data initialized successfully");
		//initialization is now complete!
	}
	
	//PRINT CONTENTS
	public void printContents(){
		System.out.print("this neural network consists of " + keys.length + " neurons:\n");
		if(keys.length>0){for(int i = 0; i<keys.length-1; i++){System.out.print(keys[i] + ", ");}System.out.print(keys[keys.length-1] + "\n");}
		System.out.print("\n");
	}
	
	//Convert a JSON file into JSON string
	private String fileToString(File file){
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
	
	//convert string to an array
	private int[] stringToArray(String arr){
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
