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

package org.kie.kogito.svg.service;

import java.util.Optional;

import org.kie.kogito.svg.AbstractProcessSvgService;
import org.kie.kogito.svg.dataindex.DataIndexClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SpringBootProcessSvgService extends AbstractProcessSvgService {

    @Autowired
    public SpringBootProcessSvgService(@Autowired(required = false) DataIndexClient dataIndexClient,
            @Value("${kogito.svg.folder.path:#{null}}") Optional<String> svgResourcesPath,
            @Value("${kogito.svg.color.completed:" + DEFAULT_COMPLETED_COLOR + "}") String completedColor,
            @Value("${kogito.svg.color.completed.border:" + DEFAULT_COMPLETED_BORDER_COLOR + "}") String completedBorderColor,
            @Value("${kogito.svg.color.active.border:" + DEFAULT_ACTIVE_BORDER_COLOR + "}") String activeBorderColor) {
        super(dataIndexClient, svgResourcesPath, completedColor, completedBorderColor, activeBorderColor);
    }
}
