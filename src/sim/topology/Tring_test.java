/*
 * Copyright (c) 2012, Simon Aquino
 * All rights reserved.
 * 
 * Made available under the BSD license - see the LICENSE file
 */ 

package sim.topology;

import sim.Utilities;
import sim.topology.Tring;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;



public class Tring_test {
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testTotalNodes(){
		int no_nodes;
		
		no_nodes = Tring.CalculateNoNodes(4, 4);
		assertEquals(no_nodes, 212);
		
		no_nodes = Tring.CalculateNoNodes(4, 3);
		assertEquals(no_nodes, 68);
		
		no_nodes = Tring.CalculateNoNodes(4, 1);
		assertEquals(no_nodes, 4);
		
		no_nodes = Tring.CalculateNoNodes(1, 5);
		assertEquals(no_nodes, 5);

		no_nodes = Tring.CalculateNoNodes(3, 4);
		assertEquals(no_nodes, 66);
	}
	
	@Test
	public void testRingsInLevel(){
		int rings_in_level;
		
		rings_in_level = Tring.RingsInLevel(4, 4);
		assertEquals(rings_in_level, 36);

		rings_in_level = Tring.RingsInLevel(4, 3);
		assertEquals(rings_in_level, 12);
		
		rings_in_level = Tring.RingsInLevel(4, 5);
		assertEquals(rings_in_level, 108);
		
		rings_in_level = Tring.RingsInLevel(5, 1);
		assertEquals(rings_in_level, 1);
		
		rings_in_level = Tring.RingsInLevel(5, 0);
		assertEquals(rings_in_level, 0);
		
		rings_in_level = Tring.RingsInLevel(2, 2);
		assertEquals(rings_in_level, 2);
	}
	
	@Test
	public void testLevelOfNode(){
		int level_node;
		
		level_node = Tring.LevelOfNode(1, 4);
		assertEquals(level_node, 1);
		
		level_node = Tring.LevelOfNode(17, 4);
		assertEquals(level_node, 2);
		
		level_node = Tring.LevelOfNode(19, 4);
		assertEquals(level_node, 2);
		
		level_node = Tring.LevelOfNode(20, 4);
		assertEquals(level_node, 3);
		
		level_node = Tring.LevelOfNode(21, 4);
		assertEquals(level_node, 3);

		level_node = Tring.LevelOfNode(0, 3);
		assertEquals(level_node, 1);
		
		level_node = Tring.LevelOfNode(2, 3);
		assertEquals(level_node, 1);
		
		level_node = Tring.LevelOfNode(3, 3);
		assertEquals(level_node, 2);
		
		level_node = Tring.LevelOfNode(0, 0);
		assertEquals(level_node, 0);
		
		level_node = Tring.LevelOfNode(0, 54);
		assertEquals(level_node, 1);
		
		level_node = Tring.LevelOfNode(165, 4);
		assertEquals(level_node, 4);

		level_node = Tring.LevelOfNode(4, 1);
		assertEquals(level_node, 5);
		
		level_node = Tring.LevelOfNode(3, 2);
		assertEquals(level_node, 2);
		
	}
	
	@Test
	public void testFirstNodeIDLevel(){
		int level_node;
		
		level_node = Tring.FirstNodeIDLevel(1, 4);
		assertEquals(level_node, 0);
		
		level_node = Tring.FirstNodeIDLevel(17, 4);
		assertEquals(level_node, 4);
		
		level_node = Tring.FirstNodeIDLevel(19, 4);
		assertEquals(level_node, 4);
		
		level_node = Tring.FirstNodeIDLevel(20, 4);
		assertEquals(level_node, 20);
		
		level_node = Tring.FirstNodeIDLevel(21, 4);
		assertEquals(level_node, 20);

		level_node = Tring.FirstNodeIDLevel(0, 3);
		assertEquals(level_node, 0);
		
		level_node = Tring.FirstNodeIDLevel(2, 3);
		assertEquals(level_node, 0);
		
		level_node = Tring.FirstNodeIDLevel(3, 3);
		assertEquals(level_node, 3);
		
		level_node = Tring.FirstNodeIDLevel(0, 0);
		assertEquals(level_node, 0);
		
		level_node = Tring.FirstNodeIDLevel(0, 54);
		assertEquals(level_node, 0);
		
		level_node = Tring.FirstNodeIDLevel(80, 4);
		assertEquals(level_node, 68);
		
		level_node = Tring.FirstNodeIDLevel(312, 4);
		assertEquals(level_node, 212);
	}
	
