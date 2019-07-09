/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.drools.model;

import org.drools.model.view.QueryCallViewItem;

public interface Query2Def<T1, T2> extends QueryDef {

    default QueryCallViewItem call(Argument<T1> var1, Argument<T2> var2) {
        return call(true, var1, var2);
    }

    QueryCallViewItem call(boolean open, Argument<T1> var1, Argument<T2> var2);

    Variable<T1> getArg1();

    Variable<T2> getArg2();
}
