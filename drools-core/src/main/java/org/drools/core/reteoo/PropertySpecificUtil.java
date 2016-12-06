/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.reteoo;

import org.drools.core.base.ClassObjectType;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.ObjectType;
import org.drools.core.util.bitmask.AllSetBitMask;
import org.drools.core.util.bitmask.AllSetButLastBitMask;
import org.drools.core.util.bitmask.BitMask;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.bitmask.EmptyBitMask;
import org.drools.core.util.bitmask.EmptyButLastBitMask;

import java.util.List;

public class PropertySpecificUtil {

    public static final int TRAITABLE_BIT = 0;
    public static final int CUSTOM_BITS_OFFSET = 1;

    public static boolean isPropertyReactive(BuildContext context, ObjectType objectType) {
        return objectType instanceof ClassObjectType && isPropertyReactive(context, ((ClassObjectType) objectType).getClassType());
    }

    public static boolean isPropertyReactive(BuildContext context, Class<?> objectClass) {
        TypeDeclaration typeDeclaration = context.getKnowledgeBase().getTypeDeclaration( objectClass );
        return typeDeclaration != null && typeDeclaration.isPropertyReactive();
    }

    public static BitMask getEmptyPropertyReactiveMask(int settablePropertiesSize) {
        return BitMask.Factory.getEmpty(settablePropertiesSize + CUSTOM_BITS_OFFSET);
    }

    public static BitMask onlyTraitBitSetMask() {
        return EmptyButLastBitMask.get();
    }

    public static BitMask allSetButTraitBitMask() {
        return AllSetButLastBitMask.get();
    }

    public static boolean isAllSetPropertyReactiveMask(BitMask mask) {
        return mask instanceof AllSetButLastBitMask;
    }

    public static BitMask calculatePositiveMask(List<String> listenedProperties, List<String> settableProperties) {
        return calculatePatternMask(listenedProperties, settableProperties, true);
    }

    public static BitMask calculateNegativeMask(List<String> listenedProperties, List<String> settableProperties) {
        return calculatePatternMask(listenedProperties, settableProperties, false);
    }

    private static BitMask calculatePatternMask(List<String> listenedProperties, List<String> settableProperties, boolean isPositive) {
        if (listenedProperties == null) {
            return EmptyBitMask.get();
        }

        BitMask mask = getEmptyPropertyReactiveMask(settableProperties.size());
        if (listenedProperties.contains( TraitableBean.TRAITSET_FIELD_NAME )) {
            if (isPositive && listenedProperties.contains( TraitableBean.TRAITSET_FIELD_NAME ) ) {
                mask = mask.set(TRAITABLE_BIT);
            }
        }
        for (String propertyName : listenedProperties) {
            if (propertyName.equals(isPositive ? "*" : "!*")) {
                return isPositive ? AllSetBitMask.get() : allSetButTraitBitMask();
            }
            if (propertyName.startsWith("!") ^ !isPositive) {
                continue;
            }
            if (!isPositive) {
                propertyName = propertyName.substring(1);
            }

            mask = setPropertyOnMask(mask, settableProperties, propertyName);
        }
        return mask;
    }

    public static BitMask setPropertyOnMask(BitMask mask, List<String> settableProperties, String propertyName) {
        int index = settableProperties.indexOf(propertyName);
        if (index < 0) {
            throw new RuntimeException("Unknown property: " + propertyName);
        }
        return setPropertyOnMask(mask, index);
    }

    public static BitMask setPropertyOnMask(BitMask mask, int index) {
        return mask.set(index + CUSTOM_BITS_OFFSET);
    }

    public static boolean isPropertySetOnMask(BitMask mask, int index) {
        return mask.isSet(index + CUSTOM_BITS_OFFSET);
    }

    public static List<String> getSettableProperties(InternalWorkingMemory workingMemory, ObjectTypeNode objectTypeNode) {
        return getSettableProperties(workingMemory.getKnowledgeBase(), objectTypeNode);
    }

    public static List<String> getSettableProperties(InternalKnowledgeBase kBase, ObjectTypeNode objectTypeNode) {
        return getSettableProperties(kBase, getNodeClass(objectTypeNode));
    }

    public static List<String> getSettableProperties(InternalKnowledgeBase kBase, Class<?> nodeClass) {
        if (nodeClass == null) {
            return null;
        }
        TypeDeclaration typeDeclaration = kBase.getExactTypeDeclaration(nodeClass);
        if (typeDeclaration == null) {
            return ClassUtils.getSettableProperties(nodeClass);
        }
        typeDeclaration.setTypeClass(nodeClass);
        return typeDeclaration.getSettableProperties();
    }

    private static Class<?> getNodeClass( ObjectTypeNode objectTypeNode ) {
        if (objectTypeNode == null) {
            return null;
        }
        ObjectType objectType = objectTypeNode.getObjectType();
        return objectType != null && objectType instanceof ClassObjectType ? ((ClassObjectType)objectType).getClassType() : null;
    }
}
