/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.verifier.core.relations;

import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.maps.InspectorList;
import org.drools.verifier.core.maps.util.HasUUID;
import org.drools.verifier.core.util.PortablePreconditions;

public abstract class BlockerRelation<T extends BlockerRelation>
        extends Relation<T>{

    private final InspectorList list;
    final HasUUID item;

    protected BlockerRelation() {
        super(null);
        list = null;
        item = null;
    }

    public BlockerRelation(final InspectorList list,
                          final HasUUID item) {
        this(list, item, null);
    }

    public BlockerRelation(final InspectorList list,
                          final HasUUID item,
                          final T origin) {
        super(origin);
        this.list = PortablePreconditions.checkNotNull("list",
                                                       list);
        this.item = PortablePreconditions.checkNotNull("item",
                                                       item);
    }

    public InspectorList getList() {
        return list;
    }

    @Override
    protected HasUUID getItem() {
        return item;
    }

    @Override
    public boolean foundIssue() {
        return item != null;
    }

    @Override
    public UUIDKey otherUUID() {
        return item.getUuidKey();
    }

}
