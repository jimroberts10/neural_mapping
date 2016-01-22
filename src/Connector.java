
public class Connector {
	private int id;
	private int[] location;
	private int[] presynaptic_to; //essentially parent (row 0: skeleton id, row 1: connector id)
	private int[][] postsynaptic_to; //essentially child (row 0: skeleton id, row 1: node id)
	private Treenode presynaptic_node;
	private Treenode[] postsynaptic_node;
	
	//set variable calls
	public void setId(int id){
		this.id = id;
	}
	public void setLocation(int[] location){
		this.location = location;
	}
	public void setPresynaptic_to(int[] presynaptic_to){
		this.presynaptic_to = presynaptic_to;
	}
	public void setPostsynaptic_to(int[][] postsynaptic_to){
		this.postsynaptic_to = postsynaptic_to;
	}
	public void setPresynaptic_node(Treenode presynaptic_node){
		this.presynaptic_node = presynaptic_node;
	}
	public void setPostsynaptic_node(Treenode[] postsynaptic_node){
		this.postsynaptic_node = postsynaptic_node;
	}
	public void setPostsynaptic_node(Treenode postsynaptic_node, int i){
		this.postsynaptic_node[i] = postsynaptic_node;
	}
	
	//get variable calls
	public int getId(){
		return id;
	}
	public int[] getLocation(){
		return location;
	}
	public int[] getPresynaptic_to(){
		return presynaptic_to;
	}
	public int[][] getPostsynaptic_to(){
		return postsynaptic_to;
	}
	public Treenode getPresynaptic_node(){
		return presynaptic_node;
	}
	public Treenode[] getPostsynaptic_node(){
		return postsynaptic_node;
	}
	public Treenode getPostsynaptic_node(int i){
		return postsynaptic_node[i];
	}
	
	//CONSTRUCTOR CLASS
	public Connector(int id){
		this.id = id;
	}
	
	//PRINT CONTENTS
		public void printContents(){
			System.out.print("node #" + id + ":\n");
			System.out.print("Location: [" + location[0] + "," + location[1] + "," + location[2] + "]\n");
			if(presynaptic_to!=null){System.out.println("Presynaptic to: " + presynaptic_to[1]);}
			if(presynaptic_node!=null){System.out.print("Presynaptic node: " + presynaptic_node.getId() + "\n");}
			else{System.out.print("The presynaptic node for this connector was not available in this dataset\n");}
//			if(postsynaptic_to!=null){System.out.print("Postsynaptic to: ");for(int i = 0; i<postsynaptic_to.length-1; i++){System.out.print(postsynaptic_to[i][1] + ", ");}System.out.print(postsynaptic_to[postsynaptic_to.length-1][1] + "\n");}
			if(postsynaptic_node!=null){if(postsynaptic_node.length>0){System.out.print("Postsynaptic nodes: ");for(int i = 0; i<postsynaptic_node.length-1; i++){System.out.print(postsynaptic_node[i].getId() + ", ");}System.out.print(postsynaptic_node[postsynaptic_node.length-1].getId() + "\n");}}
			else{System.out.print("this node has no children\n");}
			System.out.print("\n");
		}
}
