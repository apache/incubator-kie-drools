/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.internal.builder.conf;

import org.kie.api.conf.SingleValueKieBaseOption;


/**
 * By default all the Drools artifacts under the resources folder, at any level, are included into the KieBase.
 * The package attribute of the kmodule.xml allows to limit the artifacts that will be compiled in this KieBase
 * to only the ones belonging to the list of packages. However older versions of Drools actually checked the folder
 * name instead of the package one. This option allows to re-eanble that old folder-based behaviour.
 *
 * drools.groupDRLsInKieBasesByFolder = &lt;true|false&gt;
 *
 * DEFAULT = false
 */
public enum GroupDRLsInKieBasesByFolderOption implements SingleValueKnowledgeBuilderOption, SingleValueKieBaseOption {

    ENABLED(true),
    DISABLED(false);

    /**
     * The property name for the enabling/disabling trim of cells values
     */
    public static final String PROPERTY_NAME = "drools.groupDRLsInKieBasesByFolder";

    private boolean value;

    GroupDRLsInKieBasesByFolderOption( final boolean value ) {
        this.value = value;
    }

    @Override
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isGroupDRLsInKieBasesByFolder() {
        return this.value;
    }
}
