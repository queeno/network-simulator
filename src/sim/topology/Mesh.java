/*
 * Copyright (c) 2010-2012, James Hanlon & Simon Aquino
 * All rights reserved.
 * 
 * Made available under the BSD license - see the LICENSE file
 */ 
package sim.topology;

import sim.Config;
import sim.Config.TopologyType;
import sim.components.Node;
import java.util.ArrayList;

public class Mesh {

	private static int _k;
	private static int _n;
	
	// Useful to retrieve upper level.
	public static enum Direction {Up, Down, Left, Right, UpRight, DownRight, UpLeft, DownLeft};
	
	/*
	 * For each node and each dimension, make the left and right connections,
	 * except on edges where posInDim==0 or k-1
	 * 
	 * Dimension i: (L) <-- (N) --> (R)
	 */
	public static Node[] create(int k, int n) {

		_k = k;
		_n = n;
		
		int numNodes = (int) Math.pow(_k, _n);
		Node[] nodes = new Node[numNodes];
		
		// Create all the nodes
		for(int node=0; node<numNodes; node++) {
			int connections = getNumConns(node);
		    nodes[node] = new Node(node, connections, connections);
		}
		
		// Connect them
		for(int node=0; node<nodes.length; node++) {
			
			for(int dim = 0; dim < n; dim++) {

				//System.out.println("Dimension "+dim);
				
			    int leftNode  = getLeftNode(node, dim);
			    int rightNode = getRightNode(node, dim);
			  
			    if(leftNode != -1) {
			    	int leftPort = getLeftPort(node, dim);
			  	    int leftNodeRightPort = getRightPort(leftNode, dim);
				    //System.out.println("Connecting node "+node+"["+leftPort+"] to leftNode "+leftNode+"["+leftNodeRightPort+"]");
				    nodes[node].connectTo(nodes[leftNode], leftPort, leftNodeRightPort);
			    }
			
			    if(rightNode != -1) {
			    	int rightPort = getRightPort(node, dim);
				    int rightNodeLeftPort = getLeftPort(rightNode, dim);
				    //System.out.println("Connecting node "+node+"["+rightPort+"] to rightNode "+rightNode+"["+rightNodeLeftPort+"]");
				    nodes[node].connectTo(nodes[rightNode], rightPort, rightNodeLeftPort);
			    }
		    }

		    nodes[node].finishConnecting();
		}
	
		return nodes;
	}

	/*
	 * Return the number of the base (i.e. left) port in dimension dim
	 */
	public static int getLeftPort(int node, int dim) {
		
		int port = -1;
		
		//System.out.print("get left port of node "+node+": ");
		for(int d = 0; d <= dim; d++) {
			int kToD = (int) Math.pow(_k, d);
			int posInDim = (node / kToD) % _k;
			
			//System.out.print("D"+d+" pos="+posInDim);
			// For each available direction in a dimension, add a port
			if(posInDim > 0)
				port++;
			if(posInDim < _k-1 && d < dim)
				port++;
		}
		
		//System.out.println(" PORT = "+port);
		return port;
	}
	
	public static int getRightPort(int node, int dim) {
		
		int port = -1;
		
		//System.out.print("get right port of node "+node+": ");
		for(int d = 0; d <= dim; d++) {
			int kToD = (int) Math.pow(_k, d);
			int posInDim = (node / kToD) % _k;
			
			//System.out.print("D"+d+" pos="+posInDim);
			// For each available direction in a dimension, add a port
			if(posInDim > 0)
				port++;
			if(posInDim < _k-1)
				port++;
		}
		
		//System.out.println(" PORT = "+port);
		return port;
	}
	
	/*
	 * Return the number of connections (input and output) to a node
	 */
	private static int getNumConns(int node) {
		
		int connections = 2*_n;
		
		// For each dimension, take off a connection if at edge
		for(int dim = 0; dim < _n; dim++) {
			int kToDim = (int) Math.pow(_k, dim);
			int posInDim = (node / kToDim) % _k;
			
			if(posInDim == 0 || posInDim == _k-1)
				connections--;
		}
		
		return connections;
	}
	
