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

package org.drools.model.impl;

import org.drools.model.Argument;
import org.drools.model.Query6Def;
import org.drools.model.Variable;
import org.drools.model.view.QueryCallViewItem;
import org.drools.model.view.QueryCallViewItemImpl;
import static org.drools.model.FlowDSL.declarationOf;
import static org.drools.model.impl.RuleBuilder.DEFAULT_PACKAGE;

public class Query6DefImpl<T1, T2, T3, T4, T5, T6> extends QueryDefImpl implements ModelComponent, Query6Def<T1, T2, T3, T4, T5, T6> {

    private final Variable<T1> arg1;

    private final Variable<T2> arg2;

    private final Variable<T3> arg3;

    private final Variable<T4> arg4;

    private final Variable<T5> arg5;

    private final Variable<T6> arg6;

    public Query6DefImpl(ViewBuilder viewBuilder, String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6) {
        this(viewBuilder, DEFAULT_PACKAGE, name, type1, type2, type3, type4, type5, type6);
    }

    public Query6DefImpl(ViewBuilder viewBuilder, String pkg, String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6) {
        super(viewBuilder, pkg, name);
        this.arg1 = declarationOf(type1);
        this.arg2 = declarationOf(type2);
        this.arg3 = declarationOf(type3);
        this.arg4 = declarationOf(type4);
        this.arg5 = declarationOf(type5);
        this.arg6 = declarationOf(type6);
    }

    public Query6DefImpl(ViewBuilder viewBuilder, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name) {
        this(viewBuilder, DEFAULT_PACKAGE, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name, type5, arg5name, type6, arg6name);
    }

    public Query6DefImpl(ViewBuilder viewBuilder, String pkg, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name, Class<T5> type5, String arg5name, Class<T6> type6, String arg6name) {
        super(viewBuilder, pkg, name);
        this.arg1 = declarationOf(type1, arg1name);
        this.arg2 = declarationOf(type2, arg2name);
        this.arg3 = declarationOf(type3, arg3name);
        this.arg4 = declarationOf(type4, arg4name);
        this.arg5 = declarationOf(type5, arg5name);
        this.arg6 = declarationOf(type6, arg6name);
    }

    @Override()
    public QueryCallViewItem call(boolean open, Argument<T1> var1, Argument<T2> var2, Argument<T3> var3, Argument<T4> var4, Argument<T5> var5, Argument<T6> var6) {
        return new QueryCallViewItemImpl(this, open, var1, var2, var3, var4, var5, var6);
    }

    @Override()
    public Variable<?>[] getArguments() {
        return new Variable<?>[] { arg1, arg2, arg3, arg4, arg5, arg6 };
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

    @Override
    public boolean isEqualTo(ModelComponent other) {
        if (this == other)
            return true;
        if (!(other instanceof Query6DefImpl))
            return false;
        Query6DefImpl that = (Query6DefImpl) other;
        return true && ModelComponent.areEqualInModel(arg1, that.arg1) && ModelComponent.areEqualInModel(arg2, that.arg2) && ModelComponent.areEqualInModel(arg3, that.arg3) && ModelComponent.areEqualInModel(arg4, that.arg4) && ModelComponent.areEqualInModel(arg5, that.arg5) && ModelComponent.areEqualInModel(arg6, that.arg6);
    }
}
