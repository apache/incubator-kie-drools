/*
 * Copyright 2005 JBoss Inc
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

package org.drools.core.util;

import java.util.function.Function;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAliasType;
import com.thoughtworks.xstream.annotations.XStreamInclude;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import com.thoughtworks.xstream.security.AnyTypePermission;
import com.thoughtworks.xstream.security.TypePermission;
import com.thoughtworks.xstream.security.WildcardTypePermission;

public class XStreamUtils {
    private static final String[] WHITELISTED_PACKAGES = new String[]{"org.drools.core.command.**", "org.drools.core.runtime.impl.ExecutionResultImpl", "org.drools.core.runtime.rule.impl.FlatQueryResults", "org.drools.core.common.DefaultFactHandle", "org.drools.core.common.EventFactHandle"};

    private XStreamUtils() { }

    public static XStream createTrustingXStream() {
        return internalCreateTrustingXStream(new XStream());
    }

    public static XStream createTrustingXStream(HierarchicalStreamDriver hierarchicalStreamDriver) {
        return internalCreateTrustingXStream(new XStream(hierarchicalStreamDriver));
    }

    public static XStream createTrustingXStream(HierarchicalStreamDriver hierarchicalStreamDriver, ClassLoader classLoader) {
        return internalCreateTrustingXStream(new XStream((ReflectionProvider)null, hierarchicalStreamDriver, new ClassLoaderReference(classLoader)));
    }

    public static XStream createTrustingXStream(ReflectionProvider reflectionProvider) {
        return internalCreateTrustingXStream(new XStream(reflectionProvider));
    }

    public static XStream createTrustingXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver hierarchicalStreamDriver) {
        return internalCreateTrustingXStream(new XStream(reflectionProvider, hierarchicalStreamDriver));
    }

    public static XStream createTrustingXStream(ReflectionProvider reflectionProvider, final Function<MapperWrapper, MapperWrapper> mapper) {
        return internalCreateTrustingXStream(new XStream(reflectionProvider) {
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return (MapperWrapper)mapper.apply(next);
            }
        });
    }

    private static XStream internalCreateTrustingXStream(XStream xstream) {
        XStream.setupDefaultSecurity(xstream);
        xstream.addPermission(new AnyTypePermission());
        return xstream;
    }

    public static XStream createNonTrustingXStream() {
        return internalCreateNonTrustingXStream(new XStream());
    }

    public static XStream createNonTrustingXStream(HierarchicalStreamDriver hierarchicalStreamDriver) {
        return internalCreateNonTrustingXStream(new XStream(hierarchicalStreamDriver));
    }

    public static XStream createNonTrustingXStream(HierarchicalStreamDriver hierarchicalStreamDriver, ClassLoader classLoader) {
        return internalCreateNonTrustingXStream(new XStream((ReflectionProvider)null, hierarchicalStreamDriver, new ClassLoaderReference(classLoader)));
    }

    public static XStream createNonTrustingXStream(ReflectionProvider reflectionProvider) {
        return internalCreateNonTrustingXStream(new XStream(reflectionProvider));
    }

    public static XStream createNonTrustingXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver hierarchicalStreamDriver) {
        return internalCreateNonTrustingXStream(new XStream(reflectionProvider, hierarchicalStreamDriver));
    }

    public static XStream createNonTrustingXStream(ReflectionProvider reflectionProvider, final Function<MapperWrapper, MapperWrapper> mapper) {
        return internalCreateNonTrustingXStream(new XStream(reflectionProvider) {
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return (MapperWrapper)mapper.apply(next);
            }
        });
    }

    private static XStream internalCreateNonTrustingXStream(XStream xstream) {
        XStream.setupDefaultSecurity(xstream);
        xstream.addPermission(new AnyAnnotationTypePermission());
        xstream.addPermission(new WildcardTypePermission(WHITELISTED_PACKAGES));
        return xstream;
    }

    public static class AnyAnnotationTypePermission implements TypePermission {
        public boolean allows(Class type) {
            if (type == null) {
                return false;
            } else {
                return type.isAnnotationPresent( XStreamAlias.class) || type.isAnnotationPresent( XStreamAliasType.class) || type.isAnnotationPresent( XStreamInclude.class);
            }
        }
    }
}