	@Test
	public void testRingOfNode(){
		int rid;
		
		rid = Tring.RingOfNode(4, 4);
		assertEquals(rid, 0);
		
		rid = Tring.RingOfNode(11, 4);
		assertEquals(rid, 1);		

		rid = Tring.RingOfNode(8, 4);
		assertEquals(rid, 1);
		
		rid = Tring.RingOfNode(13, 4);
		assertEquals(rid, 2);
		
		rid = Tring.RingOfNode(16, 4);
		assertEquals(rid, 3);
		
		rid = Tring.RingOfNode(19, 4);
		assertEquals(rid, 3);
		
		rid = Tring.RingOfNode(20, 4);
		assertEquals(rid, 0);
		
		rid = Tring.RingOfNode(13, 4);
		assertEquals(rid, 2);

		rid = Tring.RingOfNode(124, 4);
		assertEquals(rid, 14);
		
		rid = Tring.RingOfNode(125, 4);
		assertEquals(rid, 14);
		
		rid = Tring.RingOfNode(127, 4);
		assertEquals(rid, 14);
		
		rid = Tring.RingOfNode(128, 4);
		assertEquals(rid, 15);
		
		rid = Tring.RingOfNode(128, 1);
		assertEquals(rid, 0);

		rid = Tring.RingOfNode(128, 2);
		assertEquals(rid, 1);
		
	}
	
	@Test
	public void testNodeWithinRing(){
		int r_nid;
		
		r_nid = Tring.NodeWithinRing(4, 4);
		assertEquals(r_nid, 0);
		
		r_nid = Tring.NodeWithinRing(11, 4);
		assertEquals(r_nid, 3);		

		r_nid = Tring.NodeWithinRing(8, 4);
		assertEquals(r_nid, 0);
		
		r_nid = Tring.NodeWithinRing(13, 4);
		assertEquals(r_nid, 1);
		
		r_nid = Tring.NodeWithinRing(124, 4);
		assertEquals(r_nid, 0);
		
		r_nid = Tring.NodeWithinRing(125, 4);
		assertEquals(r_nid, 1);
		
		r_nid = Tring.NodeWithinRing(127, 4);
		assertEquals(r_nid, 3);
		
		r_nid = Tring.NodeWithinRing(128, 4);
		assertEquals(r_nid, 0);
		
	}
	
	@Test
	public void testIsNodeTreeConnected(){
		boolean result;
		
		result = Tring.IsNodeTreeConnected(4, 4, 2);
		assertEquals(result, false);
		
		result = Tring.IsNodeTreeConnected(4, 4, 2);
		assertEquals(result, false);
		
		result = Tring.IsNodeTreeConnected(5, 4, 2);
		assertEquals(result, false);
		
		result = Tring.IsNodeTreeConnected(7, 4, 2);
		assertEquals(result, true);
		
		result = Tring.IsNodeTreeConnected(5, 4, 3);
		assertEquals(result, true);
		
		result = Tring.IsNodeTreeConnected(40, 4, 2);
		assertEquals(result, false);
		
		result = Tring.IsNodeTreeConnected(1, 5, 1);
		assertEquals(result, false);
		
		result = Tring.IsNodeTreeConnected(132, 5, 10);
		assertEquals(result, true);
		
		result = Tring.IsNodeTreeConnected(50, 3, 4);
		assertEquals(result, true);
		
	}
	
