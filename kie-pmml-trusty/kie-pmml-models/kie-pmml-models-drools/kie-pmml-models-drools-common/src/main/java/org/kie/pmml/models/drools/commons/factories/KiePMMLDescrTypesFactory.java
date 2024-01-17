/**
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
package org.kie.pmml.models.drools.commons.factories;

import java.util.List;

import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to generate <b>Types</b> (descr) out of a <b>List&lt;KiePMMLDroolsType&gt;</b>
 */
public class KiePMMLDescrTypesFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLDescrTypesFactory.class.getName());

    private final PackageDescrBuilder builder;

    private KiePMMLDescrTypesFactory(final PackageDescrBuilder builder) {
        this.builder = builder;
    }

    public static KiePMMLDescrTypesFactory factory(final PackageDescrBuilder builder) {
        return new KiePMMLDescrTypesFactory(builder);
    }

    /**
     * Create types out of original <code>List&lt;KiePMMLDroolsType&gt;</code>s,
     * @param types
     */
    public void declareTypes(final List<KiePMMLDroolsType> types) {
        logger.trace("declareTypes {} ", types);
        types.forEach(this::declareType);
    }

    /**
     * Create type out of original <code>DataField</code>;
     * <b>populate</b> the <b>fieldNameTypeNameMap</b> with mapping between original field' name and <b>original type/generated type</b> tuple
     * @param type
     */
    protected void declareType(final KiePMMLDroolsType type) {
        logger.trace("declareType {} ", type);
        String generatedType = type.getName();
        String fieldType = type.getType();
        builder.newDeclare()
                .type()
                .name(generatedType)
                .newField("value").type(fieldType)
                .end()
                .end();
    }
}
