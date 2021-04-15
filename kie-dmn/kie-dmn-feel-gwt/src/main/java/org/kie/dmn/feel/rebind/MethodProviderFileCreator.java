/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.feel.rebind;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Optional;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import org.kie.dmn.feel.gwt.functions.api.FunctionOverrideVariation;
import org.kie.dmn.feel.gwtreflection.MethodProvider;

public class MethodProviderFileCreator {

    public static final String GENERATED_CLASS_FQCN = MethodProvider.class.getSimpleName() + "Impl";

    public static final String PACKAGE_NAME = MethodProvider.class.getPackage().getName();

    private final TreeLogger logger;

    private final GeneratorContext context;

    public MethodProviderFileCreator(final GeneratorContext context,
                                     final TreeLogger logger) {
        this.logger = logger;
        this.context = context;
    }

    private Optional<SourceWriter> getSourceWriter(final GeneratorContext context,
                                                   final TreeLogger logger) {

        final ClassSourceFileComposerFactory composerFactory = getClassSourceFileComposerFactory();
        final Optional<PrintWriter> printWriter = Optional.ofNullable(context.tryCreate(logger, PACKAGE_NAME, GENERATED_CLASS_FQCN));

        return printWriter.map(pw -> composerFactory.createSourceWriter(context, pw));
    }

    ClassSourceFileComposerFactory getClassSourceFileComposerFactory() {

        final ClassSourceFileComposerFactory composerFactory = makeComposerFactory();

        composerFactory.addImport(MethodProvider.class.getCanonicalName());
        composerFactory.addImport(Method.class.getCanonicalName());
        composerFactory.addImport(FunctionOverrideVariation.class.getCanonicalName());

        return composerFactory;
    }

    public void write() {
        getSourceWriter(context, logger).ifPresent(sourceWriter -> {
            final String template = new StringBuilder()
                    .append("public Method get(FunctionOverrideVariation variation) {")
                    .append("   return new Method( variation );")
                    .append("}")
                    .toString();
            sourceWriter.print(template);
            sourceWriter.commit(logger);
        });
    }

    ClassSourceFileComposerFactory makeComposerFactory() {
        return new ClassSourceFileComposerFactory(PACKAGE_NAME, GENERATED_CLASS_FQCN);
    }
}
