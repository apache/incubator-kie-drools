/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.instance.event;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.ProtobufMessages.ActionQueue.Action;
import org.drools.core.phreak.PropagationEntry;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.EventListener;
import org.kie.api.runtime.process.ProcessInstance;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultSignalManager implements SignalManager {
	
	private Map<String, List<EventListener>> processEventListeners = new ConcurrentHashMap<String, List<EventListener>>();
	private InternalKnowledgeRuntime kruntime;
	
	public DefaultSignalManager(InternalKnowledgeRuntime kruntime) {
		this.kruntime = kruntime;
	}
	
	public InternalKnowledgeRuntime getKnowledgeRuntime() {
		return kruntime;
	}

	public void addEventListener(String type, EventListener eventListener) {
		List<EventListener> eventListeners = processEventListeners.get(type);
		//this first "if" is not pretty, but allows to synchronize only when needed
		if (eventListeners == null) {
			synchronized(processEventListeners){
				eventListeners = processEventListeners.get(type);
				if(eventListeners==null){
					eventListeners = new CopyOnWriteArrayList<EventListener>();
					processEventListeners.put(type, eventListeners);
				}
			}
		}		
		eventListeners.add(eventListener);
	}
	
	public void removeEventListener(String type, EventListener eventListener) {
		if (processEventListeners != null) {
			List<EventListener> eventListeners = processEventListeners.get(type);
			if (eventListeners != null) {
				eventListeners.remove(eventListener);
				if (eventListeners.isEmpty()) {
					processEventListeners.remove(type);
					eventListeners = null;
				}
			}
		}
	}
	
	public void signalEvent(String type, Object event) {
	    ((DefaultSignalManager) ((InternalProcessRuntime) kruntime.getProcessRuntime()).getSignalManager()).internalSignalEvent(type, event);
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
		ProcessInstance processInstance = kruntime.getProcessInstance(processInstanceId);
		if (processInstance != null) {
		    processInstance.signalEvent(type, event);
		}
	}
	
	public static class SignalProcessInstanceAction extends PropagationEntry.AbstractPropagationEntry implements WorkingMemoryAction {

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

		public void execute(InternalKnowledgeRuntime kruntime) {
			ProcessInstance processInstance = kruntime.getProcessInstance(processInstanceId);
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

        public Action serialize(MarshallerWriteContext context) throws IOException {
            // TODO Auto-generated method stub
            return null;
        }
	}
	
	public static class SignalAction extends PropagationEntry.AbstractPropagationEntry implements WorkingMemoryAction {

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
			((DefaultSignalManager) ((InternalProcessRuntime) workingMemory.getProcessRuntime()).getSignalManager()).internalSignalEvent(type, event);
		}

        public void execute(InternalKnowledgeRuntime kruntime) {
        	((DefaultSignalManager) ((InternalProcessRuntime) kruntime.getProcessRuntime()).getSignalManager()).internalSignalEvent(type, event);
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

        public Action serialize(MarshallerWriteContext context) throws IOException {
            // TODO Auto-generated method stub
            return null;
        }
		
	}	
}
