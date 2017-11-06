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

package org.jbpm.document.marshalling;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.drools.core.common.DroolsObjectInputStream;
import org.jbpm.document.Document;
import org.jbpm.document.Documents;
import org.kie.api.marshalling.ObjectMarshallingStrategy;

/**
 * {@link ObjectMarshallingStrategy} for a collection (List) of {@link Document Documents}.
 */
public class DocumentsMarshallingStrategy implements ObjectMarshallingStrategy {

	/**
	 * Marshalling strategy that marshals the individual documents of our collection.
	 */
	private DocumentMarshallingStrategy docMarshallingStrategy;

	public DocumentsMarshallingStrategy(DocumentMarshallingStrategy docMarshallingStrategy) {
		this.docMarshallingStrategy = docMarshallingStrategy;
	}

	public boolean accept(Object o) {
		return o instanceof Documents;
	}

	public byte[] marshal(Context ctx, ObjectOutputStream objectOutputStream, Object o) throws IOException {
		ByteArrayOutputStream buff = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(buff)) {
			Documents documents = (Documents) o;
			// Write the number of documents in the list.
			oos.writeInt(documents.getDocuments().size());
			for (Document nextDocument : documents.getDocuments()) {
				// Use the DocumentMarshallingStrategy to marshal individual documents.
				byte[] nextMarshalledDocument = docMarshallingStrategy.marshal(ctx, objectOutputStream, nextDocument);
				oos.writeInt(nextMarshalledDocument.length);
				oos.write(nextMarshalledDocument);
				// Need to call reset on the stream in order for the Document bytes to be written correctly.
				oos.reset();
			}
		}

		return buff.toByteArray();
	}

	public Object unmarshal(Context ctx, ObjectInputStream objectInputStream, byte[] object, ClassLoader classLoader)
			throws IOException, ClassNotFoundException {

		Documents documents = new Documents();

		try (DroolsObjectInputStream is = new DroolsObjectInputStream(new ByteArrayInputStream(object), classLoader)) {
			// first we read the size of the list we've stored.
			int size = is.readInt();

			for (int i = 0; i < size; i++) {
				// Use the DocumentMarshallingStrategy to unmarshal the individual documents.
				int length = is.readInt();
				byte[] marshalledDocument = new byte[length];
				is.readFully(marshalledDocument);
				Document nextDocument = (Document) docMarshallingStrategy.unmarshal(ctx, objectInputStream, marshalledDocument,
						classLoader);
				documents.addDocument(nextDocument);
			}
		}

		return documents;
	}

	public Object read(ObjectInputStream arg0) throws IOException, ClassNotFoundException {
		// Read and write are only used in previous versions of jBPM before the platform used protobuf storage for sessions.
		throw new UnsupportedOperationException("This marshalling strategy supports jBPM 6.5 and higher.");
	}

	public void write(ObjectOutputStream arg0, Object arg1) throws IOException {
		// Read and write are only used in previous versions of jBPM before the platform used protobuf storage for sessions.
		throw new UnsupportedOperationException("This marshalling strategy supports jBPM 6.5 and higher.");
	}

	public Context createContext() {
		return null;
	}

}
