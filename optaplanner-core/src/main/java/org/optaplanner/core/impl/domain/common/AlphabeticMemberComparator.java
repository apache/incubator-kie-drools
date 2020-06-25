/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Comparator;

public class AlphabeticMemberComparator implements Comparator<Member> {

    @Override
    public int compare(Member a, Member b) {
        int compareTo = a.getName().compareTo(b.getName());
        if (compareTo != 0) {
            return compareTo;
        }
        if (a instanceof Method) {
            if (!(b instanceof Method)) {
                return 1;
            }
            int parameterNameCompareTo = compareParameterTypes(
                    ((Method) a).getParameterTypes(),
                    ((Method) b).getParameterTypes());
            if (parameterNameCompareTo != 0) {
                return parameterNameCompareTo;
            }
        } else if (b instanceof Method) {
            return -1;
        }
        if (a instanceof Constructor) {
            if (!(b instanceof Constructor)) {
                return 1;
            }
            int parameterNameCompareTo = compareParameterTypes(
                    ((Constructor) a).getParameterTypes(),
                    ((Constructor) b).getParameterTypes());
            if (parameterNameCompareTo != 0) {
                return parameterNameCompareTo;
            }
        } else if (b instanceof Constructor) {
            return -1;
        }
        return 0;
    }

    protected int compareParameterTypes(Class<?>[] aParameterTypes, Class<?>[] bParameterTypes) {
        if (aParameterTypes.length != bParameterTypes.length) {
            return aParameterTypes.length > bParameterTypes.length ? 1 : -1;
        }
        for (int i = 0; i < aParameterTypes.length; i++) {
            int parameterNameCompareTo = aParameterTypes[i].getName().compareTo(bParameterTypes[i].getName());
            if (parameterNameCompareTo != 0) {
                return parameterNameCompareTo;
            }
        }
        return 0;
    }

}
