import java.util.ArrayList;

public class Connector {
	private int id;
	private int[] location;
	private Integer[] presynaptic_to; //essentially parent (row 0: skeleton id, row 1: connector id)
	private ArrayList<Integer[]> postsynaptic_to; //essentially child (row 0: skeleton id, row 1: node id)
	private Treenode presynaptic_node;
	private ArrayList<Treenode> postsynaptic_node;
	
	//set variable calls
	public void setId(int id){
		this.id = id;
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
	
	//get variable calls
	public int getId(){
		return id;
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
	
	//CONSTRUCTOR CLASS
	public Connector(int id){
		this.id = id;
		this.location = new int[3];
	}
	
	//PRINT CONTENTS
	public void printContents(){
		System.out.print("node #" + id + ":\n");
		System.out.print("Location: [" + location[0] + "," + location[1] + "," + location[2] + "]\n");
		if(presynaptic_to!=null){
			System.out.println("Presynaptic to: " + presynaptic_to[1]);
		}
		if(presynaptic_node!=null){
			System.out.print("Presynaptic node: " + presynaptic_node.getId() + "\n");
		}
		else{
			System.out.print("The presynaptic node for this connector was not available in this dataset\n");
		}
		if(postsynaptic_to!=null){
			System.out.print("Postsynaptic to: \n");
			for(Integer[] node_id : postsynaptic_to){
				System.out.print("\n + node: " + node_id[1] + "\t");
			}
			for(Integer[] node_id : postsynaptic_to){
				System.out.print("\n + neuron: " + node_id[0] + "\t");
			}
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
