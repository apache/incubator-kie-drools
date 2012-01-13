/*
 * Copyright 2010 JBoss Inc
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

package org.drools.base;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.drools.base.mvel.MVELCompilationUnit.DroolsVarFactory;
import org.drools.core.util.BitMaskUtil;
import org.mvel2.ast.ASTNode;
import org.mvel2.ast.WithNode;
import org.mvel2.compiler.CompiledAccExpression;
import org.mvel2.compiler.ExecutableAccessor;
import org.mvel2.integration.Interceptor;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.optimizers.impl.refl.nodes.IndexedVariableAccessor;
import org.mvel2.optimizers.impl.refl.nodes.MethodAccessor;
import org.mvel2.optimizers.impl.refl.nodes.SetterAccessor;

import static org.drools.core.util.ClassUtils.*;

public class ModifyInterceptor
    implements
    Interceptor,
    Externalizable {
    private static final long serialVersionUID = 510l;

    private long modificationMask = -1L;

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        modificationMask = in.readLong();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(modificationMask);
    }

    public int doBefore(ASTNode node,
                        VariableResolverFactory factory) {
        return 0;
    }

    public int doAfter(Object value,
                       ASTNode node,
                       VariableResolverFactory factory) {
        while ( factory != null && !(factory instanceof DroolsVarFactory)) {
            factory =  factory.getNextFactory();
        }
        
        if ( factory == null ) {
            throw new RuntimeException( "Unable to find DroolsMVELIndexedFactory" );
        }

        if (modificationMask < 0) {
            calculateModificationMask((WithNode)node);
        }
        
        ((DroolsVarFactory)factory).getKnowledgeHelper().update(value, modificationMask);
        return 0;
    }

    private void calculateModificationMask(WithNode node) {
        Class<?> nodeClass = node.getEgressType();
        List<String> settableProperties = getSettableProperties(nodeClass);

        modificationMask = 0L;

        // TODO: access parmValuePairs without reflection
        WithNode.ParmValuePair[] parmValuePairs = getFieldValue(WithNode.class, "withExpressions", node);
        for (WithNode.ParmValuePair parmValuePair : parmValuePairs) {
            Method method = extractMethod(parmValuePair);
            if (method == null) {
                modificationMask = Long.MAX_VALUE;
                return;
            }

            String propertyName = setter2property(method.getName());
            if (propertyName != null) {
                int pos = settableProperties.indexOf(propertyName);
                modificationMask = BitMaskUtil.set(modificationMask, pos);
            } else {
                // Invocation of a non-setter => cannot calculate the mask
                modificationMask = Long.MAX_VALUE;
                return;
            }
        }
    }

    private Method extractMethod(WithNode.ParmValuePair parmValuePair) {
        Serializable setExpression = parmValuePair.getSetExpression();
        if (setExpression != null) {
            SetterAccessor setterAccessor = (SetterAccessor)((CompiledAccExpression) setExpression).getAccessor();
            return setterAccessor.getMethod();
        } else {
            ExecutableAccessor accessor = (ExecutableAccessor)parmValuePair.getStatement();
            IndexedVariableAccessor variableAccessor = (IndexedVariableAccessor)accessor.getNode().getAccessor();
            MethodAccessor methodAccessor = (MethodAccessor)variableAccessor.getNextNode();
            return methodAccessor.getMethod();
        }
    }

    private <T, V> V getFieldValue(Class<T> clazz, String fieldName, T object) {
        try {
            Field f = clazz.getDeclaredField(fieldName);
            f.setAccessible(true);
            return (V)f.get(object);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
