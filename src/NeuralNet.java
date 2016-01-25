import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

//NeuralNet is a java class to replace the JSON data.
//NeuralNet contain an array of Skeletons
  //a Skeleton contains an array of Treenodes
    //a Treenode contains an id, a coordinate [x,y,z], a parent id, and a reference to a parent node
  //a Skeleton contains an array of Connectors
    //a Connector contains an id, a coordinate [x,y,x], presynaptic connections, and postsynaptic connections
public class NeuralNet {
	private String filename;
	private ArrayList<Skeleton> skeletons;
	private ArrayList<Connector> connectors; //connectors belong to the network, not necessarily individual neurons
	private ArrayList<Integer> skeleton_id;
	private ArrayList<Integer> connector_id;
	
	//Set variables
	public void setFilename(String filename){
		this.filename = filename;
	}
	public void setSkeleton(ArrayList<Skeleton> skeletons){
		this.skeletons = skeletons;
	}
	public void setSkeleton(Skeleton skeleton){
		this.skeletons.add(skeleton);
	}
	public void setSkeleton_id(ArrayList<Integer> skeleton_id){
		this.skeleton_id = skeleton_id;
	}
	
	//Get variables
	public String getFilename(){
		return filename;
	}
	public ArrayList<Skeleton> getSkeleton(){
		return skeletons;
	}
	public Skeleton getSkeleton(int i){
		return skeletons.get(i);
	}
	public ArrayList<Integer> getSkeletonId(){
		return skeleton_id;
	}
	public Skeleton getSkeletonById(int id){
		//for getting the skeleton by its ID 
		int i = 0; 
		while(skeletons.get(i).getId()!=id){
			i++;
		}
		return skeletons.get(i);
	}
	public ArrayList<Connector> getConnectors(){
		return this.connectors;
	}
	
	public void sortSkeleton(){
		skeletons.sort(null);
	}
	
	//CONSTRUCTOR CLASS (takes in data from JSON file)	
	public NeuralNet(File file){
		load_to_classes(file); //Load JSON data into Java classes
		link_treenodes(); //Link treenodes in all neurons
		link_connectors(); //Link connectors in all neurons
		
	}
	
	public void printContents(){
		//PRINT CONTENTS OF NEURALNET
		System.out.print("File \"" + filename + "\" contains " + skeletons.size() + " neurons: \n");
		for(Integer i: skeleton_id){
			System.out.print(i + "\t");
		}
		System.out.print("\n\n");
	}
	
	private void load_to_classes(File file){
		//Save the name of the file used for this neural network (serves as "ID")
		setFilename(file.getName());
		
		//Load data from file 
		String s = fileToString(file);
				
		
		////////////
		//JSONObject root = (JSONObject)JSONValue.parse(s); 							//level 0, root (subelement: "skeletons")
		//JSONObject skeleton_obj_list = (JSONObject)root.get("skeletons"); 			 //level 1, skeletons(subelement: {"####0", "####1", ...} -->skeleton IDs) 
		//JSONObject skeleton_obj = (JSONObject)skeleton_obj_list.get("#####"); 		  //level 2, skeleton ##### (subelement: {"treenodes, "connectors"})
		//JSONObject treenode_obj_list = (JSONObject)skeleton_obj.get("treenodes"); 	   //level 3, treenodes (subelement: {"####0", "####1", ...} -->treenode IDs)
		//JSONObject treenode_obj = (JSONObject)treenode_obj_list.get("#####"); 		    //level 4, treenode ##### (subelement: {"location", "parent_id"})
		//JSONObject connector_obj_list = (JSONObject)skeleton_obj.get("connectors"); 	   //level 3, connectors (sublement: {"####0", "####1", ...} -->connector IDs)
		//JSONObject connector_obj = (JSONObject)connector_obj_list.get("#####"); 		    //level 4, connector ##### (subelement: {"location", "presynaptic_to", "postsynaptic_to"})
		////////////

		//Convert data to JSON 
		JSONObject root = (JSONObject)JSONValue.parse(s); //level 0, root (subelement: "skeletons")
		
		//Extract skeleton data
		JSONObject skeleton_obj_list = (JSONObject)root.get("skeletons");  //level 1, skeletons(subelement: {"####0", "####1", ...} -->skeleton IDs)
		String[] s_keys = object_array_to_string_array(skeleton_obj_list.keySet().toArray()); //(keys for each skeleton)
		
		//Create ArrayList for skeletons
		ArrayList<Skeleton> skeletons = new ArrayList<Skeleton>();
		ArrayList<Integer> skeleton_id = new ArrayList<Integer>();
		
		//Loop through each skeleton individually and build skeleton
		for(String s_id : s_keys){
			JSONObject skeleton_obj = (JSONObject)skeleton_obj_list.get(s_id); //level 2, skeleton ##### (subelement: {"treenodes, "connectors"})
			Skeleton skeleton = new Skeleton(Integer.parseInt(s_id)); 
			initialize_skeleton(skeleton, skeleton_obj); //put skeleton_obj data into skeleton
			skeletons.add(skeleton);
			skeleton_id.add(Integer.parseInt(s_id));
		}
		
		//Add elements to Network
		setSkeleton(skeletons);
		setSkeleton_id(skeleton_id);
	}
	
