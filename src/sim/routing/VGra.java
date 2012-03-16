/*
 * Copyright (c) 2012, Simon Aquino
 * All rights reserved.
 * 
 * Made available under the BSD license - see the LICENSE file
 */ 

package sim.routing;

import sim.components.Network;
import sim.components.Router;
import sim.topology.Tring;

public class VGra extends Gra implements RoutingFunction {
	
	private int ch_ring = 0;
	private int ch_inter_ring = 2;
	
	public VGra(){
		// Call constructor. Edge 0 -> 1 is my dateline.
		super(0, 1);
		
		// Set VC to true.
		super.SetVC(true);
	}
	
	// Only for testing purposes.
	public VGra(int k, int n){
		// Call constructor. Edge 0 -> 1 is my dateline.
		super(k, n);
		
		// Set VC to true.
		super.SetVC(true);
	}
	
	private boolean CheckDatelines(int c_rnid, int d_rnid){
		
		boolean[][] Datelines = GetPaths();
		
		if ((Datelines[c_rnid][d_rnid] == false) ||
				(Datelines[d_rnid][c_rnid] == false))
			return true;
		else
			return false;
		
	}
	
	@Override
	public int getOutputVC(int current, int inputVC, int source, int dest) {
		
		// Get Tags for source node
		//int c_lev	= Tring.LevelOfNode		(current, super.GetK());
		//int c_rid 	= Tring.RingOfNode		(current, super.GetK());
		int c_rnid  = Tring.NodeWithinRing	(current, super.GetK());
		
		// Get Tags for destination node
		//int d_lev	= Tring.LevelOfNode		(dest, super.GetK());
		//int d_rid 	= Tring.RingOfNode		(dest, super.GetK());
		int d_rnid  = Tring.NodeWithinRing	(dest, super.GetK());

		int k = super.GetK();
		//int n = super.GetN();
		
		int output_port = super.getOutputPort(current, inputVC, source, dest);
		
		if (output_port == Tring.GetLeftPort() || output_port == Tring.GetRightPort()){
			// I'm staying in the ring and destination is within ring.
			if (Tring.IsNodeInRing(dest, current, k)){
				// You are in the same ring. Return same input VC+1 only
				// if you cross the dateline.
				if (CheckDatelines(c_rnid, d_rnid))
					return ch_ring+1;
				else
					return (inputVC == ch_ring+1) ? ch_ring+1 : ch_ring;
			}
			else{
				// I'm going outside the ring and destination is outside ring.
				if (output_port == Tring.GetLeftPort()){
					int leftnode = Tring.GetLeftNode(current, k);
					
					int l_rnid = Tring.NodeWithinRing(leftnode, k);
					
					if (CheckDatelines(c_rnid, l_rnid))
						return ch_inter_ring+1;
					else
						return (inputVC == ch_inter_ring+1) ?
								ch_inter_ring+1 : ch_inter_ring;					
				}
				// Must be right port.
				else{
					int rightnode = Tring.GetRightNode(current, k);
						
					int r_rnid = Tring.NodeWithinRing(rightnode, k);
						
					if (CheckDatelines(c_rnid, r_rnid))
						return ch_inter_ring+1;
					else
						return (inputVC == ch_inter_ring+1) ? ch_inter_ring+1 : ch_inter_ring;						
				}
			}
		}
		else{
			// Either up or down.
			return ch_inter_ring;	
		}
	}
	
	public static void configNetwork(Network network){
		for (Router r : network.getRouters()){
			r.setRoutingFn(new VGra());
		}
	}
}
