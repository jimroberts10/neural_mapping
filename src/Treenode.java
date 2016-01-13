import org.json.simple.JSONObject;

public class Treenode {
	private int id;
	private int[] location;
	private int parent_id;
	
	//set variable calls
	public void setId(int id){
		this.id = id;
	}
	public void setLocation(int[] location){
		this.location = location;
	}
	public void setParent_id(int parent_id){
		this.parent_id = parent_id;
	}
	
	//get variable calls
	public int getId(){
		return id;
	}
	public int[] getLocation(){
		return location;
	}
	public int getParent_id(){
		return parent_id;
	}
	
	//CONSTRUCTOR CLASS
	public Treenode(int id){
		this.id = id;
	}
}
