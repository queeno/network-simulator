/*
 * Copyright (c) 2012, Simon Aquino
 * All rights reserved.
 * 
 * Made available under the BSD license - see the LICENSE file
 */ 

package sim.mapreduce;

import org.junit.Before;
import org.junit.Test;

//import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import sim.mapreduce.ReduceAlgorithm;
import sim.mapreduce.Chunk;

public class ReduceAlgorithm_test {

	@Before
	public void setUp() throws Exception { }

	@Test
	public void testChopChunk(){
		
		int data[] ={10,11,12,13,14,15};
		Chunk chunk = new Chunk(data);
		
		LinkedList<Deque<Integer>> al = ReduceAlgorithm.ChopChunk(chunk);
		System.out.println(al.toString());
		
	}
	
	@Test
	public void testMergeSort(){
		
		int[] data1 = {-27,-2,1,89,232,551,552,800};
		int[] data2 = {23,100};
		int[] data3 = {-7};
		int[] data4 = {-32,-12,-3,1,21,22,42,43,44,78};
		int[] data5 = {-254,-32,-31,-17,-14,-10,-9,-8,-7,-6,12,34,56,78,99};
		
		Chunk data1c = new Chunk(data1);
		Chunk data2c = new Chunk(data2);
		Chunk data3c = new Chunk(data3);
		Chunk data4c = new Chunk(data4);
		Chunk data5c = new Chunk(data5);
		
		ArrayList<Chunk> chunks = new ArrayList<Chunk>();
		
		chunks.add(data1c);
		chunks.add(data2c);
		chunks.add(data3c);
		chunks.add(data4c);
		chunks.add(data5c);
		
		Chunk result = ReduceAlgorithm.MergeSort(chunks);
		
		System.out.println(result);
	}
	
	@Test
	public void testQuickSort(){
		
		int[] data = {333,-254,32,-31,17,-14,10,-9,8,-7,-6,-12,34,56,-78,99,-9999, 6, 7, -23,-9,4};
		
		for (int i=0;i<data.length; i++){
			ArrayList<Integer> arr_integer = new ArrayList<Integer>();
			arr_integer.add(data[i]);
		}
		
		Chunk result = ReduceAlgorithm.QuickSort(new Chunk(data));
		
		System.out.println(result);
		
	}
}