	@Test
	public void testCalculateNoConnections(){
		int result;
		
		result = Tring.CalculateNoConnections(5, 4, 2);
		assertEquals(result, 2);
		
		result = Tring.CalculateNoConnections(5, 4, 3);
		assertEquals(result, 3);
		
		result = Tring.CalculateNoConnections(1, 4, 2);
		assertEquals(result, 3);
		
		result = Tring.CalculateNoConnections(15, 4, 2);
		assertEquals(result, 3);

		result = Tring.CalculateNoConnections(22, 4, 3);
		assertEquals(result, 2);
		
		result = Tring.CalculateNoConnections(31, 4, 3);
		assertEquals(result, 3);
		
		result = Tring.CalculateNoConnections(31, 4, 2);
		assertEquals(result, -1);
		
		result = Tring.CalculateNoConnections(3, 1, 4);
		assertEquals(result, 1);

		result = Tring.CalculateNoConnections(50, 3, 4);
		assertEquals(result, 3);
		
		result = Tring.CalculateNoConnections(2, 1, 4);
		assertEquals(result, 2);
		
		result = Tring.CalculateNoConnections(2, 1, 1);
		assertEquals(result, -1);
	}
	
	@Test
	public void testRingInNeighbourhood(){
		int result;
		
		result = Tring.RingInNeighbourhood(5, 4);
		assertEquals(result, 0);
		
		result = Tring.RingInNeighbourhood(25, 4);
		assertEquals(result, 1);
		
		result = Tring.RingInNeighbourhood(13, 4);
		assertEquals(result, 2);
		
		result = Tring.RingInNeighbourhood(3, 4);
		assertEquals(result, 0);
		
		result = Tring.RingInNeighbourhood(13, 1);
		assertEquals(result, 0);
		
		result = Tring.RingInNeighbourhood(2, 1);
		assertEquals(result, 0);
		
		result = Tring.RingInNeighbourhood(4, 2);
		assertEquals(result, 1);

		result = Tring.RingInNeighbourhood(3, 2);
		assertEquals(result, 0);
		
		result = Tring.RingInNeighbourhood(6, 2);
		assertEquals(result, 0);
		
		result = Tring.RingInNeighbourhood(0, 2);
		assertEquals(result, 0);
		
	}
	
	@Test
	public void testFirstNodeIDLevelGivenN(){
		int result;
		
		result = Tring.FirstNodeIDLevelGivenN(4, 3);
		assertEquals(result, 20);
		
		result = Tring.FirstNodeIDLevelGivenN(4, 1);
		assertEquals(result, 0);
		
		result = Tring.FirstNodeIDLevelGivenN(4, 3);
		assertEquals(result, 20);

		result = Tring.FirstNodeIDLevelGivenN(2, 2);
		assertEquals(result, 2);
		
		result = Tring.FirstNodeIDLevelGivenN(1, 3);
		assertEquals(result, 2);
		
	}
	
	@Test
	public void testFromRnidAndRidToID(){
		int result;
		
		result = Tring.FromRnidAndRidToID(3, 3, 4, 2);
		assertEquals(result, 19);
		
		result = Tring.FromRnidAndRidToID(2, 0, 4, 1);
		assertEquals(result, 2);
		
		result = Tring.FromRnidAndRidToID(2, 0, 1, 1);
		assertEquals(result, -1);
		
		result = Tring.FromRnidAndRidToID(3, 0, 1, 1);
		assertEquals(result, -1);
		
		result = Tring.FromRnidAndRidToID(0, 0, 1, 3);
		assertEquals(result, 2);

		result = Tring.FromRnidAndRidToID(0, 1, 1, 3);
		assertEquals(result, -1);
		
		result = Tring.FromRnidAndRidToID(0, 1, 2, 3);
		assertEquals(result, 8);
		
		result = Tring.FromRnidAndRidToID(1, 1, 2, 3);
		assertEquals(result, 9);
		
	}
	
	@Test
	public void testParentRingID(){
		int result;
		
		result = Tring.ParentRingID(5, 4);
		assertEquals(result, 0);
		
		result = Tring.ParentRingID(24, 4);
		assertEquals(result, 0);
		
		result = Tring.ParentRingID(10, 4);
		assertEquals(result, 0);
		
		result = Tring.ParentRingID(31, 4);
		assertEquals(result, 0);
		
		result = Tring.ParentRingID(32, 4);
		assertEquals(result, 1);
		
		result = Tring.ParentRingID(32, 1);
		assertEquals(result, 0);
		
		result = Tring.ParentRingID(4, 2);
		assertEquals(result, 0);
		
		result = Tring.ParentRingID(7, 2);
		assertEquals(result, 0);
		
		result = Tring.ParentRingID(8, 2);
		assertEquals(result, 1);
		
		result = Tring.ParentRingID(2, 2);
		assertEquals(result, 0);
	}
	
