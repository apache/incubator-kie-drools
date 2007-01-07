package org.mvel.util;


import static java.lang.String.valueOf;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import static java.lang.reflect.Modifier.PUBLIC;
import static java.lang.reflect.Modifier.isPublic;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.compile;

public class PropertyTools {
    private static final Pattern truePattern = compile("(on|yes|true|1|hi|high|y)");
    public static final Pattern ognlStatement = compile("[{}\\[\\]'\"]");

    public static Object[] arrayWrap(Object o) {
        if (o == null) return null;
        if (o.getClass().isArray()) return (Object[]) o;
        return new Object[]{o};
    }

    public static Object arrayUnwrap(Object o) {
        if (o == null) return null;
        if (o.getClass().isArray()) {
            return ((Object[]) o)[0];
        }
        return o;
    }

    /**
     * Converts a standard wildcard string to a compiled regular expression.
     *
     * @param wildcard - a wildcard expression.
     * @return compiled expr.
     */
    public static Pattern wildcardToRegex(String wildcard) {
        return compile(wildcard
                .replaceAll("(\\*|%)", ".+")
                .replaceAll("\\.", "\\.")
                .replaceAll("\\?", "."));
    }

    /**
     * Queries a map based on the specified wildcard and returns a Map containing
     * only matching elements.
     *
     * @param wildcard a wildcard expression
     * @param map      a map
     * @return Map
     */
    public static <K, V> Map<K, V> mapQuery(String wildcard, Map<K, V> map) {
        Pattern qPattern = wildcardToRegex(wildcard);

        if (map == null) return null;
        Map<K, V> newMap = new HashMap<K, V>(map.size() * 2);

        for (K name : map.keySet()) {
            if (qPattern.matcher(valueOf(name)).find())
                newMap.put(name, map.get(name));
        }

        return newMap;
    }

    public static boolean isEmptyOrWhitespace(Object o) {
        return (o == null || isEmpty(String.valueOf(o).trim()));
    }

    public static boolean isEmpty(Object o) {
        if (o != null) {
            if (o instanceof Object[]) {
                return ((Object[]) o).length == 0 ||
                        (((Object[]) o).length == 1 && isEmpty(((Object[]) o)[0]));
            }
            else {
                return ("".equals(valueOf(o)))
                        || "null".equals(valueOf(o))
                        || (o instanceof Collection && ((Collection) o).size() == 0)
                        || (o instanceof Map && ((Map) o).size() == 0);
            }
        }
        return true;
    }

