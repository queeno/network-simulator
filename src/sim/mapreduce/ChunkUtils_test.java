/*
 * Copyright (c) 2012, Simon Aquino
 * All rights reserved.
 * 
 * Made available under the BSD license - see the LICENSE file
 */ 

package sim.mapreduce;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

import sim.mapreduce.ChunkUtils;

public class ChunkUtils_test {

	@Before
	public void setUp() throws Exception { }

	@Test
	public void testsplitChunk(){
		
		System.out.println("Running Test SplitChunk.....");
		
		Chunk chunk = ChunkUtils.readFile("inputs/input1.mr");
		String str = chunk.toString();
		
		System.out.println("Input: " + str);
		
		ArrayList<Chunk> chunks = ChunkUtils.splitChunk(chunk, 3);
		
		for (int i=0; i<chunks.size(); i++)
			System.out.println(chunks.get(i).toString());
	}
	
	@Test
	public void testMergeChunks(){
		
		System.out.println("Running Test MergeChunks.....");
		
		int[] array1 = new int[3];
		array1[0] = 2;
		array1[1] = 3;
		array1[2] = 4;
		
		int[] array2 = new int[4];
		array2[0] = 5;
		array2[1] = 6;
		array2[2] = 7;
		array2[3] = 8;
		
		Chunk chunk1 = new Chunk(array1);
		Chunk chunk2 = new Chunk(array2);
		
		ArrayList<Chunk> chunks = new ArrayList<Chunk>();
		chunks.add(chunk1);
		chunks.add(chunk2);
		
		Chunk chunk = ChunkUtils.MergeChunks(chunks);
		
		System.out.println(chunk.toString());
		
	}
	
	@Test
	public void testMergeTwoChunks(){
		
		System.out.println("Running Test MergeTwoChunks.....");
		
		int[] array1 = new int[3];
		array1[0] = 2;
		array1[1] = 3;
		array1[2] = 4;
		
		int[] array2 = new int[4];
		array2[0] = 5;
		array2[1] = 6;
		array2[2] = 7;
		array2[3] = 8;
		
		Chunk chunk1 = new Chunk(array1);
		Chunk chunk2 = new Chunk(array2);
		
		Chunk chunk = ChunkUtils.MergeTwoChunks(chunk1, chunk2);
		
		System.out.println(chunk.toString());
		
	}
	
}
