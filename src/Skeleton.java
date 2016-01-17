public class Skeleton {
	private int id;
	private Connector[] connectors;
	private Treenode[] treenodes;
	private int[] connectorKeys;
	private int[] treenodeKeys;
	
	//set calls (currently for entire arrays only)
	public void setId(int id){
		this.id = id;
	}
	public void setConnectors(Connector[] connectors){
		//set for an array of connectors
		this.connectors = connectors;
	}
	public void setTreenodes(Treenode[] treenodes){
		//set for an array of treenodes
		this.treenodes = treenodes;
	}
	public void setConnector(int i, Connector connector){
		//set an individual connector
		connectors[i] = connector;
	}
	public void setTreenode(int i, Treenode treenode){
		//set an individual treenode
		treenodes[i] = treenode;
	}
	public void setConnectorKeys(int[] connectorKeys){
		this.connectorKeys = connectorKeys;
	}
	public void setTreenodeKeys(int[] treenodeKeys){
		this.treenodeKeys = treenodeKeys;
	}
	
	
	//Get calls (currently for entire arrays only)
	public int getId(){
		return id;
	}
	public Connector[] getConnectors(){
		//get all connectors (as an array)
		return connectors;
	}
	public Treenode[] getTreenodes(){
		//get all treenodes (as an array)
		return treenodes;
	}
	public Connector getConnector(int i){
		//get connector at index i
		return connectors[i];
	}
	public Treenode getTreenode(int i){
		//get treenode at index i
		return treenodes[i];
	}
	public int[] getConnectorKeys(){
		return connectorKeys;
	}
	public int[] getTreenodeKeys(){
		return treenodeKeys;
	}
	
	//CONSTRUCTOR CLASS
	public Skeleton(int id){
		this.id = id;
	}
	 
	//PRINT CONTENTS
	public void printContents(){
		System.out.print("neuron #" + id + " consists of: " + treenodes.length + " nodes and " + connectors.length + " connectors:\n");
		System.out.print("this matches the same value from keys: " + treenodes.length + " nodes and " + connectors.length + " connectors:\n");
		if(treenodes.length>0){System.out.print("nodes: ");for(int i = 0; i<treenodes.length-1; i++){System.out.print(treenodes[i].getId() + ", ");}System.out.print(treenodes[treenodes.length-1].getId() + "\n");}
		if(connectors.length>0){System.out.print("connectors: ");for(int i = 0; i<connectors.length-1; i++){System.out.print(connectors[i].getId() + ", ");}System.out.print(connectors[connectors.length-1].getId() + "\n");}
		System.out.print("\n");
	}
	
}
