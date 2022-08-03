/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package $Package$;


import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kie.kogito.Application;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.correlation.CompositeCorrelation;
import org.kie.kogito.event.EventUnmarshaller;
import org.kie.kogito.event.EventReceiver;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessService;
import org.kie.kogito.services.event.impl.AbstractMessageConsumer;
import org.kie.kogito.event.EventExecutorServiceFactory;


@io.quarkus.runtime.Startup
public class $Type$MessageConsumer extends AbstractMessageConsumer<$Type$, $DataType$> {

    @Inject
    Application application;

    @Inject
    EventUnmarshaller<Object> eventUnmarshaller;

    @Inject
    @Named("$ProcessName$")
    Process<$Type$> process;

    @Inject
    ConfigBean configBean;

    @Inject
    ProcessService processService;
    
    @Inject
    EventReceiver eventReceiver;
    
    @Inject
    EventExecutorServiceFactory factory;
    
    ExecutorService executor;

    @Inject
    ObjectMapper objectMapper;

    Set<String> correlation;
    

    @javax.annotation.PostConstruct
    void init() {
        executor = factory.getExecutorService("$Trigger$"); 
        init(application,
             process,
             "$Trigger$",
             eventReceiver,
             $DataType$.class,
             configBean.useCloudEvents(),
             processService,
             executor,
             eventUnmarshaller,
             correlation);
    }

    @javax.annotation.PreDestroy
    public void close() {
        executor.shutdownNow();
    }


    private $Type$ eventToModel(Object event) {
        $Type$ model = new $Type$();
        if(event != null) {
            model.$SetModelMethodName$(objectMapper.convertValue(event, $DataType$.class));
        }
        return model;
    }

    @Override()
    protected Optional<Function<Object, $Type$>> getModelConverter() {
        return Optional.of(this::eventToModel);
    }
}
