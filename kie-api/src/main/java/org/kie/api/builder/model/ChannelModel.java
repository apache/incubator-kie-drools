/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.builder.model;

/**
 * ChannelModel is a model allowing to programmatically define a Channel and wire it to a KieSession
 */
public interface ChannelModel {

    /**
     * @return the name of the channel
     */
    String getName();

    /**
     * Returns the type of this ChannelModel
     * (i.e. the name of the class implementing the Channel)
     */
    String getType();

    QualifierModel getQualifierModel();

    QualifierModel newQualifierModel(String type);
}
