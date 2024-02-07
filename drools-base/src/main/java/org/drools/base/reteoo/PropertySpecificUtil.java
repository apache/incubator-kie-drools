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
package org.drools.base.reteoo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.base.RuleBase;
import org.drools.base.base.ClassObjectType;
import org.drools.base.base.ObjectType;
import org.drools.base.factmodel.traits.TraitConstants;
import org.drools.base.prototype.PrototypeObjectType;
import org.drools.base.rule.TypeDeclaration;
import org.drools.util.bitmask.AllSetBitMask;
import org.drools.util.bitmask.AllSetButLastBitMask;
import org.drools.util.bitmask.BitMask;
import org.drools.util.bitmask.EmptyBitMask;
import org.drools.util.bitmask.EmptyButLastBitMask;

public class PropertySpecificUtil {

    public static final int TRAITABLE_BIT = 0;
    public static final int CUSTOM_BITS_OFFSET = 1;

    public static boolean isPropertyReactive(RuleBase ruleBase, ObjectType objectType) {
        if (objectType.isPrototype()) {
            return !((PrototypeObjectType) objectType).getFieldNames().isEmpty();
        }
        TypeDeclaration typeDeclaration = ruleBase.getTypeDeclaration(((ClassObjectType) objectType).getClassType() );
        return typeDeclaration != null && typeDeclaration.isPropertyReactive();
    }

    public static BitMask getEmptyPropertyReactiveMask(int settablePropertiesSize) {
        return BitMask.getEmpty(settablePropertiesSize + CUSTOM_BITS_OFFSET);
    }

    public static BitMask onlyTraitBitSetMask() {
        return EmptyButLastBitMask.get();
    }

    public static BitMask allSetBitMask() {
        return AllSetBitMask.get();
    }

    public static BitMask allSetButTraitBitMask() {
        return AllSetButLastBitMask.get();
    }

    public static boolean isAllSetPropertyReactiveMask(BitMask mask) {
        return mask instanceof AllSetButLastBitMask;
    }

    public static BitMask calculatePositiveMask( ObjectType modifiedType, Collection<String> listenedProperties, List<String> accessibleProperties ) {
        return calculatePositiveMask( modifiedType.getClassName(), listenedProperties, accessibleProperties );
    }

    public static BitMask calculatePositiveMask( String modifiedTypeName, Collection<String> listenedProperties, List<String> accessibleProperties ) {
        return calculatePatternMask(modifiedTypeName, listenedProperties, accessibleProperties, true);
    }

    public static BitMask calculateNegativeMask(ObjectType modifiedType, Collection<String> listenedProperties, List<String> accessibleProperties) {
        return calculateNegativeMask(modifiedType.getClassName(), listenedProperties, accessibleProperties);
    }

    public static BitMask calculateNegativeMask(String modifiedTypeName, Collection<String> listenedProperties, List<String> accessibleProperties) {
        return calculatePatternMask(modifiedTypeName, listenedProperties, accessibleProperties, false);
    }

    private static BitMask calculatePatternMask(String modifiedTypeName, Collection<String> listenedProperties, List<String> accessibleProperties, boolean isPositive) {
        if (listenedProperties.isEmpty()) {
            return EmptyBitMask.get();
        }

        BitMask mask = getEmptyPropertyReactiveMask(accessibleProperties.size());

        if (listenedProperties.contains( TraitConstants.TRAITSET_FIELD_NAME )) {
            if (isPositive && listenedProperties.contains( TraitConstants.TRAITSET_FIELD_NAME ) ) {
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

            mask = setPropertyOnMask(modifiedTypeName, mask, accessibleProperties, propertyName);
        }
        return mask;
    }

    public static BitMask setPropertyOnMask(String modifiedTypeName, BitMask mask, List<String> settableProperties, String propertyName) {
        int index = settableProperties.indexOf(propertyName);
        if (index < 0) {
            throw new RuntimeException("Unknown property '" + propertyName + "' on " + modifiedTypeName);
        }
        return setPropertyOnMask(mask, index);
    }

    public static BitMask setPropertyOnMask(BitMask mask, int index) {
        return mask.set(index + CUSTOM_BITS_OFFSET);
    }

    public static boolean isPropertySetOnMask(BitMask mask, int index) {
        return mask.isSet(index + CUSTOM_BITS_OFFSET);
    }

    public static List<String> getAccessibleProperties(RuleBase ruleBase, ObjectType objectType) {
        return objectType.isPrototype() ?
                new ArrayList<>(((PrototypeObjectType) objectType).getFieldNames()) :
                getAccessibleProperties(ruleBase, ((ClassObjectType) objectType).getClassType());
    }

    public static List<String> getAccessibleProperties(RuleBase ruleBase, Class<?> nodeClass ) {
        return ruleBase.getOrCreateExactTypeDeclaration(nodeClass).getAccessibleProperties();
    }
}
