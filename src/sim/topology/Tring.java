/*
 * Copyright (c) 2012, Simon Aquino
 * All rights reserved.
 * 
 * Made available under the BSD license - see the LICENSE file
 */ 
package sim.topology;

import sim.Utilities;
import sim.components.Node;

import java.util.ArrayList;

public class Tring {

	private static int left_port  = 0;
	private static int right_port = 1;
	private static int up_port	  = 2;
	private static int down_port  = 2;

	
	/* Start building a ring.
	 * k nodes should connect each other and then create a tree for n levels,
	 * making sure all the nodes are connected before creating another level.
	 * 
	 * (N) <----> (N)        (N) <----> (N)
	 *  ^		   ^          ^          ^
	 *  |		   |          |          |
	 *  |		   |          |          |
	 *  v          v          v          v
	 * (N) <----> (N)        (N) <----> (N)
	 *             ^          ^
	 *             |          |
	 *             |          |
	 *             v          v
	 *            (N) <----> (N)
	 *  		   ^          ^
	 *  		   |          |
	 *  		   |          |
	 *             v          v
	 *            (N) <----> (N)
	 *             ^          ^
	 *             |          |
	 *             |          |
	 *             v          v
	 * (N) <----> (N)        (N) <----> (N)
	 *  ^		   ^          ^          ^
	 *  |		   |          |          |
	 *  |		   |          |          |
	 *  v          v          v          v
	 * (N) <----> (N)        (N) <----> (N)
	 *             
	 * 
	 *               Tring
	 *            k = 4  n = 2
	 * 
	 * Variable conventions:
	 * 
	 * Node_id (within tring)	:	node
	 * Ring_id (within level)	:	rid
	 * Node_id (within ring)	:	r_nid
	 * Number of rings in level	:	rings_lev
	 * Specific level of tring	:	lev
	 * 
	 */
	
	public static Node[] create(int k, int n){

		
		int no_nodes = CalculateNoNodes(k, n);
		int no_conn;
		
		Node[] nodes = new Node[no_nodes];
		
		// Create all nodes
		for(int node=0; node<no_nodes;node++){
			no_conn = CalculateNoConnections(node, k, n);
			nodes[node] = new Node(node, no_conn, no_conn);
		}
		
		// Connect them
		for (int node=0; node<no_nodes; node++){
			
			int left_node = GetLeftNode(node, k);
			int right_node = GetRightNode(node, k);
			int upper_node = GetUpNode(node, k);
			int lower_node = GetDownNode(node, k, n);
			
			// Connect to left node.
			if (left_node != -1){
				nodes[node].connectTo(nodes[left_node], left_port, right_port);
			}
			
			// Connect to right node.
			if (right_node != -1){
				nodes[node].connectTo(nodes[right_node], right_port, left_port);
			}
			
			// Connect to upper level.
			if (upper_node != -1){
				nodes[node].connectTo(nodes[upper_node], down_port, up_port);
			}
			
			// Connect to lower level.
			if (lower_node != -1){
				nodes[node].connectTo(nodes[lower_node], up_port, down_port);
			}
			
			nodes[node].finishConnecting();
		}
		return nodes;
	}
	
	public static ArrayList<Integer> ReturnNodesLowerRing(int node, int k, int n){
		/* Returns an array containing the no. of the nodes connecting in the
		 * lower ring 
		 * 
		 * 
		 * Args:
		 * 		node	:	id of the node.
		 * 		k		:	no. of nodes in a ring
		 * 		n		:	levels in Tring.		
		 * 
		 * Returns:
		 * 		Array containing the no. of nodes connecting to lower ring minus
		 * 		adjacent node.
		 * 
		 * 
		 *            7
		 *   --------(N)---------
		 *   |                  |
		 *   |					|
		 * 6(N)				   (N)4<------(N)0
		 *   |					|
		 *   |					|
		 *   --------(N)---------
		 *			  5
		 *
		 *   Returns [5,6,7] 
		 */
		int[] array = new int[k-1];
		
		if (k == 0)
			return null;
		
		int down_node = GetDownNode(node, k, n);
		
		if (down_node == -1)
			return null;
		
		if (k == 1){
			int[] array1 = new int[1];
			array1[0] = down_node;
		}
		
		int next_node = down_node;
		
		for (int i=0; i<k-1; i++){
			array[i] = GetLeftNode(next_node, k);
			next_node = array[i];
		}
		
		return Utilities.convertToArrayList(array);
		
	}
	
