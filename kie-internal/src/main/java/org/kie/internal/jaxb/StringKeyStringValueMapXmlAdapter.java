/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.internal.jaxb;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This is a {@link XmlAdapter} for mapping Map&lt;String, String&gt; instances
 * to classes/instances that <b>both</b> JAXB/XML and JSON can deal with.
 * <p>
 * The most important reason for the existence of this class is that it works well
 * with jackson JSON! JaxbMapAdapter, on the other hand, does <b>not</b>!
 */
public class StringKeyStringValueMapXmlAdapter extends XmlAdapter<StringKeyStringValueMap, Map<String, String>> {

    @Override
    public StringKeyStringValueMap marshal(Map<String, String> map) throws Exception {
        if( map == null ) {
            return null;
        }
        StringKeyStringValueMap xmlMap = new StringKeyStringValueMap();
        for(Entry<String, String> entry : map.entrySet()) {
            String value = entry.getValue();
            String key = entry.getKey();
            StringKeyStringValueEntry xmlEntry = new StringKeyStringValueEntry(key, value);
            xmlMap.addEntry(xmlEntry);
        }
        return xmlMap;
    }


    @Override
    public Map<String, String> unmarshal(StringKeyStringValueMap xmlMap) {
        if( xmlMap == null ) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        for( StringKeyStringValueEntry xmlEntry : xmlMap.entries ) {
            String key = xmlEntry.getKey();
            String value = xmlEntry.getValue();
            map.put(key, value);
        }
        return map;
    }
}
