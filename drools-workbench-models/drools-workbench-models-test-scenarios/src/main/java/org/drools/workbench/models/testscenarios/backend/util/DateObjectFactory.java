/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
