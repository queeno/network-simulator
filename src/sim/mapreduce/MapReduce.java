/*
 * Copyright (c) 2012, Simon Aquino
 * All rights reserved.
 * 
 * Made available under the BSD license - see the LICENSE file
 */ 

package sim.mapreduce;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import sim.mapreduce.Chunk;
import sim.mapreduce.ChunkUtils;
import sim.mapreduce.ReduceAlgorithm;
import sim.mapreduce.State;

import sim.Config;
import sim.Config.TopologyType;
import sim.topology.Tring;
import sim.topology.Mesh;
import sim.components.Processor;

public class MapReduce {
	// Properties for map reduce.
	private int	job_id;
	
	private State				   state = State.Wait;
	private String				   FileName;
	private Chunk				   final_result;

	private ArrayList<Chunk>	   master_buffer;
	private ArrayList<Integer>	   master_sent_nodes;

	private Map<Integer,ArrayList<Chunk>> buffer =
			new HashMap<Integer,ArrayList<Chunk>>();
	private Map<Integer,ArrayList<Integer>>	sent_nodes =
			new HashMap<Integer,ArrayList<Integer>>();
	
	
	// MapReduce constructor.
	public MapReduce(Processor p, int job_id, String FileName, Chunk input){
		
		this.job_id = job_id;
		this.FileName = FileName;
		MasterMapRoutine(p, input);
		
	}
	
	public void RetirePacket(Processor p, int source, ArrayList<Integer> data, Tag tag){
		/*
		 * Retire Packet is called by Processor every time a packet is received
		 * by a processor in the network.
		 * 
		 * Args:
		 * 		p		:	Processor of the network.
		 * 		data	:	Data contained in the packet.
		 * 		tag		: 	Tag of the packet.
		 * 
		 *  Also sets the state of MapReduce that will be read by the processor.
		 *  
		 */
		int node_id = p.getNodeId();
		
		Chunk chunk = ChunkUtils.IntegerListToChunk(data) ;

		// Buffer the output of the root ring in the master buffer.
		if (IsInMasterSentNodes(source) && node_id == p.getMaster())
			master_buffer.add(chunk);
		else
			AddChunkToBuffer(chunk, node_id);
		
		// Do this only if the chunk is NOT empty.
		if (!chunk.isEmpty()){
			if (tag == Tag.Map)
				// If you see a Map packet, then run MAP.
				SetState( State.Map );
			else if (tag == Tag.Reduce){
				
				// Delete the chunk from the queue of the sent nodes.
				if (IsInMasterSentNodes(source) && IsInSentNodes(source, node_id))
					RemoveSentNodes(new Integer(source), node_id);
				else{
					RemoveSentNodes(new Integer(source), node_id);
					RemoveMasterSentNodes(new Integer(source));
				}
				
				//System.out.println((GetChunkFromBuffer(node_id)).toString());
				//System.out.println("NODES IN MASTER " + master_sent_nodes);
				//System.out.println("NODES IN SLAVE " + node_id + " NODES: " + sent_nodes.toString());
				
				// If the source node queue of the master node is empty then
				// result should be ready in final_result;;
				
				if  ( IsMasterSentNodesEmpty() && IsSentNodesEmpty(p.getMaster()) ){
					// Make sure only master calls termination.
					if (node_id == p.getMaster()){
						SetState ( State.Terminated );
						return;
					}
					
				}
				
				// Check whether all the expected packets have arrived.
				if  ( IsSentNodesEmpty(node_id) ){
					// Don't send the node to yourself!
					if (node_id != p.getMaster())
						SetState( State.Reduce );
					return;
				}
				
				// Wait to get all the receive packets back before reducing.
				SetState( State.Wait );	
				
			}
			else
				// Just in case.
				SetState( State.Wait );
		}
		else
			// Just in case.
			ResetBuffer(node_id);
	}
	