	private void initialize_skeleton(Skeleton skeleton, JSONObject skeleton_obj_list){
		//Carry the skeleton ID through the function
		int s_id = skeleton.getId();
		
		//////Extract treenode data
		JSONObject treenode_obj_list = (JSONObject)skeleton_obj_list.get("treenodes"); //level 3, treenodes (subelement: {"####0", "####1", ...} -->treenode IDs)
		String[] tr_keys = object_array_to_string_array(treenode_obj_list.keySet().toArray()); //(keys for each treenode)
		
		//Create an ArrayList for treenodes
		ArrayList<Treenode> treenodes = new ArrayList<Treenode>();
		ArrayList<Integer> treenode_id = new ArrayList<Integer>();
		
		//Loop through each treenode individually and build the node
		for(String tr_id: tr_keys){
			JSONObject treenode_obj = (JSONObject)treenode_obj_list.get(tr_id); //level 4, treenode ##### (subelement: {"location", "parent_id"})
			Treenode node = new Treenode(Integer.parseInt(tr_id), s_id);
			initialize_treenode(node, treenode_obj); //put treenode_obj data into node
			treenode_id.add(Integer.parseInt(tr_id));
			treenodes.add(node);
		}
		
		//////Extract connector data
		JSONObject connector_obj_list = (JSONObject)skeleton_obj_list.get("connectors"); //level 3, connectors (sublement: {"####0", "####1", ...} -->connector IDs)
		String[] co_keys = object_array_to_string_array(connector_obj_list.keySet().toArray()); //(keys for each connector)
		
		//Create an ArrayList for connectors
		ArrayList<Connector> connectors = new ArrayList<Connector>();
		ArrayList<Integer> connector_id = new ArrayList<Integer>();
		
		//Loop through each treenode individually and build the node
		for(String co_id: co_keys){
			JSONObject connector_obj = (JSONObject)connector_obj_list.get(co_id); //level 4, connector ##### (subelement: {"location", "presynaptic_to", "postsynaptic_to"})
			Connector connector = new Connector(Integer.parseInt(co_id), s_id);
			initialize_connectors(connector, connector_obj, s_id); //put connector_obj data into connector
			connector_id.add(Integer.parseInt(co_id));
			connectors.add(connector);
		}

		skeleton.setTreenode_id(treenode_id);
		skeleton.setConnector_id(connector_id);
		skeleton.setConnector(connectors);
		skeleton.setTreenode(treenodes);
	}
	
	private void initialize_treenode(Treenode node, JSONObject treenode_obj){
		//(this could be moved to Skeleton class)
		/////Get Location and Parent ID data from the JSON Object 
		//Location:
		String location_str = treenode_obj.get("location").toString();
		int[] location = stringToArray(location_str);
		
		//Parent ID:
		Integer parent_id = -1; //if this is the root node (i.e. parent_id == "null" in JSON object), set parent ID to -1
		Object parent_id_obj = treenode_obj.get("parent_id");
		if(parent_id_obj != null){
			parent_id = Integer.parseInt(parent_id_obj.toString());
		}
		
		/////Set Location and Parent ID information to the node
		node.setLocation(location);
		node.setParent_id(parent_id);
	}
	
	private void initialize_connectors(Connector connector, JSONObject connector_obj, int s_id){
		//(this could be moved to Skeleton class)
		/////Get Location, Presynapse, and Postsynapse data from the JSON Object
		//Location
		String location_str = connector_obj.get("location").toString();
		int[] location = stringToArray(location_str);
		
		//Presynapse
		String presynaptic_to_str = connector_obj.get("presynaptic_to").toString().replaceAll("\\[", "").replaceAll("\\]", ""); //load without brackets
		Integer[] presynaptic_to = null;
		if(!presynaptic_to_str.equals("")){ //There are either 1 or 0 presynapses available. Return null if none; {node_id, skeleton_id} if 1
			Integer[] presyn = {Integer.parseInt(presynaptic_to_str), s_id};
			presynaptic_to = presyn;
		}
		
		//Postsynapse
		String[] postsynaptic_to_str = connector_obj.get("postsynaptic_to").toString().replaceAll("\\[", "").replaceAll("\\]", "").split(","); //load without brackets and as an array
		ArrayList<Integer[]> postsynaptic_to = null;
		if(!postsynaptic_to_str[0].equals("")){
			postsynaptic_to = new ArrayList<Integer[]>();
			for(String str : postsynaptic_to_str){
				Integer[] element = {Integer.parseInt(str), s_id};
				postsynaptic_to.add(element);
			}
		}
		
		//Add elements to connector
		connector.setLocation(location);
		connector.setPresynaptic_to(presynaptic_to);
		connector.setPostsynaptic_to(postsynaptic_to);
	}

