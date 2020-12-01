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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.PropertySpecificUtil;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.Tuple;
import org.drools.core.util.bitmask.AllSetBitMask;
import org.drools.core.util.bitmask.BitMask;
import org.drools.mvel.expr.MVELCompilationUnit;
import org.mvel2.ast.ASTNode;
import org.mvel2.ast.WithNode;
import org.mvel2.compiler.AccessorNode;
import org.mvel2.compiler.CompiledAccExpression;
import org.mvel2.compiler.ExecutableAccessor;
import org.mvel2.integration.Interceptor;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.optimizers.impl.refl.nodes.GetterAccessor;
import org.mvel2.optimizers.impl.refl.nodes.MethodAccessor;
import org.mvel2.optimizers.impl.refl.nodes.SetterAccessor;

import static org.drools.core.reteoo.PropertySpecificUtil.allSetButTraitBitMask;
import static org.drools.core.reteoo.PropertySpecificUtil.getEmptyPropertyReactiveMask;
import static org.drools.core.reteoo.PropertySpecificUtil.setPropertyOnMask;
import static org.drools.core.util.ClassUtils.getter2property;
import static org.drools.core.util.ClassUtils.setter2property;

public class ModifyInterceptor
    implements
    Interceptor,
    Externalizable {
    private static final long serialVersionUID = 510l;

    private BitMask modificationMask = AllSetBitMask.get();
    private Boolean isEqualityMode;

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        modificationMask = (BitMask) in.readObject();
        isEqualityMode = (Boolean) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(modificationMask);
        out.writeObject(isEqualityMode);
    }

    public int doBefore(ASTNode node,
                        VariableResolverFactory factory) {
        return 0;
    }

    public int doAfter(Object value,
                       ASTNode node,
                       VariableResolverFactory factory) {
        while ( factory != null && !(factory instanceof MVELCompilationUnit.DroolsVarFactory)) {
            factory =  factory.getNextFactory();
        }
        
        if ( factory == null ) {
            throw new RuntimeException( "Unable to find DroolsMVELIndexedFactory" );
        }

        KnowledgeHelper knowledgeHelper = ((MVELCompilationUnit.DroolsVarFactory)factory).getKnowledgeHelper();
        if (isEqualityMode == null) {
            isEqualityMode = knowledgeHelper.getWorkingMemory().getKnowledgeBase().getConfiguration().getAssertBehaviour() == RuleBaseConfiguration.AssertBehaviour.EQUALITY;
        }

        if (modificationMask.isSet(PropertySpecificUtil.TRAITABLE_BIT)) {
            calculateModificationMask(knowledgeHelper, (WithNode)node);
        }

        if (isEqualityMode) {
            Tuple tuple = knowledgeHelper.getTuple();
            InternalFactHandle modifiedFh = tuple.getFactHandle();
            while (modifiedFh == null || modifiedFh.getObject() != value) {
                tuple = tuple.getParent();
                modifiedFh = tuple.getFactHandle();
            }
            knowledgeHelper.update(modifiedFh, modificationMask, value.getClass());
        } else {
            knowledgeHelper.update(value, modificationMask, value.getClass());
        }

        return 0;
    }

    private void calculateModificationMask(KnowledgeHelper knowledgeHelper, WithNode node) {
        Class<?> nodeClass = node.getEgressType();
        TypeDeclaration typeDeclaration = knowledgeHelper.getWorkingMemory().getKnowledgeBase().getTypeDeclaration(nodeClass);
        if (typeDeclaration == null || !typeDeclaration.isPropertyReactive()) {
            modificationMask = allSetButTraitBitMask();
            return;
        }

        List<String> settableProperties = typeDeclaration.getAccessibleProperties();
        modificationMask = getEmptyPropertyReactiveMask(settableProperties.size());

        // TODO: access parmValuePairs without reflection
        WithNode.ParmValuePair[] parmValuePairs = getFieldValue(WithNode.class, "withExpressions", node);
        for (WithNode.ParmValuePair parmValuePair : parmValuePairs) {
            Method method = extractMethod(parmValuePair);
            if (method == null) {
                modificationMask = allSetButTraitBitMask();
                return;
            }

            String propertyName = setter2property(method.getName());
            if (propertyName == null) {
                propertyName = getter2property(method.getName());
            }

            if (propertyName != null) {
                int index = settableProperties.indexOf(propertyName);
                if (index >= 0) {
                    modificationMask = setPropertyOnMask(modificationMask, index);
                }
            }


            List<String> modifiedProps = typeDeclaration.getTypeClassDef().getModifiedPropsByMethod(method);
            if (modifiedProps != null) {
                for (String modifiedProp : modifiedProps) {
                    int index = settableProperties.indexOf(modifiedProp);
                    if (index >= 0) {
                        modificationMask = setPropertyOnMask(modificationMask, index);
                    }
                }
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
            AccessorNode accessorNode = ((AccessorNode)accessor.getNode().getAccessor()).getNextNode();
            if (accessorNode instanceof MethodAccessor) {
                return (( MethodAccessor ) accessorNode).getMethod();
            } else if (accessorNode instanceof GetterAccessor ) {
                return (( GetterAccessor ) accessorNode).getMethod();
            }
            return null;
        }
    }

    private <T, V> V getFieldValue(Class<T> clazz, String fieldName, T object) {
        try {
            Field f = clazz.getDeclaredField(fieldName);
            f.setAccessible(true);
            return (V)f.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
