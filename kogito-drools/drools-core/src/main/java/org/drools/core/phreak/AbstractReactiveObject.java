/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.drools.core.phreak;

import org.drools.core.spi.Tuple;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class AbstractReactiveObject implements ReactiveObject {

    private Collection<Tuple> lts;

    public void addLeftTuple(Tuple leftTuple) {
        if (lts == null) {
            lts = new HashSet<Tuple>();
        }
        lts.add(leftTuple);
    }

    public Collection<Tuple> getLeftTuples() {
        return lts != null ? lts : Collections.emptyList();
    }

    protected void notifyModification() {
        ReactiveObjectUtil.notifyModification(this);
    }

    @Override
    public void removeLeftTuple(Tuple leftTuple) {
        lts.remove(leftTuple);
    }
}
