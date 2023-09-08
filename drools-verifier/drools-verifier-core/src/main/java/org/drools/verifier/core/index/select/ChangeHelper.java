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
