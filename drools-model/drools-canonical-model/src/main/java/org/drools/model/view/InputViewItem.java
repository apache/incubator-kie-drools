package org.drools.model.view;

public interface InputViewItem<T> extends ViewItem<T> {
    InputViewItem<T> watch(String... props);
}