	@Test
	public void testGetUpNode(){
		int result;
		
		result = Tring.GetUpNode(31, 4);
		assertEquals(result, 6);
		
		result = Tring.GetUpNode(19, 4);
		assertEquals(result, 3);
		
		result = Tring.GetUpNode(20, 4);
		assertEquals(result, -1);
		
		result = Tring.GetUpNode(3, 4);
		assertEquals(result, -1);
		
		result = Tring.GetUpNode(4, 1);
		assertEquals(result, 3);

		result = Tring.GetUpNode(0, 1);
		assertEquals(result, -1);
		
		result = Tring.GetUpNode(1, 1);
		assertEquals(result, 0);
		
		result = Tring.GetUpNode(4, 2);
		assertEquals(result, -1);
		
		result = Tring.GetUpNode(9, 2);
		assertEquals(result, 4);
		
		result = Tring.GetUpNode(50, 3);
		assertEquals(result, 21);
		
		result = Tring.GetUpNode(6, 2);
		assertEquals(result, -1);
	}
	
	@Test
	public void testRingInLevel(){
		int result;
		
		result = Tring.RingInLevel(1, 0, 4, 2);
		assertEquals(result, 1);
		
		result = Tring.RingInLevel(0, 2, 4, 2);
		assertEquals(result, 6);
		
		result = Tring.RingInLevel(3, 0, 4, 1);
		assertEquals(result, 3);
		
		result = Tring.RingInLevel(3, 0, 4, 2);
		assertEquals(result, -1);

		result = Tring.RingInLevel(3, 0, 1, 2);
		assertEquals(result, -1);
		
		result = Tring.RingInLevel(0, 0, 1, 2);
		assertEquals(result, 0);
		
		result = Tring.RingInLevel(0, 1, 2, 2);
		assertEquals(result, 1);
		
		result = Tring.RingInLevel(1, 1, 2, 2);
		assertEquals(result, -1);
		
		result = Tring.RingInLevel(1, 0, 2, 1);
		assertEquals(result, 1);

		result = Tring.RingInLevel(0, 0, 2, 1);
		assertEquals(result, 0);
		
		result = Tring.RingInLevel(1, 0, 2, 4);
		assertEquals(result, -1);
		
		result = Tring.RingInLevel(0, 0, 2, 1);
		assertEquals(result, 0);
	}
	
	@Test
	public void testDownNode(){
		int result;
		
		result = Tring.GetDownNode(21, 3, 4);
		assertEquals(result, 50);
		
		result = Tring.GetDownNode(31, 4, 4);
		assertEquals(result, -1);
		
		result = Tring.GetDownNode(19, 4, 2);
		assertEquals(result, -1);
		
		result = Tring.GetDownNode(20, 4, 4);
		assertEquals(result, 71);
		
		result = Tring.GetDownNode(3, 4, 4);
		assertEquals(result, 19);
		
		result = Tring.GetDownNode(3, 4, 1);
		assertEquals(result, -1);

		result = Tring.GetDownNode(0, 1, 1);
		assertEquals(result, -1);
		
		result = Tring.GetDownNode(1, 1, 2);
		assertEquals(result, -1);
		
		result = Tring.GetDownNode(4, 2, 70);
		assertEquals(result, 9);
		
		result = Tring.GetDownNode(5, 2, 3);
		assertEquals(result, -1);
		
		result = Tring.GetDownNode(6, 2, 3);
		assertEquals(result, -1);

		result = Tring.GetDownNode(3, 1, 5);
		assertEquals(result, 5);
		
		result = Tring.GetDownNode(18, 4, 3);
		assertEquals(result, 67);
		
		result = Tring.GetDownNode(67, 4, 3);
		assertEquals(result, -1);
		
		result = Tring.GetDownNode(67, 4, 30);
		assertEquals(result, -1);
	}
	
