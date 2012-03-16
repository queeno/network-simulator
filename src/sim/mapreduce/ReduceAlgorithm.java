/*
 * Copyright (c) 2012, Simon Aquino
 * All rights reserved.
 * 
 * Made available under the BSD license - see the LICENSE file
 */ 

package sim.mapreduce;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import sim.mapreduce.Chunk;

public class ReduceAlgorithm {

	public static Chunk RunReduceAlgorithm(ArrayList<Chunk> chunk){
		
		Chunk result;
		
		// Convert list of chunks into two dimensional array.
		
		// Run custom reduce function. DEFAULT: Mergesort.	
		result = MergeSort(chunk);
	
		return result;
		
	}
	
	// Insert your own algorithm here. It can be private, but it is left public
	// for UnitTesting.
	
	public static Chunk MergeSort(ArrayList<Chunk> data){
		
		// Instantiate structure
		LinkedList<Deque<Integer>> list_of_deques;
		ArrayList<Integer> final_result = new ArrayList<Integer>();
		
		// Just in case.
		if (data.size() == 0)
			return null;
		
		// If there is just one element in the array, it means we're on the
		// last level.
		if (data.size() == 1)
			//list_of_deques = ChopChunk(data.get(0));
			return QuickSort(data.get(0));
		else
			list_of_deques = ToLinkedListOfDeques(data);
		
		// Run algorithm.
			
		while (!list_of_deques.isEmpty()){
			
			// Instantiate array of elements to compare.
			int[] compare_array = new int[list_of_deques.size()];
			
			for (int i=0; i<list_of_deques.size(); i++){

				// Get element from queue.
				Integer elem = list_of_deques.get(i).peekFirst();
				
				// Insert element in array.
				compare_array[i] = elem.intValue();
			}
			
			int min_pos = 0;
			
			// Compare elems in array taking the MIN
			for (int i=0; i<compare_array.length; i++){
				
				// if the current element is smaller, then
				if (compare_array[i] < compare_array[min_pos])
					// swap
					min_pos = i;
				
			}
			
			final_result.add(list_of_deques.get(min_pos).pop());
			
			// Remove deque if empty.
			if (list_of_deques.get(min_pos).isEmpty())
				list_of_deques.remove(min_pos);
			
		}	
			
		return ChunkUtils.IntegerListToChunk(final_result);
	}
	
	public static Chunk QuickSort(Chunk data){
		
		int[] data_arr = data.GetData();
		
		if (data_arr.length == 0)
			return new Chunk (data_arr);
		else if (data_arr.length == 1)
			return new Chunk (data_arr);
		
		QSort(data_arr, 0, data_arr.length-1);
		
		return new Chunk(data_arr); 
		
	}
	
	private static void QSort(int[] data_arr, int x, int y){
		
		/* // Make sure you run this algorithm only if there are at least 2 elems.
		if (x < y){
			// Take pivot in the middle of the array
			int m = (x+y)/2;
			int pivot = data_arr[m];
		
			// Indices
			int l = x;
			int r = y;
			
			// Move pivot to the end (won't be compared to itself)
			Swap(data_arr, m, r);
			
			for (int i=x; i<y; i++){
				
				if (data_arr[i] < pivot){
					Swap(data_arr, i, l);
					l++;
				}
			}
			
			// Move pivot back in place.
			Swap(data_arr, l, y);
			
			QSort(data_arr, x, l-1);
			QSort(data_arr, l+1, y);
		} */
		
		// Make sure you run this algorithm only if there are at least 2 elems.
		if (x < y){
			
			// Indices
			int i = x;
			int j = y;
			
			// Pivot
			int m = (x+y)/2;
			int pivot = data_arr[m];
			
			while (i <= j){
				
				while (data_arr[i] < pivot)
					i++;
				while (data_arr[j] > pivot)
					j--;
				
				if (i <= j){
					Swap(data_arr, i, j);
					i++; j--;	
				}
			}
			// Rerun recursively for each chunk previously divided.
			QSort(data_arr, x, i-1);
			QSort(data_arr, i, y);				
		}
	}
	
	// Supporting function for quick sort.
	private static void Swap (int[] data_arr, int i, int j){
		
		int temp = data_arr[i];
		data_arr[i] = data_arr[j];
		data_arr[j] = temp;
		
	}
	
	// Supporting function for merge sort.
	// Takes a chunk of n numbers and chops it in n parts.
	public static LinkedList<Deque<Integer>> ChopChunk(Chunk data){
		
		LinkedList<Deque<Integer>> ll = new LinkedList<Deque<Integer>>();
		
		for (int i=0; i<data.GetData().length; i++){
			
			Deque<Integer> data_arr = new LinkedList<Integer>(); 
			data_arr.add( data.GetData()[i] ) ;
			ll.add( data_arr );
			
		}
		
		return ll;
		
	}
	
	// Supporting function for merge sort.
	private static LinkedList<Deque<Integer>> ToLinkedListOfDeques(ArrayList<Chunk> data){
		
		LinkedList<Deque<Integer>> ll = new LinkedList<Deque<Integer>>();
		
		for (int i=0; i<data.size(); i++)
			ll.add(data.get(i).ToDeque());
		
		return ll;
	}
	
	
}
