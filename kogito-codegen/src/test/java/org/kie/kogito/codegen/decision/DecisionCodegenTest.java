/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen.decision;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.grafana.JGrafana;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DecisionCodegenTest {

    @Test
    public void generateSingleFile() throws Exception {
        DecisionCodegen codeGenerator = DecisionCodegen.ofPath(Paths.get("src/test/resources/decision").toAbsolutePath());

        List<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertEquals(2, generatedFiles.size());

        ClassOrInterfaceDeclaration classDeclaration = codeGenerator.moduleGenerator().classDeclaration();
        assertNotNull(classDeclaration);
    }

    @Test
    public void GivenADMNModel_WhenMonitoringIsActive_ThenGrafanaDashboardsAreGenerated() throws Exception {
        DecisionCodegen codeGenerator = DecisionCodegen.ofPath(Paths.get("src/test/resources/decision").toAbsolutePath()).withMonitoring(true);

        List<GeneratedFile> generatedFiles = codeGenerator.generate();

        List<GeneratedFile> dashboards =  generatedFiles.stream().filter(x -> x.getType() == GeneratedFile.Type.RESOURCE).collect(Collectors.toList());

        assertEquals(2,dashboards.size());

        JGrafana vacationDashboard = JGrafana.parse(new String(dashboards.stream().filter(x -> x.relativePath().contains("Vacations.json")).findFirst().get().contents()));

        assertEquals(7, vacationDashboard.getDashboard().panels.size());
    }
}
