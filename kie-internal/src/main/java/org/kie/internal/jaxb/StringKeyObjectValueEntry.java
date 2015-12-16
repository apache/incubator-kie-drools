/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.internal.jaxb;

import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class StringKeyObjectValueEntry {

    @XmlAttribute
    private String key;

    @XmlAttribute(name="class-name")
    private String className;

    @XmlValue
    @XmlSchemaType(name = "base64Binary")
    private byte[] value;

    public StringKeyObjectValueEntry() {
        // default
    }
    
    public StringKeyObjectValueEntry(Entry<String, Object> entry) { 
       this.key = entry.getKey();
       Object object = entry.getValue();
       if( object != null ) { 
           this.value = StringKeyObjectValueMapXmlAdapter.serializeObject(object, key);
           this.className = object.getClass().getName();
       }
    }

    public StringKeyObjectValueEntry(String key, String className, byte [] bytes) { 
        this.key = key;
        this.className = className;
        this.value = bytes;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public byte[] getBytes() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

}
