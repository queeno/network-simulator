/*
 * Copyright (c) 2012, Simon Aquino
 * All rights reserved.
 * 
 * Made available under the BSD license - see the LICENSE file
 */ 

package sim.topology;

import sim.topology.Mesh;
import sim.topology.Mesh.Direction;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;


public class Mesh_test {
	
	@Before
	public void setUp() throws Exception { }

	@Test
	public void testGetLevel(){
		int result;
		
		result = Mesh.getLevel(10, 3);
		assertEquals(result, -1);
		
		result = Mesh.getLevel(16, 5);
		assertEquals(result, 1);

		result = Mesh.getLevel(12, 5);
		assertEquals(result, 0);
		
	}
	
	@Test
	public void testReturnLowerNodes(){
		ArrayList<Integer> result;
		
		result = Mesh.ReturnLowerNodes(6, 5, 4);
		assertEquals(result.get(0).intValue(), 7);
		
		result = Mesh.ReturnLowerNodes(5, 5, 4);
		assertEquals(result.get(0).intValue(), 6);
		assertEquals(result.get(1).intValue(), 4);
		assertEquals(result.get(2).intValue(), 1);
		assertEquals(result.get(3).intValue(), 9);
		
		result = Mesh.ReturnLowerNodes(10, 5, 4);
		assertEquals(result.get(0).intValue(), 11);
		
		result = Mesh.ReturnLowerNodes(15, 5, 4);
		assertEquals(result, null);
		
		result = Mesh.ReturnLowerNodes(13, 12, 5);
		assertEquals(result.get(0).intValue(), 14);
		
		result = Mesh.ReturnLowerNodes(12, 12, 5);
		assertEquals(result.get(0).intValue(), 13);
		assertEquals(result.get(1).intValue(), 11);
		assertEquals(result.get(2).intValue(), 7);
		assertEquals(result.get(3).intValue(), 17);
		
		result = Mesh.ReturnLowerNodes(16, 12, 5);
		assertEquals(result.get(0).intValue(), 15);
		
		result = Mesh.ReturnLowerNodes(1, 12, 5);
		assertEquals(result, null);
		
	}
	
	@Test
	public void testIsLeftAllowed(){
		boolean result;
		
		result = Mesh.IsLeftAllowed(7, 12, 5);
		assertEquals(result, true);
		
		result = Mesh.IsLeftAllowed(5, 4, 3);
		assertEquals(result, false);
		
		result = Mesh.IsLeftAllowed(17, 12, 5);
		assertEquals(result, false);
		
		result = Mesh.IsLeftAllowed(21, 12, 5);
		assertEquals(result, false);
		
		result = Mesh.IsLeftAllowed(1, 12, 5);
		assertEquals(result, false);
		
		result = Mesh.IsLeftAllowed(11, 21, 5);
		assertEquals(result, true);
		
		result = Mesh.IsLeftAllowed(9, 12, 5);
		assertEquals(result, false );
		
		result = Mesh.IsLeftAllowed(10, 5, 4);
		assertEquals(result, false);
		
		result = Mesh.IsLeftAllowed(1, 5, 4);
		assertEquals(result, true);
		
		result = Mesh.IsLeftAllowed(9, 5, 4);
		assertEquals(result, false);
	}
	
	@Test
	public void testIsRightAllowed(){
		boolean result;
		
		result = Mesh.IsRightAllowed(7, 12, 5);
		assertEquals(result, false);
		
		result = Mesh.IsRightAllowed(5, 4, 3);
		assertEquals(result, false);
		
		result = Mesh.IsRightAllowed(17, 12, 5);
		assertEquals(result, true);
		
		result = Mesh.IsRightAllowed(21, 12, 5);
		assertEquals(result, false);
		
		result = Mesh.IsRightAllowed(1, 12, 5);
		assertEquals(result, false);

		result = Mesh.IsRightAllowed(11, 12, 5);
		assertEquals(result, false);
		
		result = Mesh.IsRightAllowed(22, 12, 5);
		assertEquals(result, true);
		
		result = Mesh.IsRightAllowed(10, 5, 4);
		assertEquals(result, false);
		
		result = Mesh.IsRightAllowed(1, 5, 4);
		assertEquals(result, false);
		
		result = Mesh.IsRightAllowed(9, 5, 4);
		assertEquals(result, true);
		
	}
	
