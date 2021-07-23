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

import java.util.concurrent.ExecutorService;

import org.kie.kogito.Application;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.event.impl.DefaultEventConsumerFactory;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessService;
import org.kie.kogito.services.event.impl.AbstractMessageConsumer;
import org.kie.kogito.services.event.impl.JsonStringToObject;
import org.kie.kogito.event.EventConverter;
import org.kie.kogito.event.EventReceiver;
import org.kie.kogito.event.KogitoEventExecutor;


@org.springframework.stereotype.Component()
public class $Type$MessageConsumer extends AbstractMessageConsumer<$Type$, $DataType$, $DataEventType$> {

    @org.springframework.beans.factory.annotation.Autowired()
    $Type$MessageConsumer(
            Application application,
            @org.springframework.beans.factory.annotation.Qualifier("$ProcessName$") Process<$Type$> process,
            ConfigBean configBean,
            EventReceiver eventReceiver,
            ProcessService processService,
            @org.springframework.beans.factory.annotation.Qualifier(KogitoEventExecutor.BEAN_NAME) ExecutorService executorService,
            EventConverter<String> eventConverter) {
        super(application,
              process,
              "$Trigger$",
              new DefaultEventConsumerFactory(),
              eventReceiver,
              $DataType$.class,
              $DataEventType$.class,
              configBean.useCloudEvents(),
              processService,
              executorService,
              eventConverter);
    }

    protected $Type$ eventToModel($DataType$ event) {
        $Type$ model = new $Type$();
        model.set$DataType$(event);
        return model;
    }
}