	public void SlaveMapRoutine(Processor p){
		/*
		 * Slave MAP Routine for MapReduce. Executed every time a processor
		 * receives a packet containing the MAP flag.
		 * 
		 * Args:
		 * 		p		:	Processor calling this function.
		 * 
		 */
		
		int k = Config.k();
		int n;
		
		int node_id = p.getNodeId();
		int master_id = p.getMaster();
		
		if (IsTring())
			n = Config.n();
		else
			// Use node 0 of mesh as reference point for level.
			n = Mesh.getLevel(0, k);
		
		ArrayList<Integer> unused_recipients = new ArrayList<Integer>();
		
		// Get current level
		int c_lev;
		
		if (IsTring())
			c_lev = Tring.LevelOfNode (node_id, k);
		else
			c_lev = Mesh.getLevel(node_id, k);
		
		// Check you're not on the lowest level. If you are then change state
		// to reduce and return.
		if (c_lev == n){
			SlaveReduceRoutine(p);
			return;
		}
		
		if ( BufferIsNull(node_id) ){
			System.err.println("Running map with empty buffer. Abort simulation.");
			System.exit(1);
		}
		
		if (BufferIsUnary(node_id)){
			SlaveReduceRoutine(p);
			return;
		}

		ArrayList<Chunk> buff_chunks = GetChunkFromBuffer(node_id);
		
		// Clear buffer
		ResetBuffer(node_id);
		
		// Allow to merge chunks in the buffer at this point, although normally
		// there would be only 1.
		Chunk buff = ChunkUtils.MergeChunks(buff_chunks);
	
		// Build array of recipients
		ArrayList<Integer> recipients;
		
		if (IsTring())
			recipients = Tring.ReturnNodesLowerRing(node_id, k, n);
		else
			recipients = Mesh.ReturnLowerNodes(node_id, master_id, k);
			//recipients = Mesh.ReturnLowerNodes(node_id, k);
		
		// Split input in k parts for nodes in the root.
		ArrayList<Chunk> chunks;
		
		if (IsTring())
			chunks = ChunkUtils.splitChunk(buff, k-1);
		else{
			chunks = ChunkUtils.splitChunk(buff, recipients.size()+1);
		
			// Buffer a chunk and keeps it for reduction.
			if (chunks.size() > 1){
				AddChunkToBuffer(chunks.get(0), node_id);
				chunks.remove(0);
			}
		}
		
		for (int i=0; i<chunks.size(); i++){
			
			if (chunks.get(i).GetUnary()){
				AddChunkToBuffer(chunks.get(i), node_id);
				unused_recipients.add( recipients.get(i) );
			}
			else{
				int recipient = recipients.get(i); 
				AddSentNodes(recipient, node_id );
				
				p.generatePacket(recipient, chunks.get(i).GetData(), job_id, Tag.Map);

			}
		}

		// If there are more than 2 elements in buffer, then merge and send them
		// to destination node, if available.
		if ( !BufferIsUnary(node_id) && !( unused_recipients.isEmpty() ) ){
			
				// Merge chunks in this case before sending.
				Chunk tmp_buffer = ChunkUtils.MergeChunks(GetChunkFromBuffer(node_id));
				
				// Send
				int recipient = unused_recipients.get(0);
				
				AddSentNodes(recipient, node_id );
				
				p.generatePacket(recipient, tmp_buffer.GetData(), job_id, Tag.Map);
				
				// Empty buffer.
				ResetBuffer(node_id);
		}
		// Run reduce algorithm in buffer so it will be done when the other chunk
		// is back.
		else if (!BufferIsNull(node_id)){
			// Run reduce algorithm over chunk in buffer
			Chunk new_buff = ReduceAlgorithm.RunReduceAlgorithm(GetChunkFromBuffer(node_id));
			
			// Empty old buffer
			ResetBuffer(node_id);
			
			// Write reduced chunk into buffer.
			AddChunkToBuffer(new_buff, node_id);
		}

		// Finally, change the state of the processor to wait.
		SetState(State.Wait);

	}

