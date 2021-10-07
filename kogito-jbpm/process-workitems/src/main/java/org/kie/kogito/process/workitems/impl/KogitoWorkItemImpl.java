/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.process.workitems.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.process.workitems.InternalKogitoWorkItem;

public class KogitoWorkItemImpl implements InternalKogitoWorkItem, Serializable {

    private static final long serialVersionUID = 510l;

    private String id;
    private String name;
    private int state = 0;
    private Map<String, Object> parameters = new ProxyMap(new HashMap<>());
    private Map<String, Object> results = new HashMap<>();
    private String processInstanceId;
    private String deploymentId;
    private String nodeInstanceId;
    private long nodeId;

    private String phaseId;
    private String phaseStatus;

    private Date startDate;
    private Date completeDate;

    private transient KogitoProcessInstance processInstance;
    private transient KogitoNodeInstance nodeInstance;

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public long getId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getStringId() {
        return id;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setState(int state) {
        this.state = state;
    }

    @Override
    public void setProcessInstanceId(long processInstanceId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = new ProxyMap(parameters);
    }

    @Override
    public void setParameter(String name, Object value) {
        this.parameters.put(name, value);
    }

    @Override
    public Object getParameter(String name) {
        return this.parameters.get(name);
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }

    @Override
    public void setResults(Map<String, Object> results) {
        if (results != null) {
            this.results = results;
        }
    }

    public void setResult(String name, Object value) {
        results.put(name, value);
    }

    @Override
    public Object getResult(String name) {
        return results.get(name);
    }

    @Override
    public Map<String, Object> getResults() {
        return results;
    }

    @Override
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    @Override
    public long getProcessInstanceId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProcessInstanceStringId() {
        return processInstanceId;
    }

    @Override
    public String getDeploymentId() {
        return deploymentId;
    }

    @Override
    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    @Override
    public void setNodeInstanceId(long deploymentId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getNodeInstanceId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getNodeInstanceStringId() {
        return nodeInstanceId;
    }

    @Override
    public void setNodeInstanceId(String nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
    }

    @Override
    public long getNodeId() {
        return nodeId;
    }

    @Override
    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public String getPhaseId() {
        return this.phaseId;
    }

    @Override
    public String getPhaseStatus() {
        return this.phaseStatus;
    }

    @Override
    public void setPhaseId(String phaseId) {
        this.phaseId = phaseId;
    }

    @Override
    public void setPhaseStatus(String phaseStatus) {
        this.phaseStatus = phaseStatus;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getCompleteDate() {
        return completeDate;
    }

    @Override
    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("WorkItem ");
        b.append(id);
        b.append(" [name=");
        b.append(name);
        b.append(", state=");
        b.append(state);
        b.append(", processInstanceId=");
        b.append(processInstanceId);
        b.append(", parameters{");
        for (Iterator<Map.Entry<String, Object>> iterator = parameters.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, Object> entry = iterator.next();
            b.append(entry.getKey());
            b.append("=");
            b.append(entry.getValue());
            if (iterator.hasNext()) {
                b.append(", ");
            }
        }
        b.append("}]");
        return b.toString();
    }

    @Override
    public KogitoNodeInstance getNodeInstance() {
        return this.nodeInstance;
    }

    @Override
    public KogitoProcessInstance getProcessInstance() {
        return this.processInstance;
    }

    @Override
    public void setNodeInstance(KogitoNodeInstance nodeInstance) {
        this.nodeInstance = nodeInstance;
    }

    @Override
    public void setProcessInstance(KogitoProcessInstance processInstance) {
        this.processInstance = processInstance;
    }

    private class ProxyMap implements Map<String, Object> {

        private Map<String, Object> map;

        public ProxyMap(Map<String, Object> map) {
            this.map = map;
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean isEmpty() {
            return map.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return map.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return map.containsValue(value);
        }

        @Override
        public Object get(Object key) {
            return processValue(map.get(key));
        }

        @Override
        public Object put(String key, Object value) {
            return map.put(key, value);
        }

        @Override
        public Object remove(Object key) {
            return map.remove(key);
        }

        @Override
        public void putAll(Map<? extends String, ? extends Object> m) {
            map.putAll(m);

        }

        @Override
        public void clear() {
            map.clear();

        }

        @Override
        public Set<String> keySet() {
            return map.keySet();
        }

        @Override
        public Collection<Object> values() {
            return new ProxyCollection(map.values());
        }

        @Override
        public Set<Entry<String, Object>> entrySet() {
            return new ProxyEntrySet(map.entrySet());
        }
    }

    private abstract class AbstractProxyCollection<T> {

        protected Collection<T> values;

        protected AbstractProxyCollection(Collection<T> values) {
            this.values = values;
        }

        public int size() {
            return values.size();
        }

        public boolean isEmpty() {
            return values.isEmpty();
        }

        public boolean contains(Object o) {
            return values.contains(o);
        }

        public boolean remove(Object o) {
            return values.remove(o);
        }

        public boolean containsAll(Collection<?> c) {
            return values.containsAll(c);
        }

        public boolean retainAll(Collection<?> c) {
            return values.retainAll(c);
        }

        public boolean removeAll(Collection<?> c) {
            return values.removeAll(c);
        }

        public void clear() {
            values.clear();
        }

        public boolean addAll(Collection<? extends T> c) {
            return values.addAll(c);
        }

        public boolean add(T e) {
            return values.add(e);
        }

    }

    private class ProxyEntrySet extends AbstractProxyCollection<Entry<String, Object>> implements
            Set<Entry<String, Object>> {

        public ProxyEntrySet(Set<Entry<String, Object>> entrySet) {
            super(entrySet);
        }

        @Override
        public Iterator<Entry<String, Object>> iterator() {
            return new ProxyEntryIterator(values.iterator());
        }

        @Override
        public Object[] toArray() {
            return processEntries(values.toArray());
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return processEntries(values.toArray(a));
        }

        private <T> T[] processEntries(T[] array) {
            for (int i = 0; i < array.length; i++) {
                array[i] = (T) new ProxyEntry((Entry<String, Object>) array[i]);
            }
            return array;
        }
    }

    private class ProxyCollection extends AbstractProxyCollection<Object> implements Collection<Object> {

        public ProxyCollection(Collection<Object> values) {
            super(values);
        }

        @Override
        public Iterator<Object> iterator() {
            return new ProxyIterator(values.iterator());
        }

        @Override
        public Object[] toArray() {
            return processArray(values.toArray());
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return processArray(values.toArray(a));
        }

        private <S> S[] processArray(S[] array) {
            for (int i = 0; i < array.length; i++) {
                array[i] = (S) processValue(array[i]);
            }
            return array;
        }
    }

    private class ProxyEntry implements Entry<String, Object> {

        private Entry<String, Object> entry;

        private ProxyEntry(Entry<String, Object> entry) {
            this.entry = entry;
        }

        @Override
        public String getKey() {
            return entry.getKey();
        }

        @Override
        public Object getValue() {
            return processValue(entry.getValue());
        }

        @Override
        public Object setValue(Object value) {
            return entry.setValue(value);
        }

    }

    private class ProxyIterator implements Iterator<Object> {

        private Iterator<Object> iter;

        public ProxyIterator(Iterator<Object> iter) {
            this.iter = iter;
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public Object next() {
            return processValue(iter.next());
        }

        @Override
        public void remove() {
            iter.remove();
        }
    }

    private class ProxyEntryIterator implements Iterator<Entry<String, Object>> {

        private Iterator<Entry<String, Object>> iter;

        public ProxyEntryIterator(Iterator<Entry<String, Object>> iter) {
            this.iter = iter;
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public Entry<String, Object> next() {
            return new ProxyEntry(iter.next());
        }

        @Override
        public void remove() {
            iter.remove();
        }
    }

    private <T> Object processValue(T obj) {
        return obj instanceof WorkItemParamResolver ? ((WorkItemParamResolver) obj).apply(this) : obj;
    }

}
