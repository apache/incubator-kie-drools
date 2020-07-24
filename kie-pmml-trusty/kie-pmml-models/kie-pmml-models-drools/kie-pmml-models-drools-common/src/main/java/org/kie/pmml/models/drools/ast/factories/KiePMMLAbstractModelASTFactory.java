/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.models.drools.ast.factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class to be extended to generate a <code>KiePMMLDroolsAST</code> out of a <code>DataDictionary</code> and a <b>model</b>
 */
public abstract class KiePMMLAbstractModelASTFactory {

    public static final String SURROGATE_RULENAME_PATTERN = "%s_surrogate_%s";
    public static final String SURROGATE_GROUP_PATTERN = "%s_surrogate";
    public static final String STATUS_NULL = "status == null";
    public static final String STATUS_PATTERN = "status == \"%s\"";
    public static final String PATH_PATTERN = "%s_%s";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLAbstractModelASTFactory.class.getName());

    protected KiePMMLAbstractModelASTFactory() {
        // Avoid instantiation
    }
}