    public static boolean valueInArray(Object[] array, Object value) {
        if (array.length == 0) return false;
        for (Object aArray : array) {
            if (aArray.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean safeEquals(Object one, Object two) {
        return !((one == null) ^ (two == null)) && (one == null || one.equals(two));
    }

    public static boolean parseBoolean(Object value) {
        return truePattern.matcher(valueOf(value).toLowerCase()).matches();
    }

    public static Method getSetter(Class clazz, String property) {
        String setter = ReflectionUtil.getSetter(property);

        for (Method meth : clazz.getDeclaredMethods()) {
            if ((meth.getModifiers() & PUBLIC) == 0
                    && meth.getParameterTypes().length != 0) continue;

            if (setter.equals(meth.getName())) {
                return meth;
            }
        }

        return null;

    }

    public static boolean hasGetter(Field field) {
        Method meth = getGetter(field.getDeclaringClass(), field.getName());
        return meth != null && field.getType().isAssignableFrom(meth.getReturnType());
    }

    public static boolean hasSetter(Field field) {
        Method meth = getSetter(field.getDeclaringClass(), field.getName());
        return meth != null && meth.getParameterTypes().length == 1 &&
                field.getType().isAssignableFrom(meth.getParameterTypes()[0]);
    }

    public static Method getGetter(Class clazz, String property) {
        String get = ReflectionUtil.getGetter(property);
        String isGet = ReflectionUtil.getIsGetter(property);

        for (Method meth : clazz.getDeclaredMethods()) {
            if ((meth.getModifiers() & PUBLIC) == 0
                    || meth.getParameterTypes().length != 0
                    ) {
            }
            else if (get.equals(meth.getName()) ||
                    isGet.equals(meth.getName())) {
                return meth;
            }
        }

        return null;
    }

    public static boolean isPropertyReadAndWrite(Field field) {
        return isPublic(field.getModifiers()) || hasGetter(field) && hasSetter(field);
    }

    public static boolean isPropertyReadAndWrite(Class clazz, String property) {
        return getWritableFieldOrAccessor(clazz, property) != null &&
                getFieldOrAccessor(clazz, property) != null;
    }

    public static Member getWritableFieldOrAccessor(Class clazz, String property) {
        Field field;
        try {
            if ((field = clazz.getField(property)) != null &&
                    isPublic(field.getModifiers())) return field;
        }
        catch (NullPointerException e) {
            return null;
        }
        catch (NoSuchFieldException e) {
            // do nothing.
        }

        return getSetter(clazz, property);
    }

    public static Member getFieldOrAccessor(Class clazz, String property) {
        if (property.charAt(property.length() - 1) == ')') return getGetter(clazz, property);

        try {
            Field fld = clazz.getField(property);

            if ((fld.getModifiers() & PUBLIC) != 0) return fld;
        }
        catch (Exception e) {
            // do nothing.
        }
        return getGetter(clazz, property);
    }

    public static Member getFieldOrWriteAccessor(Class clazz, String property) {
        Field field;
        try {
            if ((field = clazz.getField(property)) != null &&
                    isPublic(field.getModifiers())) {
                return field;
            }
        }
        catch (NullPointerException e) {
            return null;
        }
        catch (NoSuchFieldException e) {
            // do nothing.
        }

        return getSetter(clazz, property);
    }

  
    public static boolean isNumeric(Object val) {
        if (val == null) return false;

        Class clz;
        if (val instanceof Class) {
            clz = (Class) val;
        }
        else {
            clz = val.getClass();
        }

        return clz == int.class || clz == long.class || clz == short.class || clz == double.class ||
                clz == float.class || Number.class.isAssignableFrom(clz);

    }

    public static boolean isNumber(char[] val) {
        int len = val.length;
        char c;
        int i = 0;
        if (len > 1) {
            if (val[0] == '-') i++;
            else if (val[0] == '~') {
                i++;
                if (val[1] == '-') i++;
            }
        }
        for (; i < len; i++) {
            if (!isDigit(c = val[i]) && c != '.') return false;
        }

        return len > 0;      
    }


    public static boolean isNumber(Object val) {
        if (val == null) return false;
        if (val instanceof String) return isNumber((String) val);
        if (val instanceof char[]) return isNumber((char[]) val);
        return val instanceof Integer || val instanceof BigDecimal
                || val instanceof Float || val instanceof Double || val instanceof Long
                || val instanceof Short;
    }
   
    public static boolean isNumber(final String val) {
        int len = val.length();
        char[] a = val.toCharArray();
        char c;
        int i = 0;
        if (len > 1) {
            if (a[0] == '-') i++;
            else if (a[0] == '~') {
                i++;
                if (a[1] == '-') i++;
            }
        }
        for (; i < len; i++) {
            if (!isDigit(c = a[i]) && c != '.') return false;
        }

        return len > 0;
    }

    public static boolean contains(Object toCompare, Object testValue) {
        if (toCompare == null)
            return false;
        else if (toCompare instanceof String)
            return ((String) toCompare).contains(valueOf(testValue));
        else if (toCompare instanceof Collection)
            return ((Collection) toCompare).contains(testValue);
        else if (toCompare instanceof Map)
            return ((Map) toCompare).containsKey(testValue);
        else if (toCompare.getClass().isArray()) {
            for (Object o : ((Object[]) toCompare)) {
                if (testValue == null && o == null) return true;
                else if (o != null && o.equals(testValue)) return true;
            }
        }
        return false;
    }

    public static int find(char[] c, char find) {
        for (int i = 0; i < c.length; i++) if (c[i] == find) return i;
        return -1;
    }

    public static boolean equals(char[] obj1, String obj2) {
        for (int i = 0; i < obj1.length && i < obj2.length(); i++) {
            if (obj1[i] == obj2.charAt(i)) return false;
        }
        return true;
    }


    public static boolean isIdentifierPart(final int c) {
        return ((c >= 97 && c <= 122)
                || (c >= 65 && c <= 90) || (c >= 48 && c <= 57) || (c == '_') || (c == '$'));
    }

    public static boolean isDigit(final int c) {
        return c >= '0' && c <= '9';
    }


    public static float similarity(String s1, String s2) {
        if (s1 == null || s2 == null)
            return s1 == null && s2 == null ? 1f : 0f;

        char[] c1 = s1.toCharArray();
        char[] c2 = s2.toCharArray();
        
        char[] comp;
        char[] against;

        float same = 0;
        float baselength;

        int cur1 = 0;
        //      int cur2 = 0;

        if (c1.length > c2.length) {
            baselength = c1.length;
            comp = c1;
            against = c2;
        }
        else {
            baselength = c2.length;
            comp = c2;
            against = c1;
        }


        while (cur1 < comp.length && cur1 < against.length) {
            if (comp[cur1] == against[cur1]) {
                same++;
            }

            cur1++;
        }

        if (c1.length != c2.length) {
            cur1--;
            int offset = c2.length - c1.length;

            while (cur1 > 0) {
                if (comp[cur1] == against[cur1 - offset]) {
                    same++;
                }

                cur1--;
            }
        }

        return same / baselength;
    }

}
