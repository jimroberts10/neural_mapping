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
		System.out.println("");
		int[] skeleton_keys = testnet.getKeys();
		
		//Connect the treenodes in each neuron
		link_nodes(testnet);
		link_connectors(testnet);
		
		Skeleton neuron = testnet.getSkeleton(0);
		Treenode node = neuron.getTreenode(0);
		node.printContents();
		while(node.getChild()!=null){
			node = node.getChild(0);
			node.printContents();
		}
		
		/*
		//do sample calculations on a neuron
		Skeleton neuron = testnet.getSkeleton(0);
		
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
		*/
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
		tree_counter = sort_array_by_row(tree_counter,2); //This array was reusable. During code optimization, reuse the first generation of this array
		
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
	
	public static int[][] sort_array_by_row(int[][] array, int row){ 
		//sort array based on selected row
		//This is an easy to code, though inefficient sorting method [O(n^2)]
		//If code is running slowly, improve this algorithm first.
			
			
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
			return array;
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
		//build a counter that stores the id and locations (within the neuron arrays) of all connector nodes
		int[][] connector_counter = build_connector_counter(network);
		
		
		
		//build a new counter that merges duplicate entries (try not doing this)
		int[][] merged_counter = merge_counter(connector_counter, network);
		
		//still to do: link the remaining nodes
		
		//Look at the contents of the nodes
		for(int j = 0; j<merged_counter.length; j++){
			for(int i = 0; i<merged_counter[0].length;i++){
				System.out.print(merged_counter[j][i] + ", ");
			} System.out.println();
		} System.out.println("\n");
		
		
		
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
		int[][] connector_counter = new int[array_size][5]; 
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
				connector_counter[c_i][4] = 0; //row 4: is this a duplicate (0 if no, 1 if yes)
				for(int k = 0; k < c_i; k++){ //change row 4 = 1 if the id for this node already exists
					if (connector_counter[k][3]==connector_counter[c_i][3]){
						connector_counter[c_i][4] = 1;
					}
				}
				c_i++;
			}
		}
		return connector_counter;
	}
	
	public static int[][] merge_counter(int[][] counter, NeuralNet network){
		//determine new length (by shortening old length by 1 per duplicate node)
		int array_size = counter.length; //base size of same size as old array
		for(int i = 0; i<counter.length; i++){
			array_size -= counter[i][4]; //shorten the array by 1 for every duplicate (row 4 in counter kept track of duplicates)
		}
		int[][] m_counter = new int[array_size][5];
		int c_i = 0; //iterator that keeps track of next empty location in array
		//first place non-duplicates
		for(int i = 0; i<counter.length; i++){
			if(counter[i][4]==0){ /*don't handle duplicates yet*/ 
				m_counter[c_i][4] = 0; //while we conveniently have this loop, initialize # of postsynapse connections at 0;
				Connector connector = network.getSkeleton(counter[i][0]).getConnector(counter[i][1]); 
				if(connector.getPresynaptic_to() != -1){ //if this node has information on the presynapse connection
					m_counter[c_i][0] = counter[i][0]; //row 0: index for skeleton of presynaptic_to (i.e. parent node)
					m_counter[c_i][1] = counter[i][1]; //row 1: index for connector of presynaptic_to 
					m_counter[c_i][2] = counter[i][2]; //row 2: presynapse skeleton id
					m_counter[c_i][3] = counter[i][3]; //row 3: presynapse connector id
				}
				else{
					m_counter[c_i][4] += 1; //row 4: number of postsynpase connections (this 
				}
				c_i++;
			}
		}
		//now insert duplicates
		int t_i = 0; //iterator that identifies which location is being currently manipulated
		for(int i = 0; i<counter.length; i++){
			if(counter[i][4]==1){ /*for duplicates*/ 
				int id = counter[i][3];
				t_i = 0; //reset t_i
				while(counter[t_i][3]!=counter[i][3] && counter[t_i][4]==0){ //look for the instance where counter[t_i] was not labeled as the duplicate
					t_i++;
				}
				Connector connector = network.getSkeleton(counter[i][0]).getConnector(counter[i][1]); 
				if(connector.getPresynaptic_to() != -1){ //if this node has information on the presynapse connection
					m_counter[t_i][0] = counter[i][0]; //row 0: index for skeleton of presynaptic_to (i.e. parent node)
					m_counter[t_i][1] = counter[i][1]; //row 1: index for connector of presynaptic_to 
					m_counter[t_i][2] = counter[i][2]; //row 2: presynapse skeleton id
					m_counter[t_i][3] = counter[i][3]; //row 3: presynapse connector id
				}
				else{
					m_counter[t_i][4] += 1; //row 4: number of postsynpase connections 
				}
			}
		}
		return m_counter;
	}
}