	@Test
	public void testIsUpAllowed(){
		boolean result;
		
		result = Mesh.IsUpAllowed(7, 12, 5);
		assertEquals(result, true);
		
		result = Mesh.IsUpAllowed(8, 12, 5);
		assertEquals(result, true);

		result = Mesh.IsUpAllowed(5, 12, 5);
		assertEquals(result, false);
		
		result = Mesh.IsUpAllowed(4, 12, 5);
		assertEquals(result, true);
		
		result = Mesh.IsUpAllowed(18, 12, 5);
		assertEquals(result, true);
		
		result = Mesh.IsUpAllowed(13, 12, 5);
		assertEquals(result, true);
		
		result = Mesh.IsUpAllowed(24, 12, 5);
		assertEquals(result, true);
		
		result = Mesh.IsUpAllowed(20, 12, 5);
		assertEquals(result, false);
		
		result = Mesh.IsUpAllowed(10, 5, 4);
		assertEquals(result, true);
		
		result = Mesh.IsUpAllowed(9, 5, 4);
		assertEquals(result, true);
		
		result = Mesh.IsUpAllowed(12, 5, 4);
		assertEquals(result, false);
		
		result = Mesh.IsUpAllowed(2, 5, 4);
		assertEquals(result, true);
		
		result = Mesh.IsUpAllowed(0, 5, 4);
		assertEquals(result, false);
		
		result = Mesh.IsUpAllowed(4, 4, 3);
		assertEquals(result, true);
		
	}
	
	@Test
	public void testIsDownAllowed(){
		boolean result;
		
		result = Mesh.IsDownAllowed(7, 12, 5);
		assertEquals(result, true);
		
		result = Mesh.IsDownAllowed(8, 12, 5);
		assertEquals(result, false);

		result = Mesh.IsDownAllowed(5, 12, 5);
		assertEquals(result, true);
		
		result = Mesh.IsDownAllowed(4, 12, 5);
		assertEquals(result, false);
		
		result = Mesh.IsDownAllowed(10, 12, 5);
		assertEquals(result, true);
		
		result = Mesh.IsDownAllowed(13, 12, 5);
		assertEquals(result, false);
		
		result = Mesh.IsDownAllowed(21, 12, 5);
		assertEquals(result, true);
		
		result = Mesh.IsDownAllowed(22, 19, 5);
		assertEquals(result, true);
		
		result = Mesh.IsDownAllowed(10, 5, 4);
		assertEquals(result, false);
		
		result = Mesh.IsDownAllowed(9, 5, 4);
		assertEquals(result, true);
		
		result = Mesh.IsDownAllowed(13, 5, 4);
		assertEquals(result, true);
		
		result = Mesh.IsDownAllowed(2, 5, 4);
		assertEquals(result, false);
		
		result = Mesh.IsDownAllowed(0, 5, 4);
		assertEquals(result, true);
		
		result = Mesh.IsDownAllowed(3, 4, 3);
		assertEquals(result, true);
		
	}
	
	@Test
	public void testChoosePath(){
		int result;
		
		result = Mesh.ChoosePath(1, 5, Direction.Down);
		assertEquals(result, 0);
		
		result = Mesh.ChoosePath(0, 5, Direction.Up);
		assertEquals(result, 1);
		
		result = Mesh.ChoosePath(5, 5, Direction.Left);
		assertEquals(result, 0);
		
		result = Mesh.ChoosePath(6, 5, Direction.Left);
		assertEquals(result, 1);
		
		result = Mesh.ChoosePath(24, 5, Direction.Up);
		assertEquals(result, -1);
		
		result = Mesh.ChoosePath(19, 5, Direction.Down);
		assertEquals(result, 18);
		
		result = Mesh.ChoosePath(23, 5, Direction.Right);
		assertEquals(result, -1);
		
		result = Mesh.ChoosePath(18, 5, Direction.Right);
		assertEquals(result, 23);
		
		result = Mesh.ChoosePath(4, 5, Direction.Left);
		assertEquals(result, -1);
		
		result = Mesh.ChoosePath(3, 5, Direction.Right);
		assertEquals(result, 8);
		
		result = Mesh.ChoosePath(9, 5, Direction.Right);
		assertEquals(result, 14);
		
		result = Mesh.ChoosePath(8, 5, Direction.Up);
		assertEquals(result, 9);
		
		result = Mesh.ChoosePath(20, 5, Direction.Down);
		assertEquals(result, -1);
		
		result = Mesh.ChoosePath(15, 5, Direction.Left);
		assertEquals(result, 10);
		
		result = Mesh.ChoosePath(21, 5, Direction.Left);
		assertEquals(result, 16);
		
		result = Mesh.ChoosePath(16, 5, Direction.Right);
		assertEquals(result, 21);

		result = Mesh.ChoosePath(8, 4, Direction.Right);
		assertEquals(result, 12);

		result = Mesh.ChoosePath(14, 4, Direction.Right);
		assertEquals(result, -1);
		
		result = Mesh.ChoosePath(10, 4, Direction.Up);
		assertEquals(result, 11);
		
		result = Mesh.ChoosePath(3, 4, Direction.Left);
		assertEquals(result, -1);
		
		result = Mesh.ChoosePath(6, 4, Direction.Left);
		assertEquals(result, 2);
		
		result = Mesh.ChoosePath(5, 4, Direction.Down);
		assertEquals(result, 4);

	}
	
