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
package org.kie.kogito.addon.cloudevents.quarkus;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.kogito.event.InputTriggerAware;
import org.kie.kogito.event.OutputTriggerAware;

@ApplicationScoped
public class ProcessChannelResolver implements ChannelResolver {

    @Inject
    private Instance<InputTriggerAware> inputChannelsProvider;
    @Inject
    private Instance<OutputTriggerAware> outputChannelsProvider;
    private Set<String> inputChannels;
    private Set<String> outputChannels;

    @Override
    public Collection<String> getOuputChannels() {
        return outputChannels;
    }

    @Override
    public Collection<String> getInputChannels() {
        return inputChannels;
    }

    @PostConstruct
    private void init() {
        inputChannels = getChannels(inputChannelsProvider, InputTriggerAware::getInputTrigger);
        outputChannels = getChannels(outputChannelsProvider, OutputTriggerAware::getOutputTrigger);
    }

    private <T> Set<String> getChannels(Instance<T> channelProvider, Function<T, String> triggerResolver) {
        final Set<String> channels = new HashSet<>();
        channelProvider.forEach(instance -> channels.add(triggerResolver.apply(instance)));
        return Collections.unmodifiableSet(channels);
    }

}
