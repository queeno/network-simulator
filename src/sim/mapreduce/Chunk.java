/*
 * Copyright (c) 2012, Simon Aquino
 * All rights reserved.
 * 
 * Made available under the BSD license - see the LICENSE file
 */ 

package sim.mapreduce;

import java.util.LinkedList;
import java.util.Deque;

public class Chunk {
	/*
	 * This class is part of the MapReduce implementation.
	 * 
	 * A chunk is an implementation abstraction of an int array to contain
	 * data being split across the nodes.
	 * 
	 * [ 3, 4, 5, 6, 7, 10, 1, 3]
	 *   <--chunk->  <--chunk-->
	 * 
	 */
	
	private int[] data;
	private int size;
	private boolean unary;
	
	// Empty chunk allowed just to initialise. Remember to use SetChunk later.
	public Chunk(){ }
	
	public Chunk(int[] data){
		this.data = data;
		this.size = data.length;
		
		if (this.size == 1)
			this.unary = true;
		else
			this.unary = false;
	}
	
	// Converts chunk to Deque of integers. Useful for mapreduce reduction.
	public Deque<Integer> ToDeque(){
		
		Deque<Integer> ll = new LinkedList<Integer>();
		
		for (int i=0; i<data.length; i++)
			ll.add(data[i]);
		
		return ll;
	}
	
	public String toString(){
		String str = "";
		for (int i=0;i<size;i++)
			str += Integer.toString(data[i]) + ", ";
		
		return str;
	}
	
	public boolean isEmpty(){
		return (size==0) ? true : false;
	}
	
	// Getters
	public int[] 	GetData()	{ return data; }
	public int	 	GetSize()	{ return size; }
	public boolean	GetUnary()	{ return unary;}
	
	// Setters
	public void SetData(int[] data){ this.data = data; }
	
}
