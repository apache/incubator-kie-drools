package org.drools.persistence.processinstance.mapdb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.ProtobufInputMarshaller;
import org.drools.core.marshalling.impl.ProtobufOutputMarshaller;
import org.drools.core.process.instance.WorkItem;
import org.drools.persistence.PersistentWorkItem;
import org.drools.persistence.mapdb.MapDBTransformable;
import org.drools.persistence.mapdb.PersistentWorkItemSerializer;
import org.kie.api.persistence.ObjectStoringStrategy;
import org.kie.api.runtime.Environment;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.Serializer;

public class MapDBWorkItem implements PersistentWorkItem, MapDBTransformable {

	private Long id;
	private int state;
	private byte[] data;
	private WorkItem workItem;
	private Environment env;
	private long processInstanceId;
	
	public MapDBWorkItem() {
	}
	
	public MapDBWorkItem(WorkItem workItem, Environment env) {
		this.workItem = workItem;
		this.env = env;
	}

	@Override
	public void setEnvironment(Environment env) {
		this.env = env;
	}

	@Override
	public void transform() {
		this.state = workItem.getState();
		this.processInstanceId = workItem.getProcessInstanceId();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            MarshallerWriteContext context = new MarshallerWriteContext( baos,
            		null, null, null, null, this.env);
            ProtobufOutputMarshaller.writeWorkItem(context, workItem);
            context.close();
            this.data = baos.toByteArray();
        } catch ( IOException e ) {
            throw new IllegalArgumentException( "IOException while storing workItem " + workItem.getId() + ": " + e.getMessage() );
        }
	}

	public void setState(int state) {
		this.state = state;
	}
	
	public int getState() {
		return state;
	}
	
	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public WorkItem getWorkItem(Environment env, InternalKnowledgeBase kieBase) {
		ByteArrayInputStream bais = new ByteArrayInputStream( this.data );
		try {
			MarshallerReaderContext context = new MarshallerReaderContext( bais,
					kieBase, null, null, null, env);
			this.workItem = ProtobufInputMarshaller.readWorkItem(context);
			return this.workItem;
		} catch (IOException e) {
            throw new RuntimeException("Unable to read work item ", e);
		}
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	public byte[] getData() {
		return data;
	}

	@Override
	public String getMapKey() {
		return "workItem";
	}
	
	public long getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	@Override
	public boolean updateOnMap(DB db, ObjectStoringStrategy[] strategies) {
		BTreeMap<Long, PersistentWorkItem> map = db.treeMap(getMapKey(), Serializer.LONG, new PersistentWorkItemSerializer()).open();
		map.put(id, this);
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
		result = prime * result + ((env == null) ? 0 : env.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ (int) (processInstanceId ^ (processInstanceId >>> 32));
		result = prime * result + state;
		result = prime * result
				+ ((workItem == null) ? 0 : workItem.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapDBWorkItem other = (MapDBWorkItem) obj;
		if (!Arrays.equals(data, other.data))
			return false;
		if (env == null) {
			if (other.env != null)
				return false;
		} else if (!env.equals(other.env))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (processInstanceId != other.processInstanceId)
			return false;
		if (state != other.state)
			return false;
		if (workItem == null) {
			if (other.workItem != null)
				return false;
		} else if (!workItem.equals(other.workItem))
			return false;
		return true;
	}
}
