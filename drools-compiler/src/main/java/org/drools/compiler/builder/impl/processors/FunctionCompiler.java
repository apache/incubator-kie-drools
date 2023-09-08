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
package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.impl.AssetFilter;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.internal.builder.ResourceChange;

public class FunctionCompiler extends ImmutableFunctionCompiler {

    public static CompilationPhase of(PackageRegistry pkgRegistry, PackageDescr packageDescr, AssetFilter assetFilter, ClassLoader rootClassLoader) {
        if (assetFilter == null) {
            return new ImmutableFunctionCompiler(pkgRegistry, packageDescr, rootClassLoader);
        } else {
            return new FunctionCompiler(pkgRegistry, packageDescr, assetFilter, rootClassLoader);
        }
    }

    private final AssetFilter assetFilter;

    private FunctionCompiler(PackageRegistry pkgRegistry, PackageDescr packageDescr, AssetFilter assetFilter, ClassLoader rootClassLoader) {
        super(pkgRegistry, packageDescr, rootClassLoader);
        this.assetFilter = assetFilter;
    }


    @Override
    protected void postCompileAddFunction(FunctionDescr functionDescr) {
        if (filterAccepts(functionDescr)) {
            super.postCompileAddFunction(functionDescr);
        }
    }

    @Override
    protected void addFunction(FunctionDescr functionDescr) {
        if (filterAccepts(functionDescr)) {
            super.addFunction(functionDescr);
        }
    }

    private boolean filterAccepts(FunctionDescr functionDescr) {
        return assetFilter == null ||
                !AssetFilter.Action.DO_NOTHING.equals(
                        assetFilter.accept(
                                ResourceChange.Type.FUNCTION,
                                functionDescr.getNamespace(),
                                functionDescr.getName()));
    }

}
