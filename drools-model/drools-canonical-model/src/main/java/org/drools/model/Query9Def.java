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

public interface Query9Def<T1, T2, T3, T4, T5, T6, T7, T8, T9> extends QueryDef {

    default QueryCallViewItem call(Argument<T1> var1, Argument<T2> var2, Argument<T3> var3, Argument<T4> var4, Argument<T5> var5, Argument<T6> var6, Argument<T7> var7, Argument<T8> var8, Argument<T9> var9) {
        return call(true, var1, var2, var3, var4, var5, var6, var7, var8, var9);
    }

    QueryCallViewItem call(boolean open, Argument<T1> var1, Argument<T2> var2, Argument<T3> var3, Argument<T4> var4, Argument<T5> var5, Argument<T6> var6, Argument<T7> var7, Argument<T8> var8, Argument<T9> var9);

    Variable<T1> getArg1();

    Variable<T2> getArg2();

    Variable<T3> getArg3();

    Variable<T4> getArg4();

    Variable<T5> getArg5();

    Variable<T6> getArg6();

    Variable<T7> getArg7();

    Variable<T8> getArg8();

    Variable<T9> getArg9();
}
