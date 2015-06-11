/*
 * Copied from the Hibernate Validator project
 * Original authors: Hardy Ferentschik, Gunnar Morling and Kevin Pollet.
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
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Map;

public final class ReflectionHelper {

    private static final String PROPERTY_ACCESSOR_PREFIX_GET = "get";
    private static final String PROPERTY_ACCESSOR_PREFIX_IS = "is";
    private static final String PROPERTY_ACCESSOR_PREFIX_HAS = "has";
    private static final String[] PROPERTY_ACCESSOR_PREFIXES = {
            PROPERTY_ACCESSOR_PREFIX_GET,
            PROPERTY_ACCESSOR_PREFIX_IS,
            PROPERTY_ACCESSOR_PREFIX_HAS
    };

    /**
     * Private constructor in order to avoid instantiation.
     */
    private ReflectionHelper() {
    }

    /**
     * Returns the JavaBeans property name of the given member.
     * <p>
     * For fields, the field name will be returned. For getter methods, the
     * decapitalized property name will be returned, with the "get", "is" or "has"
     * prefix stripped off. Getter methods are methods
     * </p>
     * <ul>
     * <li>whose name start with "get" and who have a return type but no parameter
     * or</li>
     * <li>whose name starts with "is" and who have no parameter and return
     * {@code boolean} or</li>
     * <li>whose name starts with "has" and who have no parameter and return
     * {@code boolean} (HV-specific, not mandated by JavaBeans spec).</li>
     * </ul>
     *
     * @param member The member for which to get the property name.
     *
     * @return The property name for the given member or {@code null} if the
     *         member is neither a field nor a getter method according to the
     *         JavaBeans standard.
     */
    public static String getPropertyName(Member member) {
        String name = null;

        if ( member instanceof Field ) {
            name = member.getName();
        }

        if ( member instanceof Method ) {
            String methodName = member.getName();
            for ( String prefix : PROPERTY_ACCESSOR_PREFIXES ) {
                if ( methodName.startsWith( prefix ) ) {
                    name = decapitalize( methodName.substring( prefix.length() ) );
                }
            }
        }
        return name;
    }

    /**
     * Returns the given string, with its first letter changed to lower-case unless the string starts with more than
     * one upper-case letter, in which case the string will be returned unaltered.
     * <p>
     * Provided to avoid a dependency on the {@link java.beans.Introspector} API which is not available on the Android
     * platform (HV-779).
     *
     * @param string the string to decapitalize
     *
     * @return the given string, decapitalized. {@code null} is returned if {@code null} is passed as input; An empty
     *         string is returned if an empty string is passed as input
     *
     * @see java.beans.Introspector#decapitalize(String)
     */
    public static String decapitalize(String string) {
        if ( string == null || string.isEmpty() || startsWithSeveralUpperCaseLetters( string ) ) {
            return string;
        }
        else {
            return string.substring( 0, 1 ).toLowerCase() + string.substring( 1 );
        }
    }

    private static boolean startsWithSeveralUpperCaseLetters(String string) {
        return string.length() > 1 &&
                Character.isUpperCase( string.charAt( 0 ) ) &&
                Character.isUpperCase( string.charAt( 1 ) );
    }

    /**
     * Checks whether the given method is a valid JavaBeans getter method, which
     * is the case if
     * <ul>
     * <li>its name starts with "get" and it has a return type but no parameter or</li>
     * <li>its name starts with "is", it has no parameter and is returning
     * {@code boolean} or</li>
     * <li>its name starts with "has", it has no parameter and is returning
     * {@code boolean} (HV-specific, not mandated by JavaBeans spec).</li>
     * </ul>
     *
     * @param method The method of interest.
     *
     * @return {@code true}, if the given method is a JavaBeans getter method,
     *         {@code false} otherwise.
     */
    public static boolean isGetterMethod(Method method) {
        if ( method.getParameterTypes().length != 0 ) {
            return false;
        }

        String methodName = method.getName();

        //<PropertyType> get<PropertyName>()
        if ( methodName.startsWith( PROPERTY_ACCESSOR_PREFIX_GET ) && method.getReturnType() != void.class ) {
            return true;
        }
        //boolean is<PropertyName>()
        else if ( methodName.startsWith( PROPERTY_ACCESSOR_PREFIX_IS ) && method.getReturnType() == boolean.class ) {
            return true;
        }
        //boolean has<PropertyName>()
        else if ( methodName.startsWith( PROPERTY_ACCESSOR_PREFIX_HAS ) && method.getReturnType() == boolean.class ) {
            return true;
        }

        return false;
    }

    /**
     * @param type the type to check.
     *
     * @return Returns <code>true</code> if <code>type</code> is implementing <code>Map</code>, <code>false</code> otherwise.
     */
    public static boolean isMap(Type type) {
        if ( type instanceof Class && Map.class.isAssignableFrom( (Class<?>) type ) ) {
            return true;
        }
        if ( type instanceof ParameterizedType ) {
            return isMap( ( (ParameterizedType) type ).getRawType() );
        }
        if ( type instanceof WildcardType ) {
            Type[] upperBounds = ( (WildcardType) type ).getUpperBounds();
            return upperBounds.length != 0 && isMap( upperBounds[0] );
        }
        return false;
    }

    /**
     * @param type the type to check.
     *
     * @return Returns <code>true</code> if <code>type</code> is implementing <code>List</code>, <code>false</code> otherwise.
     */
    public static boolean isList(Type type) {
        if ( type instanceof Class && List.class.isAssignableFrom( (Class<?>) type ) ) {
            return true;
        }
        if ( type instanceof ParameterizedType ) {
            return isList( ( (ParameterizedType) type ).getRawType() );
        }
        if ( type instanceof WildcardType ) {
            Type[] upperBounds = ( (WildcardType) type ).getUpperBounds();
            return upperBounds.length != 0 && isList( upperBounds[0] );
        }
        return false;
    }

}