	private void link_treenodes(){
		for(Skeleton skeleton: this.skeletons){
			skeleton.link_treenodes();
		}
	}

	private void link_connectors(){
		//initialize connector ArrayList 
		ArrayList<Connector> connectors = new ArrayList<Connector>();
		
		//add all connectors, including repeats
		load_connectors_from_skeletons(connectors);
		
		//merge duplicate connectors
		remove_duplicate_connectors(connectors);
		
		//link connectors to nodes
		link_connectors(connectors);
		
		//Assign connectors to Neural net
		this.connectors = connectors;
	}
	
	private void load_connectors_from_skeletons(ArrayList<Connector> connectors){
		//initialize connectors and add all connectors, including repeats
		for(Skeleton skeleton: skeletons){
			for(Connector con : skeleton.getConnector()){
				connectors.add(con);
			}
		}
	}

	private void remove_duplicate_connectors(ArrayList<Connector> connectors){
		//Compare each connector with each other connector
		//If a connector [j] is a duplicate..
		//  Merge contents [from j] into first occurence [i] (presyn/postsyn treenode info)
		//  Redirect repeat occurence to the merged occurence in Skeleton [a = find(i); b = find(j); b.getparentSkel.setb(a)
		//  Remove repeat occurence from connectors [connectors.remove(j)]
		for(int i = 0; i<connectors.size()-1; i++){
			for(int j = i+1; j<connectors.size(); j++){
				if(connectors.get(i).getId()==connectors.get(j).getId()){ //j is a duplicate						
					Connector conA = connectors.get(i);
					Connector conB = connectors.get(j);
					redirect_parent_skeleton(conA, conB);
					merge_contents(conA, conB); 
					conB.setTag_for_deletion(true);
				}
			}
		}
		//remove connectors from list
		for(int i = 0; i<connectors.size(); i++){
			if(connectors.get(i).getTag_for_deletion()==true){
				connectors.remove(i);
			}
		}
	}
	
	private void merge_contents(Connector conA, Connector conB){
		//Add contents of presynaptic_to and postsynaptic_to from conB into conA
		conA.addPresynaptic_to(conB.getPresynaptic_to());
		conA.addPostsynaptic_to(conB.getPostsynaptic_to()); //Merge contents from j into i
	}

	private void redirect_parent_skeleton(Connector conA, Connector conB){
		//Make change all references to conB to conA
		int conB_id = conB.getId();
		int skelB_id = conB.getSkeleton_id();
		Skeleton skelB = getSkeletonById(skelB_id);  
		int index = skelB.getConnectorIndex(conB_id);
		skelB.setConnector(index, conA);
	}
	
	private void link_connectors(ArrayList<Connector> connectors){
		for(Connector con: connectors){
			link_presynapses(con);
			link_postsynapses(con);
		}
	}
	
	private void link_presynapses(Connector con){
		Integer[] presynapse_to = con.getPresynaptic_to();
		if(presynapse_to == null){ //don't do this part if there is no parent to connect to
			return;
		}
		int skel_id = presynapse_to[1];
		int node_id = presynapse_to[0];
		Treenode node = getSkeletonById(skel_id).getTreenodeById(node_id);
		con.setPresynaptic_node(node);
		node.setPresynaptic_connector(con);
	}
	
	private void link_postsynapses(Connector con){
		ArrayList<Integer[]> postsynaptic_to = con.getPostsynaptic_to();
		if(postsynaptic_to == null){
			return;
		}
		for(Integer[] post : postsynaptic_to){
			int skel_id = post[1];
			int node_id = post[0];
			Treenode node = getSkeletonById(skel_id).getTreenodeById(node_id);
			con.addPostsynaptic_node(node);
			node.setPostsynaptic_connector(con);
		}
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
	
	private String[] object_array_to_string_array(Object[] obj_arr){
		String[] str_arr = new String[obj_arr.length];
		for(int i = 0; i<obj_arr.length; i++){
			str_arr[i] = obj_arr[i].toString();
		}
		return str_arr;
	}

	private int[] stringToArray(String arr){
	//convert string to an array
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
