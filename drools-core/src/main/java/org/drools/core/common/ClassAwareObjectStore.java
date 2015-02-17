package org.drools.core.common;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.factmodel.traits.CoreWrapper;
import org.drools.core.util.HashTableIterator;
import org.drools.core.util.JavaIteratorAdapter;
import org.drools.core.util.ObjectHashMap;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.ObjectFilter;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public class ClassAwareObjectStore implements Externalizable, ObjectStore {

    private Lock lock;

    private Map<String, SingleClassStore> storesMap = new HashMap<String, SingleClassStore>();
    private List<ConcreteClassStore> concreteStores = new ArrayList<ConcreteClassStore>();

    private ObjectHashMap equalityMap;

    private boolean isEqualityBehaviour;

    private int size;

    public ClassAwareObjectStore() { }

    public ClassAwareObjectStore(RuleBaseConfiguration conf, Lock lock) {
        this.lock = lock;
        this.isEqualityBehaviour = RuleBaseConfiguration.AssertBehaviour.EQUALITY.equals(conf.getAssertBehaviour());
        if (isEqualityBehaviour) {
            this.equalityMap = new ObjectHashMap();
            this.equalityMap.setComparator( new EqualityAssertMapComparator() );
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(storesMap);
        out.writeObject(concreteStores);
        out.writeObject(equalityMap);
        out.writeInt(size);
        out.writeBoolean(isEqualityBehaviour);
        out.writeObject(lock);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        storesMap = (Map<String, SingleClassStore>) in.readObject();
        concreteStores = (List<ConcreteClassStore>) in.readObject();
        equalityMap = (ObjectHashMap) in.readObject();
        size = in.readInt();
        isEqualityBehaviour = in.readBoolean();
        lock = (Lock)in.readObject();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        storesMap.clear();
        concreteStores.clear();
        if (isEqualityBehaviour) {
            equalityMap.clear();
        }
        size = 0;
    }

    @Override
    public Object getObjectForHandle(InternalFactHandle handle) {
        try {
            this.lock.lock();
            InternalFactHandle internalHandle = isEqualityBehaviour ?
                                                (InternalFactHandle) equalityMap.get(handle) :
                                                (InternalFactHandle) getOrCreateConcreteClassStore(handle.getObject()).getAssertMap().get(handle);
            return internalHandle != null ? internalHandle.getObject() : null;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public InternalFactHandle reconnect(InternalFactHandle handle) {
        SingleClassStore store = getClassStore(handle.getObjectClassName());
        if (!store.isConcrete()) {
            return null;
        }

        return handle.isNegated() ?
               (InternalFactHandle) ((ConcreteClassStore) store).getNegMap().get(handle) :
               (InternalFactHandle) ((ConcreteClassStore) store).getIdentityMap().get(handle);
    }

    @Override
    public InternalFactHandle getHandleForObject(Object object) {
        if ( object == null ) {
            return null;
        }

        return isEqualityBehaviour ?
               (InternalFactHandle) equalityMap.get(object) :
               (InternalFactHandle) getOrCreateConcreteClassStore(object).getAssertMap().get(object);
    }

    @Override
    public InternalFactHandle getHandleForObjectIdentity(Object object) {
        return (InternalFactHandle) getOrCreateConcreteClassStore(object).getIdentityMap().get(object);
    }

    @Override
    public void updateHandle(InternalFactHandle handle, Object object) {
        removeHandle(handle);
        handle.setObject(object);
        addHandle(handle, object);
    }

    @Override
    public void addHandle(InternalFactHandle handle, Object object) {
        if ( getOrCreateConcreteClassStore(object).addHandle(handle, object) ) {
            size++;
        }
    }

    @Override
    public void removeHandle(InternalFactHandle handle) {
        if ( getOrCreateConcreteClassStore(handle.getObject()).removeHandle(handle) != null ) {
            size--;
        }
    }

    @Override
    public Iterator<Object> iterateObjects() {
        return new CompositeObjectIterator(concreteStores, true);
    }

    public Iterator<Object> iterateObjects(Class<?> clazz) {
        return getOrCreateClassStore(clazz).objectsIterator(true);
    }

    @Override
    public Iterator<Object> iterateObjects(ObjectFilter filter) {
        if (filter instanceof ClassObjectFilter) {
            return getOrCreateClassStore(((ClassObjectFilter) filter).getFilteredClass()).objectsIterator(true);
        }
        return new CompositeObjectIterator(concreteStores, true, filter);
    }

    @Override
    public Iterator<InternalFactHandle> iterateFactHandles() {
        return new CompositeFactHandleIterator(concreteStores, true);
    }

    public Iterator<InternalFactHandle> iterateFactHandles(Class<?> clazz) {
        return getOrCreateClassStore(clazz).factHandlesIterator(true);
    }

    @Override
    public Iterator<InternalFactHandle> iterateFactHandles(ObjectFilter filter) {
        if (filter instanceof ClassObjectFilter) {
            return getOrCreateClassStore(((ClassObjectFilter) filter).getFilteredClass()).factHandlesIterator(true);
        }
        return new CompositeFactHandleIterator(concreteStores, true, filter);
    }

    @Override
    public Iterator<Object> iterateNegObjects(ObjectFilter filter) {
        if (filter instanceof ClassObjectFilter) {
            return getOrCreateClassStore(((ClassObjectFilter) filter).getFilteredClass()).objectsIterator(false);
        }
        return new CompositeObjectIterator(concreteStores, false, filter);
    }

    @Override
    public Iterator<InternalFactHandle> iterateNegFactHandles(ObjectFilter filter) {
        if (filter instanceof ClassObjectFilter) {
            return getOrCreateClassStore(((ClassObjectFilter) filter).getFilteredClass()).factHandlesIterator(false);
        }
        return new CompositeFactHandleIterator(concreteStores, false, filter);
    }

    // /////////////////////
    // /// Internal Store
    // /////////////////////

    private SingleClassStore getClassStore(String className) {
        return storesMap.get(className);
    }

    public static Class<?> getActualClass(Object object) {
        return object instanceof CoreWrapper ? ((CoreWrapper)object).getCore().getClass() : object.getClass();
    }

    private SingleClassStore getOrCreateClassStore(Object object) {
        return getOrCreateConcreteClassStore(getActualClass(object));
    }

    public SingleClassStore getOrCreateClassStore(Class<?> clazz) {
        SingleClassStore store = storesMap.get(clazz.getName());
        if (store == null) {
            store = createClassStore(clazz);
            for (SingleClassStore classStore : storesMap.values()) {
                if (classStore.isConcrete() && clazz.isAssignableFrom(classStore.getStoredClass())) {
                    store.addConcreteStore(((ConcreteClassStore) classStore));
                }
            }
            storesMap.put(clazz.getName(), store);
        }
        return store;
    }

    private ConcreteClassStore getOrCreateConcreteClassStore(Object object) {
        return getOrCreateConcreteClassStore(getActualClass(object));
    }

    private ConcreteClassStore getOrCreateConcreteClassStore(Class<?> clazz) {
        SingleClassStore existingStore = storesMap.get(clazz.getName());
        if (existingStore == null) {
            // There was no store for this class
            ConcreteClassStore store = createClassStore(clazz).makeConcrete();
            for (SingleClassStore classStore : storesMap.values()) {
                if (classStore.getStoredClass().isAssignableFrom(clazz)) {
                    classStore.addConcreteStore(store);
                } else if (classStore.isConcrete() && clazz.isAssignableFrom(classStore.getStoredClass())) {
                    store.addConcreteStore(((ConcreteClassStore) classStore));
                }
            }
            storesMap.put(clazz.getName(), store);
            concreteStores.add(store);
            return store;
        } else if (existingStore.isConcrete()) {
            return (ConcreteClassStore) existingStore;
        } else {
            // The existing store was abstract so has to be converted in a concrete one
            ConcreteClassStore store = existingStore.makeConcrete();
            concreteStores.add(store);
            return store;
        }
    }

    private SingleClassStore createClassStore(Class<?> clazz) {
        return isEqualityBehaviour ? new ConcreteEqualityClassStore(clazz, equalityMap) : new ConcreteIdentityClassStore(clazz);
    }

    public interface SingleClassStore extends Externalizable {
        Class<?> getStoredClass();

        void addConcreteStore(ConcreteClassStore store);
        void addConcreteStores(List<ConcreteClassStore> concreteStores);
        List<ConcreteClassStore> getConcreteStores();

        Iterator<Object> objectsIterator(boolean assrt);
        Iterator<InternalFactHandle> factHandlesIterator(boolean assrt);

        boolean isConcrete();
        ConcreteClassStore makeConcrete();
    }

    private abstract static class AbstractClassStore implements SingleClassStore {
        private Class<?> storedClass;
        private List<ConcreteClassStore> concreteStores = new ArrayList<ConcreteClassStore>();

        public AbstractClassStore() { }

        private AbstractClassStore(Class<?> storedClass) {
            this.storedClass = storedClass;
        }

        public void addConcreteStore(ConcreteClassStore store) {
            concreteStores.add(store);
        }

        public void addConcreteStores(List<ConcreteClassStore> concreteStores) {
            this.concreteStores.addAll(concreteStores);
        }

        public List<ConcreteClassStore> getConcreteStores() {
            return concreteStores;
        }

        public Iterator<Object> objectsIterator(boolean assrt) {
            return new CompositeObjectIterator(concreteStores, assrt);
        }

        public Iterator<InternalFactHandle> factHandlesIterator(boolean assrt) {
            return new CompositeFactHandleIterator(concreteStores, assrt);
        }

        @Override
        public Class<?> getStoredClass() {
            return storedClass;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(storedClass);
            out.writeObject(concreteStores);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            storedClass = (Class<?>)in.readObject();
            concreteStores = (List<ConcreteClassStore>)in.readObject();
        }

        @Override
        public String toString() {
            return "Object store for class: " + storedClass;
        }
    }

    private interface ConcreteClassStore extends SingleClassStore {
        boolean addHandle(InternalFactHandle handle, Object object);
        InternalFactHandle removeHandle(InternalFactHandle handle);

        ObjectHashMap getAssertMap();
        ObjectHashMap getIdentityMap();
        ObjectHashMap getNegMap();
    }

    private static class ConcreteIdentityClassStore extends AbstractClassStore implements ConcreteClassStore {

        private ObjectHashMap identityMap;

        private ObjectHashMap negMap;

        public ConcreteIdentityClassStore() { }

        public ConcreteIdentityClassStore(Class<?> storedClass) {
            super(storedClass);
        }

        @Override
        public boolean addHandle(InternalFactHandle handle, Object object) {
            if ( handle.isNegated() ) {
                negMap.put(handle, handle, false);
                return false;
            }
            return identityMap.put(handle, handle, false) == null;
        }

        @Override
        public InternalFactHandle removeHandle(InternalFactHandle handle) {
            if ( handle.isNegated() ) {
                negMap.remove(handle);
                return null;
            }
            return (InternalFactHandle) identityMap.remove(handle);
        }

        @Override
        public ObjectHashMap getAssertMap() {
            return identityMap;
        }

        @Override
        public ObjectHashMap getNegMap() {
            return negMap;
        }

        @Override
        public ObjectHashMap getIdentityMap() {
            return identityMap;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal(out);
            out.writeObject(identityMap);
            out.writeObject(negMap);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            super.readExternal(in);
            identityMap = (ObjectHashMap)in.readObject();
            negMap = (ObjectHashMap)in.readObject();
        }

        @Override
        public boolean isConcrete() {
            return identityMap != null;
        }

        @Override
        public ConcreteClassStore makeConcrete() {
            negMap = new ObjectHashMap();
            identityMap = new ObjectHashMap();
            identityMap.setComparator( new IdentityAssertMapComparator() );
            addConcreteStore(this);
            return this;
        }
    }

    private static class ConcreteEqualityClassStore extends ConcreteIdentityClassStore {

        private ObjectHashMap equalityMap;

        public ConcreteEqualityClassStore() { }

        public ConcreteEqualityClassStore(Class<?> storedClass, ObjectHashMap equalityMap) {
            super(storedClass);
            this.equalityMap = equalityMap;
        }

        @Override
        public boolean addHandle(InternalFactHandle handle, Object object) {
            boolean isNew = super.addHandle(handle, object);
            equalityMap.put(handle, handle, false);
            return isNew;
        }

        @Override
        public InternalFactHandle removeHandle(InternalFactHandle handle) {
            InternalFactHandle removedHandle = super.removeHandle(handle);
            equalityMap.remove(handle);
            return removedHandle;
        }

        @Override
        public ObjectHashMap getAssertMap() {
            return equalityMap;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal(out);
            out.writeObject(equalityMap);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            super.readExternal(in);
            equalityMap = (ObjectHashMap)in.readObject();
        }
    }

    private static abstract class AbstractCompositeIterator<T> implements Iterator<T> {
        protected final Iterator<ConcreteClassStore> stores;
        protected final boolean assrt;
        protected final ObjectFilter filter;

        protected Iterator<T> currentIterator;
        protected T currentNext;

        private AbstractCompositeIterator(Iterable<ConcreteClassStore> stores, boolean assrt) {
            this(stores, assrt, null);
        }

        private AbstractCompositeIterator(Iterable<ConcreteClassStore> stores, boolean assrt, ObjectFilter filter) {
            this.stores = stores.iterator();
            this.assrt = assrt;
            this.filter = filter;
            fetchNext();
        }

        private void fetchNext() {
            while (currentNext == null) {
                nextIterator();
                if (currentIterator == null) {
                    break;
                }
                currentNext = currentIterator.next();
                if (filter != null && !accept()) {
                    currentNext = null;
                }
            }
        }

        private void nextIterator() {
            while (currentIterator == null || !currentIterator.hasNext()) {
                if (stores.hasNext()) {
                    fetchNextIterator();
                } else {
                    currentIterator = null;
                    break;
                }
            }
        }

        protected abstract void fetchNextIterator();

        protected abstract boolean accept();

        @Override
        public boolean hasNext() {
            return currentNext != null;
        }

        @Override
        public T next() {
            T next = currentNext;
            currentNext = null;
            fetchNext();
            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class CompositeObjectIterator extends AbstractCompositeIterator<Object> {
        private CompositeObjectIterator(Iterable<ConcreteClassStore> stores, boolean assrt) {
            super(stores, assrt);
        }

        private CompositeObjectIterator(Iterable<ConcreteClassStore> stores, boolean assrt, ObjectFilter filter) {
            super(stores, assrt, filter);
        }

        @Override
        protected void fetchNextIterator() {
            HashTableIterator iterator = assrt ?
                                         new HashTableIterator( stores.next().getIdentityMap() ) :
                                         new HashTableIterator( stores.next().getNegMap() );
            iterator.reset();
            currentIterator = new JavaIteratorAdapter<Object>( iterator, JavaIteratorAdapter.OBJECT );
        }

        @Override
        protected boolean accept() {
            return filter.accept(currentNext);
        }
    }

    private static class CompositeFactHandleIterator extends AbstractCompositeIterator<InternalFactHandle> {
        private CompositeFactHandleIterator(Iterable<ConcreteClassStore> stores, boolean assrt) {
            super(stores, assrt);
        }

        private CompositeFactHandleIterator(Iterable<ConcreteClassStore> stores, boolean assrt, ObjectFilter filter) {
            super(stores, assrt, filter);
        }

        @Override
        protected void fetchNextIterator() {
            HashTableIterator iterator = assrt ?
                                         new HashTableIterator( stores.next().getIdentityMap() ) :
                                         new HashTableIterator( stores.next().getNegMap() );
            iterator.reset();
            currentIterator = new JavaIteratorAdapter<InternalFactHandle>( iterator, JavaIteratorAdapter.FACT_HANDLE );
        }

        @Override
        protected boolean accept() {
            return filter.accept(currentNext.getObject());
        }
    }
}
