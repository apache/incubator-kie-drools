package org.drools.core.reteoo;

import org.drools.core.base.ClassObjectType;
import org.drools.core.common.InternalRuleBase;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.util.BitMaskUtil;
import org.drools.core.util.ClassUtils;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.ObjectType;

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
            // at this point the property is valid (a getter exists).
            // The corresponding setter may not, but the property might be @modified anyway
            // so we ignore properties that are not settable rather than throwing an exception
            if (pos >= 0) {
                mask = BitMaskUtil.set(mask, pos);
            }
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
