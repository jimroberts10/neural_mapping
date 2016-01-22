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
		NeuralNet testnet = new NeuralNet(jsonData3);
		NeuralNet jimnet = new NeuralNet(jsonData3);
		System.out.println("");
		int[] skeleton_keys = testnet.getKeys();
		
		
		//Connect the treenodes in each neuron
		link_nodes(testnet);
		link_nodes(jimnet);
		
		Skeleton neuron = testnet.getSkeleton(0);
		Skeleton noron = jimnet.getSkeleton(0);
		
		
		
		//Connect connectors to nodes
		link_connectors(testnet);
		
		for(int i = 0; i< neuron.getConnectors().length; i++){
			System.out.print("===========================================\n=====================\n");
			System.out.println(neuron.getConnectors().length);
			System.out.println("i: " + i);
			noron.getConnector(i).printContents();
			System.out.print("===becomes===\n");
			neuron.getConnector(i).printContents();
		}
		
		//Confirm that connectors are now connected
		Connector connector = neuron.getConnector(0);
		
		//calculate bounding box
		int[] bounding_box = calculate_bounding_box(neuron); //just do for 1 skeleton to show this works
		System.out.print("Bounding box for neuron " + neuron.getId() + ":\n");
		System.out.print("[" + bounding_box[0] + "," + bounding_box[1] + "," + bounding_box[2] + "],");
		System.out.print("[" + bounding_box[3] + "," + bounding_box[4] + "," + bounding_box[5] + "]\n\n");
		
		//calculate cable length
		double total_cable_length = calculate_total_cable_length(neuron); 
		
		System.out.print("Total cable length for neuron " + neuron.getId() + ": " + total_cable_length + "\n\n");
		int[] x = {bounding_box[0],bounding_box[1],bounding_box[2]}; int[] y = {bounding_box[3],bounding_box[4],bounding_box[5]};
		System.out.print("Length to diagonals for bounding box: " + calculate_cable_length(x,y) + "\n\n");
				
		System.out.println("Theoretically, we are now done");
	}
	public static void link_nodes(NeuralNet network){
		int[] skeleton_keys = network.getKeys();
		for(int i = 0; i<skeleton_keys.length; i++){
			Skeleton neuron = network.getSkeleton(i);
			link_nodes(neuron);
		}
	}
	
	public static void link_nodes(Skeleton neuron){
		Treenode[] treenodes = neuron.getTreenodes();
		link_nodes(treenodes);
	}
	
	public static void link_nodes(Treenode[] treenodes){
		//associates nodes with children nodes for a treenode array 

		//making this a separate function to not break code that works, despite copy/paste abuse
		int[][] tree_counter = new int[treenodes.length][4]; //tree_counter pairs treenodeID with number of child nodes
		for(int i = 0; i<treenodes.length; i++){
			tree_counter[i][0] = i; //keep track of original indices (because they correspond to the treenode array)
			tree_counter[i][1] = treenodes[i].getId(); //fill second row with IDs
			tree_counter[i][2] = treenodes[i].getParent_id(); //fill third row with Parent ID
			tree_counter[i][3] = 0; //This is reserved to track number of child nodes for each node
		}
		
		//Sort so parent comes first
		sort_array_by_row(tree_counter,2); //This array was reusable. During code optimization, reuse the first generation of this array
		
		//make the link to the parents from the child node, and keep a counter of how many children the parents have
		Treenode root = treenodes[0]; //base case had parent as -1, which after sorting will always come first
		for(int i = 1; i<treenodes.length; i++){
			int t_i = tree_counter[i][0]; //corresponding index in treenodes (because we sorted tree_counter)
			int parent_id = tree_counter[i][2];
			//find index of treenode that is parent of this treenode
			int p_i = 0;
			while(treenodes[p_i].getId() != parent_id){
				p_i++;
			}
			treenodes[t_i].setParent(treenodes[p_i]);
			tree_counter[p_i][3] = tree_counter[p_i][3] + 1;
		}
		//Parent nodes have been linked and tree_counter now knows how many child nodes each has
		
		//Make space to place each child node individually 
		for(int i = 0; i<treenodes.length; i++){
			int frame_size = tree_counter[i][3]; 
			if(frame_size > 0){ //was hoping the value would remain null if size 0, but didn't happen so I needed this if statement
				Treenode[] frame = new Treenode[frame_size]; //named frame because it's an empty array that will soon be filled with child treenodes
				treenodes[i].setChild(frame);
			}
		}
		//Copy/pasted from a few lines ago. This time we can place child nodes (which we couldn't before because we didn't know how many children each node has
		for(int i = 1; i<treenodes.length; i++){
			int t_i = tree_counter[i][0]; //corresponding index in treenodes (because we sorted tree_counter)
			int parent_id = tree_counter[i][2];
			//find index of treenode that is parent of this treenode
			int p_i = 0;
			while(treenodes[p_i].getId() != parent_id){
				p_i++;
			}
			//link node where the child-node array is not already taken
			int j = 0;
			while(treenodes[p_i].getChild(j) != null){
				j++;
			}
			treenodes[p_i].setChild(treenodes[t_i],j);
		}
		
		/*//This section of code is good for debugging since it prints elements of the tree_counter array. Keep this commented
		for(int i = 0; i<tree_counter.length;i++){
			System.out.print(tree_counter[i][0] + ", ");
		} System.out.println();
		for(int i = 0; i<tree_counter.length;i++){
			System.out.print(tree_counter[i][1] + ", ");
		} System.out.println();
		for(int i = 0; i<tree_counter.length;i++){
			System.out.print(tree_counter[i][2] + ", ");
		} System.out.println();
		for(int i = 0; i<tree_counter.length;i++){
			System.out.print(tree_counter[i][3] + ", ");
		} System.out.println("\n");
		*/
		
	}
	
	public static void sort_array_by_row(int[][] array, int row){ 
		//sort array based on selected row
		//This is an easy to code, though inefficient sorting method [O(n^2)]
		//If code is running slowly, improve this algorithm first.
		//Further, note that sorting by row A, then by row B, does not preserve internal sorting of row A 
			for (int i = 0; i< array.length-1; i++){
				for (int j = i+1; j<array.length; j++){
					if(array[i][row] > array[j][row]){
						
						//Have to do this stupid extra for loop because values don't copy directly for arrays
						int[] tmp = new int[array[0].length];
						for (int rowi = 0; rowi < array[0].length; rowi++){
							tmp[rowi] = array[j][rowi];
							array[j][rowi] = array[i][rowi];
							array[i][rowi] = tmp[rowi];
						}
					}
				}
			}
		}
	
	public static void sort_array(int[] array){
		//Sorting algorithm (O(n^2), improve for efficiency)
		for(int i = 0; i<array.length-1;i++){
			for(int j = i+1; j<array.length; j++){
				if(array[i] > array[j]){
					int tmp = array[j];
					array[j] = array[i];
					array[i] = tmp;
				}
			}
		}
	}
	
	public static int[] calculate_bounding_box(Skeleton neuron){
		Treenode[] tree = neuron.getTreenodes();
		int[] box_coords = calculate_bounding_box(tree);
		return box_coords;
	}
	
	public static int[] calculate_bounding_box(Treenode[] tree){
		//returns x,y,z coordinates
		int[] bounding_box = new int[6];
		
		//initialize [xyz]min, [xyz]max with values that already exist
		int xmin = tree[0].getLocation()[0]; int ymin = tree[0].getLocation()[1]; int zmin = tree[0].getLocation()[2]; 
		int xmax = tree[0].getLocation()[0]; int ymax = tree[0].getLocation()[1]; int zmax = tree[0].getLocation()[2];

		for(int i = 0; i<tree.length; i++){
			if(tree[i].getLocation()[0] < xmin){ xmin = tree[i].getLocation()[0];}
			if(tree[i].getLocation()[1] < ymin){ ymin = tree[i].getLocation()[1];}
			if(tree[i].getLocation()[2] < zmin){ zmin = tree[i].getLocation()[2];}
			if(tree[i].getLocation()[0] > xmax){ xmax = tree[i].getLocation()[0];}
			if(tree[i].getLocation()[1] > ymax){ ymax = tree[i].getLocation()[1];}
			if(tree[i].getLocation()[2] > zmax){ zmax = tree[i].getLocation()[2];}
		}
		bounding_box[0] = xmin; bounding_box[1] = ymin; bounding_box[2] = zmin;
		bounding_box[3] = xmax; bounding_box[4] = ymax; bounding_box[5] = zmax;
		
		return bounding_box;
	}
	
	public static double calculate_total_cable_length(Skeleton neuron){
		Treenode[] treenode = neuron.getTreenodes();
		double total_cable_length = calculate_total_cable_length(treenode);
		return total_cable_length;
	}
	
	public static double calculate_total_cable_length(Treenode[] treenode){
		Treenode root = find_root_treenode(treenode);
		double total_cable_length = calculate_cable_length_of_all_child(root);
		return total_cable_length;
	}
	
	public static Treenode find_root_treenode(Treenode[] treenode){
		//please sure you have a root node and this doesn't cycle forever
		Treenode our_node = treenode[0];
		while(our_node.getParent() != null){
			our_node = our_node.getParent(); 
		}
		return our_node;
	}
	
	public static double calculate_cable_length_of_all_child(Treenode treenode){
		//recursively add the lengths from all child nodes, and their child nodes
		
		double cable_length = 0;
		//base case: no more child nodes
		if(treenode.getChild() == null){
			return 0;
		}
		//depth-first approach
		for(int i = 0; i<treenode.getChild().length; i++){ //for all child nodes
			double distance_to_child = calculate_cable_length(treenode,treenode.getChild(i));
			double cable_length_of_all_child = calculate_cable_length_of_all_child(treenode.getChild(i));
			cable_length = cable_length + cable_length_of_all_child + distance_to_child;
		}
		return cable_length;
	}
	
	public static double calculate_cable_length(Treenode node1, Treenode node2){
		double cable_length;
		int[] x = node1.getLocation();
		int[] y = node2.getLocation();
		cable_length = calculate_cable_length(x, y);
		return cable_length;
	}
	
	public static double calculate_cable_length(int[] location1, int[] location2){
		int[] x = location1; int[] y = location2;
		double cable_length = Math.sqrt(Math.pow(x[0]-y[0], 2) + Math.pow(x[1]-y[1], 2) + Math.pow(x[2]-y[2], 2)); //distance formula
		return cable_length;
	}
	
	public static void link_connectors(NeuralNet network){
		//Connector Counter structure:
				// [0]: index in Skeleton[]
				// [1]: index in Connector[]
				// [2]: ID for skeleton
				// [3]: ID for connector
				// [4]: Flag for this connector to be merged with an existing one		
		int[][] connector_counter = build_connector_counter(network);
				
		//use this structure to link connectors to treenodes
		link_connectors(connector_counter, network);
		
	}
	
	public static int[][] build_connector_counter(NeuralNet network){
		//First: determine the necessary length of the array (there will be repeat connectors, ignore for now)
		int[] skeleton_keys = network.getKeys();
		int array_size = 0;
		for(int i = 0; i<skeleton_keys.length; i++){
			Skeleton neuron = network.getSkeleton(i);
			Connector[] connectors = neuron.getConnectors();
			array_size += connectors.length;
		}
		
		//Second: build the array
		int[][] connector_counter = new int[array_size][6]; 
		int c_i = 0; //keep track of the next empty column in the connector array
		for(int i = 0; i < skeleton_keys.length; i++){ //cycle through all neurons
			Skeleton neuron = network.getSkeleton(i);
			Connector[] connectors = neuron.getConnectors();
			for(int j = 0; j<connectors.length; j++){ //cycle through all connectors
				Connector connector = connectors[j];
				connector_counter[c_i][0] = i; //[row 0, row 1]: keep track of the indices (for skeleton and   
				connector_counter[c_i][1] = j; //connector) because their positions in the array don't change 
				connector_counter[c_i][2] = neuron.getId(); //row 2: skeleton id
				connector_counter[c_i][3] = connector.getId(); //row 3: connector id
				connector_counter[c_i][4] = 0; //row 4: is this a duplicate (0 if no, -1 if yes)
				connector_counter[c_i][5] = 0; //row 5: reserved to map connector_counter to merged_counter
				for(int k = 0; k < c_i; k++){ //change row 4 = -1 if the id for this node already exists
					if (connector_counter[k][3]==connector_counter[c_i][3]){
						connector_counter[c_i][4] = -1;
					}
				}
				c_i++;
			}
		}
		return connector_counter;
	}
	
	public static int[][] build_merged_counter(int[][] counter, NeuralNet network){
		int m_array_size = counter.length; //base size of same size as old array
		for(int i = 0; i<counter.length; i++){
			m_array_size += counter[i][4]; //shorten the array by 1 for every duplicate (row 4 in counter kept track of duplicates)
		}
		
		int[][] m_counter = new int[m_array_size][6];
		int m1_i = 0; //iterator that keeps track of next empty index in array
		
		//first place non-duplicates
		for(int i = 0; i<counter.length; i++){
			if(counter[i][4]==0){ //don't handle duplicates yet 
				counter[i][5] = m1_i; //row 4 maps counter to m_counter (stays 0 if not 'original' value)
				m_counter[m1_i][4] = 0; //while we conveniently have this loop, initialize # of postsynapse connections at 0;
				m_counter[m1_i][5] = m1_i; //keeps track of index in case m_counter is sorted (these arrays are messes, fix them when your code works)
				Connector connector = network.getSkeleton(counter[i][0]).getConnector(counter[i][1]); 
				if(connector.getPresynaptic_to() != null){ //if this node has information on the presynapse connection
					m_counter[m1_i][0] = counter[i][0]; //row 0: index for skeleton of presynaptic_to (i.e. parent node)
					m_counter[m1_i][1] = counter[i][1]; //row 1: index for connector of presynaptic_to 
					m_counter[m1_i][2] = counter[i][2]; //row 2: skeleton id (for presynapse connection)
					m_counter[m1_i][3] = counter[i][3]; //row 3: connector id
				}
				else{
					m_counter[m1_i][3] = counter[i][3]; //row 3: connector id
					m_counter[m1_i][4] += connector.getPostsynaptic_to().length; //row 4: number of postsynpase connections
				}
				m1_i++;
			}
		}
		return m_counter;
	}
	
	public static void link_connectors(int[][] counter, NeuralNet network){
		//determine new length (by shortening old length by 1 per duplicate node)
		//throughout this prefix m_* indicates the connectors are merged
		//this function is pretty horrible if you're trying to follow by reading; I will update for readability in the future.
		
		int[][] m_counter = build_merged_counter(counter, network);
		int m_array_size = m_counter.length;
		
		//now insert duplicates
		int t_i = 0; //iterator that identifies which index is being manipulated
		for(int i = 0; i<counter.length; i++){
			if(counter[i][4]==-1){ //for duplicates
				int id = counter[i][3];
				t_i = 0; //reset t_i
				while( !(counter[t_i][3]==counter[i][3] && counter[t_i][4]>-1) ){ //look for the instance where counter[t_i] is the same connector, but not labeled as the duplicate
					t_i++; 
				}
				int m2_i = counter[t_i][4]; //this is where counter[t_i] was mapped to on m_counter
				//we are merging the contents of counter[i] with counter[t_i]. Find where we put counter[t_i] in m_counter
				Connector connector = network.getSkeleton(counter[i][0]).getConnector(counter[i][1]); 
				if(connector.getPresynaptic_to() != null){ //if this node has information on the presynapse connection
					m_counter[m2_i][0] = counter[i][0]; //row 0: index for skeleton of presynaptic_to (i.e. parent node)
					m_counter[m2_i][1] = counter[i][1]; //row 1: index for connector of presynaptic_to 
					m_counter[m2_i][2] = counter[i][2]; //row 2: skeleton id (for presynapse connection)
					m_counter[m2_i][3] = counter[i][3]; //row 3: connector id
				}
				else{
					m_counter[m2_i][3] = counter[i][3]; //row 3: connector id
					m_counter[m2_i][4] += connector.getPostsynaptic_to().length; //row 4: number of postsynpase connections 
				}
			}
		}
		//Sorting here will make parts of the code later slightly faster (and easier to visualize in trouble-shooting)
		sort_array_by_row(counter,3);
		sort_array_by_row(m_counter,3);

		Connector[] m_connectors = new Connector[m_array_size]; //the array is temporary, the connectors within them are permanent
		
		sort_array_by_row(m_counter,4);
		
		
		for(int m3_i = 0; m3_i<m_array_size; m3_i++){ 
			m_connectors[m3_i] = new Connector(m_counter[m3_i][3]); //finally build a connector with corresponding id
			
			//set presynapse treenode if possible
			int id = m_counter[m3_i][3]; //row 3 is the unique connector ID
			link_presynapse_node(network, counter, m_counter, id);
			link_postsynapse_node(network, counter, m_counter, id);
			make_node_main(network,counter,m_counter,id);
//			link_connector(network,counter,m_counter,id);
		}
	}
	
	
	public static void link_presynapse_node(NeuralNet network, int[][] counter, int[][] m_counter, int connector_id){
		int param_conn_id = 3; //row in counter that has connector ID
		int param_main_id = 4; //this row is -1 if the index is a repeat, and a positive number corresponding counter to m_counter if not
		
		//Find the node tagged as parent (not repeat)
		int[] params = {param_conn_id, connector_id, param_main_id, 0}; //this value will be 0 if it's the 'original', or -1 if it's a repeat 
		int c_parent = find_column_by_unique_value_array(counter, params);
		int[] c_all = find_all_column_by_unique_value(counter, param_conn_id, connector_id);
		
		//The purpose of c_parent vs c_all is that there are multiple references to the same connector from different neurons. 
		//c_parent indicates which one we will keep, but we need to merge the information from the other references onto this one.
		//This is the easy part because connectors only have 1 (or zero) presynaptic connections.
		
		//(all references to this connector will be in columns (counter[c_all[i]][whichever value])
		//Find the connector that has information on presynapse
		//Set that information to the connector that will not be deleted
		//then erase the presynaptic connector
		for(int i = 0; i<c_all.length; i++){
			int skel_ind = counter[c_all[i]][0];
			int conn_ind = counter[c_all[i]][1];
			Skeleton ref_skel = network.getSkeleton(skel_ind);
			Connector ref_con = network.getSkeleton(skel_ind).getConnector(conn_ind);
			if(ref_con.getPresynaptic_to()[1]>-1){
				//Find the treenode to connect to 
				int tr_skeleton_id = ref_con.getPresynaptic_to()[0];
				int tr_treenode_id = ref_con.getPresynaptic_to()[1];
				Treenode tr_presynap = network.getSkeletonById(tr_skeleton_id).getTreenodeById(tr_treenode_id);
				
				//Find our main connector
				int co_skeleton_index = counter[c_parent][0]; //using the known index directly is faster than findSkeletonById, which requires a for-loop
				int co_connector_index = counter[c_parent][1];
				Connector co_main = network.getSkeleton(co_skeleton_index).getConnector(co_connector_index);
				
				//Assign presynaptic connection to the actual treenode
				co_main.setPresynaptic_node(tr_presynap);
				
				//Copy info to main that might not already have been there
				co_main.setPresynaptic_to(ref_con.getPresynaptic_to());
				
				//Redirect all references from ref to our main connector (this does nothing if ref was already the main connector)
//				network.getSkeleton(skel_ind).setConnector(conn_ind, co_main); //do this after all pre/postsynapse nodes have been set
			}	
		}
	}
	
	public static void link_postsynapse_node(NeuralNet network, int[][] counter, int[][] m_counter, int connector_id){
		int param_conn_id = 3; //row in counter that has connector ID
		int param_main_id = 4; //this row is -1 if the index is a repeat, and a positive number corresponding counter to m_counter if not
		
		//Find the node tagged as parent (not repeat)
		int[] params = {param_conn_id, connector_id, param_main_id, 0}; //this value will be 0 if it's the 'original', or -1 if it's a repeat 
		int c_parent = find_column_by_unique_value_array(counter, params);
		int[] c_all = find_all_column_by_unique_value(counter, param_conn_id, connector_id);
		
		//The purpose of c_parent vs c_all is that there are multiple references to the same connector from different neurons. 
		//c_parent indicates which one we will keep, but we need to merge the information from the other references onto this one.
		//This is the hard part because connectors can have multiple postsynaptic connections.
		
		//(all references to this connector will be in columns (counter[c_all[i]][whichever value])
		//Loop through all connectors that have information on postsynapse
		//Set that information to the connector that will not be deleted
		//then erase the postsynapse_connector
		
		
		//Find our main connector
		int co_skeleton_index = counter[c_parent][0]; //using the known index directly is faster than findSkeletonById, which requires a for-loop
		int co_connector_index = counter[c_parent][1];
		Connector co_main = network.getSkeleton(co_skeleton_index).getConnector(co_connector_index);
		
		System.out.println(co_main);
		int s_i = 0; //determine the total number of postsynaptic nodes we're about to place
		for(int i = 0; i<c_all.length; i++){ 
			int skel_ind = counter[c_all[i]][0];
			int conn_ind = counter[c_all[i]][1];
			Skeleton ref_skel = network.getSkeleton(skel_ind);
			Connector ref_con = network.getSkeleton(skel_ind).getConnector(conn_ind);
			s_i += ref_con.getPostsynaptic_to().length;
		} System.out.println(s_i);

		
		int p_i = 0; //Keep track of where to place the next connector in the array
		int[][] postsynaptic_to = new int[s_i][2];
		Treenode[] tr_postsynap_arr = new Treenode[s_i]; //initialize the treenode array so that we have somewhere to place our nodes

		for(int i = 0; i<c_all.length; i++){
			int skel_ind = counter[c_all[i]][0];
			int conn_ind = counter[c_all[i]][1];
			Skeleton ref_skel = network.getSkeleton(skel_ind);
			Connector ref_con = network.getSkeleton(skel_ind).getConnector(conn_ind);
			
			
			//loop through all connectors referenced in this node
			for(int j = 0; j<ref_con.getPostsynaptic_to().length; j++){ //<--getPostsynaptic_to().length = 0 when there are no postsynapses
				//Find the treenode to connect to 
				int tr_skeleton_id = ref_con.getPostsynaptic_to()[j][0];
				int tr_treenode_id = ref_con.getPostsynaptic_to()[j][1];
				Treenode tr_postsynap = network.getSkeletonById(tr_skeleton_id).getTreenodeById(tr_treenode_id);
				
				System.out.print("Skeleton: " + skel_ind +  " Connector: " + ref_con + " Connector ID: " + ref_con.getId());
				System.out.println(" Treenode: " + tr_postsynap + " treenode ID: " + tr_postsynap.getId());
				
				//Assign postsynaptic connection to the actual treenode
				postsynaptic_to[p_i][0] = tr_skeleton_id;
				postsynaptic_to[p_i][1] = tr_treenode_id;
				tr_postsynap_arr[p_i]=tr_postsynap;
				p_i++;
			}	
			//Copy info to main that might not already have been there
//			co_main.setPostsynaptic_to(postsynaptic_to);
			co_main.setPostsynaptic_node(tr_postsynap_arr);
			//Redirect all references from ref to our main connector (this does nothing if ref was already the main connector)
			network.getSkeleton(skel_ind).setConnector(conn_ind, co_main); //do this after presynapse nodes have been set
		}
	}
	
	public static void make_node_main(NeuralNet network,int[][] counter,int[][] m_counter,int connector_id){
		int param_conn_id = 3; //row in counter that has connector ID
		int param_main_id = 4; //this row is -1 if the index is a repeat, and 0 if not
		
		//Find the node tagged as parent (not repeat)
		int[] params = {param_conn_id, connector_id, param_main_id, 0}; //this value will be 0 if it's the 'original', or -1 if it's a repeat 
		int c_parent = find_column_by_unique_value_array(counter, params);
		int[] c_all = find_all_column_by_unique_value(counter, param_conn_id, connector_id);
		
		for(int i = 0; i<c_all.length; i++){
			if(counter[c_all[i]][param_main_id]>-1){
				int skel_ind = counter[c_all[i]][0];
				int conn_ind = counter[c_all[i]][1];
				Skeleton ref_skel = network.getSkeleton(skel_ind);
				Connector ref_con = network.getSkeleton(skel_ind).getConnector(conn_ind);
				
				//Find our main connector
				int co_skeleton_index = counter[c_parent][0]; //using the known index directly is faster than findSkeletonById, which requires a for-loop
				int co_connector_index = counter[c_parent][1];
				Connector co_main = network.getSkeleton(co_skeleton_index).getConnector(co_connector_index);
				
				//Redirect all references from ref to our main connector (this does nothing if ref was already the main connector)
				network.getSkeleton(skel_ind).setConnector(conn_ind, co_main); //do this after all pre/postsynapse nodes have been set
			}	
		}
	}
	
	public static int find_column_by_unique_value(int[][] matrix, int row, int value){
		for(int i = 0; i<matrix.length; i++){
			if(matrix[i][row]==value){
				return i; //return upon first finding
			}
		}
		return -1; //if value never matched
	}
	
	public static int find_column_by_unique_value_array(int[][] matrix, int[] params){
		//Params{row_1, val_1, row_2, val_2, ...}
		//Every pair of inputs is to find all columns with value [val] in row [row].
		
		
		//calculate necessary size of an array that would pair all columns with any matching values
		int column_size = 0;
		for(int i = 0; i<params.length; i+=2){
			column_size += find_all_column_by_unique_value(matrix, params[i],params[i+1]).length;
		}
		
		//fill an array that returns all columns with a matching parameter pair
		int next = 0;
		int[] all_possible_columns = new int[column_size];
		for(int i = 0; i<params.length; i+=2){
			 int[] columns = find_all_column_by_unique_value(matrix, params[i],params[i+1]);
			 for(int j = 0; j<columns.length;j++){
				 all_possible_columns[next] = columns[j]; 
				 next++;
			 }
		}
		
		//Find the column that had matched for every parameter
		//To do this, sort the array and then find the where the value matches with all of the last [#params]
		sort_array(all_possible_columns);
		int param_quant = params.length/2;
		
		for(int i = param_quant-1; i<all_possible_columns.length; i++){
			if(all_possible_columns[i] == all_possible_columns[i-(param_quant-1)]){
				//If this value matches with all of the last, then we have succeeded in finding the column that matches all of our parameters!
				return all_possible_columns[i];
			}
		}
		return -1;
		
	}
	
	public static int[] find_all_column_by_unique_value(int[][] matrix, int row, int value){
		int columnSize = 0;
		for(int i = 0; i<matrix.length; i++){
			if(matrix[i][row]==value){
				columnSize++; //return upon first finding
			}
		}
		if(columnSize == 0){
			return null;
		}
		int c_i = 0;
		int[] columnList = new int[columnSize];

		for(int i = 0; i<matrix.length; i++){
			if(matrix[i][row]==value){
				columnList[c_i] = i;
				c_i++;
			}
		}
		return columnList; //null if nothing matched
	}
	
	
}
