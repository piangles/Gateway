package org.piangles.gateway.dto;

import java.io.Serializable;

public final class SystemInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String processName = null;
	private String processId = null;
	private String threadId = null; 
	
	public SystemInfo(String processName, String processId)
	{
		this.processName = processName;
		this.processId = processId;
	}

	public String getProcessName()
	{
		return processName;
	}

	public String getProcessId()
	{
		return processId;
	}

	public String getThreadId()
	{
		return threadId;
	}

	/**
	 * Taken from experience with Scala but limited only to one member.
	 */
	public SystemInfo cloneAndCopy(String threadId)
	{
		SystemInfo si = new SystemInfo(processName, processId);
		si.threadId = threadId;
		return si;
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append(processName).append(" ");
		sb.append(processId).append(" ");
		sb.append(threadId);
		sb.append("]");
		return sb.toString();
	}
}