	public void MasterMapRoutine(Processor p, Chunk input){
		/*
		 * Master MAP Routine for MapReduce.
		 * Called by the Master Processor to initiate a job resolution.
		 * 
		 * Args:
		 * 		p		:		Calling processor (master).
		 * 		input	:		The input wrapped in a Chunk object.
		 * 
		 */
		
		
		int k = Config.k();
		int node_id = p.getNodeId();
		
		// Initialise master_sent_nodes and master buffer
		master_sent_nodes = new ArrayList<Integer>();
		master_buffer = new ArrayList<Chunk>();
		
		// Build array of recipients
		ArrayList<Integer> recipients;
		
		if (IsTring())
			recipients = Tring.ReturnNodesInRing(node_id, k);
		else{
			recipients = Mesh.ReturnNodesInAllDirections(node_id, k);
		
			if (recipients == null){
				System.err.println("Master node not allowed to be on lowest level.");
				System.err.println("Select master node on higher level.");
				System.exit(1);
			}
				
		}
		// Split input in k parts for nodes in the root.
		ArrayList<Chunk> chunks;
		
		if (IsTring())
			chunks = ChunkUtils.splitChunk(input, k);
		else
			chunks = ChunkUtils.splitChunk(input, recipients.size());
			
		// Take master off the list.
		recipients.remove(new Integer(node_id));
		
		// Work out how many chunks to send.
		int chunks_to_send = chunks.size() < k ? chunks.size() : chunks.size()-1;
		
		for (int i=0; i<chunks_to_send; i++){

			// Else, send!
			int recipient = recipients.get(i);
			master_sent_nodes.add( recipient );
			/*System.out.println("I'm sending chunk: " +
			chunks.get(i).toString() + " to " + recipient + " Job_ID "
					+ job_id);*/
			p.generatePacket(recipient, chunks.get(i).GetData(), job_id, Tag.Map);

		}
		
		// The remaining chunk should be buffered in the 'slave node' so it will be
		// split within the family.
		if (chunks.size() == k){
			AddChunkToBuffer( chunks.get(k-1), p.getMaster() );

			// This will trigger SlaveMapRoutine for master node as well.
			SlaveMapRoutine(p);
		}

		state = State.Wait;
	}
	
	public void SlaveReduceRoutine(Processor p){
		/*
		 * Slave Reduce Routine for MapReduce.
		 * 
		 * This function allows the Reduction function to be called and a new
		 * packet to be generated every time a new packet is received containing
		 * the reduction flag.
		 * 
		 * Args:
		 * 		p		:	Calling processor.
		 * 
		 * I made sure to enter this function only when all data has been
		 * received. If the buffer is still empty and I'm here, there's
		 * something wrong.
		 * 
		 */ 
		
		int k = Config.k();
		
		int dest_add = -1;
		
		int node_id = p.getNodeId();
		int master_id = p.getMaster();

		// Get current level
		int c_lev;
		
		if (IsTring())
			c_lev = Tring.LevelOfNode (node_id, k);
		else
			c_lev = Mesh.getLevel(node_id, k);

		if ( BufferIsNull(node_id) )
			System.err.println("ERROR in MapReduce: NULL buffer" +
					" when state was reduce.");
		
		ArrayList<Chunk> buff = GetChunkFromBuffer( node_id );
		
		// Run reduce algorithm.
		Chunk output = ReduceAlgorithm.RunReduceAlgorithm(buff);
		
		//System.out.println("NODE " + node_id + ": " + output.toString());
		
		// Send chunk to node on upper level.
		
		// Work out address. If I'm on level 1, then destination address is
		// master node. If I'm the master node, then update buffer and return.
		if (node_id == master_id){
			
			ResetBuffer(node_id);
			AddChunkToBuffer(output, node_id);
	
			state = State.Wait;
			return;
		}
		else{
			if (IsTring()){
				if (c_lev == 1)
					dest_add = master_id;
				else
					dest_add = Tring.ReturnUpperNodeConnectingRing(node_id, k);
			}
			else{
				dest_add = Mesh.ReturnUpNode(node_id, master_id, k);
				
				if (dest_add == -1)
					System.err.println("Reduction in slave mode. Master was expected.");
			}
		}
		
		// Send packet.
		p.generatePacket(dest_add, output.GetData(), job_id, Tag.Reduce);
		
		// Empty buffer.
		ResetBuffer(node_id);

		state = State.Wait;
	}
	
	public void MasterReduceRoutine(Processor p){
		/*
		 * Final stage of the MapReduce job resolution process.
		 * The master node takes the chunks remaining and performs the last
		 * reduction.
		 * 
		 * Args:
		 *		p		:	Calling processor (master).
		 
		 * I made sure to enter this function only when all data has been
		 * received. If the buffer is still empty and I'm here, there's
		 * something wrong.
		 */ 
		
		if (MasterBufferIsNull())
			System.err.println("ERROR in MapReduce: NULL buffer" +
					" when state was reduce.");
		
		// Merge slave buffer to master buffer
		
		ArrayList<Chunk> slave_buffer = GetChunkFromBuffer(p.getMaster());
		
		if (slave_buffer != null)
			master_buffer.addAll(slave_buffer);
		
		// Run reduce algorithm.
		Chunk result = ReduceAlgorithm.RunReduceAlgorithm(master_buffer);
		
		//System.out.println("NODE " + p.getMaster() + ": " + result.toString());
		
		final_result = result; 
	}
	
