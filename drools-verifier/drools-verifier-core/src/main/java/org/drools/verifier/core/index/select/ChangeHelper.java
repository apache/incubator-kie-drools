package org.drools.verifier.core.index.select;

import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.matchers.Matcher;
import org.drools.verifier.core.maps.MultiMapChangeHandler;

class ChangeHelper<T> {

    private final Select<T> addedSelector;
    private final Select<T> removedSelector;

    ChangeHelper(final MultiMapChangeHandler.ChangeSet<Value, T> changeSet,
                 final Matcher matcher) {
        addedSelector = new Select<>(changeSet.getAdded(),
                                      matcher);
        removedSelector = new Select<>(changeSet.getRemoved(),
                                        matcher);
    }

    boolean firstChanged(final Select.Entry first) {
        if (containsEntry(removedSelector,
                          first)) {
            return true;
        } else if (addedSelector.exists()) {
            return first.getKey().compareTo(addedSelector.firstEntry().getKey()) > 0;
        } else {
            return false;
        }
    }

    private boolean containsEntry(final Select<T> select,
                                  final Select.Entry entry) {
        return select.asMap().keySet().contains(entry.getKey()) && select.all().contains(entry.getValue());
    }

    boolean lastChanged(final Select.Entry last) {
        if (containsEntry(removedSelector,
                          last)) {
            return true;
        } else if (addedSelector.exists()) {
            return last.getKey().compareTo(addedSelector.lastEntry().getKey()) < 0;
        } else {
            return false;
        }
    }
}
