
public class Connector {
	private int id;
	private int[] location;
	private int[] postsynaptic_to;
	private int[] presynaptic_to;
	
	//set variable calls
	public void setId(int id){
		this.id = id;
	}
	public void setLocation(int[] location){
		this.location = location;
	}
	public void setPostsynaptic_to(int[] postsynaptic_to){
		this.postsynaptic_to = postsynaptic_to;
	}
	public void setPresynaptic_to(int[] presynaptic_to){
		this.presynaptic_to = presynaptic_to;
	}
	
	//get variable calls
	public int getId(){
		return id;
	}
	public int[] getLocation(){
		return location;
	}
	public int[] getPostsynaptic_to(){
		return postsynaptic_to;
	}
	public int[] getPresynaptic_to(){
		return presynaptic_to;
	}
	
	//CONSTRUCTOR CLASS
	public Connector(int id){
		this.id = id;
	}
}
