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

package org.kie.kogito.taskassigning.service.util;

import java.util.Iterator;
import java.util.List;

/**
 * Facilitates the ordering of elements given an index but also the faculty of being pinned.
 * Pinned elements goes always before non-pinned elements and ordered by index.
 * Non-pinned elements goes always after the pinned elements and ordered by index.
 */
public class IndexedElement<T> {

    private T element;
    private int index;
    private boolean pinned;

    public IndexedElement(T element, int index, boolean pinned) {
        this.element = element;
        this.index = index;
        this.pinned = pinned;
    }

    public T getElement() {
        return element;
    }

    public int getIndex() {
        return index;
    }

    public boolean isPinned() {
        return pinned;
    }

    public static <T> void addInOrder(List<IndexedElement<T>> indexedElements, IndexedElement<T> element) {
        boolean pinned = element.isPinned();
        int index = element.getIndex();
        int insertIndex = 0;
        IndexedElement<T> currentElement;
        final Iterator<IndexedElement<T>> it = indexedElements.iterator();
        boolean found = false;
        while (!found && it.hasNext()) {
            currentElement = it.next();
            if (pinned && currentElement.isPinned()) {
                found = (index >= 0) && (currentElement.getIndex() < 0 || index < currentElement.getIndex());
            } else if (pinned && !currentElement.isPinned()) {
                found = true;
            } else if (!pinned && !currentElement.isPinned()) {
                found = (index >= 0) && (currentElement.getIndex() < 0 || index < currentElement.getIndex());
            }
            insertIndex = !found ? insertIndex + 1 : insertIndex;
        }
        indexedElements.add(insertIndex, element);
    }
}
