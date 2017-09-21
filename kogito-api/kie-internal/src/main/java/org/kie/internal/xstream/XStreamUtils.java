/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.internal.xstream;

import java.util.function.Function;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import com.thoughtworks.xstream.security.WildcardTypePermission;

import static com.thoughtworks.xstream.XStream.setupDefaultSecurity;

public class XStreamUtils {
    private static final String[] VOID_TYPES = {"void.class", "Void.class"};

    public static XStream createXStream() {
        return internalCreateXStream( new XStream() );
    }

    public static XStream createXStream(HierarchicalStreamDriver hierarchicalStreamDriver) {
        return internalCreateXStream( new XStream(hierarchicalStreamDriver) );
    }

    public static XStream createXStream(HierarchicalStreamDriver hierarchicalStreamDriver, ClassLoader classLoader) {
        return internalCreateXStream( new XStream(null, hierarchicalStreamDriver, new ClassLoaderReference( classLoader )) );
    }

    public static XStream createXStream(ReflectionProvider reflectionProvider ) {
        return internalCreateXStream( new XStream(reflectionProvider) );
    }

    public static XStream createXStream(ReflectionProvider reflectionProvider, Function<MapperWrapper, MapperWrapper> mapper ) {
        return internalCreateXStream( new XStream(reflectionProvider) {
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return mapper.apply( next );
            }
        });
    }

    private static XStream internalCreateXStream( XStream xstream ) {
        setupDefaultSecurity(xstream);
        xstream.addPermission( new WildcardTypePermission( new String[] {
                "java.**", "javax.**", "org.kie.**", "org.drools.**", "org.jbpm.**", "org.optaplanner.**", "org.appformer.**"
        } ) );
        return xstream;
    }
}
