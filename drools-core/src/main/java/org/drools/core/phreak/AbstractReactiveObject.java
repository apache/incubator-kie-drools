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

import java.util.ArrayList;
import java.util.List;

public class AbstractReactiveObject implements ReactiveObject {
    private List<Tuple> lts;

    public void addLeftTuple(Tuple leftTuple) {
        if (lts == null) {
            lts = new ArrayList<Tuple>();
        }
        lts.add(leftTuple);
    }

    public List<Tuple> getLeftTuples() {
        return lts;
    }

    protected void notifyModification() {
        ReactiveObjectUtil.notifyModification(this);
    }
}
