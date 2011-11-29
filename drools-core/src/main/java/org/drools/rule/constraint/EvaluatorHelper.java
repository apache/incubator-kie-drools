package org.drools.rule.constraint;

import java.util.Collection;

public class EvaluatorHelper {

    private EvaluatorHelper() { }

    public static boolean contains(Object list, Object item) {
        if (list == null) return false;
        if (list instanceof Collection) {
            return ((Collection)list).contains(item);
        } else {
            Object[] array = (Object[])list;
            for (Object i : array) {
                if (i.equals(item)) return true;
            }
        }
        return false;
    }
}
