package org.drools.persistence.jta;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

public class TransactionTestObjectBridge implements FieldBridge {

	@Override
	public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {
		if (value != null) {
			try {
				TransactionTestObject obj = (TransactionTestObject) value;
				Long objId = obj.getId();
				String objName = obj.getName();
				
				ByteArrayOutputStream baout = new ByteArrayOutputStream();
				ObjectOutputStream oout = new ObjectOutputStream(baout);
				oout.writeObject(objId);
				oout.writeUTF(objName);
				Field field = new Field(name, baout.toByteArray());
				field.setBoost(luceneOptions.getBoost());
				
				document.add(field);
			} catch (Exception e) {
				throw new RuntimeException("problem bridging SessionInfo", e);
			}
		}

	}

}
