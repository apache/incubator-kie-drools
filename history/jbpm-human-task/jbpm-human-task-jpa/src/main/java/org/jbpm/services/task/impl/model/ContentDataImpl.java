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

package org.jbpm.services.task.impl.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

import org.kie.internal.task.api.model.AccessType;

public class ContentDataImpl implements org.kie.internal.task.api.model.ContentData {
	
	private AccessType accessType;
	private String type;
	private byte[] content;

	public AccessType getAccessType() {
		return accessType;
	}

	public void setAccessType(AccessType accessType) {
		this.accessType = accessType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
	
	public Object getContentObject() {
		return null;
	}
	
	public void setContentObject(Object object) {
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		if ( accessType != null ) {
            out.writeBoolean( true );
            out.writeUTF( accessType.toString() );
		} else {
            out.writeBoolean( false );
        }
		if ( type != null ) {
            out.writeBoolean( true );
            out.writeUTF( type );
		} else {
            out.writeBoolean( false );
        }
		if ( content != null ) {
            out.writeBoolean( true );
            out.writeInt( content.length );
            out.write( content );
		} else {
            out.writeBoolean( false );
        }
    }
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
    	if (in.readBoolean()) {
    		accessType = AccessType.valueOf(in.readUTF());
    	}
    	if (in.readBoolean()) {
    		type = in.readUTF();
    	}
    	if (in.readBoolean()) {
    		content = new byte[ in.readInt() ];
    		in.readFully( content );
    	}
    }

}