	@Test
	public void testReturnUpNode(){
		int result;
		
		result = Mesh.ReturnUpNode(5, 12, 5);
		assertEquals(result, 6);
		
		result = Mesh.ReturnUpNode(16, 12, 5);
		assertEquals(result, 17);
		
		result = Mesh.ReturnUpNode(7, 12, 5);
		assertEquals(result, 12);
		
		result = Mesh.ReturnUpNode(12, 12, 5);
		assertEquals(result, -1);
		
		result = Mesh.ReturnUpNode(6, 12, 5);
		assertEquals(result, 7);
		
		result = Mesh.ReturnUpNode(8, 5, 4);
		assertEquals(result, 9);
		
		result = Mesh.ReturnUpNode(10, 5, 4);
		assertEquals(result, 9);
		
		result = Mesh.ReturnUpNode(3, 5, 4);
		assertEquals(result, 2);
		
		result = Mesh.ReturnUpNode(4, 5, 4);
		assertEquals(result, 5);
		
		result = Mesh.ReturnUpNode(1, 4, 3);
		assertEquals(result, 4);
		
		result = Mesh.ReturnUpNode(0, 4, 3);
		assertEquals(result, 1);
		
		result = Mesh.ReturnUpNode(4, 4, 3);
		assertEquals(result, -1);
	}
	
	@Test
	public void testReturnNodesInAllDirections(){
		ArrayList<Integer> result;
		
		result = Mesh.ReturnNodesInAllDirections(2, 5);
		assertEquals(result, null);
		
		result = Mesh.ReturnNodesInAllDirections(12, 5);
		assertEquals(result.get(0).intValue(), 13);
		assertEquals(result.get(1).intValue(), 11);
		assertEquals(result.get(2).intValue(), 17);
		assertEquals(result.get(3).intValue(), 7);
		
		result = Mesh.ReturnNodesInAllDirections(17, 5);
		assertEquals(result.get(0).intValue(), 18);
		assertEquals(result.get(1).intValue(), 16);
		assertEquals(result.get(2).intValue(), 22);
		assertEquals(result.get(3).intValue(), 12);
		
		result = Mesh.ReturnNodesInAllDirections(5, 4);
		assertEquals(result.get(0).intValue(), 6);
		assertEquals(result.get(1).intValue(), 4);
		assertEquals(result.get(2).intValue(), 9);
		assertEquals(result.get(3).intValue(), 1);
	}
	
	@Test
	public void testIsAdjacentToMaster(){
		boolean result;
		
		result = Mesh.IsAdjacentToMaster(5, 6, 5);
		assertEquals(result, true);
		
		result = Mesh.IsAdjacentToMaster(0, 6, 4);
		assertEquals(result, false);
	}
	