	public static ArrayList<Integer> ReturnNodesInRing(int node, int k){
		/* Returns an array containing the no. of the nodes connecting in the
		 * same ring 
		 * 
		 * 
		 * Args:
		 * 		node	:	id of the node.
		 * 		k		:	no. of nodes in a ring		
		 * 
		 * Returns:
		 * 		Array containing the no. of nodes connecting in the ring without
		 * 		the node known.
		 * 
		 * 
		 *            7
		 *   --------(N)---------
		 *   |                  |
		 *   |					|
		 * 6(N)				   (N)4 <-------
		 *   |					|
		 *   |					|
		 *   --------(N)---------
		 *			  5
		 *
		 *   Returns [5,6,7] 
		 */
		int[] array = new int[k-1];
		
		if (k == 0)
			return null;
		else if (k == 1)
			return null;
		
		int next_node = node;
		
		for (int i=0; i<k-1; i++){
			array[i] = GetLeftNode(next_node, k);
			next_node = array[i];
		}
		
		return Utilities.convertToArrayList(array);
		
	}
	
	public static int ReturnUpperNodeConnectingRing(int node, int k){
		/* Returns an the id of the node connecting to the ring on the higher
		 * level.
		 * 
		 * Args:
		 * 		node	:	id of the node.
		 * 		k		:	no. of nodes in a ring		
		 * 
		 * Returns:
		 * 		Id of the node connecting on the upper ring 
		 * 
		 *            7
		 *   --------(N)---------
		 *   |                  |
		 *   |					|
		 * 6(N)				   (N)4 -------> 0(N)
		 *   |					|
		 *   |					|
		 *   --------(N)---------
		 *			  5
		 *
		 *   Returns:
		 *   	Upper node (0) in case there is an upper node.
		 *      -1 in case there is no upper node.
		 */
		
		if (k == 0)
			return -1;
		else if (k == 1)
			return GetUpNode(node, k);
		
		int c_lev = Tring.LevelOfNode (node, k);
		
		// You're in the root node!
		if (c_lev == 1)
			return -1;
		
		int c_rnid  = Tring.NodeWithinRing	(node, k);
		
		// Connecting node.
		int conn_node = node + (k-1-c_rnid);
		
		return Tring.GetUpNode(conn_node, k);

	}
	
