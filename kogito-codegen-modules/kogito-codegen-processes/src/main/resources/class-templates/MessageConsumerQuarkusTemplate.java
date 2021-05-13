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

import org.kie.kogito.Application;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.event.impl.DefaultEventConsumerFactory;
import org.kie.kogito.process.Process;
import org.kie.kogito.services.event.impl.AbstractMessageConsumer;
import org.kie.kogito.event.EventReceiver;

@io.quarkus.runtime.Startup
public class $Type$MessageConsumer extends AbstractMessageConsumer<$Type$, $DataType$, $DataEventType$> {

    @javax.inject.Inject
    Application application;

    @javax.inject.Inject
    @javax.inject.Named("$ProcessName$") Process<$Type$> process;

    @javax.inject.Inject
    ConfigBean configBean;

    @javax.inject.Inject
    EventReceiver eventReceiver;

    @javax.annotation.PostConstruct
    void init() {
        init(application,
             process,
             "$Trigger$",
             new DefaultEventConsumerFactory(),
             eventReceiver,
             $DataType$.class,
             $DataEventType$.class,
             configBean.useCloudEvents());

    }

    protected $Type$ eventToModel($DataType$ event) {
        $Type$ model = new $Type$();
        model.set$DataType$(event);
        return model;
    }
}
