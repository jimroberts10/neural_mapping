
public class Connector {
	private int id;
	private int[] location;
<<<<<<< HEAD
	private int presynaptic_to; //essentially parent
	private int[] postsynaptic_to; //essentially child
	private Treenode presynaptic_node;
	private Treenode[] postsynaptic_node;
=======
	private int[] postsynaptic_to;
	private int[] presynaptic_to;
>>>>>>> a39c4d64a72e34eea8f035c03f288d7b3b7dd9b5
	
	//set variable calls
	public void setId(int id){
		this.id = id;
	}
	public void setLocation(int[] location){
		this.location = location;
	}
<<<<<<< HEAD
	public void setPresynaptic_to(int presynaptic_to){
		this.presynaptic_to = presynaptic_to;
	}
	public void setPostsynaptic_to(int[] postsynaptic_to){
		this.postsynaptic_to = postsynaptic_to;
	}
	public void setPresynaptic_node(int presynaptic_to){
		this.presynaptic_to = presynaptic_to;
	}
	public void setPostsynaptic_node(int[] postsynaptic_to){
		this.postsynaptic_to = postsynaptic_to;
	}
	
=======
	public void setPostsynaptic_to(int[] postsynaptic_to){
		this.postsynaptic_to = postsynaptic_to;
	}
	public void setPresynaptic_to(int[] presynaptic_to){
		this.presynaptic_to = presynaptic_to;
	}
>>>>>>> a39c4d64a72e34eea8f035c03f288d7b3b7dd9b5
	
	//get variable calls
	public int getId(){
		return id;
	}
	public int[] getLocation(){
		return location;
	}
<<<<<<< HEAD
	public int getPresynaptic_to(){
		return presynaptic_to;
	}
	public int[] getPostsynaptic_to(){
		return postsynaptic_to;
	}
	public Treenode getPresynaptic_node(){
		return presynaptic_node;
	}
	public Treenode[] getPostsynaptic_node(){
		return postsynaptic_node;
=======
	public int[] getPostsynaptic_to(){
		return postsynaptic_to;
	}
	public int[] getPresynaptic_to(){
		return presynaptic_to;
>>>>>>> a39c4d64a72e34eea8f035c03f288d7b3b7dd9b5
	}
	
	//CONSTRUCTOR CLASS
	public Connector(int id){
		this.id = id;
	}
}
