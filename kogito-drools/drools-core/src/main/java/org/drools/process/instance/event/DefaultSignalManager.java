package org.drools.process.instance.event;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.drools.WorkingMemory;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.WorkingMemoryAction;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.runtime.process.EventListener;
import org.drools.runtime.process.ProcessInstance;

public class DefaultSignalManager implements SignalManager {
	
	private Map<String, List<EventListener>> processEventListeners;
	private WorkingMemory workingMemory;
	
	public DefaultSignalManager(WorkingMemory workingMemory) {
		this.workingMemory = workingMemory;
	}
	
	public WorkingMemory getWorkingMemory() {
		return workingMemory;
	}

	public void addEventListener(String type, EventListener eventListener) {
		if (processEventListeners == null) {
			processEventListeners = new HashMap<String, List<EventListener>>();
		}
		List<EventListener> eventListeners = processEventListeners.get(type);
		if (eventListeners == null) {
			eventListeners = new CopyOnWriteArrayList<EventListener>();
			processEventListeners.put(type, eventListeners);
		}
		eventListeners.add(eventListener);
	}
	
	public void removeEventListener(String type, EventListener eventListener) {
		if (processEventListeners != null) {
			List<EventListener> eventListeners = processEventListeners.get(type);
			if (eventListeners != null) {
				eventListeners.remove(eventListener);
			}
		}
	}
	
	public void signalEvent(String type, Object event) {
		((InternalWorkingMemory) workingMemory).queueWorkingMemoryAction(new SignalAction(type, event));
		workingMemory.fireAllRules();
	}
	
	public void internalSignalEvent(String type, Object event) {
		if (processEventListeners != null) {
			List<EventListener> eventListeners = processEventListeners.get(type);
			if (eventListeners != null) {
				for (EventListener eventListener: eventListeners) {
					eventListener.signalEvent(type, event);
				}
			}
		}
	}
	public void signalEvent(long processInstanceId, String type, Object event) {
		ProcessInstance processInstance = workingMemory.getProcessInstance(processInstanceId);
		if (processInstance != null) {
			((InternalWorkingMemory) workingMemory).queueWorkingMemoryAction(new SignalProcessInstanceAction(processInstanceId, type, event));
		}
	}
	
	public static class SignalProcessInstanceAction implements WorkingMemoryAction {

		private long processInstanceId;
		private String type;
		private Object event;
		
		public SignalProcessInstanceAction(long processInstanceId, String type, Object event) {
			this.processInstanceId = processInstanceId;
			this.type = type;
			this.event = event;
			
		}
		
		public SignalProcessInstanceAction(MarshallerReaderContext context) throws IOException, ClassNotFoundException {
			processInstanceId = context.readLong();
			type = context.readUTF();
			if (context.readBoolean()) {
				event = context.readObject();
			}
		}
		
		public void execute(InternalWorkingMemory workingMemory) {
			ProcessInstance processInstance = workingMemory.getProcessInstance(processInstanceId);
			if (processInstance != null) {
				processInstance.signalEvent(type, event);
			}
		}

		public void write(MarshallerWriteContext context) throws IOException {
			context.writeInt( WorkingMemoryAction.SignalProcessInstanceAction );
			context.writeLong(processInstanceId);
			context.writeUTF(type);
			context.writeBoolean(event != null);
			if (event != null) {
				context.writeObject(event);
			}
		}

		public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
			processInstanceId = in.readLong();
			type = in.readUTF();
			if (in.readBoolean()) {
				event = in.readObject();
			}
		}

		public void writeExternal(ObjectOutput out) throws IOException {
			out.writeLong(processInstanceId);
			out.writeUTF(type);
			out.writeBoolean(event != null);
			if (event != null) {
				out.writeObject(event);
			}
		}
		
	}
	
	public static class SignalAction implements WorkingMemoryAction {

		private String type;
		private Object event;
		
		public SignalAction(String type, Object event) {
			this.type = type;
			this.event = event;
		}
		
		public SignalAction(MarshallerReaderContext context) throws IOException, ClassNotFoundException {
			type = context.readUTF();
			if (context.readBoolean()) {
				event = context.readObject();
			}
		}
		
		public void execute(InternalWorkingMemory workingMemory) {
			((DefaultSignalManager) workingMemory.getSignalManager()).internalSignalEvent(type, event);
		}

		public void write(MarshallerWriteContext context) throws IOException {
			context.writeInt( WorkingMemoryAction.SignalAction );
			context.writeUTF(type);
			context.writeBoolean(event != null);
			if (event != null) {
				context.writeObject(event);
			}
		}

		public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
			type = in.readUTF();
			if (in.readBoolean()) {
				event = in.readObject();
			}
		}

		public void writeExternal(ObjectOutput out) throws IOException {
			out.writeUTF(type);
			out.writeBoolean(event != null);
			if (event != null) {
				out.writeObject(event);
			}
		}
		
	}
	
}
