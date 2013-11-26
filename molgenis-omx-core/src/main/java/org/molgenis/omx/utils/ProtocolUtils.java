package org.molgenis.omx.utils;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.omx.observ.Protocol;

public class ProtocolUtils
{
	/**
	 * Returns the descendants of the given protocol including the given protocol
	 * 
	 * @param protocol
	 * @return
	 */
	public static List<Protocol> getProtocolDescendants(Protocol protocol)
	{
		return getProtocolDescendants(protocol, true);
	}

	/**
	 * Returns the descendants of the given protocol
	 * 
	 * @param protocol
	 * @param includeProtocol
	 *            whether the given protocol is in the descendants list
	 * @return
	 */
	public static List<Protocol> getProtocolDescendants(Protocol protocol, boolean includeProtocol)
	{
		List<Protocol> protocols = new ArrayList<Protocol>();
		getProtocolDescendantsRec(protocol, protocols, includeProtocol);
		return protocols;
	}

	private static void getProtocolDescendantsRec(Protocol protocol, List<Protocol> protocols, boolean includeProtocol)
	{
		if (includeProtocol) protocols.add(protocol);
			
		List<Protocol> subProtocols = protocol.getSubprotocols();
		if (subProtocols != null && !subProtocols.isEmpty())
		{
			for (Protocol subProtocol : subProtocols)
			{
				getProtocolDescendantsRec(subProtocol, protocols, true);
			}
		}
	}
	
	public static List<Protocol> getLineairizedWorkflow(Protocol protocol)
	{
		// Collect all protocols which have a direct or indirect connection with the given protocol 
		List<Protocol> protocols = new ArrayList<Protocol>();
		protocols = getWorkFlowProtocols(protocol, protocols);
		
		// Optionally linearize the collection of protocols
		if (isDirectedAcyclicGraph(protocols))
		{
			protocols = linearizeWorkflow(protocols);
		}
		
		return protocols;
	}
	
	private static List<Protocol> getWorkFlowProtocols(Protocol protocol, List<Protocol> protocols)
	{
		protocols.add(protocol);
			
		List<Protocol> subProtocols = protocol.getSubprotocols();
		if (subProtocols != null && !subProtocols.isEmpty())
		{
			for (Protocol subProtocol : subProtocols)
			{
				getWorkFlowProtocols(subProtocol, protocols);
			}
		}
		
		return protocols;
	}
	
	/**
	 * Detect if the current list of protocols is a directed acyclic graph
	 *  
	 * @return A boolean indicating if the current list of protocols is a directed acyclic graph or not
	 */
	public static boolean isDirectedAcyclicGraph(List<Protocol> protocols)
	{
		boolean dag = false;
		
		if (protocols != null && !protocols.isEmpty())
		{
			int[] protocolsCnt = new int[protocols.size()];
			for (int j = 0; j < protocolsCnt.length; j++)
			{
				protocolsCnt[j] = 0;
			}
			
			// detect if a protocol is present more then 2 times 
			for (int i = 0; i < protocols.size(); i++)
			{
				if (protocols.get(i) != null)
				{
					for (int j = 0; j < protocols.size(); j++)
					{
						if (protocols.get(j) != null)
						{
							if (protocols.get(i).equals(protocols.get(j)))
							{
								protocolsCnt[i]++;
							}
						}
					}
					if (protocolsCnt[i] > 2)
					{
						dag = true;
					}
				}
			}
			
			protocolsCnt = null;
		}
		
		return dag;
	}
	
	/**
	 * Alter the existing directed-acyclic-graph collection of protocols into a linear collection
	 */
	private static List<Protocol> linearizeWorkflow(List<Protocol> protocols)
	{
		// Remove double entries of the protocol collection
		List<Protocol> protocolCleaned = new ArrayList<Protocol>(); 
		for (Protocol prot : protocols)
		{
			if (prot != null)
			{
				if (protocolCleaned.size() == 0)
				{
					protocolCleaned.add(prot);
				}
				else
				{
					boolean present = false;
					for (Protocol protClean : protocolCleaned)
					{
						if (prot.equals(protClean))
						{
							present = true;
						}
					}
					if (!present)
					{
						protocolCleaned.add(prot);
					}
				}
			}
		}
		
		// Collect subProtocols of cleaned protocol collection
		List<List<Protocol>> protSubs = new ArrayList<List<Protocol>>();
		for (Protocol prot : protocolCleaned)
		{
			protSubs.add(prot.getSubprotocols());
			//protSubs.add(prot);
		}
		
		// TODO: Convert collection of protocols into list of steps with dependency to previous protocol steps
		// The protocolCleaned are the previous steps
				
		// Set the matrix booleans with direct dependencies to true
		boolean[][] matrixDirect = new boolean[protocolCleaned.size()][protocolCleaned.size()]; 
		for (int i = 0; i < protSubs.size(); i++)
		{
			if (protSubs.get(i) != null)
			{
				// For each protocol with subprotocols set the protocol as previous step to true on the subprotocols 
				for (int j = 0; j < protSubs.get(i).size(); j++)
				{
					if (protocolCleaned.get(i).equals(protSubs.get(i).get(j)))
					{
						matrixDirect[i][j] = true;
					}
				}
			}
		}
		
		// Set the matrix booleans with indirect dependencies to true
		boolean[][] matrixIndirect = new boolean[protocolCleaned.size()][protocolCleaned.size()]; 
		boolean[][] matrixIndirectCurrent = matrixDirect; 
		while (!matrixIndirect.equals(matrixIndirectCurrent))
		{
			matrixIndirect = matrixIndirectCurrent;
			// TODO:
			for ()
		}
		
		// Linearize the matrix
		
		return protocols;
	}
}
