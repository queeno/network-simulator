/*
 * Copyright (c) 2012, Simon Aquino
 * All rights reserved.
 * 
 * Made available under the BSD license - see the LICENSE file
 */ 

package sim.routing;

import sim.routing.VGra;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class VGra_test {

	private VGra vgra;
	
	@Before
	public void setUp() throws Exception {
		vgra = new VGra(4, 3);
	}

	@Test
	public void testgetOutputVC(){
		int result;
		
		result = vgra.getOutputVC(4, 0, 25, 5);
		assertEquals(result, 1);
		
	}
}