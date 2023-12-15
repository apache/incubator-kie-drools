/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package $Package$;


import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.kie.kogito.process.Process;
import org.kie.kogito.addon.quarkus.messaging.common.QuarkusMessageConsumer;
import org.kie.kogito.event.EventReceiver;


@io.quarkus.runtime.Startup
public class $Type$MessageConsumer extends QuarkusMessageConsumer<$Type$, $DataType$> {

    @Inject
    @Named("$ProcessName$")
    Process<$Type$> process;
    
    @Inject
    EventReceiver eventReceiver;
    
    private Set<String> correlation;

    @jakarta.annotation.PostConstruct
    void init() {
        init(process,"$Trigger$",$DataType$.class, eventReceiver, correlation);
    }

    private $Type$ eventToModel($DataType$ event) {
        $Type$ model = new $Type$();
        if(event != null) {
            model.$SetModelMethodName$(event);
        }
        return model;
    }

    @Override()
    protected Optional<Function<$DataType$, $Type$>> getModelConverter() {
        return Optional.of(this::eventToModel);
    }
}
