/*
 * Copyright (c) 2012, Simon Aquino
 * All rights reserved.
 * 
 * Made available under the BSD license - see the LICENSE file
 */ 

package sim.routing;

import sim.routing.Gra;
import sim.topology.Tring;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class Gra_test {

	private Gra gra;
	
	@Before
	public void setUp() throws Exception {
		gra = new Gra(4, 3);
	}

	@Test
	public void testRouteInRing(){
		int result;
		
		result = gra.RouteInRing(1, 0);
		assertEquals(result, 0);

		result = gra.RouteInRing(3, 0);
		assertEquals(result, 0);
		
		result = gra.RouteInRing(0, 1);
		assertEquals(result, 1);

		result = gra.RouteInRing(1, 2);
		assertEquals(result, 0);

		result = gra.RouteInRing(3, 2);
		assertEquals(result, 1);
		
		result = gra.RouteInRing(3, 3);
		assertEquals(result, -1);
		
	}
	
	@Test
	public void testRouteUp(){
		int result;
		
		result = gra.RouteUp(2);
		assertEquals(result, Tring.GetLeftPort());
		
		result = gra.RouteUp(0);
		assertEquals(result, Tring.GetRightPort());

		result = gra.RouteUp(1);
		assertEquals(result, Tring.GetLeftPort());

		result = gra.RouteUp(3);
		assertEquals(result, Tring.GetUpPort());
		
	}
	
	@Test
	public void testRouteLeftOrRight(){
		int result;
		
		result = gra.RouteLeftOrRight(2, 3);
		assertEquals(result, Tring.GetLeftPort());
		
		result = gra.RouteLeftOrRight(1, 3);
		assertEquals(result, Tring.GetLeftPort());

		result = gra.RouteLeftOrRight(3, 0);
		assertEquals(result, Tring.GetLeftPort());

		result = gra.RouteLeftOrRight(3, 1);
		assertEquals(result, Tring.GetRightPort());
		
		result = gra.RouteLeftOrRight(1, 1);
		assertEquals(result, -1);
		
	}
	
	@Test
	public void testRouteDown(){
		int result;
		
		result = gra.RouteDown(39, 67);
		assertEquals(result, Tring.GetDownPort());
		
		result = gra.RouteDown(1, 49);
		assertEquals(result, Tring.GetLeftPort());

		result = gra.RouteDown(3, 22);
		assertEquals(result, Tring.GetLeftPort());
		
		result = gra.RouteDown(23, 3);
		assertEquals(result, Tring.GetDownPort());
		
		result = gra.RouteDown(23, 4);
		assertEquals(result, Tring.GetDownPort());

		result = gra.RouteDown(59, 65);
		assertEquals(result, Tring.GetDownPort());
		
		result = gra.RouteDown(17, 25);
		assertEquals(result, Tring.GetLeftPort());
		
		result = gra.RouteDown(35, 70);
		assertEquals(result, Tring.GetDownPort());
	}
}
