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
package org.drools.model.codegen.project;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.drools.model.codegen.execmodel.DeclaredTypeWriter;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.PackageModelWriter;
import org.drools.model.codegen.execmodel.PackageSources;
import org.drools.model.codegen.execmodel.QueryModel;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CodegenPackageSources extends PackageSources {

    private static final Logger logger = LoggerFactory.getLogger(CodegenPackageSources.class);
    private static final String REFLECTION_PERMISSIONS =
            "        \"allDeclaredConstructors\": true,\n" +
                    "        \"allPublicConstructors\": true,\n" +
                    "        \"allDeclaredMethods\": true,\n" +
                    "        \"allPublicMethods\": true,\n" +
                    "        \"allDeclaredFields\": true,\n" +
                    "        \"allPublicFields\": true\n";
    private static final String JSON_PREFIX = "[\n" +
            "    {\n" +
            "        \"name\": \"";
    private static final String JSON_DELIMITER = "\",\n" +
            REFLECTION_PERMISSIONS +
            "    },\n" +
            "    {\n" +
            "        \"name\": \"";
    private static final String JSON_SUFFIX = "\",\n" +
            REFLECTION_PERMISSIONS +
            "    }\n" +
            "]";

    private GeneratedFile reflectConfigSource;
    private Collection<RuleUnitDescription> ruleUnits;
    private String rulesFileName;
    private Map<String, Collection<QueryModel>> queries;

    private String pkgName;

    public static CodegenPackageSources dumpSources(PackageModelWriter packageModelWriter) {
        CodegenPackageSources sources = dumpPojos(packageModelWriter);
        PackageModel pkgModel = packageModelWriter.getPackageModel();

        PackageSources.writeRules(pkgModel, sources, packageModelWriter);
        sources.rulesFileName = pkgModel.getRulesFileName();

        sources.ruleUnits = pkgModel.getRuleUnits();
        if (!sources.ruleUnits.isEmpty()) {
            sources.queries = new HashMap<>();
            for (RuleUnitDescription ruleUnit : sources.ruleUnits) {
                String ruleUnitCanonicalName = ruleUnit.getCanonicalName();
                sources.queries.put(ruleUnitCanonicalName, pkgModel.getQueriesInRuleUnit(ruleUnit));
            }
        }
        sources.executableRulesClasses = pkgModel.getExecutableRulesClasses();
        return sources;
    }

    private static CodegenPackageSources dumpPojos(PackageModelWriter packageModelWriter) {
        CodegenPackageSources sources = new CodegenPackageSources();
        PackageModel pkgModel = packageModelWriter.getPackageModel();
        sources.pkgName = pkgModel.getName();

        List<String> pojoClasses = new ArrayList<>();
        for (DeclaredTypeWriter declaredType : packageModelWriter.getDeclaredTypes()) {
            sources.pojoSources.add(new GeneratedFile(GeneratedFileType.DECLARED_TYPE, declaredType.getName(),
                    PackageSources.logSource(declaredType.getSource())));
            pojoClasses.add(declaredType.getClassName());
        }

        if (!pojoClasses.isEmpty()) {
            sources.reflectConfigSource = getReflectConfigFile(pkgModel.getPathName(), pojoClasses);
        }

        return sources;
    }

    public static GeneratedFile getReflectConfigFile(String pathName, List<String> pojoClasses) {
        return new GeneratedFile(GeneratedFileType.CONFIG, "META-INF/native-image/" + pathName + "/reflect-config.json",
                reflectConfigSource(pojoClasses));
    }

    private static String reflectConfigSource(List<String> pojoClasses) {
        return pojoClasses.stream().collect(Collectors.joining(JSON_DELIMITER, JSON_PREFIX, JSON_SUFFIX));
    }

    public String getPackageName() {
        return pkgName;
    }

    public Collection<RuleUnitDescription> getRuleUnits() {
        return ruleUnits;
    }

    public String getRulesFileName() {
        return rulesFileName;
    }

    public Collection<QueryModel> getQueriesInRuleUnit(String ruleUnitCanonicalName) {
        return queries.get(ruleUnitCanonicalName);
    }

    public GeneratedFile getReflectConfigSource() {
        return reflectConfigSource;
    }
}
