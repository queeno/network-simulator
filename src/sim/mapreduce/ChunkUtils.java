/*
 * Copyright (c) 2012, Simon Aquino
 * All rights reserved.
 * 
 * Made available under the BSD license - see the LICENSE file
 */ 

package sim.mapreduce;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import sim.mapreduce.Chunk;
import sim.Utilities;

public class ChunkUtils {
	
	/* Takes a file and returns the sequence of integers that is contained as an
	 * array.
	 */
	public static Chunk readFile(String filename){
		try {
			FileReader input = new FileReader(filename);
			BufferedReader buf_read = new BufferedReader(input);
		
			String text = buf_read.readLine();
	
			String[] temp = text.split(",");
			int[] array_int = new int[temp.length];
			
			for (int i=0; i<temp.length; i++)
				array_int[i] = Integer.parseInt(temp[i]);
			
			Chunk chunk = new Chunk(array_int);
			
			return chunk;
			
	    } catch (IOException e) {
	    	System.err.println("Error: could not read config file "+filename);
	    }
		
		return null;
	}
	
	
	public static ArrayList<Chunk> splitChunk(Chunk chunk_to_split, int parts){
		
		int length = chunk_to_split.GetSize();
		
		// Limit cases
		if (length == 0)
			return null;
		
		// If I'm trying to create more parts than those that can be actually
		// created, then don't create more parts.
		if (length < parts)
			parts = length;
		
		Chunk[] split_chunks = new Chunk[parts];
		
		int elems_per_part = length/parts;
		int remain_elems = length % parts; // elements to append to the last part
		int index = 0;

		for (int i=0; i<parts; i++){
			
			int size_chunk = 0;
			
			// If this is the last chunk, then append remainder
			if (remain_elems > 0){
				size_chunk = elems_per_part + 1;
				remain_elems--;
			}
			else
				// Else the size is just the number of elements
				size_chunk = elems_per_part;
			
			int[] new_chunk = new int[size_chunk];
			
			// Create the chunk elem by elem.
			for (int j=0; j<size_chunk; j++)
				new_chunk[j] = chunk_to_split.GetData()[index + j];
			
			// Create the chunk object;
			split_chunks[i] = new Chunk(new_chunk);
			
			// Increment index
			index += size_chunk;
			
		}
		
		ArrayList<Chunk> chunks_list = new ArrayList<Chunk>();
		
		for (int i=0; i<split_chunks.length; i++)
			chunks_list.add(split_chunks[i]);
			
		return chunks_list;
		
	}
	
	public static Chunk MergeTwoChunks (Chunk chunk1, Chunk chunk2){
		
		if ((chunk1 == null) && (chunk2 == null))
			return null;
		else if (chunk1 == null)
			return chunk2;
		else if (chunk2 == null)
			return chunk1;
		
		// work out size of combined chunk
		int combined_length = chunk1.GetData().length + chunk2.GetData().length;
		
		int[] data = Arrays.copyOf(chunk1.GetData(), combined_length);
		System.arraycopy(chunk2.GetData(), 0, data, chunk1.GetData().length,
				chunk2.GetData().length);
		
		return new Chunk(data);
	}
	
	public static Chunk MergeChunks (ArrayList<Chunk> chunks){
		
		// work out size of combined chunk
		int size = 0;
		int partial_size = 0;

		// if it's empty, then escape!
		if (chunks == null)
			return null;

		for (int i=0; i<chunks.size(); i++)
			size += chunks.get(i).GetSize();
		
		int[] array = new int[size];
		int j;
		
		for (int i=0; i<chunks.size(); i++){
			for (j=0; j<chunks.get(i).GetSize(); j++){
				array[partial_size+j] = chunks.get(i).GetData()[j];
			}
			partial_size += j;
		}
		
		return new Chunk(array);
	}
	
	public static Chunk IntegerListToChunk(ArrayList<Integer> list){
		
		int[] data = Utilities.convertToArray(list);
		return new Chunk(data);
	}
	
	public static void WriteFile(String path, Chunk contents) {
		   try {
		        BufferedWriter out = new BufferedWriter(new FileWriter(path));
		        out.write(contents.toString());
		        out.close();
		    } catch (IOException e) {
		    	System.err.println("Error: writing file "+path);
		    }
		}
	
}
