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

package org.jbpm.casemgmt.impl.marshalling;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents individual content that is part of CaseFileInstance that is persisted 
 * by independent marshaller.
 *
 */
public class SerializedContent implements Serializable {

    private static final long serialVersionUID = 8407186976255442673L;
    
    private String marshaller;
    private String name;
    private byte[] content;
    
    public SerializedContent() {
        
    }
    
    public SerializedContent(String marshaller, String name, byte[] content) {
        super();
        this.marshaller = marshaller;
        this.name = name;
        this.content = content;
    }

    public String getMarshaller() {
        return marshaller;
    }
    
    public void setMarshaller(String marshaller) {
        this.marshaller = marshaller;
    }
    
    public byte[] getContent() {
        return content;
    }
    
    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SerializedContent [marshaller=" + marshaller + ", name=" + name + ", content=" + (content == null ? null : content.length) + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(content);
        result = prime * result + ((marshaller == null) ? 0 : marshaller.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        SerializedContent other = (SerializedContent) obj;
        if (!Arrays.equals(content, other.content))
            return false;
        if (marshaller == null) {
            if (other.marshaller != null)
                return false;
        } else if (!marshaller.equals(other.marshaller))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