	private static int getLeftNode(int node, int dim) {

	  int kToDim = (int) Math.pow(_k, dim);
	  int posInDim = (node / kToDim) % _k;

	  // if at the left edge of the dimension, return no node (-1)
	  return posInDim == 0 ? -1 : (node - kToDim);
	}

	private static int getRightNode(int node, int dim) {

	  int kToDim = (int) Math.pow(_k, dim);
	  int posInDim = (node / kToDim) % _k;

	  // if at the right edge of the dimension, return no node (-1)
	  return posInDim == _k-1 ? -1 : (node + kToDim);
	}
	

	public static ArrayList<Integer> ReturnNodesInAllDirections(int node, int k){
		/*
		 * Given a node, returns all the node in its neighbourhood.
		 * 
		 * It is expected the given node won't be on the lowest level, otherwise
		 * the function will return null.
		 * 
		 * 	Returns:
		 * 		All the nodes in the neighbourhood.
		 * 		null if node is on lowest level.
		 */
		int[][] dest_matrix = GenerateDestMatrix(k);
		
		ArrayList<Integer> nodes_in_all_directions = new ArrayList<Integer>();
		
		int n_x = node / k;
		int n_y = node % k;
		
		if (getLevel(0, k) == getLevel(node, k))
			return null;
		
		// Up
		nodes_in_all_directions.add(dest_matrix[n_x][n_y+1]);
		// Down
		nodes_in_all_directions.add(dest_matrix[n_x][n_y-1]);
		// Right
		nodes_in_all_directions.add(dest_matrix[n_x+1][n_y]);
		// Left
		nodes_in_all_directions.add(dest_matrix[n_x-1][n_y]);
		
		return nodes_in_all_directions;
	}
	
	public static int ChoosePath(int node, int k, Direction dir){
		/*
		 * Given a node and a direction, return the node in the direction
		 * specified.
		 * 
		 * Args:
		 * 		node		:	id of the node.
		 * 		k			:	nodes in a single dimension.
		 * 		dir			:	direction of node.
		 * 
		 * Returns:
		 * 		Node in that direction.
		 * 		-1 if doesn't exist
		 * 
		 */
		
		int[][] dest_matrix = GenerateDestMatrix(k);
		
		int n_x = node / k;
		int n_y = node % k;
		
		int t_node = -1;
		
		try{
			switch (dir){
		
				case Up:
					t_node = dest_matrix[n_x][n_y+1];
					break;
				
				case Down:
					t_node = dest_matrix[n_x][n_y-1];
					break;
				
				case Left:
					t_node = dest_matrix[n_x-1][n_y];
					break;
				
				case Right:
					t_node = dest_matrix[n_x+1][n_y];
					break;
				
			}
		}
		// Return -1 if I make an illegal request (getting out of bounds).
		catch(ArrayIndexOutOfBoundsException e){
			return -1;
		}
		
		return t_node;
	}
	
	public static int ReturnUpNode(int node, int master, int k){
		/*
		 * Given a node and K, return node in upper level or source in current level.
		 * This function can be used in a MapReduce application.
		 * 
		 * Args:
		 * 		node		:	id of the node.
		 * 		k			:	nodes in single dimension.
		 * 
		 * Return:
		 * 		Node in the upper level or source in current level.
		 * 		-1 if upper level doesn't exist (current level is already the
		 *  									 highest).
		 */
		
		int up_node = -1;
		
		if (node == master)
			return -1;
		
		// Check BOTH first and then left/right - up/down singularly.
		
		if (IsUpAllowed(node, master, k) && !IsLeftOrRightAllowed(node, master, k))
			// route down
			up_node = ChoosePath(node, k, Direction.Down);
			
		else if (IsDownAllowed(node, master, k) && !IsLeftOrRightAllowed(node, master, k))
			// route up
			up_node = ChoosePath(node, k, Direction.Up);
			
		else if (IsLeftAllowed(node, master, k))
			// route right
			up_node = ChoosePath(node, k, Direction.Right);
			
		else if (IsRightAllowed(node, master, k))
			// route left
			up_node = ChoosePath(node, k, Direction.Left);
	
		return up_node;
	}
	
