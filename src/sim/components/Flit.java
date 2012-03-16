/*
 * Copyright (c) 2010, James Hanlon & Simon Aquino
 * All rights reserved.
 * 
 * Made available under the BSD license - see the LICENSE file
 */ 
package sim.components;

import sim.Simulator;
import sim.mapreduce.Tag;

public class Flit extends Signal {
	
	private int     m_msgId;
	private int     m_sample;
	private int     m_vc;
	private long    m_injectionTime;
	private long    m_received;
	private int     m_hops;
	
	public Flit(int messageId, int sample) {
		super();
		m_msgId = messageId;
		m_sample = sample;
		m_vc = -1;
		m_hops = 0;
		m_injectionTime = Simulator.clock();
	}
	
	public int     getMsgId()              { return m_msgId; }
	public boolean isMmt()                 { return m_sample!=-1; }
	public int     sampleNum()             { return m_sample; }
	public int     setVC(int vc)           { return m_vc = vc; }
	public int     getVC()                 { return m_vc; }
	public long    getInjTime()            { return m_injectionTime; }
	public void    incHops()               { m_hops++; }
	public int     getNumHops()            { return m_hops; }
	public long    getTimeReceived()       { return m_received; }
	public void    setTimeReceived(long t) {  m_received = t; }
	public long    getLatency()            { return m_received - m_injectionTime; }
	public int     getHops()               { return m_hops; }
}

class HeaderFlit extends Flit {
	
	private int m_src;
	private int m_dest;
	private int m_length;
	private int m_job;
	private Tag m_tag;
	
	//LinkedList<Integer> m_sources;
	
	public HeaderFlit(int messageId, int sample, int src, int dest, int length) {
		super(messageId, sample);
		m_src = src;
		m_dest = dest;
		m_length = length;
	}
	
	public HeaderFlit(int messageId, int sample, int src, int dest, int length, int job, Tag tag) {
		super(messageId, sample);
		m_src = src;
		m_dest = dest;
		m_length = length;
		m_job = job;
		m_tag = tag;
	}
	
	/*public HeaderFlit(int messageId, int sample, int src, LinkedList<Integer> sources, int dest, int length, int job, Tag tag) {
		super(messageId, sample);
		m_src = src;
		m_dest = dest;
		m_length = length;
		m_job = job;
		m_tag = tag;
		m_sources = sources;
	}*/
	
	public int    getSrc()    { return m_src; }
	public int    getDest()   { return m_dest; }
	public int    getLength() { return m_length; }
	public String toString()  { return "[H"+getMsgId()+":"+m_dest+"]"; }
	public int	  getJob()	  { return m_job; }
	public Tag	  getTag()	  { return m_tag; }
	
	//public LinkedList<Integer> getSources() { return m_sources; }
}

class BodyFlit extends Flit {

	private int	m_data;
	int m_seqNum;
	
	public BodyFlit(int messageId, int sample, int seqNum, int data) {
		super(messageId, sample);
		m_seqNum = seqNum;
		m_data = data;
	}
	
	public BodyFlit(int messageId, int sample, int seqNum) {
		super(messageId, sample);
		m_seqNum = seqNum;
	}
	
	public String toString() { return "[B"+getMsgId()+"."+m_seqNum+"]"; }
	public int getData()  { return m_data; }
}

class TailFlit extends Flit {

	public TailFlit(int messageId, int sample) {
		super(messageId, sample);
	}

	public String toString() { return "[T"+getMsgId()+"]"; }
}