	public static int CalculateNoConnections(int id, int k, int n){
		/* Returns no. of connections to a certain node.
		 * 
		 * Each node, can't have more than 3 links connecting to it.
		 * 2 connections for a ring and 1 connection more in case of
		 * connecting to a tree.
		 * 
		 * If k = 1 then the each node has only 1 connection (limit case). 
		 * 
		 * 
		 * Args:
		 * 		id		:	id of the node.
		 * 		k		:	no. of nodes in a ring
		 * 		n		:	levels in Tring.		
		 * 
		 * Returns:
		 * 		Max no. of connections per node.
		 */
		
		int no_connections=0;
		
		// Node not in the tree.
		if (id > CalculateNoNodes(k, n))
			return -1;
		
		
		// Limit case: Tring is actually a tree (no ring).
		if (k == 1){
			// Limit case: just 1 point, no connections.
			if (n == 1)
				no_connections = 0;
			else{
				// limit_id: id of the first id on the last level.
				int limit_id = CalculateNoNodes(1, n-1);
			
				// Always = 2, unless nodes at far end of tree. In that case = 1
				if (limit_id == id || limit_id+1 == id)
					no_connections = 1;
				else
					no_connections = 2;	
			}
		}
		else{
			// Limit case: Tring is actually just a ring (no ring).
			if (n == 1)
				no_connections = 2;
			// All the other cases
			else{
				// limit_id: id of the first id on the last level.
				int limit_id = CalculateNoNodes(k, n-1);
				
				// if node is not on last level, then it has 3 connections.
				if (id < limit_id)
					no_connections = 3;
				else{
					if (IsNodeTreeConnected(id, k, n))
						no_connections = 3;
					else
						no_connections = 2;	
				}
			}
		}
		
		return no_connections;
	}

	
	public static int RingInNeighbourhood (int node, int k){
		/*
		 * Given a node, calculate the id of the ring with respect to its
		 * neighbourhood.
		 * 
		 * Args:
		 * 		node	:	id of the node.
		 * 		k		:	no. of nodes in a ring
		 * 		
		 * Returns:
		 * 		Id of the ring in its neighbourhood.
		 */
		
		int level = -1;
		int ring = -1;
		int tot_rings_in_neig = -1;
		int ring_in_neighbourhood = -1;
		
		level = LevelOfNode(node, k);
		
		if (level == 0){
			tot_rings_in_neig = 0;
		}
		else if (level == 1){
			tot_rings_in_neig = 1;
		}
		else if (level == 2){
			tot_rings_in_neig = k;
		}
		else{
			tot_rings_in_neig = k-1;
		}
		
		if (k == 0){
			tot_rings_in_neig = 0;
		}
		else if (k == 1){
			tot_rings_in_neig = 1;
		}
		
		ring = RingOfNode(node, k);
		
		ring_in_neighbourhood = ring % tot_rings_in_neig;
		
		return ring_in_neighbourhood;
		
	}
	
	public static boolean IsNodeInRing (int node, int node_in_ring, int k){
		/*
		 * Given a node and a sample node within the ring, say whether the
		 * node is in the same ring.
		 * 
		 * Args:
		 * 		node			:	node wishing to test.
		 * 		node_in_ring	:	sample node within the ring.
		 * 		k				:	no. of nodes in a ring.
		 * 		
		 * Returns:
		 * 		true	:	node within ring.
		 * 		false	:	node not in ring.
		 */	
		
		int r_nid = NodeWithinRing(node_in_ring, k);
		
		// Subtract
		int first_node = node_in_ring - r_nid;
		
		// Add
		int last_node = node_in_ring + (k - 1 - r_nid);
		
		if (node >= first_node && node <= last_node)
			return true;
		else
			return false;
		
	}
	
	public static int FromRnidAndRidToID (int r_nid, int rid, int k, int n){
		/*
		 * Given a ring id within level and a node id within ring, calculate
		 * overall node id.
		 * 
		 * Args:
		 * 		r_nid	:	id of the node within ring.
		 * 		rid		:	ring id within level.	
		 * 		k		:	no. of nodes in a ring
		 * 		n		:	level where the node is.
		 * 		
		 * Returns:
		 * 		Node ID within tree.
		 * 		-1 if node ID doesn't exist.
		 */
		
		int node;
		int id_first_node;
		
		if (k == 0){
			return 0;
		}
		
		if (r_nid > (k-1)){
			return -1;
		}
		
		if (rid > RingsInLevel(k, n)){
			return -1;
		}
		
		id_first_node = FirstNodeIDLevelGivenN(k, n);
		
		node = (rid*k) + r_nid + id_first_node;
		
		return node;
		
	}
	
	public static int GetLeftNode (int node, int k){
		/*
		 * Given a node, calculate the id of the node it connects to on the
		 * left of it.
		 * 
		 * Args:
		 * 		node	:	id of the node.
		 * 		k		:	no. of nodes in a ring.
		 * 		
		 * Returns:
		 * 		Id of node on the left.
		 * 		-1 if it's impossible to determine.
		 */		
		
		if (k == 0)
			return -1;
		else if (k == 1)
			return -1;
		
		int left_node = -1;
		int left_node_within_tring = -1;
		int r_nid = NodeWithinRing(node, k);
		int rid = RingOfNode (node, k);
		int n = LevelOfNode(node, k);
		 
		if (r_nid == k-1){
			left_node = 0;
		}
		else{
			left_node = r_nid + 1;
		}
		
		left_node_within_tring = FromRnidAndRidToID(left_node, rid, k, n);
		
		return left_node_within_tring;
	}
	
