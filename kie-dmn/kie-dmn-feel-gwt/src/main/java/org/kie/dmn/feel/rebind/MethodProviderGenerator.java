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

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import org.kie.dmn.feel.gwtreflection.MethodProvider;

public class MethodProviderGenerator extends Generator {

    @Override
    public String generate(final TreeLogger logger,
                           final GeneratorContext context,
                           final String requestedClass) {
        try {
            final TypeOracle typeOracle = context.getTypeOracle();
            final JClassType functionType = typeOracle.findType(requestedClass);

            assertMethodProviderClass(functionType);
            getFileCreator(logger, context).write();

            return MethodProviderFileCreator.PACKAGE_NAME + "." + MethodProviderFileCreator.GENERATED_CLASS_FQCN;
        } catch (final Exception e) {
            return null;
        }
    }

    MethodProviderFileCreator getFileCreator(final TreeLogger logger,
                                             final GeneratorContext context) {
        return new MethodProviderFileCreator(context, logger);
    }
    @SuppressWarnings("ConstantConditions")
    void assertMethodProviderClass(final JClassType functionType) {
        // JClassType#getClass cannot be mocked (GWT API)
        assert MethodProvider.class.equals(functionType.getClass());
    }
}