	public static boolean IsLeftOrRightAllowed(int node, int master, int k){
		
		return IsLeftAllowed(node, master, k) || IsRightAllowed(node, master, k);
	
	}
	
	public static boolean IsAdjacentToMaster (int node, int master, int k){
		/*
		 * Given node, k and master node, say whether the master is directly
		 * reachable from node.
		 * 
		 * Please don't select a master on the lowest level.
		 * 
		 * Args:
		 * 		node		:	id of the node.
		 * 		k			:	nodes in single dimension.
		 * 		master		:	id of master node.
		 * 
		 * Return:
		 * 		True: reachable.
		 * 		False: not reachable.
		 */
		
		int[][] dest_matrix = GenerateDestMatrix(k);
		
		int n_x = master / k;
		int n_y = master % k;
		
		if (dest_matrix[n_x][n_y+1] == node)
			return true;
		else if (dest_matrix[n_x][n_y-1] == node)
			return true;
		else if (dest_matrix[n_x+1][n_y] == node)
			return true;
		else if (dest_matrix[n_x-1][n_y] == node)
			return true;
		
		return false;
	}
	
	
	
	public static ArrayList<Integer> ReturnLowerNodes(int node, int master, int k){
		
		int[][] dest_matrix = GenerateDestMatrix(k);
		
		ArrayList<Integer> lower_nodes = new ArrayList<Integer>();
		
		int n_x = node / k;
		int n_y = node % k;
		
		// Check the level, referencing the 0 node.
		if (getLevel(0, k) == getLevel(node, k))
			// you are already on lowest level.
			return null;
			
		if (IsUpAllowed(node, master, k))
			lower_nodes.add( dest_matrix[n_x][n_y+1] );
		
		if (IsDownAllowed(node, master, k))
			lower_nodes.add( dest_matrix[n_x][n_y-1] );
		
		if (IsLeftAllowed(node, master, k) )
			lower_nodes.add( dest_matrix[n_x-1][n_y] );
		
		if (IsRightAllowed(node, master, k) )
			lower_nodes.add( dest_matrix[n_x+1][n_y] );
		
		return lower_nodes;
		
	}

	public static boolean IsUpAllowed(int node, int master, int k){
		
		int n_y = node % k;
		int m_y = master % k;
		
		if (n_y >= m_y)
			return true;
		else
			return false;
		
	}
	
	public static boolean IsDownAllowed(int node, int master, int k){

		int n_y = node % k;
		int m_y = master % k;
		
		if (n_y <= m_y)
			return true;
		else
			return false;
	}
	
	public static boolean IsRightAllowed(int node, int master, int k){
		
		int n_x = node / k;
		int n_y = node % k;
		
		int m_x = master / k;
		int m_y = master % k;
		
		if (n_y == m_y){
			if (n_x >= m_x)
				return true;
		}
		
		return false;
	}
	
	public static boolean IsLeftAllowed(int node, int master, int k){
		
		int n_x = node / k;
		int n_y = node % k;
		
		int m_x = master / k;
		int m_y = master % k;
		
		if (n_y == m_y){
			if (n_x <= m_x)
				return true;
		}

		return false;
	}
	
	public static int getLevel(int node, int k){
		/*
		 * Given a node id and K, work out the level id.
		 *
		 * Returns:
		 * 		Level identifier.
		 * 		-1 if node doesn't exist in mesh.
		 */
		if (node >= k*k)
			return -1;
		
		int[][] n_matrix = GenerateLevelMatrix(k);
		
		int n_x = node / k;
		int n_y = node % k;

		return n_matrix[n_x][n_y];
		
	}
	
	public static int DefaultMaster(){
		/*
		 * Calculates default master for Mesh and Tring.
		 * 
		 * Default master for MESH:	 m = DestMatrix[k/2][k/2]
		 * Default master for TRING: m = 0 
		 * 
		 */
		int default_master = 0;
		
		int k = Config.k();
		
		int[][] n_matrix = GenerateDestMatrix(k);
	
		if (Config.topology().equals(TopologyType.TRING))
			default_master = 0;
		else
			default_master = n_matrix[k/2][k/2];
		
		return default_master;
	}
	
