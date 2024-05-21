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
package org.kie.kogito.addons.quarkus.data.index.deployment;

import java.util.List;

import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.SystemPropertyBuildItem;
import io.quarkus.devui.spi.page.CardPageBuildItem;
import io.quarkus.devui.spi.page.Page;

public class InfinispanDataIndexProcessor extends AbstractKogitoAddonsQuarkusDataIndexProcessor {

    private static final String FEATURE = "kogito-addons-quarkus-data-index-infinispan";

    @BuildStep
    public FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep(onlyIf = { IsDevelopment.class })
    CardPageBuildItem createDevUILink(List<SystemPropertyBuildItem> systemPropertyBuildItems) {
        CardPageBuildItem cardPageBuildItem = new CardPageBuildItem();
        cardPageBuildItem.addPage(Page.externalPageBuilder("Data Index GraphQL UI")
                .url("/q/graphql-ui/")
                .isHtmlContent()
                .icon("font-awesome-solid:signs-post"));
        return cardPageBuildItem;
    }
}
