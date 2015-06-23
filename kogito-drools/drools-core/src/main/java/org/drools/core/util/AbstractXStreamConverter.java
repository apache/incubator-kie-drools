/*
 * Copyright 2015 JBoss Inc
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

package org.drools.core.util;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.*;

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
        if (map != null && !map.isEmpty()) {
            writer.startNode(mapName);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                writer.startNode("property");
                writer.addAttribute("key", entry.getKey());
                writer.addAttribute("value", entry.getValue());
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
        List<String> list = new ArrayList<String>();
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
        List<T> list = new ArrayList<T>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            list.add((T) context.convertAnother(reader.getValue(), clazz));
            reader.moveUp();
        }
        return list;
    }

    protected Map<String, String> readPropertyMap(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Map<String, String> map = new HashMap<String, String>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            map.put(reader.getAttribute("key"), reader.getAttribute("value"));
            reader.moveUp();
        }
        return map;
    }

    public interface NodeReader {
        void onNode(HierarchicalStreamReader reader, String name, String value);
    }
}
