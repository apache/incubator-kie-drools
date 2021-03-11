/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
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
 */

package org.drools.mvel;

import java.io.IOException;
import java.util.Date;

import org.drools.core.base.ClassFieldInspector;
import org.drools.core.base.CoreComponentsBuilder;
import org.drools.core.rule.DialectRuntimeData;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.util.MVELExecutor;
import org.drools.mvel.asm.ClassFieldInspectorImpl;
import org.drools.mvel.extractors.MVELDateClassFieldReader;
import org.drools.mvel.extractors.MVELNumberClassFieldReader;
import org.drools.mvel.extractors.MVELObjectClassFieldReader;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;

public class MVELCoreComponentsBuilder implements CoreComponentsBuilder {

    @Override
    public InternalReadAccessor getReadAcessor( String className, String expr, boolean typesafe, Class<?> returnType) {
        if (Number.class.isAssignableFrom( returnType ) ||
                ( returnType == byte.class ||
                        returnType == short.class ||
                        returnType == int.class ||
                        returnType == long.class ||
                        returnType == float.class ||
                        returnType == double.class ) ) {
            return new MVELNumberClassFieldReader( className, expr, typesafe );
        } else if (  Date.class.isAssignableFrom( returnType ) ) {
            return new MVELDateClassFieldReader( className, expr, typesafe );
        } else {
            return new MVELObjectClassFieldReader( className, expr, typesafe );
        }
    }

    @Override
    public Object evaluateMvelExpression(DialectRuntimeData data, ClassLoader classLoader, String expr) {
        return MVELSafeHelper.getEvaluator().executeExpression( MVEL.compileExpression( expr, getParserContext(data, classLoader) ) );
    }

    @Override
    public ClassFieldInspector createClassFieldInspector( Class<?> classUnderInspection, boolean includeFinalMethods ) throws IOException {
        return new ClassFieldInspectorImpl( classUnderInspection, includeFinalMethods );
    }

    @Override
    public MVELExecutor getMVELExecutor() {
        return (MVELExecutor) MVELSafeHelper.getEvaluator();
    }

    static ParserContext getParserContext(DialectRuntimeData data, ClassLoader classLoader) {
        ParserConfiguration conf = (( MVELDialectRuntimeData)data).getParserConfiguration();
        conf.setClassLoader( classLoader );
        return new ParserContext( conf );
    }
}
