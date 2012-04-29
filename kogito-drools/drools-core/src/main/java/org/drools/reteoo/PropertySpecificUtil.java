package org.drools.reteoo;

import org.drools.base.ClassObjectType;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.core.util.BitMaskUtil;
import org.drools.core.util.ClassUtils;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.TypeDeclaration;
import org.drools.spi.ObjectType;

import java.util.List;

public class PropertySpecificUtil {

    public static boolean isPropertyReactive(BuildContext context, ObjectType objectType) {
        return objectType instanceof ClassObjectType && isPropertyReactive(context, ((ClassObjectType) objectType).getClassType());
    }

    public static boolean isPropertyReactive(BuildContext context, Class<?> objectClass) {
        TypeDeclaration typeDeclaration = context.getRuleBase().getTypeDeclaration( objectClass );
        return typeDeclaration != null && typeDeclaration.isPropertyReactive();
    }

    public static long calculatePositiveMask(List<String> listenedProperties, List<String> settableProperties) {
        return calculatePatternMask(listenedProperties, settableProperties, true);
    }

    public static long calculateNegativeMask(List<String> listenedProperties, List<String> settableProperties) {
        return calculatePatternMask(listenedProperties, settableProperties, false);
    }

    private static long calculatePatternMask(List<String> listenedProperties, List<String> settableProperties, boolean isPositive) {
        long mask = 0L;
        if (listenedProperties == null) {
            return mask;
        }
        for (String propertyName : listenedProperties) {
            if (propertyName.equals(isPositive ? "*" : "!*")) {
                return Long.MAX_VALUE;
            }
            if (propertyName.startsWith("!") ^ !isPositive) {
                continue;
            }
            if (!isPositive) {
                propertyName = propertyName.substring(1);
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
