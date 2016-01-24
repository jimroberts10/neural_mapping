import java.util.ArrayList;

public class Connector {
	private int id;
	private int skeleton_id;
	private int[] location;
	private Integer[] presynaptic_to; //essentially parent (row 0: skeleton id, row 1: connector id)
	private ArrayList<Integer[]> postsynaptic_to; //essentially child (row 0: skeleton id, row 1: node id)
	private Treenode presynaptic_node;
	private ArrayList<Treenode> postsynaptic_node;
	private boolean tag_for_deletion;
	
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
	public void setPresynaptic_to(Integer[] presynaptic_to){
		this.presynaptic_to = presynaptic_to;
	}
	public void setPostsynaptic_to(ArrayList<Integer[]> postsynaptic_to){
		this.postsynaptic_to = postsynaptic_to;
	}
	public void setPresynaptic_node(Treenode presynaptic_node){
		this.presynaptic_node = presynaptic_node;
	}
	public void setPostsynaptic_node(ArrayList<Treenode> postsynaptic_node){
		this.postsynaptic_node = postsynaptic_node;
	}
	public void setPostsynaptic_node(Treenode postsynaptic_node, int i){
		this.postsynaptic_node.set(i, postsynaptic_node);
	}
	public void setTag_for_deletion(boolean tag_for_deletion){
		this.tag_for_deletion = tag_for_deletion;
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
	public Integer[] getPresynaptic_to(){
		return presynaptic_to;
	}
	public ArrayList<Integer[]> getPostsynaptic_to(){
		return postsynaptic_to;
	}
	public Treenode getPresynaptic_node(){
		return presynaptic_node;
	}
	public ArrayList<Treenode> getPostsynaptic_node(){
		return postsynaptic_node;
	}
	public Treenode getPostsynaptic_node(int i){
		return postsynaptic_node.get(i);
	}
	public boolean getTag_for_deletion(){
		return tag_for_deletion;
	}
	
	//add calls (same as get calls, but with checks for if contents are full or adding is null)
	public void addPresynaptic_to(Integer[] presynaptic_to){
		if(presynaptic_to == null){
			return;
		}
		if(this.presynaptic_to != null){
			System.out.println("Presynaptic node is already filled");
			return;
		}
		this.presynaptic_to = presynaptic_to;
	}
	public void addPostsynaptic_to(ArrayList<Integer[]> postsynaptic_to){
		if(postsynaptic_to == null){
			return;
		}
		if(postsynaptic_to.size() < 1){
			return;
		}
		if(this.postsynaptic_to == null){
			this.postsynaptic_to = postsynaptic_to;
			return;
		}
		for(Integer[] syn: postsynaptic_to){
			this.postsynaptic_to.add(syn);
		}
	}
	public void addPostsynaptic_node(Treenode postsynaptic_node){
		if(postsynaptic_node==null){
			return;
		}
		if(this.postsynaptic_node == null){
			this.postsynaptic_node = new ArrayList<Treenode>();
		}
		this.postsynaptic_node.add(postsynaptic_node);
	}
	public void addPostsynaptic_node(ArrayList<Treenode> postsynaptic_node){
		if(postsynaptic_node == null){
			return;
		}
		if(postsynaptic_node.size() < 1){
			return;
		}
		if(this.postsynaptic_node == null){
			this.postsynaptic_node = postsynaptic_node;
			return;
		}
		for(Treenode syn: postsynaptic_node){
			this.postsynaptic_node.add(syn);
		}
	}
	
	//CONSTRUCTOR CLASS
	public Connector(int id, int skeleton_id){
		this.id = id;
		this.skeleton_id = skeleton_id;
		this.location = new int[3];
		this.tag_for_deletion = false;
	}
	
	//PRINT CONTENTS
	public void printContents(){
		System.out.print("Connector #" + id + ":\n");
		System.out.print("Location: [" + location[0] + "," + location[1] + "," + location[2] + "]\n");
		if(presynaptic_to!=null){
			System.out.println("Presynaptic to: " + presynaptic_to[0] + " in " + presynaptic_to[1]);
		}
		if(presynaptic_node!=null){
			System.out.print("Presynaptic node: " + presynaptic_node.getId() + " in " + presynaptic_node.getSkeleton_id() + "\n");
		}
		else{
			System.out.print("The presynaptic node for this connector was not available in this dataset\n");
		}
		if(postsynaptic_to!=null){
			System.out.print("Postsynaptic to: \n");
			for(Integer[] node_id : postsynaptic_to){
				System.out.print("\tnode: " + node_id[0]);
			} System.out.print("\n");
			for(Integer[] node_id : postsynaptic_to){
				System.out.print("\tneuron: " + node_id[1]);
			} System.out.print("\n");
		}
		if(postsynaptic_node!=null){
			System.out.print("Postsynaptic nodes: ");
			for(Treenode node : postsynaptic_node){
				System.out.print(node.getId() + "\t");
			}
		}
		System.out.print("\n");
	}
}
