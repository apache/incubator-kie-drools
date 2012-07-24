/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.task.utils;

import java.io.Serializable;
import java.util.Arrays;


public class MarshalledContentWrapper implements Serializable{
    private byte[] content;
    private String marshaller;
    private Class type;

    public MarshalledContentWrapper(byte[] content, String marshaller, Class type) {
        this.content = content;
        this.marshaller = marshaller;
        this.type = type;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getMarshaller() {
        return marshaller;
    }

    public void setMarshaller(String marshaller) {
        this.marshaller = marshaller;
    }

    @Override
    public String toString() {
        return "MarshalledContentWrapper{" + "content=" + content + ", marshaller=" + marshaller + ", type=" + type + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MarshalledContentWrapper other = (MarshalledContentWrapper) obj;
        if (!Arrays.equals(this.content, other.content)) {
            return false;
        }
        if ((this.marshaller == null) ? (other.marshaller != null) : !this.marshaller.equals(other.marshaller)) {
            return false;
        }
        if (this.type != other.type && (this.type == null || !this.type.equals(other.type))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Arrays.hashCode(this.content);
        hash = 53 * hash + (this.marshaller != null ? this.marshaller.hashCode() : 0);
        hash = 53 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }
    
    
}
