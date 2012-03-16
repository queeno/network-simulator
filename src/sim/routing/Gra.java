/*
 * Copyright (c) 2012, Simon Aquino
 * All rights reserved.
 * 
 * Made available under the BSD license - see the LICENSE file
 */ 
package sim.routing;

import sim.Config;
import sim.components.Network;
import sim.components.Router;
import sim.topology.Tring;

public class Gra implements RoutingFunction {
	
	private int _k;
	private int _n;
	private boolean[][] Paths;
	
	private int clockwise = 0;
	private int anticlockwise = 1;
	private int error = -1;
	
	// Set VC = False. Gra doesn't support VC by default;
	private boolean VC = false; 
	
	public int		   	GetK()				{ return _k; }
	public int 			GetN()				{ return _n; }
	public boolean[][] 	GetPaths()			{ return Paths; }
	public int		 	GetClockwise()		{ return clockwise; }
	public int			GetAntiClockwise()	{ return anticlockwise; }
	public int			GetError()			{ return error; }
	public boolean		GetVC()				{ return VC; }
	
	
	public void SetK		(int k)					{ _k = k; }
	public void SetN		(int n)					{ _n = n; }
	public void SetPaths 	(boolean[][] Paths) 	{ this.Paths = Paths; }
	public void SetVC		(boolean VC)			{ this.VC = VC; }
	
	public Gra(){
		_k = Config.k();
		_n = Config.n();
		
		// Define disallowed edge in terms of vertices
		int dis_vertex1 = 0;
		int dis_vertex2 = 1;
		
		// Create the paths
		CreateRingPaths(dis_vertex1, dis_vertex2);
	}
	
	// Create the class specifying the disabled path explicitly.
	public Gra(int disabled1, int disabled2){
		_k = Config.k();
		_n = Config.n();
		
		// Define disallowed edge in terms of vertices
		int dis_vertex1 = disabled1;
		int dis_vertex2 = disabled2;
				
		// Create the paths
		CreateRingPaths(dis_vertex1, dis_vertex2);
	}
	
	// Only for testing purposes. Comment in when using Gra_test
	/*public Gra(int k, int n){
		_k = k;
		_n = n;
		
		// Define disallowed edge in terms of vertices
		int dis_vertex1 = 0;
		int dis_vertex2 = 1;
		
		// Create the paths
		CreateRingPaths(dis_vertex1, dis_vertex2);
	}*/
	
	private int NextNode(int node){
		return ((node+1) == _k) ? 0 : (node+1);
	}
	
	private int PreviousNode(int node){
		return ((node-1) == -1) ? (_k-1) : (node-1);
	}
	
	private int RingModulus(int source, int dest){
		return (source-dest) < 0 ? (source-dest+_k) : (source-dest);
	}
	
	public int RouteInRing(int current, int dest){
		/*
		 * Given the current and the destination nodes within the same ring,
		 * find what is the path is allowed.
		 * 
		 * Args:
		 * 		current	:	id of the current node within ring.
		 * 		dest	:	id of the destination node within ring.
		 * 		
		 * Returns:
		 * 	    1 anti-clockwise path.
		 * 		0 clockwise path.
		 *     -1 current == destination
		 */
		if (current == dest){
			return error;
		}
		
		if (VC){
			// Select shortest path to destination.
		if (RingModulus(current, dest) < RingModulus(dest, current))
				return anticlockwise;
			else
				return clockwise;
		}
		else{
			int ver2 = PreviousNode(current);
			int ver1 = current;
			
			// Try going right.
			do{
				ver1 = NextNode(ver1);
				ver2 = NextNode(ver2);
				
				if (Paths[ver1][ver2] == false)
					// Clockwise path not allowed, go anti-clockwise.
					return anticlockwise;
	
			} while (ver1 != dest);
	
			// Clockwise path allowed.
			return clockwise;
		}
	}
	
	public int RouteLeftOrRight(int c_rnid, int d_rnid){
		/*
		 * Given the current and the destination nodes within the ring,
		 * find the output port to route the packet either left or right.
		 * 
		 * Args:
		 * 		c_rnid	:	id of the current node within ring.
		 * 		d_rnid	:	id of the destination node within ring.
		 * 		
		 * Returns:
		 * 	    Tring.LeftPort or Tring.RightPort
		 */
		int direction = RouteInRing(c_rnid, d_rnid);
		
		if (direction == clockwise)
			return Tring.GetLeftPort();
		else if (direction == anticlockwise)
			return Tring.GetRightPort();
		else
			return error;
	}
	
	public int RouteUp (int c_rnid){
		/*
		 * Given the current node within the ring, find the output port to
		 * route the packet to the upper level.
		 * 
		 * Args:
		 * 		c_rnid	:	id of the current node within ring.
		 * 		
		 * Returns:
		 * 	    Tring.LeftPort, Tring.RightPort or Tring.UpPort
		 */
		if (c_rnid == _k-1)
			return Tring.GetUpPort();
		else
			return RouteLeftOrRight(c_rnid, _k-1);
	}
	
