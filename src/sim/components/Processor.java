/*
 * Copyright (c) 2012, James Hanlon & Simon Aquino
 * All rights reserved.
 * 
 * Made available under the BSD license - see the LICENSE file
 */ 
package sim.components;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;

import java.io.File;

import sim.Config;
import sim.DebugMode;
import sim.RunMode;
import sim.Simulator;
import sim.Config.TrafficType;
import sim.stats.Stats;
import sim.traffic.Injection;
import sim.traffic.TraceEvent;
import sim.traffic.Traffic;

import sim.mapreduce.Chunk;
import sim.mapreduce.ChunkUtils;
import sim.mapreduce.MapReduce;
import sim.mapreduce.Tag;
import sim.mapreduce.State;

public class Processor extends Console 
implements Component {
	
	public  static final int       PROC_VC = 0;
	private static int             m_msgIdCount = 0;
	private int                    m_nodeId;
	private InputPort              m_inputPort;
	private OutputPort             m_outputPort;
	private LinkedList<Flit>       m_pendingFlits;
	private LinkedList<Flit>       m_receivedFlits;
	private LinkedList<TraceEvent> m_pendingEvents;
	
	// Reserved for MapReduce
	private static ArrayList<MapReduce>   mapreduce;
	private static int 					  master = Integer.parseInt(Config.mapReduceMaster());
	private static StateMaster			  state_m = StateMaster.Initial;
	private String						  result_folder = "results";

	
	public Processor(int nodeId, ProcessorLink fromRouter, ProcessorLink toRouter) {
		super();
		m_nodeId        = nodeId;
		m_pendingFlits  = new LinkedList<Flit>();
		m_receivedFlits = new LinkedList<Flit>();
		
		// Connect router
		m_inputPort     = new InputPort(this, m_nodeId, 0, fromRouter, 1);
		m_outputPort    = new OutputPort(this, m_nodeId, 0, toRouter, 1);
		fromRouter.connectTo(m_inputPort);
		toRouter.connectFrom(m_outputPort);
		
		// MapReduce
		mapreduce = new ArrayList<MapReduce>();
	}
	
	static long total = 0;
	long start, end;
	
	/*
	 * Generate uniform traffic and read received credits
	 */
	public void update() {
		readIncomingCredits();
		readIncomingFlits();
		generateTraffic();
		routeOutgoingFlit();
	}

	/*
	 * Write flits to send to router input
	 */
	public void copy() {
		m_inputPort.writeOuputCredit();
		m_outputPort.writeOutputFlit();
	}

	/*
	 * Read any new credits from output port and update VC credits
	 */
	private void readIncomingCredits() {
		Credit credit = m_outputPort.readInputCredit();
		if(credit != null)
			m_outputPort.incrementCredits(credit.getVC());
	}
	
	/*
	 * Read incoming flits from link, and add them to received flits list.
	 * Call incCurrVCCredits() as processor consumes flits
	 * 
	 * NOTE: for now, don't store all received flits
	 * NOTE: for non-minimal algorithms will require a ROB
	 */
	private void readIncomingFlits() {
		
		// Initialise variables for MapReduce.
		Tag tag = Tag.Undefined;
		ArrayList<Integer> data = new ArrayList<Integer>();
		int source = -1;
		int job_id = -1;
		
		m_inputPort.readInputFlit();
		Flit flit = m_inputPort.peekNextFlit();

		if(flit != null) {
			flit.setTimeReceived(Simulator.clock());
			m_receivedFlits.add(m_inputPort.takeNextFlit());
			console("Received flit "+flit);
			m_inputPort.incCurrVCCredits();
			
			// Only record stats once per packet, remove the packet from received
			if(flit instanceof TailFlit) {
				
				boolean gotHead = false;
				int bodyCount = 0;
				int bodySize = -1;
				for(Iterator<Flit> it  = m_receivedFlits.iterator(); it.hasNext(); ) {
					Flit f = it.next();
					if(flit.getMsgId() == f.getMsgId()) {
						if(f instanceof HeaderFlit) {
							gotHead = true;
							bodySize = ((HeaderFlit)f).getLength();
							
							// If I'm running mapreduce, message will have a tag
							if (Config.traffic().equals(TrafficType.MAPREDUCE)){
								job_id = ((HeaderFlit)f).getJob();
								tag = ((HeaderFlit)f).getTag();
								source = ((HeaderFlit)f).getSrc();
								//sources = ((HeaderFlit)f).getSources();
							}
						}
						if(f instanceof BodyFlit){
							bodyCount++;
							if (Config.traffic().equals(TrafficType.MAPREDUCE)){
								data.add( ((BodyFlit)f).getData() );
							}
						}
						it.remove();
					}
				}

				if(gotHead && bodyCount == bodySize) {
					Stats.retirePacket(m_nodeId, flit);
					
					if (Config.traffic().equals(TrafficType.MAPREDUCE)){
						mapreduce.get(job_id).RetirePacket(this, source, data, tag);
					}

				} else {
					System.err.println("Error: incomplete packet");
				}
			}
		}
		Stats.retireFlit(m_nodeId, flit);
	}
	
	/*
	 * Take first pending flit and put on output port if that port is ready 
	 * to accept flits. I.e. it is not waiting for credits.
	 */
	private void routeOutgoingFlit() {	
		if(!m_pendingFlits.isEmpty() && m_outputPort.hasCredits(PROC_VC)) {
			Flit flit = m_pendingFlits.getFirst();
			int outputVC = 0;
			
			if(flit instanceof HeaderFlit) {
				outputVC = m_outputPort.allocVC();
				
				if(outputVC == -1) {
					//console("could not allocate the VC for the processor");
					return;
				} else {
					m_outputPort.setupConnection(PROC_VC, 0, 0);
					console(flit+" opening connection through O[0:0]");
				}
			}
			
			m_outputPort.setCurrVC(outputVC);
			m_outputPort.addFlit(PROC_VC, m_pendingFlits.removeFirst());
		}
	}

	/*
	 * Generate uniform traffic to a random node, don't if in debug mode and 
	 * outside of limit or when draining on a proper run.
	 */
	private void generateTraffic() {
		if((Config.debugMode() && m_msgIdCount == Config.maxMsgs() && Config.maxMsgs() > 0) ||
				(Config.runMode() && Simulator.draining()))
			return;
		
		if(Config.traffic().equals(TrafficType.TRACE)) {
			// Take all of the pending trace events for this clock cycle
			for(Iterator<TraceEvent> it = m_pendingEvents.iterator(); it.hasNext();) {
				TraceEvent e = (TraceEvent) it.next();
				if(e.clock == Simulator.clock()) {
					generatePacket(e.dest, e.burst);
					it.remove();
				} else {
					break;
				}
			}
		}
		else if (Config.traffic().equals(TrafficType.MAPREDUCE)){
			RunMapReduce();
		}
		else {
			// Use a traffic pattern generation function
			int node = Traffic.getDest(m_nodeId, Simulator.numNodes());
			int pktLen = Injection.getPacketLen(m_nodeId);
			if(pktLen > 0) {
				generatePacket(node, pktLen);
			}
		}
	}
	
	public void RunMapReduce(){

		int terminating_job = -1;
		// This function control the scheduling of the MapReduce algorithm.
		
		// Reserved for the master node.
		if (m_nodeId == master){
			if (state_m == StateMaster.Initial){
				
		        File folder = new File(Config.mapReduceDirectory());
		        File[] FileList = folder.listFiles();
		        
		        int no_jobs = 0;
		        
		        for (int i=0; i<FileList.length; i++) {
		            if (FileList[i].isFile()) {
		            	
		            	String pathOfFile = FileList[i].getAbsolutePath();
		            	String file_name = FileList[i].getName();
						Chunk chunk = InstantiateMapReduceJobs(pathOfFile);

						MapReduce mr = new MapReduce(this, no_jobs, file_name, chunk);
						
						mapreduce.add(mr);

						// One job is enough to make the whole thing run.
				        state_m = StateMaster.Running;
				        no_jobs++;
		            }
		        }
		        // Return when finishing running MasterMap
		        return;
			}
		}

		if (state_m == StateMaster.Running){
			
			// Run all the jobs in the pipeline one by one.
			for (int i=0; i<mapreduce.size(); i++){
				MapReduce job = mapreduce.get(i);
				
				// Skip completed job
				if (job == null)
					continue;
				
				// Get state.
				State state = job.getState();
				
				// Run
				switch (state){
				
				case Map:
					job.SlaveMapRoutine(this);
					break;
					
				case Reduce:
					job.SlaveReduceRoutine(this);
					break;
					
				case Terminated:
					job.MasterReduceRoutine(this);
					state_m = StateMaster.Final;
					terminating_job = job.getJobId();
					break;
	
				case Wait:
				default:
					break;
				}
			}
		}
			
		if (m_nodeId == master){
			if (state_m == StateMaster.Final){
				
				// Run master reduce function.
				Chunk result = mapreduce.get(terminating_job).getFinalResult();
				String filename = mapreduce.get(terminating_job).getFileName();
			
				String FinalPath = result_folder + "/" + filename;
			
				// Write file in results.
				ChunkUtils.WriteFile(FinalPath, result);
			
				// Delete job in the queue, substituting it with null. This will
				// prevent modification of the queue.

				mapreduce.set(terminating_job, null);

				// Check if there are other jobs running in the pipeline,
				// otherwise the simulation has just finished!

				if (ContainsAllNulls(mapreduce))
					// Keep mapreduce running for other jobs.
					state_m = StateMaster.Terminated;
				else
					state_m = StateMaster.Running;
			}
		}
		
		if (state_m ==StateMaster.Terminated){
			
			if (Config.runMode())
				RunMode.setTerminateSimulation(true);
			else if (Config.debugMode())
				DebugMode.stop();
		}

	}
	// Instantiate jobs.
	private Chunk InstantiateMapReduceJobs(String filename){
		
		// Read file and convert it into an array.
		Chunk input = ChunkUtils.readFile(filename);
		
		if (input == null){
			System.err.println("I can't read the input file. Abort simulation.");
			System.exit(1);	
		}
		return input;
	}
	
	private void generatePacket(int dest, int length) {
		int messageId = m_msgIdCount++;
		int sample = Simulator.running() ? RunMode.sampleNum() : -1;
		m_pendingFlits.addLast(new HeaderFlit(messageId, sample, m_nodeId, dest, length));
		for(int i=0; i<length; i++)
			m_pendingFlits.addLast(new BodyFlit(messageId, sample, i));
		m_pendingFlits.addLast(new TailFlit(messageId, sample));
		//System.out.println("Node "+m_nodeId+" generated packet "+length+" flits to node "+dest+" at "+Simulator.clock());
		Stats.newPacket();
	}
	
	public void generatePacket(int dest, int[] data, int job_no, Tag tag) {
		int messageId = m_msgIdCount++;
		int sample = Simulator.running() ? RunMode.sampleNum() : -1;
		m_pendingFlits.addLast(new HeaderFlit(messageId, sample, m_nodeId, dest, data.length, job_no, tag));
		for(int i=0; i<data.length; i++)
			m_pendingFlits.addLast(new BodyFlit(messageId, sample, i, data[i]));
		m_pendingFlits.addLast(new TailFlit(messageId, sample));
		//System.out.println("Node "+m_nodeId+" generated packet "+length+" flits to node "+dest+" at "+Simulator.clock());
		Stats.newPacket();
	}

	/*
	 * The following code implement source routing. It won't be used for this
	 * project and has been commented out.
	 */
	/*public void generatePacket(int dest, int[] data, LinkedList<Integer> sources, int job_no, Tag tag) {
		int messageId = m_msgIdCount++;
		int sample = Simulator.running() ? RunMode.sampleNum() : -1;
		m_pendingFlits.addLast(new HeaderFlit(messageId, sample, m_nodeId, sources, dest, data.length, job_no, tag));
		for(int i=0; i<data.length; i++)
			m_pendingFlits.addLast(new BodyFlit(messageId, sample, i, data[i]));
		m_pendingFlits.addLast(new TailFlit(messageId, sample));
		//System.out.println("Node "+m_nodeId+" generated packet "+length+" flits to node "+dest+" at "+Simulator.clock());
		Stats.newPacket();
	}*/
	
	public String toString() {
		String s = "";
		
		s += "[I/O PORTS]\n\n";
		s += String.format("%-12s%-12s%-12s%-12s%-12s\n", "Port", "To", "State", "Cdts", "Buffer");
		s += m_inputPort;
		s += m_outputPort;
		
		//s += "\n\n[OUTGOING LINK]\n\n";
		//s += "Link\tFlits\tCdts\n";
		//s += m_outputPort.getLink()+"\n";
		
		s += "\n\n[PENDING FLITS : "+m_pendingFlits.size()+"]\n\n";
		for(Flit f : m_pendingFlits)
			s += f+"\n";
		
		s += "\n\n[RECEIVED FLITS : "+m_receivedFlits.size()+"]\n\n";
		s += String.format("%-16s%-16s%-16%s\n", "Flit", "Rcvd@", "Hops");
		for(Flit f : m_receivedFlits)
			s += String.format("%-16s%-16d%-16d\n", f.toString(), f.getTimeReceived(), f.getNumHops());
		
		return s;
	}

	private static boolean ContainsAllNulls(ArrayList<MapReduce> list)
	{
	    if(list != null){
	        for(int i=0; i<list.size(); i++)
	            if(list.get(i) != null) return false;
	    }
	    return true;
	}
	
	public void reset() {
		m_pendingFlits.clear();
		m_receivedFlits.clear();
		m_inputPort.reset();
		m_outputPort.reset();
		
		if (Config.traffic().equals(TrafficType.MAPREDUCE)){
			state_m = StateMaster.Initial;
			mapreduce.removeAll(mapreduce);
		}
	}

	public int     getNodeId()         { return m_nodeId; }

	public String  getTitle()          { return "Processor "+m_nodeId; }
	public boolean hasFlits()          { return !m_pendingFlits.isEmpty(); }
	public Flit    peekFlit()          { return m_pendingFlits.peek(); }
	public Flit    takeFlit()          { return m_pendingFlits.poll(); }
	public int	   getMaster()		   { return master; }
	
	public void SetStateMaster (StateMaster state) { state_m = state; }
	
	public void addTrace(LinkedList<TraceEvent> trace) { m_pendingEvents = trace; }
	
	public enum StateMaster{Initial, Running, Final, Terminated};
	
}
