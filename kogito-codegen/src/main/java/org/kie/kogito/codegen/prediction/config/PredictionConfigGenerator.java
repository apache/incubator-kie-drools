/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.prediction.config;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import org.kie.kogito.codegen.AbstractConfigGenerator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.pmml.config.StaticPredictionConfig;

public class PredictionConfigGenerator extends AbstractConfigGenerator  {


    private DependencyInjectionAnnotator annotator;
    private List<BodyDeclaration<?>> members = new ArrayList<>();

    private static final String RESOURCE_CDI = "/class-templates/config/CdiPredictionConfigTemplate.java";
    private static final String RESOURCE_SPRING = "/class-templates/config/SpringPredictionConfigTemplate.java";

    public PredictionConfigGenerator(String packageName) {
        super(packageName,
              "PredictionConfig",
              RESOURCE_CDI,
              RESOURCE_SPRING);
    }

    public ObjectCreationExpr newInstance() {
        return new ObjectCreationExpr().setType(StaticPredictionConfig.class.getCanonicalName());
    }

    public List<BodyDeclaration<?>> members() {

        return members;
    }

}
