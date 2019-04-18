/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import org.drools.verifier.core.maps.InspectorList;
import org.drools.verifier.core.maps.util.HasUUID;

public class SubsumptionBlocker
        extends BlockerRelation<SubsumptionBlocker> {

    public static final SubsumptionBlocker EMPTY = new SubsumptionBlocker();

    public SubsumptionBlocker() {
        super();
    }

    public SubsumptionBlocker(final InspectorList list,
                              final HasUUID item) {
        super(list, item);
    }

    public SubsumptionBlocker(final InspectorList list,
                              final HasUUID item,
                              final SubsumptionBlocker origin) {
        super(list, item, origin);
    }

    @Override
    public boolean doesRelationStillExist() {
        if (origin != null
                && stillContainsBlockingItem(getOrigin().getItem())) {
            return SubsumptionResolver.isSubsumedByAnObjectInThisList(getOrigin().getList(),
                                                                      getOrigin().getItem()).foundIssue();
        } else {
            return false;
        }
    }

    protected boolean stillContainsBlockingItem(final HasUUID item) {
        if (this.item instanceof InspectorList) {
            return ((InspectorList) this.item).contains(item);
        } else {
            if (parent != null) {
                return parent.stillContainsBlockingItem(item);
            } else {
                return false;
            }
        }
    }
}
