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
package org.drools.model.impl;

import org.drools.model.Argument;
import org.drools.model.Query10Def;
import org.drools.model.Variable;
import org.drools.model.view.QueryCallViewItem;
import org.drools.model.view.QueryCallViewItemImpl;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.impl.RuleBuilder.DEFAULT_PACKAGE;

public class Query10DefImpl<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> extends QueryDefImpl implements ModelComponent, Query10Def<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> {

    private final Variable<T1> arg1;

    private final Variable<T2> arg2;

    private final Variable<T3> arg3;

    private final Variable<T4> arg4;

    private final Variable<T5> arg5;

    private final Variable<T6> arg6;

    private final Variable<T7> arg7;

    private final Variable<T8> arg8;

    private final Variable<T9> arg9;

    private final Variable<T10> arg10;

    public Query10DefImpl(ViewBuilder viewBuilder, String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6, Class<T7> type7, Class<T8> type8, Class<T9> type9, Class<T10> type10) {
        this(viewBuilder, DEFAULT_PACKAGE, name, type1, type2, type3, type4, type5, type6, type7, type8, type9, type10);
    }

    public Query10DefImpl(ViewBuilder viewBuilder, String pkg, String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6, Class<T7> type7, Class<T8> type8, Class<T9> type9, Class<T10> type10) {
        super(viewBuilder, pkg, name);
        this.arg1 = declarationOf(type1);
        this.arg2 = declarationOf(type2);
        this.arg3 = declarationOf(type3);
        this.arg4 = declarationOf(type4);
        this.arg5 = declarationOf(type5);
        this.arg6 = declarationOf(type6);
        this.arg7 = declarationOf(type7);
        this.arg8 = declarationOf(type8);
        this.arg9 = declarationOf(type9);
        this.arg10 = declarationOf(type10);
    }

    public Query10DefImpl(ViewBuilder viewBuilder, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name, Class<T7> type7, String arg7name, Class<T8> type8, String arg8name, Class<T9> type9, String arg9name, Class<T10> type10, String arg10name) {
        this(viewBuilder, DEFAULT_PACKAGE, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name, type6, arg6name, type7, arg7name, type8, arg8name, type9, arg9name, type10, arg10name);
    }

    public Query10DefImpl(ViewBuilder viewBuilder, String pkg, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name, Class<T7> type7, String arg7name, Class<T8> type8, String arg8name, Class<T9> type9, String arg9name, Class<T10> type10, String arg10name) {
        super(viewBuilder, pkg, name);
        this.arg1 = declarationOf(type1, arg1name);
        this.arg2 = declarationOf(type2, arg2name);
        this.arg3 = declarationOf(type3, arg3name);
        this.arg4 = declarationOf(type4, arg4name);
        this.arg5 = declarationOf(type5, arg5name);
        this.arg6 = declarationOf(type6, arg6name);
        this.arg7 = declarationOf(type7, arg7name);
        this.arg8 = declarationOf(type8, arg8name);
        this.arg9 = declarationOf(type9, arg9name);
        this.arg10 = declarationOf(type10, arg10name);
    }

    @Override()
    public QueryCallViewItem call(boolean open, Argument<T1> var1, Argument<T2> var2, Argument<T3> var3, Argument<T4> var4, Argument<T5> var5, Argument<T6> var6, Argument<T7> var7, Argument<T8> var8, Argument<T9> var9, Argument<T10> var10) {
        return new QueryCallViewItemImpl(this, open, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
    }

    @Override()
    public Variable<?>[] getArguments() {
        return new Variable<?>[] { arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10 };
    }

    @Override()
    public Variable<T1> getArg1() {
        return arg1;
    }

    @Override()
    public Variable<T2> getArg2() {
        return arg2;
    }

    @Override()
    public Variable<T3> getArg3() {
        return arg3;
    }

    @Override()
    public Variable<T4> getArg4() {
        return arg4;
    }

    @Override()
    public Variable<T5> getArg5() {
        return arg5;
    }

    @Override()
    public Variable<T6> getArg6() {
        return arg6;
    }

    @Override()
    public Variable<T7> getArg7() {
        return arg7;
    }

    @Override()
    public Variable<T8> getArg8() {
        return arg8;
    }

    @Override()
    public Variable<T9> getArg9() {
        return arg9;
    }

    @Override()
    public Variable<T10> getArg10() {
        return arg10;
    }

    @Override
    public boolean isEqualTo(ModelComponent other) {
        if (this == other)
            return true;
        if (!(other instanceof Query10DefImpl))
            return false;
        Query10DefImpl that = (Query10DefImpl) other;
        return true && ModelComponent.areEqualInModel(arg1, that.arg1) && ModelComponent.areEqualInModel(arg2, that.arg2) && ModelComponent.areEqualInModel(arg3, that.arg3) && ModelComponent.areEqualInModel(arg4, that.arg4) && ModelComponent.areEqualInModel(arg5, that.arg5) && ModelComponent.areEqualInModel(arg6, that.arg6) && ModelComponent.areEqualInModel(arg7, that.arg7) && ModelComponent.areEqualInModel(arg8, that.arg8) && ModelComponent.areEqualInModel(arg9, that.arg9) && ModelComponent.areEqualInModel(arg10, that.arg10);
    }
}
