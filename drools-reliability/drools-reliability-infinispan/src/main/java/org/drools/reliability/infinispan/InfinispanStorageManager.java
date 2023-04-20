package org.drools.reliability.infinispan;

import org.drools.reliability.core.ReliabilityConfigurationException;
import org.drools.reliability.core.TestableStorageManager;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.SerializationContextInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.drools.reliability.infinispan.InfinispanStorageManagerFactory.INFINISPAN_STORAGE_ALLOWED_PACKAGES;
import static org.drools.reliability.infinispan.InfinispanStorageManagerFactory.INFINISPAN_STORAGE_MARSHALLER;
import static org.drools.reliability.infinispan.InfinispanStorageManagerFactory.INFINISPAN_STORAGE_SERIALIZATION_CONTEXT_INITIALIZER;
import static org.drools.util.Config.getOptionalConfig;

public interface InfinispanStorageManager extends TestableStorageManager {

    static final Logger LOG = LoggerFactory.getLogger(InfinispanStorageManager.class);

    enum MarshallerType {
        JAVA,
        PROTOSTREAM
    }

    void setRemoteCacheManager(RemoteCacheManager remoteCacheManager);

    void setEmbeddedCacheManager(DefaultCacheManager cacheManager);

    ConfigurationBuilder provideAdditionalRemoteConfigurationBuilder();

    static String[] getAllowedPackages() {
        List<String> allowList = new ArrayList<>();
        allowList.add("org.kie.*");
        allowList.add("org.drools.*");
        allowList.add("java.*");
        getOptionalConfig(INFINISPAN_STORAGE_ALLOWED_PACKAGES)
                .ifPresent(additionalPkgs -> Arrays.stream(additionalPkgs.split(",")).forEach(p -> allowList.add(p + ".*")));
        return allowList.toArray(new String[allowList.size()]);
    }

    static MarshallerType getMarshallerType() {
        return getOptionalConfig(INFINISPAN_STORAGE_MARSHALLER)
                .map(MarshallerType::valueOf)
                .orElse(MarshallerType.JAVA);
    }

    default Optional<SerializationContextInitializer> findSerializationContextInitializer() {
        return getOptionalConfig(INFINISPAN_STORAGE_SERIALIZATION_CONTEXT_INITIALIZER)
                .map(className -> {
                    try {
                        return (SerializationContextInitializer) Class.forName(className).getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        throw new ReliabilityConfigurationException(e);
                    }
                });
    }

    default SerializationContext getSerializationContext() {
        throw new UnsupportedOperationException();
    }

    default boolean isProtoStream() {
        return false;
    }
}
