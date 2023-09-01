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
package org.drools.xml.support.converters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public abstract class AbstractXStreamConverter implements Converter {
    private final Class type;

    protected AbstractXStreamConverter(Class type) {
        this.type = type;
    }

    public boolean canConvert(Class clazz) {
        return type.isAssignableFrom(clazz);
    }

    protected void writeAttribute(HierarchicalStreamWriter writer, String name, String value) {
        if (value != null) {
            writer.addAttribute(name, value);
        }
    }

    protected void writeString(HierarchicalStreamWriter writer, String name, String value) {
        if (value != null) {
            writer.startNode(name);
            writer.setValue(value);
            writer.endNode();
        }
    }

    protected void writeObject(HierarchicalStreamWriter writer, MarshallingContext context, String name, Object value) {
        if (value != null) {
            writer.startNode(name);
            context.convertAnother(value);
            writer.endNode();
        }
    }

    protected void writeList(HierarchicalStreamWriter writer, String listName, String itemName, Iterable<String> list) {
        if (list != null) {
            java.util.Iterator<String> i = list.iterator();
            if (i.hasNext()) {
                writer.startNode(listName);
                while (i.hasNext()) {
                    writer.startNode(itemName);
                    writer.setValue(i.next());
                    writer.endNode();
                }
                writer.endNode();
            }

        }
    }

    protected void writeObjectList(HierarchicalStreamWriter writer, MarshallingContext context, String listName, String itemName, Iterable<?> list) {
        if (list != null) {
            java.util.Iterator<? extends Object> i = list.iterator();
            if (i.hasNext()) {
                writer.startNode(listName);
                while (i.hasNext()) {
                    writeObject(writer, context, itemName, i.next());
                }
                writer.endNode();
            }

        }
    }

    protected void writePropertyMap(HierarchicalStreamWriter writer, MarshallingContext context, String mapName, Map<String, String> map) {
        writeMap(writer, context, mapName, "property", "key", "value", map);
    }

    protected void writeMap(HierarchicalStreamWriter writer, MarshallingContext context, String mapName, String itemName, String keyName, String valueName, Map<String, String> map) {
        if (map != null && !map.isEmpty()) {
            writer.startNode(mapName);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                writer.startNode(itemName);
                writer.addAttribute(keyName, entry.getKey());
                writer.addAttribute(valueName, entry.getValue());
                writer.endNode();
            }
            writer.endNode();
        }
    }

    protected void readNodes(HierarchicalStreamReader reader, NodeReader nodeReader) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            nodeReader.onNode(reader, reader.getNodeName(), reader.getValue());
            reader.moveUp();
        }
    }

    protected List<String> readList(HierarchicalStreamReader reader) {
        List<String> list = new ArrayList<>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            list.add(reader.getValue());
            reader.moveUp();
        }
        return list;
    }

    protected <T> T readObject(HierarchicalStreamReader reader, UnmarshallingContext context, Class<? extends T> clazz) {
        return (T) context.convertAnother(reader.getValue(), clazz);
    }

    protected <T> List<T> readObjectList(HierarchicalStreamReader reader, UnmarshallingContext context, Class<? extends T> clazz) {
        List<T> list = new ArrayList<>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            list.add((T) context.convertAnother(reader.getValue(), clazz));
            reader.moveUp();
        }
        return list;
    }

    protected Map<String, String> readPropertyMap(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return readMap(reader, context, "key", "value");
    }

    protected Map<String, String> readMap(HierarchicalStreamReader reader, UnmarshallingContext context, String key, String value) {
        Map<String, String> map = new HashMap<>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            map.put(reader.getAttribute(key), reader.getAttribute(value));
            reader.moveUp();
        }
        return map;
    }

    public interface NodeReader {
        void onNode(HierarchicalStreamReader reader, String name, String value);
    }
}
