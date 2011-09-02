package org.drools.rule.builder.dialect.asm;

import java.util.*;

import static org.mvel2.asm.Type.getDescriptor;

public final class ASMUtil {

    private static final Map<String, String> typesMap = new HashMap<String, String>();

    static {
        typesMap.put("boolean", "Z");
        typesMap.put("char", "C");
        typesMap.put("byte", "B");
        typesMap.put("short", "S");
        typesMap.put("int", "I");
        typesMap.put("float", "F");
        typesMap.put("long", "J");
        typesMap.put("double", "D");
    }

    public static String mDescr(Class<?> type, Class<?>... args) {
        StringBuilder desc = new StringBuilder("(");
        if (args != null) for (Class<?> arg : args) desc.append(getDescriptor(arg));
        desc.append(")").append(type == null ? "V" : getDescriptor(type));
        return desc.toString();
    }

    public static String toInteralName(String type) {
        return toAsmType(type, true);
    }

    public static String toTypeDescriptor(String type) {
        return toAsmType(type, false);
    }

    private static String toAsmType(String type, boolean asInternalName) {
        String arrayPrefix = "";
        while (type.endsWith("[]")) {
            arrayPrefix += "[";
            type = type.substring(0, type.length()-2);
        }
        return arrayPrefix + toSimpleDescriptor(type, asInternalName && arrayPrefix.length() == 0);
    }

    private static final String toSimpleDescriptor(String type, boolean internal) {
        String desc = typesMap.get(type);
        if (desc != null) return desc;
        return internal ? type.replace('.', '/') : ("L" + type.replace('.', '/') + ";");
    }
}
