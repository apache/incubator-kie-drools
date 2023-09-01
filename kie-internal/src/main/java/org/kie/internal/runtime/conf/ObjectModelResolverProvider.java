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
    private static volatile List<ObjectModelResolver> resolvers;

    /**
     * Returns all found resolvers
     * @return
     */
    public static List<ObjectModelResolver> getResolvers() {
        if (resolvers == null) {
            synchronized (serviceLoader) {
                if (resolvers == null) {
                    List<ObjectModelResolver> foundResolvers = new ArrayList<>();
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
