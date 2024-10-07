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
package org.kie.kogito.event.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.TreeSet;

import org.kie.kogito.Addons;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventBatch;
import org.kie.kogito.event.impl.adapter.DataEventAdapter;
import org.kie.kogito.event.impl.adapter.DataEventAdapter.DataEventAdapterConfig;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateEventBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultInstanceEventBatch implements EventBatch {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultInstanceEventBatch.class);

    private String service;
    private Addons addons;
    private Collection<DataEvent<?>> processedEvents;
    private List<DataEventAdapter> dataEventAdapters;

    public DefaultInstanceEventBatch(String service, Addons addons) {
        this.service = service;
        this.addons = addons != null ? addons : Addons.EMTPY;
        this.processedEvents = new TreeSet<>(new Comparator<DataEvent<?>>() {
            @Override
            public int compare(DataEvent<?> event1, DataEvent<?> event2) {
                return event2 instanceof ProcessInstanceStateDataEvent &&
                        ((ProcessInstanceStateDataEvent) event2).getData().getEventType() == ProcessInstanceStateEventBody.EVENT_TYPE_ENDED
                        || event1 instanceof ProcessInstanceStateDataEvent &&
                                ((ProcessInstanceStateDataEvent) event1).getData().getEventType() == ProcessInstanceStateEventBody.EVENT_TYPE_STARTED ? -1 : 1;
            }
        });

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = this.getClass().getClassLoader();
        }
        this.dataEventAdapters = new ArrayList<>();
        ServiceLoader.load(DataEventAdapter.class, cl).forEach(this.dataEventAdapters::add);
        this.dataEventAdapters.stream().forEach(a -> a.setup(new DataEventAdapterConfig(this.service, this.addons)));
    }

    @Override
    public void append(Object event) {
        LOG.trace("event generated {}", event);
        this.dataEventAdapters.stream().filter(a -> a.accept(event)).map(a -> a.adapt(event)).forEach(this.processedEvents::add);
    }

    @Override
    public Collection<DataEvent<?>> events() {
        return processedEvents;
    }

}
