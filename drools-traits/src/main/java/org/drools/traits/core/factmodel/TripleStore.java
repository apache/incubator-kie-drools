/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.traits.core.factmodel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import org.drools.core.util.Entry;
import org.kie.api.runtime.rule.Variable;

public class TripleStore implements Externalizable {

    public static final String TYPE = "rdfs:type";
    public static final String PROXY = "drools:proxy";
    public static final String VALUE = "drools:hasValue";

    private String id;

    private Map<Triple, Triple> map;

    public TripleStore() {
        super();
        map = new Object2ObjectOpenCustomHashMap(TripleKeyComparator.getInstance());
    }

    public TripleStore(final int capacity,
                       final float loadFactor) {
        map = new Object2ObjectOpenCustomHashMap<Triple, Triple>(capacity, loadFactor, TripleKeyComparator.getInstance());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        map = (Map) in.readObject();
        id = (String) in.readObject();

    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(map);
        out.writeObject(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean put(final Triple triple, boolean checkExists) {
        return put(triple);
    }

    public boolean put(final Triple triple) {
        final Object val = triple.getValue();
        ((TripleImpl) triple).setValue(Variable.v);

        Object prev = map.compute(triple, (key, existing) -> {
            if (existing != null) {
                ((TripleImpl)existing).setValue(val);
                return existing;
            }
            return key;
        });

        ((TripleImpl) triple).setValue(val);

        return prev != triple;
    }

    public boolean add(final Triple triple) {
        map.put(triple, triple);
        return false;
    }

    public Triple get(final Triple triple) {
        return map.get(triple);
    }

    public Collection<Triple> getAll(final Triple triple) {
        List<Triple> list = new ArrayList<>();

        if (triple.getInstance() != Variable.v && triple.getProperty() != Variable.v) {
            Triple collector = new TripleCollector(list, triple);
            map.get(collector);
            return list;
        }

        Iterator<Triple> iter = map.values().iterator();
        Triple tx;
        while (iter.hasNext()) {
            tx = iter.next();
            if (TripleKeyComparator.getInstance().equals(triple, tx)) {
                list.add(tx);
            }
        }

        return list;
    }

    public int removeAll(final Triple triple) {
        int removed = 0;
        Collection<Triple> coll = getAll(triple);
        for (Triple t : coll) {
            if (remove(t)) {
                removed++;
            }
        }
        return removed;
    }

    public boolean remove(final Triple triple) {
        return map.remove(triple) != null;
    }

    public boolean contains(final Triple triple) {
        return map.containsKey(triple);
    }

    public int size() {
        return map.size();
    }


    public static class TripleKeyComparator implements Strategy, Serializable {
        private static TripleKeyComparator INSTANCE = new TripleKeyComparator();

        public static TripleKeyComparator getInstance() {
            return INSTANCE;
        }

        @Override
        public int hashCode(Object object) {
            Triple t = (Triple) object;
            final int prime = 31;
            int result = 1;
            result = prime * result + t.getInstance().hashCode();
            result = prime * result + t.getProperty().hashCode();


            if (t instanceof TripleImpl) {
                ((TripleImpl)t).hash = result;
            }

            return result;
        }

        @Override
        public boolean equals(Object object1,
                              Object object2) {
            if (object1 == null || object2 == null ) {
                return object1 == object2;
            }

            Triple t1 = (Triple) object1;
            Triple t2 = (Triple) object2;

            if (t1.getInstance() != Variable.v) {
                if (t1.getInstance() == null) {
                    return false;
                } else if (t1.getInstance() instanceof String) {
                    if (!t1.getInstance().equals(t2.getInstance())) {
                        return false;
                    }
                } else if (t1.getInstance() != t2.getInstance()) {
                    return false;
                }
            }

            if (t1.getProperty() != Variable.v && !t1.getProperty().equals(t2.getProperty())) {
                return false;
            }
            if (t1.getValue() != Variable.v) {
                if (t1.getValue() == null) {
                    return t2.getValue() == null;
                } else {
                    return t1.getValue().equals(t2.getValue());
                }
            }

            if (t1.getClass() == TripleCollector.class) {
                ((TripleCollector)t1).list.add(t2);
                return false;
            }

            return true;
        }
    }

    public class TripleCollector implements Triple {
        List<Triple> list;

        private Triple triple;

        public TripleCollector(List<Triple> list, Triple triple) {
            this.list = list;
            this.triple = triple;
        }

        @Override
        public void setNext(Entry next) {

        }

        @Override
        public Entry getNext() {
            return null;
        }

        @Override
        public Object getInstance() {
            return triple.getInstance();
        }

        @Override
        public Object getProperty() {
            return triple.getProperty();
        }

        @Override
        public Object getValue() {
            return Variable.v;
        }

    }
}
