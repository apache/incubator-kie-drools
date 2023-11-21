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

import java.lang.reflect.InvocationTargetException;

import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.AccumulateImportDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.util.TypeResolver;
import org.kie.api.runtime.rule.AccumulateFunction;

public class AccumulateFunctionCompilationPhase extends AbstractPackageCompilationPhase {
    private final TypeResolver typeResolver;

    public AccumulateFunctionCompilationPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        super(pkgRegistry, packageDescr);
        this.typeResolver = pkgRegistry.getTypeResolver();
    }

    public void process() {
        for (final AccumulateImportDescr aid : packageDescr.getAccumulateImports()) {
            AccumulateFunction af = loadAccumulateFunction(
                    aid.getFunctionName(),
                    aid.getTarget());
            pkgRegistry.getPackage().addAccumulateFunction(aid.getFunctionName(), af);
        }
    }

    @SuppressWarnings("unchecked")
    private AccumulateFunction loadAccumulateFunction(
            String identifier,
            String className) {
        try {
            Class<? extends AccumulateFunction> clazz = (Class<? extends AccumulateFunction>) typeResolver.resolveType(className);
            return clazz.getConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error loading accumulate function for identifier " + identifier + ". Class " + className + " not found",
                    e);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Error loading accumulate function for identifier " + identifier + ". Instantiation failed for class " + className,
                    e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error loading accumulate function for identifier " + identifier + ". Illegal access to class " + className,
                    e);
        }
    }
}
