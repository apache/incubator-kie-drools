package org.drools.reteoo;

import org.drools.base.ClassObjectType;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.core.util.BitMaskUtil;
import org.drools.core.util.ClassUtils;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.rule.TypeDeclaration;
import org.drools.spi.ObjectType;

import java.util.List;

public class PropertySpecificUtil {

    public static long calculateMaskFromPattern(List<String> listenedProperties, long mask, List<String> settableProperties) {
        if (listenedProperties == null) {
            return mask;
        }
        for (String propertyName : listenedProperties) {
            if (propertyName.equals("*") || propertyName.equals("!*")) {
                continue;
            }
            boolean isNegative = propertyName.startsWith("!");
            if (isNegative) {
                propertyName = propertyName.substring(1).trim();
            }

            int pos = settableProperties.indexOf(propertyName);
            if (pos < 0) {
                throw new RuntimeException("Unknown property: " + propertyName);
            }
            mask = isNegative ? BitMaskUtil.reset(mask, pos) : BitMaskUtil.set(mask, pos);
        }
        return mask;
    }

    /**
     * Add only positive listened properties ignoring negative ones (used for shared nodes)
     */
    public static long addListenedPropertiesToMask(List<String> listenedProperties, long mask, List<String> settableProperties) {
        if (listenedProperties == null) {
            return mask;
        }
        for (String propertyName : listenedProperties) {
            if (propertyName.equals("*")) {
                return Long.MAX_VALUE;
            }
            if (propertyName.startsWith("!")) {
                continue;
            }

            int pos = settableProperties.indexOf(propertyName);
            if (pos < 0) {
                throw new RuntimeException("Unknown property: " + propertyName);
            }
            mask = BitMaskUtil.set(mask, pos);
        }
        return mask;
    }

    public static List<String> getSettableProperties(InternalWorkingMemory workingMemory, ObjectTypeNode objectTypeNode) {
        return getSettableProperties((InternalRuleBase)workingMemory.getRuleBase(), objectTypeNode);
    }

    public static List<String> getSettableProperties(InternalRuleBase ruleBase, ObjectTypeNode objectTypeNode) {
        return getSettableProperties(ruleBase, getNodeClass(objectTypeNode));
    }

    public static List<String> getSettableProperties(InternalRuleBase ruleBase, Class<?> nodeClass) {
        if (nodeClass == null) {
            return null;
        }
        TypeDeclaration typeDeclaration = ruleBase.getTypeDeclaration(nodeClass);
        if (typeDeclaration == null) {
            return ClassUtils.getSettableProperties(nodeClass);
        }
        typeDeclaration.setTypeClass(nodeClass);
        return typeDeclaration.getSettableProperties();
    }

    public static Class<?> getNodeClass(ObjectTypeNode objectTypeNode) {
        if (objectTypeNode == null) {
            return null;
        }
        ObjectType objectType = objectTypeNode.getObjectType();
        return objectType != null && objectType instanceof ClassObjectType ? ((ClassObjectType)objectType).getClassType() : null;
    }
}
