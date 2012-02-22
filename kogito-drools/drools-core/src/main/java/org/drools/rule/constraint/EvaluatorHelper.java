package org.drools.rule.constraint;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.Declaration;
import org.mvel2.util.Soundex;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EvaluatorHelper {

    private EvaluatorHelper() { }

    public static Map<String, Object> valuesAsMap(Object object, InternalWorkingMemory workingMemory, LeftTuple leftTuple, Declaration[] declarations) {
        if (declarations.length == 0) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        for (Declaration declaration : declarations) {
            if (leftTuple == null) {
                map.put(declaration.getBindingName(), declaration.getExtractor().getValue(workingMemory, object));
            } else {
                InternalFactHandle fact = leftTuple.get(declaration);
                map.put(declaration.getBindingName(), declaration.getExtractor().getValue(workingMemory, fact != null ? fact.getObject() : object));
            }
        }
        return map;
    }

    public static Object[] valuesAsArray(Object object, InternalWorkingMemory workingMemory, LeftTuple leftTuple, Declaration[] declarations) {
        if (declarations.length == 0) {
            return null;
        }
        Object[] array = new Object[declarations.length];
        for (int i = 0; i < declarations.length; i++) {
            if (leftTuple == null) {
                array[i] = declarations[i].getExtractor().getValue(workingMemory, object);
            } else {
                InternalFactHandle fact = leftTuple.get(declarations[i]);
                array[i] = declarations[i].getExtractor().getValue(workingMemory, fact != null ? fact.getObject() : object);
            }
        }
        return array;
    }

    public static int arrayLenght(Object array) {
        if (array instanceof Object[]) {
            return ((Object[])array).length;
        } else if (array instanceof int[]) {
            return ((int[])array).length;
        } else if (array instanceof long[]) {
            return ((long[])array).length;
        } else if (array instanceof double[]) {
            return ((double[])array).length;
        } else if (array instanceof float[]) {
            return ((float[])array).length;
        } else if (array instanceof boolean[]) {
            return ((boolean[])array).length;
        } else if (array instanceof byte[]) {
            return ((byte[])array).length;
        } else if (array instanceof char[]) {
            return ((char[])array).length;
        } else if (array instanceof short[]) {
            return ((short[])array).length;
        }
        return 0;
    }

    public static boolean soundslike(String value1, String value2) {
        if (value1 == null || value2 == null) {
            return false;
        }
        String soundex1 = Soundex.soundex(value1);
        return soundex1 != null && soundex1.equals(Soundex.soundex(value2));
    }

    public static boolean contains(Object list, Object item) {
        if (list == null) return false;
        if (list instanceof Collection) {
            return ((Collection)list).contains(item);
        } else if (list instanceof Object[]) {
            for (Object i : (Object[])list) {
                if (i.equals(item)) return true;
            }
        } else if (list instanceof int[]) {
            return contains((int[]) list, ((Integer)item).intValue());
        } else if (list instanceof long[]) {
            return contains((long[]) list, ((Long)item).longValue());
        } else if (list instanceof double[]) {
            return contains((double[]) list, ((Double)item).doubleValue());
        } else if (list instanceof float[]) {
            return contains((float[]) list, ((Float)item).floatValue());
        } else if (list instanceof boolean[]) {
            return contains((boolean[]) list, ((Boolean)item).booleanValue());
        } else if (list instanceof byte[]) {
            return contains((byte[]) list, ((Byte)item).byteValue());
        } else if (list instanceof char[]) {
            return contains((char[]) list, ((Character)item).charValue());
        } else if (list instanceof short[]) {
            return contains((short[]) list, ((Short)item).shortValue());
        }
        return false;
    }

    public static boolean contains(Object list, boolean primitiveItem) {
        if (list instanceof Collection) {
            return ((Collection)list).contains(primitiveItem);
        }
        return contains((boolean[]) list, primitiveItem);
    }

    private static boolean contains(boolean[] list, boolean primitiveItem) {
        for (boolean i : list) {
            if (i == primitiveItem) return true;
        }
        return false;
    }

    public static boolean contains(Object list, int primitiveItem) {
        if (list instanceof Collection) {
            return ((Collection)list).contains(primitiveItem);
        }
        return contains((int[]) list, primitiveItem);
    }

    private static boolean contains(int[] list, int primitiveItem) {
        for (int i : list) {
            if (i == primitiveItem) return true;
        }
        return false;
    }

    public static boolean contains(Object list, long primitiveItem) {
        if (list instanceof Collection) {
            return ((Collection)list).contains(primitiveItem);
        }
        return contains((long[]) list, primitiveItem);
    }

    private static boolean contains(long[] list, long primitiveItem) {
        for (long i : list) {
            if (i == primitiveItem) return true;
        }
        return false;
    }

    public static boolean contains(Object list, double primitiveItem) {
        if (list instanceof Collection) {
            return ((Collection)list).contains(primitiveItem);
        }
        return contains((double[]) list, primitiveItem);
    }

    private static boolean contains(double[] list, double primitiveItem) {
        for (double i : list) {
            if (i == primitiveItem) return true;
        }
        return false;
    }

    public static boolean contains(Object list, float primitiveItem) {
        if (list instanceof Collection) {
            return ((Collection)list).contains(primitiveItem);
        }
        return contains((float[]) list, primitiveItem);
    }

    private static boolean contains(float[] list, float primitiveItem) {
        for (float i : list) {
            if (i == primitiveItem) return true;
        }
        return false;
    }

    public static boolean contains(Object list, byte primitiveItem) {
        if (list instanceof Collection) {
            return ((Collection)list).contains(primitiveItem);
        }
        return contains((byte[]) list, primitiveItem);
    }

    private static boolean contains(byte[] list, byte primitiveItem) {
        for (byte i : list) {
            if (i == primitiveItem) return true;
        }
        return false;
    }

    public static boolean contains(Object list, char primitiveItem) {
        if (list instanceof Collection) {
            return ((Collection)list).contains(primitiveItem);
        }
        return contains((char[]) list, primitiveItem);
    }

    private static boolean contains(char[] list, char primitiveItem) {
        for (char i : list) {
            if (i == primitiveItem) return true;
        }
        return false;
    }

    public static boolean contains(Object list, short primitiveItem) {
        if (list instanceof Collection) {
            return ((Collection)list).contains(primitiveItem);
        }
        return contains((short[]) list, primitiveItem);
    }

    private static boolean contains(short[] list, short primitiveItem) {
        for (short i : list) {
            if (i == primitiveItem) return true;
        }
        return false;
    }
}