	public static int[][] GenerateDestMatrix(int k){
		/*
		 * Generate matrix of node identifiers.
		 */
		int[][] n_matrix = new int[k][k];
		
		int n = 0;
				
		// Generate level
				
		for(int i=0;i<k; i++){
			for(int j=0; j<k;j++){
				
				n_matrix[i][j] = n;
				n++;
			}
		}
				
		/*for (int j=0;j<k;j++){
			for(int i=0;i<k;i++){
				System.out.print(n_matrix[i][j] + " ");
			}
			System.out.print("\n");
		}*/
		
		return n_matrix;
		
		
	}
	
	public static int[][] GenerateLevelMatrix(int k){
		/*
		 * Generate matrix of levels.
		 */
		// Build matrix
		
		int[][] n_matrix = new int[k][k];
				
		int i=0;
		int j=k-1;
		int n = k-k/2;
				
		// Generate level
				
		while (j > i){
			// Fill level 0 and k-1
			for (int ii=i; ii<=j; ii++){
				n_matrix[i][ii] = k-n;
				n_matrix[ii][i] = k-n;
			}
			
			for (int jj=j; jj>=(i); jj--){
				n_matrix[j][jj] = k-n;
				n_matrix[jj][j] = k-n;
			}
						
			i++;
			j--;
			n++;
		}
				
		/*for (i=0;i<k;i++){
			for(j=0;j<k;j++){
				System.out.print(n_matrix[i][j] + " ");
			}
			System.out.print("\n");
		}*/
		
		return n_matrix;
		
	}
	
