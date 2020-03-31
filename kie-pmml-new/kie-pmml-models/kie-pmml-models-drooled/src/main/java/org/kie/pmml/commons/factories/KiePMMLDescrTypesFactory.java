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
package org.kie.pmml.commons.factories;

import java.util.Queue;

import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to generate <b>Types</b> (descr) out of a <b>Queue&lt;KiePMMLDrooledType&gt;</b>
 */
public class KiePMMLDescrTypesFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLDescrTypesFactory.class.getName());

    private final PackageDescrBuilder builder;

    public static KiePMMLDescrTypesFactory factory(final PackageDescrBuilder builder) {
        return new KiePMMLDescrTypesFactory(builder);
    }

    private KiePMMLDescrTypesFactory(final PackageDescrBuilder builder) {
        this.builder = builder;
    }

    /**
     * Create types out of original <code>Queue&lt;KiePMMLDrooledType&gt;</code>s,
     * @param types
     */
    public void declareTypes(final Queue<KiePMMLDrooledType> types) {
        logger.debug("declareTypes {} ", types);
        types.forEach(this::declareType);
    }

    /**
     * Create type out of original <code>DataField</code>;
     * <b>populate</b> the <b>fieldNameTypeNameMap</b> with mapping between original field' name and <b>original type/generated type</b> tupla
     * @param type
     */
    protected void declareType(final KiePMMLDrooledType type) {
        logger.debug("declareType {} ", type);
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
