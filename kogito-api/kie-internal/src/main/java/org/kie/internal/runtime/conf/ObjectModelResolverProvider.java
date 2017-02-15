/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.runtime.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Provides all available implementations of <code>ObjectModelResolver</code>
 *
 */
public class ObjectModelResolverProvider {

    private static ServiceLoader<ObjectModelResolver> serviceLoader = ServiceLoader.load(ObjectModelResolver.class);
    private static List<ObjectModelResolver> resolvers;

    /**
     * Returns all found resolvers
     * @return
     */
    public static List<ObjectModelResolver> getResolvers() {
        if (resolvers == null) {
            synchronized (serviceLoader) {
                if (resolvers == null) {
                    List<ObjectModelResolver> foundResolvers = new ArrayList<ObjectModelResolver>();
                    for (ObjectModelResolver resolver : serviceLoader) {
                        foundResolvers.add(resolver);
                    }
                    resolvers = foundResolvers;
                }
            }
        }

        return resolvers;
    }

    /**
     * Returns first resolver that accepts the given resolverId.
     * In case none is found null is returned.
     * @param resolverId identifier of the resolver
     * @return found resolver or null otherwise
     */
    public static ObjectModelResolver get(String resolverId) {
        List<ObjectModelResolver> resolvers = getResolvers();

        for (ObjectModelResolver resolver : resolvers) {
            if (resolver.accept(resolverId)) {
                return resolver;
            }
        }

        return null;
    }
}
