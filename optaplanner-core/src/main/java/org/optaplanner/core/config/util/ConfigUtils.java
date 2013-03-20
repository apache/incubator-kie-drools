/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.config.util;

import java.util.ArrayList;
import java.util.List;

public class ConfigUtils {

    public static <T> T newInstance(Object bean, String propertyName, Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("The " + bean.getClass().getSimpleName() + "'s " + propertyName + " ("
                    + clazz.getName() + ") does not have a public no-arg constructor", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("The " + bean.getClass().getSimpleName() + "'s " + propertyName + " ("
                    + clazz.getName() + ") does not have a public no-arg constructor", e);
        }
    }

    public static <T> T inheritOverwritableProperty(T original, T inherited) {
        if (original != null) {
            // Original overwrites inherited
            return original;
        } else {
            return inherited;
        }
    }

    public static <T> List<T> inheritMergeableListProperty(List<T> originalList, List<T> inheritedList) {
        if (inheritedList == null) {
            return originalList;
        } else if (originalList == null) {
            return inheritedList;
        } else {
            // The inheritedList should be before the originalList
            List<T> mergedList = new ArrayList<T>(inheritedList);
            mergedList.addAll(originalList);
            return mergedList;
        }
    }

    private ConfigUtils() {
    }

}
