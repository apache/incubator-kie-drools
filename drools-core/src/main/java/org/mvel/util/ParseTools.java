package org.mvel.util;

import static org.mvel.DataConversion.canConvert;
import static org.mvel.DataConversion.convert;
import static org.mvel.ExpressionParser.eval;
import org.mvel.Token;
import org.mvel.CompileException;
import org.mvel.ParseException;
import org.mvel.integration.VariableResolverFactory;

import static java.lang.Character.isWhitespace;
import static java.lang.Class.forName;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ParseTools {
    public static String[] parseMethodOrConstructor(char[] parm) {
        int start = -1;
        for (int i = 0; i < parm.length; i++) {
            if (parm[i] == '(') {
                start = ++i;
                break;
            }
        }
        if (start != -1) {
            for (int i = parm.length - 1; i > 0; i--) {
                if (parm[i] == ')') {
                    return parseParameterList(parm, start, i - start);
                }
            }
        }

        return null;
    }

    public static String[] parseParameterList(char[] parm, int offset, int length) {
        List<String> list = new LinkedList<String>();

        if (length == -1) length = parm.length;

        int adepth = 0;
        int start = offset;
        int i = offset;
        int end = i + length;

        for (; i < end; i++) {
            switch (parm[i]) {
                case'[':
                case'{':
                    if (adepth++ == 0) start = i;
                    continue;

                case']':
                case'}':
                    if (--adepth == 0) {
                        list.add(new String(parm, start, i - start + 1));

                        while (isWhitespace(parm[i])) i++;

                        start = i + 1;
                    }
                    continue;

                case',':
                    if (adepth != 0) continue;

                    if (i > start) {
                        while (isWhitespace(parm[start])) start++;

                        list.add(new String(parm, start, i - start));
                    }

                    while (isWhitespace(parm[i])) i++;

                    start = i + 1;
            }
        }

        if (start < length && i > start) {
            String s = new String(parm, start, i - start).trim();
            if (s.length() > 0)
                list.add(s);
        }
        else if (list.size() == 0) {
            String s = new String(parm, start, length).trim();
            if (s.length() > 0)
                list.add(s);
        }

        return list.toArray(new String[list.size()]);
    }

    private static Map<String, Map<Integer, Method>> RESOLVED_METH_CACHE =
            new WeakHashMap<String, Map<Integer, Method>>(10);

    public static Method getBestCanadidate(Object[] arguments, String method, Method[] methods) {
        Class[] parmTypes;
        Method bestCandidate = null;
        int bestScore = 0;
        int score = 0;

        Class[] targetParms = new Class[arguments.length];

        for (int i = 0; i < arguments.length; i++)
            targetParms[i] = arguments[i] != null ? arguments[i].getClass() :  Object.class;

        Integer hash = createClassSignatureHash(targetParms);

        if (RESOLVED_METH_CACHE.containsKey(method) && RESOLVED_METH_CACHE.get(method).containsKey(hash))
            return RESOLVED_METH_CACHE.get(method).get(hash);

        for (Method meth : methods) {
            if (method.equals(meth.getName())) {
                if ((parmTypes = meth.getParameterTypes()).length != arguments.length) continue;
                else if (arguments.length == 0 && parmTypes.length == 0) return meth;

                for (int i = 0; i < arguments.length; i++) {
                    if (parmTypes[i].isPrimitive() && boxPrimitive(parmTypes[i]) == targetParms[i]) score += 3;
                    else if (parmTypes[i] == targetParms[i]) score += 4;
                    else if (parmTypes[i].isAssignableFrom(targetParms[i])) score += 2;
                    else if (canConvert(parmTypes[i], targetParms[i])) score += 1;
                    else {
                        score = 0;
                        break;
                    }
                }

                if (score != 0 && score > bestScore) {
                    bestCandidate = meth;
                    bestScore = score;
                }
                score = 0;
            }
        }


        if (bestCandidate != null) {
            if (!RESOLVED_METH_CACHE.containsKey(method))
                RESOLVED_METH_CACHE.put(method, new WeakHashMap<Integer, Method>());

            RESOLVED_METH_CACHE.get(method).put(hash, bestCandidate);
        }

        return bestCandidate;
    }

    private static Map<Class, Map<Integer, Constructor>> RESOLVED_CONST_CACHE = new WeakHashMap<Class, Map<Integer, Constructor>>(10);
    private static Map<Constructor, Class[]> CONSTRUCTOR_PARMS_CACHE = new WeakHashMap<Constructor, Class[]>(10);


    private static Class[] getConstructors(Constructor cns) {
        if (CONSTRUCTOR_PARMS_CACHE.containsKey(cns))
            return CONSTRUCTOR_PARMS_CACHE.get(cns);
        else {
            Class[] c = cns.getParameterTypes();
            CONSTRUCTOR_PARMS_CACHE.put(cns, c);
            return c;
        }
    }

    public static Constructor getBestConstructorCanadidate(Object[] arguments, Class cls) {
        Class[] parmTypes;
        Constructor bestCandidate = null;
        int bestScore = 0;
        int score = 0;

        Class[] targetParms = new Class[arguments.length];

        for (int i = 0; i < arguments.length; i++)
            targetParms[i] = arguments[i] != null ? arguments[i].getClass() : Object.class;

        Integer hash = createClassSignatureHash(targetParms);

        if (RESOLVED_CONST_CACHE.containsKey(cls) && RESOLVED_CONST_CACHE.get(cls).containsKey(hash))
            return RESOLVED_CONST_CACHE.get(cls).get(hash);

        for (Constructor construct : getConstructors(cls)) {
            if ((parmTypes = getConstructors(construct)).length != arguments.length) continue;
            else if (arguments.length == 0 && parmTypes.length == 0) return construct;

            for (int i = 0; i < arguments.length; i++) {
                if (parmTypes[i].isPrimitive() && boxPrimitive(parmTypes[i]) == targetParms[i]) score += 3;
                else if (parmTypes[i] == targetParms[i]) score += 4;
                else if (parmTypes[i].isAssignableFrom(targetParms[i])) score += 2;
                else if (canConvert(parmTypes[i], targetParms[i])) score += 1;
                else {
                    score = 0;
                    break;
                }
            }

            if (score != 0 && score > bestScore) {
                bestCandidate = construct;
                bestScore = score;
            }
            score = 0;

        }


        if (bestCandidate != null) {
            if (!RESOLVED_CONST_CACHE.containsKey(cls))
                RESOLVED_CONST_CACHE.put(cls, new WeakHashMap<Integer, Constructor>());

            RESOLVED_CONST_CACHE.get(cls).put(hash, bestCandidate);
        }

        return bestCandidate;
    }

    private static Map<String, Class> CLASS_RESOLVER_CACHE = new WeakHashMap<String, Class>(10);
    private static Map<Class, Constructor[]> CLASS_CONSTRUCTOR_CACHE = new WeakHashMap<Class, Constructor[]>(10);

    private static Class createClass(String className) throws ClassNotFoundException {
        if (CLASS_RESOLVER_CACHE.containsKey(className))
            return CLASS_RESOLVER_CACHE.get(className);
        else {
            Class cls = Class.forName(className);
            CLASS_RESOLVER_CACHE.put(className, cls);
            return cls;
        }
    }

    private static Constructor[] getConstructors(Class cls) {
        if (CLASS_CONSTRUCTOR_CACHE.containsKey(cls))
            return CLASS_CONSTRUCTOR_CACHE.get(cls);
        else {
            Constructor[] cns = cls.getConstructors();
            CLASS_CONSTRUCTOR_CACHE.put(cls, cns);
            return cns;
        }
    }

    public static Object constructObject(String expression, Object ctx, VariableResolverFactory vrf)
            throws InstantiationException, IllegalAccessException, InvocationTargetException,
            ClassNotFoundException {

        String[] constructorParms = parseMethodOrConstructor(expression.toCharArray());

        if (constructorParms != null) {
            Class cls = Token.LITERALS.containsKey(expression = expression.substring(0, expression.indexOf('('))) ?
                    ((Class) Token.LITERALS.get(expression)) : createClass(expression);

            Object[] parms = new Object[constructorParms.length];
            for (int i = 0; i < constructorParms.length; i++) {
                parms[i] = (eval(constructorParms[i], ctx, vrf));
            }

            Constructor cns = getBestConstructorCanadidate(parms, cls);

            if (cns == null)
                throw new CompileException("unable to find constructor for: " + cls.getName());

            for (int i = 0; i < parms.length; i++) {
                //noinspection unchecked
                parms[i] = convert(parms[i], cns.getParameterTypes()[i]);
            }

            return cns.newInstance(parms);
        }
        else {
            return forName(expression).newInstance();
        }
    }

    public static String[] captureContructorAndResidual(String token) {
        char[] cs = token.toCharArray();

        int depth = 0;

        for (int i = 0; i < cs.length; i++) {
            switch (cs[i]) {
                case'(':
                    depth++;
                    continue;
                case')':
                    if (1 == depth--) {
                        return new String[]{new String(cs, 0, ++i), new String(cs, i, cs.length - i)};
                    }
            }
        }
        return new String[]{token};
    }

    public static Class boxPrimitive(Class cls) {
        if (cls == int.class) {
            return Integer.class;
        }
        else if (cls == int[].class) {
            return Integer[].class;
        }
        else if (cls == long.class) {
            return Long.class;
        }
        else if (cls == long[].class) {
            return Long[].class;
        }
        else if (cls == short.class) {
            return Short.class;
        }
        else if (cls == short[].class) {
            return Short[].class;
        }
        else if (cls == double.class) {
            return Double.class;
        }
        else if (cls == double[].class) {
            return Double[].class;
        }
        else if (cls == float.class) {
            return Float.class;
        }
        else if (cls == float[].class) {
            return Float[].class;
        }
        else if (cls == boolean.class) {
            return Boolean.class;
        }
        else if (cls == boolean[].class) {
            return Boolean[].class;
        }
        else if (cls == byte.class) {
            return Byte.class;
        }
        else if (cls == byte[].class) {
            return Byte[].class;
        }

        return null;
    }

    public static boolean containsCheck(Object compareTo, Object compareTest) {
        if (compareTo == null)
            return false;
        else if (compareTo instanceof String)
            return ((String) compareTo).contains(String.valueOf(compareTest));
        else if (compareTo instanceof Collection)
            return ((Collection) compareTo).contains(compareTest);
        else if (compareTo instanceof Map)
            return ((Map) compareTo).containsKey(compareTest);
        else if (compareTo.getClass().isArray()) {
            for (Object o : ((Object[]) compareTo)) {
                if (compareTest == null && o == null) return true;
                else if (o != null && o.equals(compareTest)) return true;
            }
        }
        return false;
    }

    public static int createClassSignatureHash(Class[] sig) {
        int hash = 0;
        for (Class cls : sig) {
            if (cls != null) hash += cls.hashCode();
        }
        return hash + sig.length;
    }

    public static char handleEscapeSequence(char escapedChar) {
        switch (escapedChar) {
            case'\\':
                return '\\';
            case't':
                return '\t';
            case'r':
                return '\r';
            case'\'':
                return '\'';
            case'"':
                return '"';
            default:
                throw new ParseException("illegal escape sequence: " + escapedChar);
        }
    }

    public static void main(String[] args) {
        for (Package p : Package.getPackages()) {
            System.out.println(p);
        }
    }

    public static boolean debug(String str) {
        System.out.println(str);
        return true;
    }
}
