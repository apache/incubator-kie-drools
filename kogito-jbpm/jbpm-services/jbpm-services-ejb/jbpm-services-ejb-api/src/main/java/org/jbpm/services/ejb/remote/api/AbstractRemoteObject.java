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

package org.jbpm.services.ejb.remote.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.io.input.ClassLoaderObjectInputStream;

public abstract class AbstractRemoteObject {
	
	private transient ClassLoader classLoader = this.getClass().getClassLoader(); 

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	protected Object deserialize(byte[] bytes) {
		Object result = null;
		ObjectInputStream in = null;
        try {
            in = new ClassLoaderObjectInputStream(classLoader, new ByteArrayInputStream(bytes));
            result = in.readObject();
        } catch (Exception e) {                        

        	throw new RuntimeException("Unable to deserialize stream ", e);
        } finally {
            if (in != null) {
                try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
        
        return result;
	}
	
	protected byte[] serialize(Object input) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
	        ObjectOutputStream oout = new ObjectOutputStream(bout);
	        oout.writeObject(input);
	        
	        return bout.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException("Unable to serialize object " + input, e);
		}
	}
}
