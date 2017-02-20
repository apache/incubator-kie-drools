package org.drools.persistence.mapdb;

import java.io.IOException;
import java.util.Base64;

import org.drools.persistence.PersistentSession;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.serializer.GroupSerializerObjectArray;

public class PersistentSessionSerializer extends GroupSerializerObjectArray<PersistentSession> {

	public PersistentSessionSerializer() {
	}

	@Override
	public int compare(PersistentSession s1, PersistentSession s2) {
		return s1.getId().compareTo(s2.getId());
	}

	@Override
	public PersistentSession deserialize(DataInput2 input, int available) throws IOException {
		MapDBSession session = new MapDBSession();
		long id = input.readLong();
		String encodedData = input.readUTF();
		byte[] data = Base64.getDecoder().decode(encodedData);
		session.setData(data);
		if (id > -1) {
			session.setId(id);
		}
		return session;
	}

	@Override
	public void serialize(DataOutput2 output, PersistentSession session) throws IOException {
		Long id = session.getId();
		output.writeLong(id == null ? -1L : id);
		byte[] data = session.getData();
		if (data == null) {
			data = new byte[0];
		}
		String base64data = new String(Base64.getEncoder().encode(data));
		output.writeUTF(base64data);
	}

}
