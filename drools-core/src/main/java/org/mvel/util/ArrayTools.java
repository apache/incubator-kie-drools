package org.mvel.util;


import static java.lang.reflect.Array.newInstance;
import java.util.*;

public class ArrayTools {
    public static int[] intTranspose(Object[] a) {
        int[] b = new int[a.length];
        for (int i = a.length - 1; i >= 0; i--) {
            b[i] = Integer.parseInt(String.valueOf(a[i]));
        }
        return b;
    }

    public static long[] longTranspose(Object[] a) {
        long[] b = new long[a.length];
        for (int i = a.length - 1; i >= 0; i--) {
            b[i] = Long.parseLong(String.valueOf(a[i]));
        }
        return b;
    }

    public static char[] charTranspose(Object[] a) {
        char[] b = new char[a.length];
        for (int i = a.length - 1; i >= 0; i--) {
            b[i] = String.valueOf(a[i]).charAt(0);
        }
        return b;
    }

    public static float[] floatTranspose(Object[] a) {
        float[] b = new float[a.length];
        for (int i = a.length - 1; i >= 0; i--) {
            b[i] = Float.parseFloat(String.valueOf(a[i]));
        }
        return b;
    }

    public static boolean[] booleanTranspose(Object[] a) {
        boolean[] b = new boolean[a.length];
        for (int i = a.length - 1; i >= 0; i--) {
            b[i] = PropertyTools.parseBoolean(String.valueOf(a[i]));
        }
        return b;
    }

    public static short[] shortTranspose(Object[] a) {
        short[] b = new short[a.length];
        for (int i = a.length - 1; i >= 0; i--) {
            b[i] = Short.parseShort(String.valueOf(a[i]));
        }

        return b;
    }

    public static String[] stringTranspose(Object[] a) {
        String[] b = new String[a.length];
        for (int i = a.length - 1; i >= 0; i--) {
            b[i] = String.valueOf(a[i]);
        }
        return b;
    }

    public static String[] stringTranspose(int[] a) {
        String[] b = new String[a.length];
        for (int i = a.length - 1; i >= 0; i--) {
            b[i] = String.valueOf(a[i]);
        }
        return b;
    }

    public static String[] stringTranspose(long[] a) {
        String[] b = new String[a.length];
        for (int i = a.length - 1; i >= 0; i--) {
            b[i] = String.valueOf(a[i]);
        }
        return b;
    }

    public static String[] stringTranspose(short[] a) {
        String[] b = new String[a.length];
        for (int i = a.length - 1; i >= 0; i--) {
            b[i] = String.valueOf(a[i]);
        }
        return b;
    }

    public static String[] stringTranspose(boolean[] a) {
        String[] b = new String[a.length];
        for (int i = a.length - 1; i >= 0; i--) {
            b[i] = String.valueOf(a[i]);
        }
        return b;
    }

    public static String[] stringTranspose(double[] a) {
        String[] b = new String[a.length];
        for (int i = a.length - 1; i >= 0; i--) {
            b[i] = String.valueOf(a[i]);
        }
        return b;
    }

    public static String[] stringTranspose(char[] a) {
        String[] b = new String[a.length];
        for (int i = a.length - 1; i >= 0; i--) {
            b[i] = String.valueOf(a[i]);
        }
        return b;
    }

    public static String[] stringTranspose(Collection col) {
        String[] b = new String[col.size()];
        int i = 0;
        for (Object o : col) {
            b[i++] = String.valueOf(o);
        }
        return b;
    }

    public static List listTranspose(int[] a) {
        List b = new ArrayList(a.length);
        for (int i = a.length - 1; i >= 0; i--) {
            b.add(i, a[i]);
        }
        return b;
    }

    public static List listTranspose(char[] a) {
        List b = new ArrayList(a.length);
        for (int i = a.length - 1; i >= 0; i--) {
            b.add(i, a[i]);
        }
        return b;
    }

    public static List listTranspose(long[] a) {
        List b = new ArrayList(a.length);
        for (int i = a.length - 1; i >= 0; i--) {
            b.add(i, a[i]);
        }
        return b;
    }

    public static List listTranspose(boolean[] a) {
        List b = new ArrayList(a.length);
        for (int i = a.length - 1; i >= 0; i--) {
            b.add(i, a[i]);
        }
        return b;
    }

    public static List listTranspose(short[] a) {
        List b = new ArrayList(a.length);
        for (int i = a.length - 1; i >= 0; i--) {
            b.add(i, a[i]);
        }
        return b;
    }

    public static List listTranspose(double[] a) {
        List b = new ArrayList(a.length);
        for (int i = a.length - 1; i >= 0; i--) {
            b.add(i, a[i]);
        }
        return b;
    }

    public static List listTranspose(String[] a) {
        if (a == null) return new ArrayList(0);

        List b = new ArrayList(a.length);
        for (int i = a.length - 1; i >= 0; i--) {
            b.add(i, a[i]);
        }
        return b;
    }

    public static Set setTranspose(String[] a) {
        if (a == null) return new HashSet(0);

        Set b = new HashSet(a.length * 2);
        for (String c : a) {
            b.add(c);
        }
        return b;
    }

    public static <T> T[] collectionTranspose(Collection col, Class<T> type) {
        //noinspection unchecked
        T[] a = (T[]) newInstance(type, col.size());

        int i = 0;
        for (Object item : col) {
            a[i++] = (T) item;
        }

        return a;
    }

    public static int findFirst(char c, char[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == c) return i;
        }
        return -1;
    }
}