	public int RouteDown (int current, int dest){
		/*
		 * Given the current node and the destination node, decide whether
		 * node is in family. Then calculate the id of the node to route to
		 * within ring and route packet to it.
		 * 
		 * Args:
		 * 		c_rid	:	id of the current ring within level.
		 * 		d_rid	:	id of the destination ring within level.
		 * 		c_lev	:	id of the current level.
		 * 		d_lev	:	id of the destination level.
		 * 		
		 * Returns:
		 * 	    Tring.Left, Tring.Right, Tring.Down, Tring.Up.
		 */	
		// Get Tags for current node
		int c_lev	= Tring.LevelOfNode		(current, _k);
		int c_rid 	= Tring.RingOfNode		(current, _k);
		int c_rnid  = Tring.NodeWithinRing	(current, _k);
		
		// Get Tags for destination node
		int d_lev	= Tring.LevelOfNode		(dest, _k);
		int d_rid 	= Tring.RingOfNode		(dest, _k);
		
		// Check destination ring is in family of current ring.
		
		// Calculate first id within family
		int family_lowerbound = CalculateFamilyBounds(c_rid, d_lev-c_lev);
		int family_upperbound = CalculateFamilyBounds(c_rid+1, d_lev-c_lev) - 1;
		
		if (family_lowerbound <= d_rid && family_upperbound >= d_rid){
			// Ring within bounds, in family. Route to appropriate node and
			// then route down
			int d_rnid;
			int norm_rid = d_rid - family_lowerbound;
			
			if (c_lev == 1)
				d_rnid = d_rid / (Tring.RingsInLevel(_k, d_lev) / _k);
			else
				d_rnid = norm_rid / (int) Math.pow((_k-1), d_lev-c_lev-1);
			
			if (c_rnid == d_rnid)
				return Tring.GetDownPort();
			else
				return RouteLeftOrRight(c_rnid, d_rnid);
		}
		else{
			// Ring is another family, route up.
			if (c_rnid == (_k-1))
				return Tring.GetUpPort();
			else
				return RouteLeftOrRight(c_rnid, _k-1);
		}
	}
	
	private int CalculateFamilyBounds(int ring, int level){
		/*
		 * Given the a ring identifier within level and a level, calculate the
		 * first node in 
		 * 
		 * Args:
		 * 		ring	:	id of the ring within level.
		 * 		level	:	id of the current level.
		 * 		
		 * Returns:
		 * 	    The first ring id within that family.
		 */	
		int power = (int)Math.pow(_k-1, level);
		int first_family = ring*power;
		
		return first_family;
	}
	
	public int getOutputPort(int current, int inputVC, int source, int dest) {
		
		// Get the number of connections for the current node
		int no_connections = Tring.CalculateNoConnections(current, _k, _n);
		
		// Get Tags for current node
		int c_lev	= Tring.LevelOfNode		(current, _k);
		int c_rid 	= Tring.RingOfNode		(current, _k);
		int c_rnid  = Tring.NodeWithinRing	(current, _k);
		
		// Get Tags for destination node
		int d_lev	= Tring.LevelOfNode		(dest, _k);
		int d_rid 	= Tring.RingOfNode		(dest, _k);
		int d_rnid  = Tring.NodeWithinRing	(dest, _k);
		
		// Decide what port to route to.
		
		// Case one: Same level.
		if (c_lev == d_lev){
			// Case two: Same ring.
			if (c_rid == d_rid){
				// Case three: Same node.
				if (c_rnid == d_rnid){
					// Route to processor. Port no. = number of connections.
					return no_connections;
				}
				// Same level and ring, but different node.
				else
					return RouteLeftOrRight(c_rnid, d_rnid);
			}
			// Same level, but different ring and node.
			// Need to route to k-1 and go up.
			else
				return RouteUp(c_rnid);
			}
		// Different level, ring and node.
		else{
			// Case one: level_dest < level_curr
			if (d_lev < c_lev)
				return RouteUp(c_rnid);
			// Case two: d_lev > c_lev (already covered d_lev == c_lev)
			else
				return RouteDown(current, dest);
		}
	}

	public int getOutputVC(int current, int inputVC, int source, int dest) {
		return -1;
	}
	
	private void CreateRingPaths(int dis_vertex1, int dis_vertex2){
		// Create Paths
		Paths = new boolean[_k][_k];
		
		for (int i=0; i<_k; i++){
			for (int j=0; j<_k; j++){
				if ((i == dis_vertex1 && j == dis_vertex2) ||
						(i == dis_vertex2 && j == dis_vertex1)){
					Paths[i][j] = false;
				}
				else{
					Paths[i][j] = true;
				}
			}
		}	
	}
	
	public static void configNetwork(Network network){
		for (Router r : network.getRouters()){
			r.setRoutingFn(new Gra());
		}
	}
}