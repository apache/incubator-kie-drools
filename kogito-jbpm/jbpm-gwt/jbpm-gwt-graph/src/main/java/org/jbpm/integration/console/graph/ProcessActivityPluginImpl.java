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
package org.jbpm.integration.console.graph;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.bpm.console.server.plugin.ProcessActivityPlugin;
import org.jbpm.integration.console.shared.GuvnorConnectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessActivityPluginImpl implements ProcessActivityPlugin {
	private static final Logger logger = LoggerFactory.getLogger(ProcessActivityPluginImpl.class);
	private static final int BUFFER_SIZE = 512;
	
	public byte[] getProcessImage(String definitionId) {
		GuvnorConnectionUtils guvnorUtils = new GuvnorConnectionUtils();
		if(guvnorUtils.guvnorExists()) {
			try {
				return guvnorUtils.getProcessImageFromGuvnor(definitionId);
			} catch (Throwable t) {
				logger.error("Could not get process image from Guvnor: " + t.getMessage());
			}
		} else {
			logger.warn("Could not connect to Guvnor.");
		}
		
		InputStream is = ProcessActivityPluginImpl.class.getResourceAsStream("/" + definitionId + ".png");
		if (is != null) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try {
				transfer(is, os);
			} catch (IOException e) {
				throw new RuntimeException("Could not read process image: " + e.getMessage());
			}
			return os.toByteArray();
		}
		return null;
	}

    public byte[] getProcessInstanceImage(String definition, String instanceId) {
    	return getProcessImage(definition);
    }
    
    public static int transfer(InputStream in, OutputStream out) throws IOException {
		int total = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = in.read(buffer);
		while (bytesRead != -1) {
			out.write(buffer, 0, bytesRead);
			total += bytesRead;
			bytesRead = in.read(buffer);
		}
		return total;
	}
}