	public static int GetRightNode (int node, int k){
		/*
		 * Given a node, calculate the id of the node it connects to on the
		 * right of it.
		 * 
		 * Args:
		 * 		node	:	id of the node.
		 * 		k		:	no. of nodes in a ring.
		 * 		
		 * Returns:
		 * 		Id of node on the right.
		 * 		-1 if it's impossible to determine.
		 */		
		
		if (k == 0)
			return -1;
		else if (k == 1)
			return -1;
		
		int right_node = -1;
		int right_node_within_tring = -1;
		int r_nid = NodeWithinRing(node, k);
		int rid = RingOfNode (node, k);
		int n = LevelOfNode(node, k);
		 
		if (r_nid == 0){
			right_node = k-1;
		}
		else{
			right_node = r_nid - 1;
		}
		
		right_node_within_tring = FromRnidAndRidToID(right_node, rid, k, n);
		
		return right_node_within_tring;
	}
	
	public static int GetUpNode (int node, int k){
		/*
		 * Given a node, calculate the id of the node it connects to on the
		 * upper level of the Tring.
		 * 
		 * Args:
		 * 		node	:	id of the node.
		 * 		k		:	no. of nodes in a ring.
		 * 		
		 * Returns:
		 * 		Id of node on upper level.
		 * 		-1 if the node is unconnected to upper level.
		 */
		
		int ring_in_neighbourhood = -1;
		int parent_id = -1; //Rid
		int parent_node = -1;
		
		int r_nid = NodeWithinRing(node, k);
		int parent_level = LevelOfNode(node, k) - 1;
		
		// If node is root.
		if (parent_level == 0)
			return -1;
		
		if (r_nid == k-1){
			
			parent_id = ParentRingID(node, k);
			ring_in_neighbourhood = RingInNeighbourhood(node, k);
			parent_node = FromRnidAndRidToID(ring_in_neighbourhood, parent_id,
					k, parent_level);
			
		}
		else{
			parent_node = -1;
		}
		
		return parent_node;
	}
	
	public static int GetDownNode(int node, int k, int n){
		/*
		 * Given a node, calculate the id of the node it connects to on the
		 * lower level of the Tring.
		 * 
		 * Args:
		 * 		node	:	id of the node.
		 * 		k		:	no. of nodes in a ring.
		 * 		n		:	tot. levels of tree.
		 * 		
		 * Returns:
		 * 		Id of node on lower level.
		 * 		-1 if the node is unconnected to lower level.
		 */
		int child_node = -1;
		int child_rid = -1;
		
		int rid = RingOfNode(node, k);
		int child_level = LevelOfNode(node, k) + 1;
		int ring_in_neigh = NodeWithinRing(node, k);

		// There is no level below to go to.
		if (child_level > n){
			return -1;
		}
		else if (k == 0 ){
			return 0;
		}
		else if (k == 1 ){
			return child_level;
		}
		else{
			// Node corresponds to link going up, unless root where also k-1
			// goes down.
			if (ring_in_neigh == k-1){
				if (child_level == 2){
					child_rid = RingInLevel(ring_in_neigh, rid, k,
							child_level-1);
					child_node = FromRnidAndRidToID((k-1), child_rid, k,
							child_level);
				}
				else{
					return -1;
				}
			}
			else{
				child_rid = RingInLevel(ring_in_neigh, rid, k, child_level-1);
				child_node = FromRnidAndRidToID((k-1), child_rid, k,
						child_level); 
			}
		}
		
		return child_node;
		
	}
	
