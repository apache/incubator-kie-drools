/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.jobs.service.repository.infinispan;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;

import org.apache.commons.io.IOUtils;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.RemoteCacheManagerAdmin;
import org.infinispan.commons.configuration.XMLStringConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.quarkus.runtime.StartupEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InfinispanConfigurationTest {

    private InfinispanConfiguration tested;

    @BeforeEach
    public void setUp() {
        tested = new InfinispanConfiguration();
    }

    @Test
    void initializeCaches(@Mock Event<InfinispanInitialized> initializedEvent,
            @Mock RemoteCacheManager remoteCacheManager,
            @Mock Instance<RemoteCacheManager> instance,
            @Mock RemoteCacheManagerAdmin administration,
            @Mock RemoteCache<Object, Object> cache) throws IOException {
        when(instance.get()).thenReturn(remoteCacheManager);
        when(remoteCacheManager.administration()).thenReturn(administration);
        when(administration.getOrCreateCache(anyString(), any(XMLStringConfiguration.class))).thenReturn(cache);
        ArgumentCaptor<XMLStringConfiguration> templateCaptor = forClass(XMLStringConfiguration.class);

        assertThat(tested.isInitialized()).isFalse();
        tested.initializeCaches(new StartupEvent(), Optional.of("infinispan"), instance, initializedEvent);
        verify(administration).getOrCreateCache(eq(InfinispanConfiguration.Caches.JOB_DETAILS),
                templateCaptor.capture());

        assertThat(templateCaptor.getValue().toXMLString(null))
                .isEqualTo(IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(InfinispanConfiguration.CACHE_TEMPLATE_XML), Charset.forName("UTF-8")));
        verify(initializedEvent).fire(any(InfinispanInitialized.class));
        assertThat(tested.isInitialized()).isTrue();

    }
}
