package org.drools.rule.constraint;

import java.util.Collection;

public class EvaluatorHelper {

    private EvaluatorHelper() { }

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
        for (boolean i : (boolean[])list) {
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
        for (int i : (int[])list) {
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
        for (long i : (long[])list) {
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
        for (double i : (double[])list) {
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
        for (float i : (float[])list) {
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
        for (byte i : (byte[])list) {
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
        for (char i : (char[])list) {
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
        for (short i : (short[])list) {
            if (i == primitiveItem) return true;
        }
        return false;
    }
}
