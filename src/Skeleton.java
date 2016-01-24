import java.util.ArrayList;

public class Skeleton {
	private int id;
	private ArrayList<Treenode> treenodes;
	private ArrayList<Connector> connectors;
	private ArrayList<Integer> treenode_id;
	private ArrayList<Integer> connector_id;
	
	
	//set-calls (currently for entire arrays only)
	public void setId(int id){
		this.id = id;
	}
	public void setTreenode(ArrayList<Treenode> treenodes){
		this.treenodes = treenodes;
	}
	public void setTreenode(int i, Treenode treenode){
		this.treenodes.add(i, treenode);
	}
	public void setTreenode(Treenode treenode){
		this.treenodes.add(treenode);
	}
	public void setTreenode_id(ArrayList<Integer> treenode_id){
		this.treenode_id = treenode_id;
	}
	public void setConnector(ArrayList<Connector> connectors){
		this.connectors = connectors;
	}
	public void setConnector(int i, Connector connector){
		this.connectors.add(i,connector);
	}
	public void setConnector(Connector connector){
		this.connectors.add(connector);
	}
	public void setConnector_id(ArrayList<Integer> connector_id){
		this.connector_id = connector_id;
	}
	
	//Get-calls (currently for entire arrays only)
	public int getId(){
		return id;
	}
	public ArrayList<Treenode> getTreenode(){
		return treenodes;
	}
	public Treenode getTreenode(int i){
		return treenodes.get(i);
	}
	public Treenode getTreenodeById(int id){ //for getting the skeleton by its ID
		for(Treenode node : treenodes){
			if(node.getId()==id){
				return(node);
			}
		}
		System.out.println("Warning: Could not find node #" + id + " in neuron #" + this.id);
		return null; //return null if ID does not exist
	}
	public ArrayList<Connector> getConnector(){
		return connectors;
	}
	public Connector getConnector(int i){
		return connectors.get(i);
	}
	public Connector getConnectorById(int id){
		for(Connector connector : connectors){
			if(connector.getId()==id){
				return(connector);
			}
		}
		System.out.println("Warning: Could not find connector #" + id + " in neuron #" + this.id);
		return null; //return null if ID does not exist
	}
	public int getConnectorIndex(int id){
		for(int i = 0; i< connectors.size(); i++){
			if(connectors.get(i).getId()==id){
				return(i);
			}
		}
		System.out.println("Warning: Could not find connector #" + id + " in neuron #" + this.id);
		return -1; //return -1 if ID does not exist
	}
	
	//CONSTRUCTOR CLASS
	public Skeleton(int id){
		this.id = id;
	}

	public void link_treenodes(){
		//O(n^2)
		for(Treenode node: treenodes){
			Integer p_i = node.getParent_id();
			if(p_i!=null && p_i> -1){
				Treenode parent = getTreenodeById(p_i);
				node.setParent(parent);
				parent.setChild(node);
			}
		}
	}
	
	//PRINT CONTENTS
	public void printContents(){
		System.out.print("neuron #" + id + " consists of: " + treenodes.size() + " nodes and " + connectors.size() + " connectors:\n");
		if(treenodes.size()>0){
			System.out.print("nodes: ");
			for(Treenode node : treenodes){
				System.out.print(node.getId() + "\t");
			} System.out.print("\n");
		}
		if(connectors.size()>0){
			System.out.print("connectors: ");
			for(Connector connector : connectors){
				System.out.print(connector.getId() + "\t");
			} System.out.print("\n");
		}
		System.out.print("\n");
	}
}
