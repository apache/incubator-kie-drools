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