	/*
	 * ---------------------
	 * Supporting Functions.
	 * ---------------------
	 */
	
	private boolean	BufferIsUnary(int node_id) {
		if (buffer == null) return false;
		if (BufferIsNull(node_id)) return false;
		
		Chunk merged_buff = ChunkUtils.MergeChunks(GetChunkFromBuffer(node_id));
		
		return merged_buff.GetUnary();

	}
	
	private boolean BufferIsNull(int node_id) {
		if (buffer == null) return false;
		return null==GetChunkFromBuffer(node_id) ? true : false;
	}
	
	private boolean MasterBufferIsNull() {
		return null==master_buffer ? true : false;
	}
	
	private void ResetBuffer(int node_Id){
		buffer.put(new Integer(node_Id), null);
	}
	
    private ArrayList<Chunk> GetChunkFromBuffer(int node_id){
		if (buffer == null) return null;
    	return buffer.get(new Integer(node_id));
    }
    
    private void AddChunkToBuffer(Chunk elem, int node_Id){
		ArrayList<Chunk> result = buffer.get(new Integer(node_Id));
		
		if (result == null){
			result = new ArrayList<Chunk>();
			result.add(elem);
			buffer.put(new Integer(node_Id), result);
		}
		else{
			// Merge
			result.add(elem);
			buffer.put(new Integer(node_Id), result);
		}
	}
	
	private void AddSentNodes(int elem, int node_Id){
		ArrayList<Integer> result = sent_nodes.get(new Integer(node_Id));
		if(result == null){
			result = new ArrayList<Integer>();
			result.add(elem);
			sent_nodes.put(new Integer(node_Id), result);
		}
		else{
			result.add(elem);
			sent_nodes.put(new Integer(node_Id), result);
		}
	}
	
	private void RemoveSentNodes(int elem, int node_Id){
		ArrayList<Integer> result = sent_nodes.get(new Integer(node_Id));
		if(result == null){
			return;
		}
		else{
			result.remove(new Integer(elem));
			sent_nodes.put(new Integer(node_Id), result);
		}
	}
	
	private void RemoveMasterSentNodes(int elem){
		if(master_sent_nodes == null){
			System.err.println("I couldn't find any node for current Reduce" +
					" job.");
			System.exit(1);
		}
		else{
			master_sent_nodes.remove(new Integer(elem));
		}
	}
	
	private boolean IsSentNodesEmpty(int node_Id){
		ArrayList<Integer> result = sent_nodes.get(new Integer(node_Id));
		if(result == null){
			return true;
		}
		return result.isEmpty();
	}
	
	private boolean IsInMasterSentNodes(int source){
		return master_sent_nodes.contains(new Integer(source));
	}
	
	private boolean IsInSentNodes(int elem, int node_id){
		if (sent_nodes.get(new Integer(node_id)) == null)
			return false;

		return sent_nodes.get(new Integer(node_id)).contains(new Integer(elem));
		
	}
	
	private boolean IsMasterSentNodesEmpty(){
		if(master_sent_nodes == null){
			return true;
		}
		return master_sent_nodes.isEmpty();
	}
	
	/*
	 * Used for source routing, won't be used for this project.
	 */
	/*private LinkedList<Integer> CopySourceList(LinkedList<Integer> sources){
		LinkedList<Integer> n_sources = new LinkedList<Integer>();
		
		for(int i=0; i<sources.size(); i++)
			n_sources.addFirst(sources.get(i));
		
		return n_sources;
	}*/
	
	private boolean IsTring(){
		return Config.topology().equals(TopologyType.TRING);
	}
	
	public int	   getJobId()		   { return job_id; }
	public State   getState()		   { return state; }
	public String  getFileName()	   { return FileName; }
	public Chunk   getFinalResult()	   { return final_result; }
	
	public void	   SetState(State state) { this.state = state; }
}