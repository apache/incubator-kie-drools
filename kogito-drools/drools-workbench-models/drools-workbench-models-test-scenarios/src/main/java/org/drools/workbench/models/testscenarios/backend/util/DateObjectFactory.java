package org.drools.workbench.models.testscenarios.backend.util;

import org.drools.core.util.DateUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

public class DateObjectFactory {

    public static Date createTimeObject(Class<?> fieldClass,
                                        String value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Class<?> parameterTypes[] = new Class[1];
        parameterTypes[0] = Long.TYPE;
        Constructor<?> constructor
                = fieldClass.getConstructor(parameterTypes);
        Object args[] = new Object[1];
        args[0] = getTimeAsLong(value);
        return (Date) constructor.newInstance(args);
    }

    private static long getTimeAsLong(String value) {
        return DateUtils.parseDate(value, null).getTime();
    }

}
