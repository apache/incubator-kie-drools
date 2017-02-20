package org.drools.persistence.mapdb;

import java.io.IOException;
import java.util.Base64;

import org.drools.persistence.PersistentWorkItem;
import org.drools.persistence.processinstance.mapdb.MapDBWorkItem;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.serializer.GroupSerializerObjectArray;

public class PersistentWorkItemSerializer  extends GroupSerializerObjectArray<PersistentWorkItem>  {

	public PersistentWorkItemSerializer() {
	}

	@Override
	public int compare(PersistentWorkItem wi1, PersistentWorkItem wi2) {
		return wi1.getId().compareTo(wi2.getId());
	}

	@Override
	public PersistentWorkItem deserialize(DataInput2 input, int available) throws IOException {
		MapDBWorkItem workItem = new MapDBWorkItem();
		long id = input.readLong();
		long processInstanceId = input.readLong();
		String encodedData = input.readUTF();
		workItem.setProcessInstanceId(processInstanceId);
		workItem.setData(Base64.getDecoder().decode(encodedData));
		int state = input.readInt();
		workItem.setState(state);;
		if (id > -1) {
			workItem.setId(id);
		}
		return workItem;
	}

	@Override
	public void serialize(DataOutput2 output, PersistentWorkItem workItem) throws IOException {
		MapDBWorkItem dbWorkItem = (MapDBWorkItem) workItem;
		output.writeLong(workItem.getId() == null ? -1 : workItem.getId());
		output.writeLong(workItem.getProcessInstanceId());
		byte[] data = dbWorkItem.getData() == null ? new byte[0] : dbWorkItem.getData();
		String base64data = new String(Base64.getEncoder().encode(data));
		output.writeUTF(base64data);
		output.writeInt(dbWorkItem.getState());
	}
}
