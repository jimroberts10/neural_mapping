import java.util.ArrayList;

public class Treenode {
	private int id;
	private int skeleton_id;
	private int[] location;
	private Integer parent_id; 
	private Treenode parent;
	private ArrayList<Treenode> child;
	private Connector presynaptic_connector;
	private Connector postsynaptic_connector;

	//set variable calls
	public void setId(int id){
		this.id = id;
	}
	public void setSkeleton_id(int skeleton_id){
		this.skeleton_id = skeleton_id;
	}
	public void setLocation(int[] location){
		this.location = location;
	}
	public void setParent_id(int parent_id){
		this.parent_id = parent_id;
	}
	public void setParent(Treenode parent){
		this.parent = parent;
	}
	public void setChild(ArrayList<Treenode> child){
		this.child = child;
	}
	public void setChild(int i, Treenode child){
		this.child.add(i,child);
	}
	public void setChild(Treenode child){
		if(this.child==null){
			this.child = new ArrayList<Treenode>();
		}
		this.child.add(child);
	}
	public void setPresynaptic_connector(Connector presynaptic_connector){
		this.presynaptic_connector = presynaptic_connector;
	}
	public void setPostsynaptic_connector(Connector postsynaptic_connector){
		this.postsynaptic_connector = postsynaptic_connector;
	}
	
	//get variable calls
	public int getId(){
		return id;
	}
	public int getSkeleton_id(){
		return skeleton_id;
	}
	public int[] getLocation(){
		return location;
	}
	public Integer getParent_id(){
		return parent_id;
	}
	public Treenode getParent(){
		return parent;
	}
	public ArrayList<Treenode> getChild(){
		return child;
	}
	public Treenode getChild(int i){
		return child.get(i);
	}
	public Connector getPresynaptic_connector(){
		return presynaptic_connector;
	}
	public Connector getPostsynaptic_connector(){
		return postsynaptic_connector;
	}
	
	//CONSTRUCTOR CLASS
	public Treenode(int id, int skeleton_id){
		this.id = id;
		this.skeleton_id = skeleton_id;
		this.location = new int[3];
	}
	
	//PRINT CONTENTS
	public void printContents(){
		//Print node ID
		System.out.print("node #" + id + " in neuron #" + skeleton_id + ":\n");
		//Print coordinates in space
		System.out.print("Location: [" + location[0] + "," + location[1] + "," + location[2] + "]\n");
		//Print parent node (if it exists)
		if(parent!=null){
			System.out.print("Parent node: " + parent.getId() + "\n");
		}
		else{
			System.out.print("Parent node: this is root node\n");
		}
		//Print child nodes (if they exist)
		if(child!=null){
			System.out.print("Child nodes: ");
			for(Treenode node : child){
				System.out.print(node.getId() + "\t");
			}System.out.print("\n");
		}
		else{System.out.print("this node has no children\n");}
		
		//Print connections
		if(presynaptic_connector!=null){
			System.out.print("Presynapse connection: " + presynaptic_connector.getId() + "\n");
		}
		if(postsynaptic_connector!=null){
			System.out.print("Postsynapse connection: " + postsynaptic_connector.getId() + "\n");
		}
		
		System.out.print("\n");
	}
}