	public static int RingInLevel(int ring_no, int parent_id, int k, int n){
		/*
		 * Given a ring no. within neighbourhood, calculate the ring id within
		 * level.
		 * 
		 * Args:
		 * 		ring_no		:	id of the ring within its neighbourhood.
		 * 		parent_id	:	id of the parent ring within its level.
		 * 		k			:	no. of nodes in a ring.
		 * 		n			: 	level where the parent ring stands.
		 * 		
		 * Returns:
		 * 		Ring ID within level (rid).
		 * 		-1 if it's impossible to work out the ring no.
		 */
		
		int rid = -1;
		
		if (k == 0){
			return -1;
		}
		else if (k == 1 && !(ring_no == 0 && parent_id == 0)){
			return -1;
		}
		else if (k == 1){
			return 0;
		}
		else if (!(n==1 && ring_no < k && parent_id == 0) &&
			!(n >= 2 && ring_no < k-1 && parent_id < RingsInLevel(k, n))){
			return -1;
		}
		
		if (n == 0){
			return -1;
		}
		else if (n==1){
			rid = (parent_id)*k + ring_no;			
		}
		else if (n >= 2){
			rid = (parent_id)*(k-1) + ring_no;
		}
		else{
			return -1;
		}
		
		return rid;
		
	}
	
	public static int ParentRingID (int node, int k){
		/*
		 * Given a node, calculate the ring id of the parent within the upper
		 * level.
		 * 
		 * Args:
		 * 		node	:	id of the node.
		 * 		k		:	no. of nodes in a ring.
		 * 		
		 * Returns:
		 * 		Id of parent ring (rid).
		 * 		-1 if ring is root.
		 */
		
		int rid = RingOfNode(node, k);
		int level = LevelOfNode(node, k);
		int children = -1;
		int parent_id = -1;
		
		if (k == 0)
			return -1;
		else if (k == 1)
			return 0;
		
		
		if (level == 1){
			return -1;
		}
		else if (level == 2){
			children = k;
		}
		else{
			children = k-1;
		}
		
		parent_id = rid / children;
		
		return parent_id;
		
	}
	
	public static boolean IsNodeTreeConnected(int node, int k, int n){
		/*
		 * Given a node, calculate whether the node is connected to other rings
		 * apart from its own.
		 * 
		 * Args:
		 * 		node	:	id of the node we're interested to know the level.
		 * 		k		:	no. of nodes in a ring
		 * 		n		:	height of Tring.
		 * 		
		 * Returns:
		 * 		True, if node is connected to other rings.
		 * 		False, if the node is not connected.
		 */
		
		int no_nodes = CalculateNoNodes(k, n);
		int no_prec_nodes = CalculateNoNodes(k, n-1);
		
		int r_nid = NodeWithinRing(node, k);
		
		if (node <= no_nodes){
			if (node >= no_prec_nodes){
				if (r_nid != (k-1))
					return false;
				else
					return true;
			}
			else{
				return true;
			}
		}
		else{
			// In case the node doesn't exist in Tring.
			return false;
		}
	}

	public static int NodeWithinRing(int node, int k){
		/*
		 * Given a node, calculate what is its ID within the ring it belongs to.
		 * 
		 * Args:
		 * 		node	:	id of the node we're interested to know the level.
		 * 		k		:	no. of nodes in a ring
		 * 		
		 * Returns:
		 * 		r_nid	:	identifier of the node within its ring.
		 * 
		 * The formula that will be used is:
		 * 
		 * rid = (node - first_node) % rings_in_level.
		 */
		
		int first_node = FirstNodeIDLevel(node, k);
		int r_nid = (node - first_node) % k;

		return r_nid;
		
	}
	
	public static int RingOfNode(int node, int k){
		/*
		 * Given a node, calculate what is the ID of the ring it belongs to.
		 * The ring ID (rid) is calculated with reference to the current level.
		 * 
		 * Args:
		 * 		node	:	id of the node we're interested to know the level.
		 * 		k		:	no. of nodes in a ring
		 * 		
		 * Returns:
		 * 		rid		:	identifier within the level at which the node is
		 * 					located in a Tring.
		 * 					-1 if ring doesn't exist.
		 * 
		 * The formula that will be used is:
		 * 
		 * rid = (node - first_node)/rings_in_level.
		 */
		
		if (k == 0){
			return -1;
		}
		else if (k == 1){
			return 0;
		}
		
		int first_node = FirstNodeIDLevel(node, k);
		int rid = (node - first_node)/k;
		
		return rid;
		
	}
	
	
	public static int FirstNodeIDLevel(int node, int k){
		/*
		 * Given a node, calculate what the ID is of the first node to be
		 * located in its level.
		 * 
		 * Args:
		 * 		node	:	id of the node we're interested to know the level.
		 * 		k		:	no. of nodes in a ring
		 * 		
		 * Returns:
		 * 		ID of first node in node level.
		 */
		
		int lev = 0;
		int nodes_lev = 0;
		
		int nodes_pre_lev = -1;
		
		if (k==0)
			return 0;
		else if (k==1)
			return node;
		
		while (nodes_lev <= node){
			lev++;
			nodes_pre_lev = nodes_lev;
			nodes_lev = nodes_lev + (k * RingsInLevel(k, lev));
		}

		
		return nodes_pre_lev;
		
	}
	