	// The following code is another implementation of mapreduce that won't
	// be used for this project. It has been commented out.
	/*public static int ReturnUpNode(int node, int k){
		*
		 * Given a node and K, return node in upper level.
		 * This function can be used in a MapReduce application.
		 * 
		 * Args:
		 * 		node		:	id of the node.
		 * 		k			:	nodes in single dimension.
		 * 
		 * Return:
		 * 		Node in the upper level.
		 * 		-1 if upper level doesn't exist (current level is already the
		 *  									 highest).
		 *
		
		int[][] dest_matrix = GenerateDestMatrix(k);
		
		int n_x = node / k;
		int n_y = node % k;
		
		// Check BOTH first and then left/right - up/down singularly.
		
		if (IsUpDownAllowed(node, k) && IsLeftRightAllowed(node, k))
			return -1;
		
		else if (IsUpDownAllowed(node, k) && IsLeftAllowed(node, k))
			return dest_matrix[n_x+1][n_y];
	
		else if (IsUpDownAllowed(node, k) && IsRightAllowed(node, k))
			return dest_matrix[n_x-1][n_y];
		
		else if (IsDownAllowed(node, k) && IsLeftRightAllowed(node, k))
			return dest_matrix[n_x][n_y+1];
		
		else if (IsUpAllowed(node, k) && IsLeftRightAllowed(node, k))
			return dest_matrix[n_x][n_y-1];
		
		else if (IsUpAllowed(node, k) && IsLeftAllowed(node, k))
			return FindUpperLevel(node, k, Direction.UpLeft);
		
		else if (IsUpAllowed(node, k) && IsRightAllowed(node, k))
			return FindUpperLevel(node, k, Direction.UpRight);
		
		else if (IsDownAllowed(node, k) && IsLeftAllowed(node, k))
			return FindUpperLevel(node, k, Direction.DownLeft);
		
		else if (IsDownAllowed(node, k) && IsRightAllowed(node, k))
			return FindUpperLevel(node, k, Direction.DownRight);
		
		else
			return -1;
		
	}
	
	public static int FindUpperLevel(int node, int k, Direction dir){
	*
	 * Given node, k and direction, find node on upper level in mesh.
	 * 
	 * This function should only be used within the mesh class and
	 * it is public only to allow UnitTesting.
	 * 
	 * Args:
	 * 		node		:	id of the node.
	 * 		k			:	nodes in single dimension.
	 * 		dir			:	Direction enum type.
	 * 
	 * Return:
	 * 		Node in upper level in the specified direction.
	 * 		-1 if upper level doesn't exist.
	 * 	
	 *
		int[][] dest_matrix = GenerateDestMatrix(k);
		
		int up_node = -1;
		
		int n_x = node / k;
		int n_y = node % k;
		
		switch (dir){
			
			case DownLeft:

				if (n_x == n_y)
					up_node = dest_matrix[n_x+1][n_y+1];
				else if (n_x > n_y)
					up_node = dest_matrix[n_x][n_y+1];
				else if (n_x < n_y)
					up_node = dest_matrix[n_x+1][n_y];
				
				break;
				
			case DownRight:
				
				if (n_x + n_y == k-1)
					up_node = dest_matrix[n_x-1][n_y+1];
				else if (n_x == k-1)
					up_node = dest_matrix[n_x-1][n_y];
				else if (n_x != k-1)
					up_node = dest_matrix[n_x][n_y+1];
				
				break;
			
			case UpLeft:
				
				if (n_x + n_y == k-1)
					up_node = dest_matrix[n_x+1][n_y-1];
				else if (n_x == 0)
					up_node = dest_matrix[n_x+1][n_y];
				else if (n_x != 0)
					up_node = dest_matrix[n_x][n_y-1];	
				
				break;
				
			case UpRight:

				if (n_x == n_y)
					up_node = dest_matrix[n_x-1][n_y-1];
				else if (n_x > n_y)
					up_node = dest_matrix[n_x-1][n_y];
				else if (n_x < n_y)
					up_node = dest_matrix[n_x][n_y-1];
				
				break;	
		
		}
		
		// If we're still on the same level, then return -1.
		return getLevel(node, k) == getLevel(up_node, k) ? -1 : up_node;
		
	}
	
	private static boolean IsUpDownAllowed(int node, int k){
		
		return IsUpAllowed(node, k) && IsDownAllowed(node, k);
	}
	
	private static boolean IsLeftRightAllowed (int node, int k){
		
		return IsRightAllowed(node, k) && IsLeftAllowed(node, k);
	}
	
	public static ArrayList<Integer> ReturnLowerNodes(int node, int k){
		
		int[][] dest_matrix = GenerateDestMatrix(k);
		
		ArrayList<Integer> lower_nodes = new ArrayList<Integer>();
		
		int n_x = node / k;
		int n_y = node % k;
		
		// Check the level, referencing the 0 node.
		if (getLevel(0, k) == getLevel(node, k))
			// you are already on lowest level.
			return null;
		
		if (IsUpAllowed(node, k))
			lower_nodes.add( dest_matrix[n_x][n_y+1] );
		
		if (IsDownAllowed(node, k))
			lower_nodes.add( dest_matrix[n_x][n_y-1] );
		
		if (IsLeftAllowed(node, k))
			lower_nodes.add( dest_matrix[n_x-1][n_y] );
		
		if (IsRightAllowed(node, k))
			lower_nodes.add( dest_matrix[n_x+1][n_y] );
		
		return lower_nodes;
		
	}

	public static boolean IsUpAllowed(int node, int k){
		
		int n_y = node % k;
		
		if (k % 2 == 0)
			return ((k / 2)-1 < n_y) ? true : false;
		else
			return (k / 2 <= n_y) ? true : false;	
		
	}
	
	public static boolean IsDownAllowed(int node, int k){

		int n_y = node % k;
		
		if (k % 2 == 0)
			return (k / 2 > n_y) ? true : false;
		else
			return (k / 2 >= n_y) ? true : false;	
		
	}
	
	public static boolean IsRightAllowed(int node, int k){
		
		int n_x = node / k;
		
		if (k % 2 == 0)
			return ((k / 2)-1 < n_x) ? true : false;
		else
			return (k / 2 <= n_x) ? true : false;	

	}
	
	public static boolean IsLeftAllowed(int node, int k){
		
		int n_x = node / k;
		
		if (k % 2 == 0)
			return (k / 2 > n_x) ? true : false;
		else
			return (k / 2 >= n_x) ? true : false;			
		
	}
	*/
	
	/*private static void insertRandomFaults() {
		
	}*/

}
