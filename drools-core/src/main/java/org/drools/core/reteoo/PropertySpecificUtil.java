package org.drools.core.reteoo;

import org.drools.core.base.ClassObjectType;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.ObjectType;
import org.drools.core.util.BitMaskUtil;
import org.drools.core.util.ClassUtils;

import java.util.List;

public class PropertySpecificUtil {

    public static boolean isPropertyReactive(BuildContext context, ObjectType objectType) {
        return objectType instanceof ClassObjectType && isPropertyReactive(context, ((ClassObjectType) objectType).getClassType());
    }

    public static boolean isPropertyReactive(BuildContext context, Class<?> objectClass) {
        TypeDeclaration typeDeclaration = context.getKnowledgeBase().getTypeDeclaration( objectClass );
        return typeDeclaration != null && typeDeclaration.isPropertyReactive();
    }

    public static long calculatePositiveMask(List<String> listenedProperties, List<String> settableProperties) {
        return calculatePatternMask(listenedProperties, settableProperties, true);
    }

    public static long calculateNegativeMask(List<String> listenedProperties, List<String> settableProperties) {
        return calculatePatternMask(listenedProperties, settableProperties, false);
    }

    private static long calculatePatternMask(List<String> listenedProperties, List<String> settableProperties, boolean isPositive) {
        long mask = isPositive ? ( listenedProperties != null && listenedProperties.contains( TraitableBean.TRAITSET_FIELD_NAME ) ? Long.MIN_VALUE : 0 ) : 0;
        if (listenedProperties == null) {
            return mask;
        }
        for (String propertyName : listenedProperties) {
            if (propertyName.equals(isPositive ? "*" : "!*")) {
                return isPositive ? -1L : Long.MAX_VALUE;
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
        return getSettableProperties(workingMemory.getKnowledgeBase(), objectTypeNode);
    }

    public static List<String> getSettableProperties(InternalKnowledgeBase kBase, ObjectTypeNode objectTypeNode) {
        return getSettableProperties(kBase, getNodeClass(objectTypeNode));
    }

    public static List<String> getSettableProperties(InternalKnowledgeBase kBase, Class<?> nodeClass) {
        if (nodeClass == null) {
            return null;
        }
        TypeDeclaration typeDeclaration = kBase.getTypeDeclaration(nodeClass);
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
