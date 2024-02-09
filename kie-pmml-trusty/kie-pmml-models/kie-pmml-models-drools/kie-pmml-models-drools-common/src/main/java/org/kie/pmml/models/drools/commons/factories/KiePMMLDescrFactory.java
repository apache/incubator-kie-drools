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

import java.util.Map;

import org.dmg.pmml.SimplePredicate;
import org.drools.drl.ast.dsl.DescrFactory;
import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.executor.KiePMMLStatusHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to generate a <b>DROOLS</b> (descr) object out of a<b>TreeModel</b>
 */
public class KiePMMLDescrFactory {

    public static final String PMML4_RESULT = "PMML4Result";
    public static final String PMML4_RESULT_IDENTIFIER = "$pmml4Result";
    public static final String OUTPUTFIELDS_MAP = "Map";
    public static final String OUTPUTFIELDS_MAP_IDENTIFIER = "$outputFieldsMap";

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLDescrFactory.class.getName());

    private KiePMMLDescrFactory() {
        // Avoid instantiation
    }

    /**
     * Returns the <code>PackageDescr</code> built out of the given <code>KiePMMLDroolsAST</code>.
     * @param kiePMMLDroolsAST
     * @param packageName
     * @return
     */
    public static PackageDescr getBaseDescr(final KiePMMLDroolsAST kiePMMLDroolsAST, String packageName) {
        logger.trace("getBaseDescr {} {}", kiePMMLDroolsAST, packageName);
        PackageDescrBuilder builder = DescrFactory.newPackage()
                .name(packageName);
        builder.newImport().target(KiePMMLStatusHolder.class.getName());
        builder.newImport().target(SimplePredicate.class.getName());
        builder.newImport().target(PMML4Result.class.getName());
        builder.newImport().target(Map.class.getName());
        builder.newGlobal().identifier(PMML4_RESULT_IDENTIFIER).type(PMML4_RESULT);
        builder.newGlobal().identifier(OUTPUTFIELDS_MAP_IDENTIFIER).type(OUTPUTFIELDS_MAP);
        KiePMMLDescrTypesFactory.factory(builder).declareTypes(kiePMMLDroolsAST.getTypes());
        KiePMMLDescrRulesFactory.factory(builder).declareRules(kiePMMLDroolsAST.getRules());
        return builder.getDescr();
    }
}