	/*@Test
	public void testReturnLowerNodes2(){
		ArrayList<Integer> result;
		
		result = Mesh.ReturnLowerNodes(6, 4);
		assertEquals(result.get(0).intValue(), 7);
		assertEquals(result.get(1).intValue(), 2);
		
		result = Mesh.ReturnLowerNodes(5, 4);
		assertEquals(result.get(0).intValue(), 4);
		assertEquals(result.get(1).intValue(), 1);
		
		result = Mesh.ReturnLowerNodes(10, 4);
		assertEquals(result.get(0).intValue(), 11);
		assertEquals(result.get(1).intValue(), 14);
		
		result = Mesh.ReturnLowerNodes(15, 4);
		assertEquals(result, null);
		
		result = Mesh.ReturnLowerNodes(13, 5);
		assertEquals(result.get(0).intValue(), 14);
		assertEquals(result.get(1).intValue(), 8);
		assertEquals(result.get(2).intValue(), 18);
		
		result = Mesh.ReturnLowerNodes(12, 5);
		assertEquals(result.get(0).intValue(), 13);
		assertEquals(result.get(1).intValue(), 11);
		assertEquals(result.get(2).intValue(), 7);
		assertEquals(result.get(3).intValue(), 17);
		
		result = Mesh.ReturnLowerNodes(16, 5);
		assertEquals(result.get(0).intValue(), 15);
		assertEquals(result.get(1).intValue(), 21);
		
		result = Mesh.ReturnLowerNodes(1, 5);
		assertEquals(result, null);
		
	}
	
	@Test
	public void testIsLeftAllowed2(){
		boolean result;
		
		result = Mesh.IsLeftAllowed(7, 5);
		assertEquals(result, true);
		
		result = Mesh.IsLeftAllowed(5, 3);
		assertEquals(result, true);
		
		result = Mesh.IsLeftAllowed(17, 5);
		assertEquals(result, false);
		
		result = Mesh.IsLeftAllowed(21, 5);
		assertEquals(result, false);
		
		result = Mesh.IsLeftAllowed(1, 5);
		assertEquals(result, true);
		
		result = Mesh.IsLeftAllowed(11, 5);
		assertEquals(result, true);
		
		result = Mesh.IsLeftAllowed(9, 5);
		assertEquals(result, true );
		
		result = Mesh.IsLeftAllowed(10, 4);
		assertEquals(result, false);
		
		result = Mesh.IsLeftAllowed(7, 4);
		assertEquals(result, true);
	}
	
	@Test
	public void testIsRightAllowed2(){
		boolean result;
		
		result = Mesh.IsRightAllowed(7, 5);
		assertEquals(result, false);
		
		result = Mesh.IsRightAllowed(5, 3);
		assertEquals(result, true);
		
		result = Mesh.IsRightAllowed(17, 5);
		assertEquals(result, true);
		
		result = Mesh.IsRightAllowed(21, 5);
		assertEquals(result, true);
		
		result = Mesh.IsRightAllowed(1, 5);
		assertEquals(result, false);

		result = Mesh.IsRightAllowed(11, 5);
		assertEquals(result, true);
		
		result = Mesh.IsLeftAllowed(9, 5);
		assertEquals(result, true);
		
		result = Mesh.IsRightAllowed(10, 4);
		assertEquals(result, true);
		
		result = Mesh.IsRightAllowed(6, 4);
		assertEquals(result, false);
		
		result = Mesh.IsLeftAllowed(3, 4);
		assertEquals(result, true);
		
	}
	
	@Test
	public void testIsUpAllowed2(){
		boolean result;
		
		result = Mesh.IsUpAllowed(7, 5);
		assertEquals(result, true);
		
		result = Mesh.IsUpAllowed(8, 5);
		assertEquals(result, true);

		result = Mesh.IsUpAllowed(5, 5);
		assertEquals(result, false);
		
		result = Mesh.IsUpAllowed(4, 5);
		assertEquals(result, true);
		
		result = Mesh.IsUpAllowed(10, 5);
		assertEquals(result, false);
		
		result = Mesh.IsUpAllowed(13, 5);
		assertEquals(result, true);
		
		result = Mesh.IsUpAllowed(24, 5);
		assertEquals(result, true);
		
		result = Mesh.IsUpAllowed(22, 5);
		assertEquals(result, true);
		
		result = Mesh.IsUpAllowed(10, 4);
		assertEquals(result, true);
		
		result = Mesh.IsUpAllowed(9, 4);
		assertEquals(result, false);
		
		result = Mesh.IsUpAllowed(13, 4);
		assertEquals(result, false);
		
		result = Mesh.IsUpAllowed(2, 4);
		assertEquals(result, true);
		
		result = Mesh.IsUpAllowed(0, 4);
		assertEquals(result, false);
		
		result = Mesh.IsUpAllowed(4, 3);
		assertEquals(result, true);
		
	}
	
	@Test
	public void testIsDownAllowed2(){
		boolean result;
		
		result = Mesh.IsDownAllowed(7, 5);
		assertEquals(result, true);
		
		result = Mesh.IsDownAllowed(8, 5);
		assertEquals(result, false);

		result = Mesh.IsDownAllowed(5, 5);
		assertEquals(result, true);
		
		result = Mesh.IsDownAllowed(4, 5);
		assertEquals(result, false);
		
		result = Mesh.IsDownAllowed(10, 5);
		assertEquals(result, true);
		
		result = Mesh.IsDownAllowed(13, 5);
		assertEquals(result, false);
		
		result = Mesh.IsDownAllowed(21, 5);
		assertEquals(result, true);
		
		result = Mesh.IsDownAllowed(22, 5);
		assertEquals(result, true);
		
		result = Mesh.IsDownAllowed(10, 4);
		assertEquals(result, false);
		
		result = Mesh.IsDownAllowed(9, 4);
		assertEquals(result, true);
		
		result = Mesh.IsDownAllowed(13, 4);
		assertEquals(result, true);
		
		result = Mesh.IsDownAllowed(2, 4);
		assertEquals(result, false);
		
		result = Mesh.IsDownAllowed(0, 4);
		assertEquals(result, true);
		
		result = Mesh.IsDownAllowed(4, 3);
		assertEquals(result, true);
		
	}
	
	@Test
	public void testFindUpperLevel2(){
		int result;
		
		result = Mesh.FindUpperLevel(1, 5, Direction.DownLeft);
		assertEquals(result, 6);
		
		result = Mesh.FindUpperLevel(0, 5, Direction.DownLeft);
		assertEquals(result, 6);
		
		result = Mesh.FindUpperLevel(5, 5, Direction.DownLeft);
		assertEquals(result, 6);
		
		result = Mesh.FindUpperLevel(6, 5, Direction.DownLeft);
		assertEquals(result, 12);
		
		result = Mesh.FindUpperLevel(24, 5, Direction.UpRight);
		assertEquals(result, 18);
		
		result = Mesh.FindUpperLevel(19, 5, Direction.UpRight);
		assertEquals(result, 18);
		
		result = Mesh.FindUpperLevel(23, 5, Direction.UpRight);
		assertEquals(result, 18);
		
		result = Mesh.FindUpperLevel(18, 5, Direction.UpRight);
		assertEquals(result, 12);
		
		result = Mesh.FindUpperLevel(4, 5, Direction.UpLeft);
		assertEquals(result, 8);
		
		result = Mesh.FindUpperLevel(3, 5, Direction.UpLeft);
		assertEquals(result, 8);
		
		result = Mesh.FindUpperLevel(9, 5, Direction.UpLeft);
		assertEquals(result, 8);
		
		result = Mesh.FindUpperLevel(8, 5, Direction.UpLeft);
		assertEquals(result, 12);
		
		result = Mesh.FindUpperLevel(20, 5, Direction.DownRight);
		assertEquals(result, 16);
		
		result = Mesh.FindUpperLevel(15, 5, Direction.DownRight);
		assertEquals(result, 16);
		
		result = Mesh.FindUpperLevel(21, 5, Direction.DownRight);
		assertEquals(result, 16);
		
		result = Mesh.FindUpperLevel(16, 5, Direction.DownRight);
		assertEquals(result, 12);

		result = Mesh.FindUpperLevel(8, 4, Direction.DownRight);
		assertEquals(result, 9);

		result = Mesh.FindUpperLevel(14, 4, Direction.UpRight);
		assertEquals(result, 10);
		
		result = Mesh.FindUpperLevel(10, 4, Direction.UpRight);
		assertEquals(result, -1);
		
		result = Mesh.FindUpperLevel(3, 4, Direction.UpLeft);
		assertEquals(result, 6);
		
		result = Mesh.FindUpperLevel(6, 4, Direction.UpLeft);
		assertEquals(result, -1);
	}
	
	@Test
	public void testReturnUpNode2(){
		int result;
		
		result = Mesh.ReturnUpNode(5, 5);
		assertEquals(result, 6);
		
		result = Mesh.ReturnUpNode(5, 5);
		assertEquals(result, 6);
		
		result = Mesh.ReturnUpNode(16, 5);
		assertEquals(result, 12);
		
		result = Mesh.ReturnUpNode(7, 5);
		assertEquals(result, 12);
		
		result = Mesh.ReturnUpNode(12, 5);
		assertEquals(result, -1);
		
		result = Mesh.ReturnUpNode(6, 5);
		assertEquals(result, 12);
		
		result = Mesh.ReturnUpNode(8, 4);
		assertEquals(result, 9);
		
		result = Mesh.ReturnUpNode(10, 4);
		assertEquals(result, -1);
		
		result = Mesh.ReturnUpNode(3, 4);
		assertEquals(result, 6);
		
		result = Mesh.ReturnUpNode(4, 4);
		assertEquals(result, 5);
		
		result = Mesh.ReturnUpNode(1, 3);
		assertEquals(result, 4);
		
		result = Mesh.ReturnUpNode(0, 3);
		assertEquals(result, 4);
		
		result = Mesh.ReturnUpNode(4, 3);
		assertEquals(result, -1);
	}*/
	
}