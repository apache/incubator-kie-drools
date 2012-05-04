/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.task.service.hornetq;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.jbpm.task.service.SessionWriter;

public class HornetQSessionWriter implements SessionWriter {
	
	private final ClientSession session;
	private final ClientProducer producer;

	public HornetQSessionWriter(ClientSession session, ClientProducer producer) {
		this.session = session;
		this.producer = producer;
	}

	public void write(Object message) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oout;
		try {
			oout = new ObjectOutputStream(baos);
			oout.writeObject(message);
			ClientMessage clientMessage = session.createMessage(true);
			clientMessage.getBodyBuffer().writeBytes(baos.toByteArray());
			producer.send(clientMessage);
		} catch (IOException e) {
			throw new IOException("Error creating message");
		} catch (HornetQException e) {
			throw new IOException("Unable to create message");
		}
	}

}
