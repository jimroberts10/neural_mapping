import org.json.simple.JSONObject;

public class Treenode {
	private int id;
	private int[] location;
	private int parent_id;
	private Treenode parent;
	private Treenode[] child;

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
	public void setParent(Treenode parent){
		this.parent = parent;
	}
	public void setChild(Treenode[] child){
		this.child = child;
	}
	public void setChild(Treenode child, int ind){
		this.child[ind] = child;
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
	public Treenode getParent(){
		return parent;
	}
	public Treenode[] getChild(){
		return child;
	}
	public Treenode getChild(int ind){
		return child[ind];
	}
	//CONSTRUCTOR CLASS
	public Treenode(int id){
		this.id = id;
		this.location = new int[3];
	}
	
	//PRINT CONTENTS
	public void printContents(){
		System.out.print("node #" + id + ":\n");
		System.out.print("Location: [" + location[0] + "," + location[1] + "," + location[2] + "]\n");
		if(parent!=null){System.out.print("Parent node: " + parent.getId() + "\n");}
		else{System.out.print("Parent node: this is root node\n");}
		if(child!=null){System.out.print("Child nodes: ");for(int i = 0; i<child.length-1; i++){System.out.print(child[i].getId() + ", ");}System.out.print(child[child.length-1].getId() + "\n");}
		else{System.out.print("this node has no children\n");}
		System.out.print("\n");
	}
}
