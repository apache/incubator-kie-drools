package org.optaplanner.core.impl.heuristic.selector.list;

/**
 * Points to a list variable position specified by an entity and an index.
 */
public final class ElementRef {

    private final Object entity;
    private final int index;

    private ElementRef(Object entity, int index) {
        this.entity = entity;
        this.index = index;
    }

    public static ElementRef of(Object entity, int index) {
        return new ElementRef(entity, index);
    }

    public static ElementRef elementRef(Object entity, int index) {
        return new ElementRef(entity, index);
    }

    public Object getEntity() {
        return entity;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return entity + "[" + index + "]";
    }
}
