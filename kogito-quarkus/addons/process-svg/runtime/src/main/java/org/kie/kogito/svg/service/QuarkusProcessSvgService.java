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
package org.kie.kogito.svg.service;

import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.svg.AbstractProcessSvgService;
import org.kie.kogito.svg.dataindex.DataIndexClient;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class QuarkusProcessSvgService extends AbstractProcessSvgService {

    @Inject
    public QuarkusProcessSvgService(DataIndexClient dataIndexClient,
            @ConfigProperty(name = "kogito.svg.folder.path") Optional<String> svgResourcesPath,
            @ConfigProperty(name = "kogito.svg.color.completed", defaultValue = DEFAULT_COMPLETED_COLOR) String completedColor,
            @ConfigProperty(name = "kogito.svg.color.completed.border", defaultValue = DEFAULT_COMPLETED_BORDER_COLOR) String completedBorderColor,
            @ConfigProperty(name = "kogito.svg.color.active.border", defaultValue = DEFAULT_ACTIVE_BORDER_COLOR) String activeBorderColor) {
        super(dataIndexClient, svgResourcesPath, completedColor, completedBorderColor, activeBorderColor);
    }
}