	public static int FirstNodeIDLevelGivenN(int k, int n){
		/*
		 * Given a level, calculate the ID of the first node within tree.
		 * 
		 * Args:
		 * 		k		:	no. of nodes in a ring
		 * 		n		:	level.		
		 * 
		 * Returns:
		 * 		ID of first node in node level.
		 * 		-1 if Tring doesn't exist.
		 */
		int nodes_lev = -1;
		
		if (n == 0){
			return -1;
		}
		
		if (k==0)
			return 0;
		else if (k==1)
			return n-1;
		
		nodes_lev = CalculateNoNodes(k, n-1);
		
		return nodes_lev;
		
	}

	
	public static int LevelOfNode(int node, int k){
		/*
		 * Given a node, calculate a what level it is located in a Tring.
		 * 
		 * Args:
		 * 		node	:	id of the node we're interested to know the level.
		 * 		k		:	no. of nodes in a ring
		 * 		
		 * Returns:
		 * 		level at which the node is located in a Tring.
		 */
		
		int lev = 0;
		int nodes_lev = 0;
		
		if (k==0)
			return 0;
		else if (k==1)
			return node+1;
		
		while (nodes_lev <= node){
			lev++;
			nodes_lev = nodes_lev + (k * RingsInLevel(k, lev));
		}
		
		return lev;
		
	}
	
	public static int RingsInLevel(int k, int n){
		/* 
		 * Args:
		 * 		k	: no. of nodes in ring.
		 * 		n	: no. of levels in the Tring
		 * Returns:
		 * 		result	: total number of rings at a level.
		 * 
		 * Given a Tring, calculate how many rings there are at a specified
		 * level.
		 * 
		 * n is assumed to be <= _n
		 */
		
		int rings_in_level;
		
		if (n == 0)
			rings_in_level = 0;
		else if (n == 1)
			rings_in_level = 1;
		else
			rings_in_level = k * ((int) Math.pow(k-1, n-2));
		return rings_in_level;
		
	}
	
	public static int CalculateNoNodes(int k, int n){
	
		/* 
		 * Args:
		 * 		k	: no. of nodes in ring.
		 * 		n	: no. of levels in the Tring
		 * Returns:
		 * 		result	: total number of nodes.
		 * 
		 * The formula to calculate the total number of nodes in a Tring is:
		 * 
		 * For k > 2:
		 * no_nodes = k*(k*(k-1)^(n-1) -2)/(k-2)
		 * 
		 * For k <= 2:
		 * no_nodes = k + k^2 * (k-1)*(n-1)
		 * 
		 */
		
		int result = -1;
		
		int k1 = k - 1;
		int n1 = n - 1;

		if (n == 0){
			return 0;
		}
		else if (n == 1){
			return k;
		}
		
		if (k > 2){
			
			int partial1 = (int) Math.pow(k1, n1);
			result =  (k * ((partial1 * k) - 2)) / (k -2);
		}
		else if (k == 2){
			int partial_k2 = (int) Math.pow(k, 2);
			result = k + (partial_k2 * k1 * n1);	
		}
		else if (k == 1){
			result = n;
		}
		else{
			result = 0;
		}
		return result;
	}
	
	public static int   GetLeftPort()       { return left_port; }
	public static int	GetRightPort()		{ return right_port;}
	public static int	GetUpPort()			{ return up_port;	}
	public static int	GetDownPort()		{ return down_port;	}
	
}
