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

import java.util.List;

import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.wiring.api.classloader.ProjectClassLoader;

import static org.drools.core.impl.KnowledgeBaseImpl.registerFunctionClassAndInnerClasses;
import static org.drools.util.StringUtils.isEmpty;

public class ImmutableFunctionCompiler extends AbstractPackageCompilationPhase {

    private ClassLoader rootClassLoader;

    public ImmutableFunctionCompiler(PackageRegistry pkgRegistry, PackageDescr packageDescr, ClassLoader rootClassLoader) {
        super(pkgRegistry, packageDescr);
        this.rootClassLoader = rootClassLoader;
    }

    public final void process() {
        List<FunctionDescr> functions = packageDescr.getFunctions();
        if (!functions.isEmpty()) {

            for (FunctionDescr functionDescr : functions) {
                if (isEmpty(functionDescr.getNamespace())) {
                    // make sure namespace is set on components
                    functionDescr.setNamespace(packageDescr.getNamespace());
                }

                // make sure functions are compiled using java dialect
                functionDescr.setDialect("java");

                preCompileAddFunction(functionDescr, pkgRegistry);
            }

            // iterate and compile
            for (FunctionDescr functionDescr : functions) {
                addFunction(functionDescr);
            }

            // compile functions in this pkgRegistry
            pkgRegistry.compileAll();

            for (FunctionDescr functionDescr : functions) {
                postCompileAddFunction(functionDescr);
            }
        }
    }

    protected void postCompileAddFunction(FunctionDescr functionDescr) {
        postCompileAddFunction(functionDescr, pkgRegistry);
    }

    protected void addFunction(FunctionDescr functionDescr) {
        // inherit the dialect from the package
        addFunction(functionDescr, pkgRegistry);
    }

    private void preCompileAddFunction(final FunctionDescr functionDescr, PackageRegistry pkgRegistry) {
        Dialect dialect = pkgRegistry.getDialectCompiletimeRegistry().getDialect(functionDescr.getDialect());
        dialect.preCompileAddFunction(functionDescr,
                pkgRegistry.getTypeResolver());
    }


    private void addFunction(final FunctionDescr functionDescr, PackageRegistry pkgRegistry) {
        Dialect dialect = pkgRegistry.getDialectCompiletimeRegistry().getDialect(functionDescr.getDialect());
        dialect.addFunction(functionDescr,
                pkgRegistry.getTypeResolver(),
                null/*this.resource*/);
    }



    private void postCompileAddFunction(final FunctionDescr functionDescr, PackageRegistry pkgRegistry) {
        Dialect dialect = pkgRegistry.getDialectCompiletimeRegistry().getDialect(functionDescr.getDialect());
        dialect.postCompileAddFunction(functionDescr, pkgRegistry.getTypeResolver());

        if (rootClassLoader instanceof ProjectClassLoader) {
            String functionClassName = functionDescr.getClassName();
            JavaDialectRuntimeData runtime = ((JavaDialectRuntimeData) pkgRegistry.getDialectRuntimeRegistry().getDialectData("java"));
            try {
                registerFunctionClassAndInnerClasses(functionClassName, runtime, ((ProjectClassLoader) rootClassLoader)::storeClass);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
