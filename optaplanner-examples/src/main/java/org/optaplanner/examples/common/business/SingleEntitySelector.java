package org.optaplanner.examples.common.business;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.entity.AbstractEntitySelector;

final class SingleEntitySelector<Solution_> extends AbstractEntitySelector<Solution_> {

    private final EntityDescriptor<Solution_> entityDescriptor;
    private final List<Object> objectList;

    public SingleEntitySelector(EntityDescriptor<Solution_> entityDescriptor, Object entity) {
        this.entityDescriptor = entityDescriptor;
        this.objectList = Collections.singletonList(entity);
    }

    @Override
    public long getSize() {
        return 1;
    }

    @Override
    public boolean isCountable() {
        return true;
    }

    @Override
    public boolean isNeverEnding() {
        return false;
    }

    @Override
    public ListIterator<Object> listIterator() {
        return objectList.listIterator();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        return objectList.listIterator(index);
    }

    @Override
    public EntityDescriptor<Solution_> getEntityDescriptor() {
        return entityDescriptor;
    }

    @Override
    public Iterator<Object> endingIterator() {
        return iterator();
    }

    @Override
    public Iterator<Object> iterator() {
        return objectList.iterator();
    }
}