	@Test
	public void testGetLeftNode(){
		int result;
		
		result = Tring.GetLeftNode(5, 4);
		assertEquals(result, 6);
		
		result = Tring.GetLeftNode(23, 4);
		assertEquals(result, 20);
		
		result = Tring.GetLeftNode(0, 4);
		assertEquals(result, 1);
		
		result = Tring.GetLeftNode(3, 4);
		assertEquals(result, 0);
		
		result = Tring.GetLeftNode(5, 1);
		assertEquals(result, -1);
		
		result = Tring.GetLeftNode(5, 2);
		assertEquals(result, 4);
		
		result = Tring.GetLeftNode(4, 2);
		assertEquals(result, 5);
		
	}
	
	@Test
	public void testGetRightNode(){
		int result;
		
		result = Tring.GetRightNode(5, 4);
		assertEquals(result, 4);
		
		result = Tring.GetRightNode(20, 4);
		assertEquals(result, 23);
		
		result = Tring.GetRightNode(0, 4);
		assertEquals(result, 3);
		
		result = Tring.GetRightNode(3, 4);
		assertEquals(result, 2);
		
		result = Tring.GetRightNode(5, 1);
		assertEquals(result, -1);
		
		result = Tring.GetRightNode(5, 2);
		assertEquals(result, 4);
		
		result = Tring.GetRightNode(4, 2);
		assertEquals(result, 5);
		
	}
	
	@Test
	public void testIsNodeInRing(){
		boolean result;
		
		result = Tring.IsNodeInRing(5, 4, 4);
		assertEquals(result, true);

		result = Tring.IsNodeInRing(27, 4, 4);
		assertEquals(result, false);
		
		result = Tring.IsNodeInRing(27, 24, 4);
		assertEquals(result, true);
		
		result = Tring.IsNodeInRing(27, 28, 4);
		assertEquals(result, false);
		
		result = Tring.IsNodeInRing(3, 4, 2);
		assertEquals(result, false);
		
		result = Tring.IsNodeInRing(4, 5, 2);
		assertEquals(result, true);
		
		result = Tring.IsNodeInRing(5, 4, 1);
		assertEquals(result, false);
	}
	
	@Test
	public void testReturnArrayOfNodesConnecting(){
		ArrayList<Integer> result;
		
		result = Tring.ReturnNodesLowerRing(4, 4, 3);
		int[] solution = new int[3];
		
		solution[0] = 20;
		solution[1] = 21;
		solution[2] = 22;
		
		int[] resultarr = Utilities.convertToArray(result);
		
		assertEquals(resultarr[0], solution[0]);
		assertEquals(resultarr[1], solution[1]);
		assertEquals(resultarr[2], solution[2]);
		assertEquals(resultarr.length, 3);
		
		result = Tring.ReturnNodesLowerRing(7, 4, 3);
		assertEquals(result, null);
	}
	
	@Test
	public void testReturnNodesInRing(){
		ArrayList<Integer> result;
		
		result = Tring.ReturnNodesInRing(4, 4);
		int[] solution = new int[3];
		
		solution[0] = 5;
		solution[1] = 6;
		solution[2] = 7;
		
		int[] resultarr = Utilities.convertToArray(result);
		
		assertEquals(resultarr[0], solution[0]);
		assertEquals(resultarr[1], solution[1]);
		assertEquals(resultarr[2], solution[2]);
		assertEquals(resultarr.length, 3);
		
	}
	
	@Test
	public void testReturnNodesLowerRing(){
		ArrayList<Integer> result;
		
		result = Tring.ReturnNodesLowerRing(4, 4, 3);
		int[] solution = new int[3];
		
		solution[0] = 20;
		solution[1] = 21;
		solution[2] = 22;
		
		int[] resultarr = Utilities.convertToArray(result);
		
		assertEquals(resultarr[0], solution[0]);
		assertEquals(resultarr[1], solution[1]);
		assertEquals(resultarr[2], solution[2]);
		assertEquals(resultarr.length, 3);
		
	}
	
	@Test
	public void testReturnUpperNodeConnectingRing(){
		int result;
		
		result = Tring.ReturnUpperNodeConnectingRing(4, 4);
		assertEquals(result, 0);
		
		result = Tring.ReturnUpperNodeConnectingRing(4, 2);
		assertEquals(result, 1);
		
		result = Tring.ReturnUpperNodeConnectingRing(4, 1);
		assertEquals(result, 3);
	}
}
