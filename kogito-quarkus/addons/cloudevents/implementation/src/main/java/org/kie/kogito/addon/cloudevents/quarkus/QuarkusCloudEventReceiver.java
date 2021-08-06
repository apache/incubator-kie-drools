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
package org.kie.kogito.addon.cloudevents.quarkus;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment.Strategy;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.event.KogitoEventStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.reactive.messaging.ChannelRegistar;
import io.smallrye.reactive.messaging.DefaultMediatorConfiguration;
import io.smallrye.reactive.messaging.MediatorConfiguration;
import io.smallrye.reactive.messaging.Shape;
import io.smallrye.reactive.messaging.annotations.Merge;
import io.smallrye.reactive.messaging.annotations.Merge.Mode;
import io.smallrye.reactive.messaging.extension.MediatorManager;

@ApplicationScoped
@RegisterForReflection
@Named(KogitoEventStreams.DEFAULT_INCOMING_BEAN_NAME)
public class QuarkusCloudEventReceiver extends AbstractQuarkusCloudEventReceiver implements ChannelRegistar {

    private static final Logger logger = LoggerFactory.getLogger(QuarkusCloudEventReceiver.class);

    private static final String PROPERTY = "mp.messaging.incoming." + KogitoEventStreams.INCOMING + ".connector";

    @Inject
    private MediatorManager mediatorManager;
    @Inject
    private BeanManager beanManager;

    MediatorConfiguration mediatorConf() {

        Bean<?> bean = beanManager.resolve(beanManager.getBeans(QuarkusCloudEventReceiver.class));
        return new DefaultMediatorConfiguration(
                getMethod(bean),
                bean) {

            @Override
            public List<String> getIncoming() {
                return Collections.singletonList(KogitoEventStreams.INCOMING);
            }

            @Override
            public Shape shape() {
                return Shape.SUBSCRIBER;
            }

            @Override
            public Consumption consumption() {
                return Consumption.MESSAGE;
            }

            @Override
            public boolean isBlocking() {
                return false;
            }

            @Override
            public Acknowledgment.Strategy getAcknowledgment() {
                return Strategy.MANUAL;
            }

            @Override
            public Merge.Mode getMerge() {
                return Mode.MERGE;
            }
        };
    }

    private Method getMethod(Bean<?> bean) {
        Method[] methods = bean.getBeanClass().getMethods();
        for (Method m : methods) {
            if (m.getParameterCount() == 1 && Message.class.isAssignableFrom(m.getParameterTypes()[0])) {
                return m;
            }
        }
        throw new IllegalStateException("Cannot find method that accept Message as input parameter in " + Arrays.toString(methods));
    }

    @Override
    public void initialize() {
        if (ConfigProvider.getConfig().getOptionalValue(PROPERTY, String.class).isPresent()) {
            logger.info("Registering mediator {}", KogitoEventStreams.INCOMING);
            mediatorManager.addAnalyzed(Collections.singletonList(mediatorConf()));
        }
    }
}
