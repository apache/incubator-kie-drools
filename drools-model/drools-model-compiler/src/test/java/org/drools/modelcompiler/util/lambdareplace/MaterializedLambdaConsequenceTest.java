/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.modelcompiler.util.lambdareplace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import static org.drools.modelcompiler.util.lambdareplace.MaterializedLambdaTestUtils.verifyCreatedClass;

public class MaterializedLambdaConsequenceTest {

    @Test
    public void createConsequence() {
        CreatedClass aClass = new MaterializedLambdaConsequence("org.drools.modelcompiler.util.lambdareplace", "rulename", new ArrayList<>())
                .create("(org.drools.modelcompiler.domain.Person p1, org.drools.modelcompiler.domain.Person p2) -> result.setValue( p1.getName() + \" is older than \" + p2.getName())", new ArrayList<>(), new ArrayList<>());

        String classNameWithPackage = aClass.getClassNameWithPackage();
        // There is no easy way to retrieve the originally created "hashcode" because it is calculated over a CompilationUnit that soon after is modified;
        // so current "CreatedClass" contains a CompilationUnit that is different from the one used to calculate the hashcode
        String expectedPackageName = classNameWithPackage.substring(0, classNameWithPackage.lastIndexOf('.'));
        String expectedClassName = classNameWithPackage.substring(classNameWithPackage.lastIndexOf('.')+1);
        //language=JAVA
        String expectedResult = "" +
                "package PACKAGE_TOREPLACE;\n" +
                "import static rulename.*; " +
                "import org.drools.modelcompiler.dsl.pattern.D; " +
                "\n"+
                "@org.drools.compiler.kie.builder.MaterializedLambda() " +
                "public enum CLASS_TOREPLACE implements org.drools.model.functions.Block2<org.drools.modelcompiler.domain.Person, org.drools.modelcompiler.domain.Person>, org.drools.model.functions.HashedExpression  {\n" +
                "INSTANCE;\n" +
                "public static final String EXPRESSION_HASH = \"8305FF24AC76CB49E7AAE2C10356A105\";" +
                "    public java.lang.String getExpressionHash() {\n" +
                "        return EXPRESSION_HASH;\n" +
                "    } " +
        "        @Override()\n" +
                "        public void execute(org.drools.modelcompiler.domain.Person p1, org.drools.modelcompiler.domain.Person p2) throws java.lang.Exception {\n" +
                "            result.setValue(p1.getName() + \" is older than \" + p2.getName());\n" +
                "        }\n" +
                "    }\n";
        // Workaround to keep the "//language=JAVA" working
        expectedResult = expectedResult
                .replace("PACKAGE_TOREPLACE", expectedPackageName)
                .replace("CLASS_TOREPLACE", expectedClassName);

        verifyCreatedClass(aClass, expectedResult);
    }

    @Test
    public void createConsequenceWithDrools() {
        ArrayList<String> fields = new ArrayList<>();
        fields.add("\"age\"");
        MaterializedLambda.BitMaskVariable bitMaskVariable = new MaterializedLambda.BitMaskVariableWithFields("DomainClassesMetadataA3B8DE4BEBF13D94572A10FD20BBE729.org_drools_modelcompiler_domain_Person_Metadata_INSTANCE", fields, "mask_$p");

        CreatedClass aClass = new MaterializedLambdaConsequence("defaultpkg", "defaultpkg.RulesA3B8DE4BEBF13D94572A10FD20BBE729", Collections.singletonList(bitMaskVariable))
                .create("(org.drools.model.Drools drools, org.drools.modelcompiler.domain.Person $p) -> {{($p).setAge($p.getAge() + 1); drools.update($p, mask_$p);}}", new ArrayList<>(), new ArrayList<>());
        String classNameWithPackage = aClass.getClassNameWithPackage();
        String expectedPackageName = classNameWithPackage.substring(0, classNameWithPackage.lastIndexOf('.'));
        // There is no easy way to retrieve the originally created "hashcode" because it is calculated over a CompilationUnit that soon after is modified;
        // so current "CreatedClass" contains a CompilationUnit that is different from the one used to calculate the hashcode
        String expectedClassName = classNameWithPackage.substring(classNameWithPackage.lastIndexOf('.')+1);
        //language=JAVA
        String expectedResult = "" +
                "package PACKAGE_TOREPLACE;\n" +
                "import static defaultpkg.RulesA3B8DE4BEBF13D94572A10FD20BBE729.*; " +
                "import org.drools.modelcompiler.dsl.pattern.D; " +
                "\n"+
                "@org.drools.compiler.kie.builder.MaterializedLambda() " +
                "public enum CLASS_TOREPLACE implements org.drools.model.functions.Block2<org.drools.model.Drools, org.drools.modelcompiler.domain.Person>, org.drools.model.functions.HashedExpression  {\n" +
                "        INSTANCE;\n" +
                "        public static final String EXPRESSION_HASH = \"1FE08C27A04F37AADD1A62E562519E8D\";\n" +
                "    public java.lang.String getExpressionHash() {\n" +
                "        return EXPRESSION_HASH;\n" +
                "    } " +
                "        private final org.drools.model.BitMask mask_$p = org.drools.model.BitMask.getPatternMask(DomainClassesMetadataA3B8DE4BEBF13D94572A10FD20BBE729.org_drools_modelcompiler_domain_Person_Metadata_INSTANCE, \"age\");\n" +
                "        @Override()\n" +
                "        public void execute(org.drools.model.Drools drools, org.drools.modelcompiler.domain.Person $p) throws java.lang.Exception {\n" +
                "            {\n" +
                "                ($p).setAge($p.getAge() + 1);\n" +
                "                drools.update($p, mask_$p);\n" +
                "            }" +
                "        }\n" +
                "    }\n";
        // Workaround to keep the "//language=JAVA" working
        expectedResult = expectedResult
                .replace("PACKAGE_TOREPLACE", expectedPackageName)
                .replace("CLASS_TOREPLACE", expectedClassName);

        verifyCreatedClass(aClass, expectedResult);
    }

    @Test
    public void createConsequenceWithMultipleFactUpdate() {
        ArrayList<String> personFields = new ArrayList<>();
        personFields.add("\"name\"");
        MaterializedLambda.BitMaskVariable bitMaskPerson = new MaterializedLambda.BitMaskVariableWithFields("DomainClassesMetadataB45236F6195B110E0FA3A5447BC53274.org_drools_modelcompiler_domain_Person_Metadata_INSTANCE", personFields, "mask_$person");
        ArrayList<String> petFields = new ArrayList<>();
        petFields.add("\"age\"");
        MaterializedLambda.BitMaskVariable bitMaskPet = new MaterializedLambda.BitMaskVariableWithFields("DomainClassesMetadataB45236F6195B110E0FA3A5447BC53274.org_drools_modelcompiler_domain_Pet_Metadata_INSTANCE", petFields, "mask_$pet");

        String consequenceBlock = "(org.drools.model.Drools drools, org.drools.modelcompiler.domain.Pet $pet, org.drools.modelcompiler.domain.Person $person) -> {{ ($person).setName(\"George\");drools.update($person, mask_$person); ($pet).setAge($pet.getAge() + 1); drools.update($pet, mask_$pet); }}";
        CreatedClass aClass = new MaterializedLambdaConsequence("defaultpkg",
                                                                "defaultpkg.RulesB45236F6195B110E0FA3A5447BC53274",
                                                                Arrays.asList(bitMaskPerson, bitMaskPet))
                .create(consequenceBlock, new ArrayList<>(), new ArrayList<>());

        String classNameWithPackage = aClass.getClassNameWithPackage();
        // There is no easy way to retrieve the originally created "hashcode" because it is calculated over a CompilationUnit that soon after is modified;
        // so current "CreatedClass" contains a CompilationUnit that is different from the one used to calculate the hashcode
        String expectedPackageName = classNameWithPackage.substring(0, classNameWithPackage.lastIndexOf('.'));
        String expectedClassName = classNameWithPackage.substring(classNameWithPackage.lastIndexOf('.')+1);
        //language=JAVA
        String expectedResult = "" +
                "package PACKAGE_TOREPLACE;\n" +
                "import static defaultpkg.RulesB45236F6195B110E0FA3A5447BC53274.*; " +
                "import org.drools.modelcompiler.dsl.pattern.D; " +
                "\n"+
                "@org.drools.compiler.kie.builder.MaterializedLambda() " +
                "public enum CLASS_TOREPLACE implements org.drools.model.functions.Block3<org.drools.model.Drools, org.drools.modelcompiler.domain.Pet, org.drools.modelcompiler.domain.Person>, org.drools.model.functions.HashedExpression {\n" +
                "\n" +
                "    INSTANCE;\n" +
                "    public static final String EXPRESSION_HASH = \"2ABFB3D359AC0D0C1F6C1BAF91E05544\";\n" +
                "    public java.lang.String getExpressionHash() {\n" +
                "        return EXPRESSION_HASH;\n" +
                "    }" +
                "    private final org.drools.model.BitMask mask_$person = org.drools.model.BitMask.getPatternMask(DomainClassesMetadataB45236F6195B110E0FA3A5447BC53274.org_drools_modelcompiler_domain_Person_Metadata_INSTANCE, \"name\");\n" +
                "\n" +
                "    private final org.drools.model.BitMask mask_$pet = org.drools.model.BitMask.getPatternMask(DomainClassesMetadataB45236F6195B110E0FA3A5447BC53274.org_drools_modelcompiler_domain_Pet_Metadata_INSTANCE, \"age\");\n" +
                "\n" +
                "    @Override()\n" +
                "    public void execute(org.drools.model.Drools drools, org.drools.modelcompiler.domain.Pet $pet, org.drools.modelcompiler.domain.Person $person) throws java.lang.Exception {\n" +
                "        {\n" +
                "            ($person).setName(\"George\");\n" +
                "            drools.update($person, mask_$person);\n" +
                "            ($pet).setAge($pet.getAge() + 1);\n" +
                "            drools.update($pet, mask_$pet);\n" +
                "        }\n" +
                "    }\n" +
                "}";
        // Workaround to keep the "//language=JAVA" working
        expectedResult = expectedResult
                .replace("PACKAGE_TOREPLACE", expectedPackageName)
                .replace("CLASS_TOREPLACE", expectedClassName);

        verifyCreatedClass(aClass, expectedResult);
    }

    @Test
    public void createConsequenceWithMultipleFieldUpdate() {
        ArrayList<String> fields = new ArrayList<>();
        fields.add("\"age\"");
        fields.add("\"likes\"");
        MaterializedLambda.BitMaskVariable bitMaskVariable = new MaterializedLambda.BitMaskVariableWithFields("DomainClassesMetadata53448E6B9A07CB05B976425EF329E308.org_drools_modelcompiler_domain_Person_Metadata_INSTANCE", fields, "mask_$p");

        String consequenceBlock = "(org.drools.model.Drools drools, org.drools.modelcompiler.domain.Person $p) -> {{ ($p).setAge($p.getAge() + 1); ($p).setLikes(\"Cheese\"); drools.update($p,mask_$p); }}";
        CreatedClass aClass = new MaterializedLambdaConsequence("defaultpkg",
                                                                "defaultpkg.Rules53448E6B9A07CB05B976425EF329E308",
                                                                Arrays.asList(bitMaskVariable))
                .create(consequenceBlock, new ArrayList<>(), new ArrayList<>());

        String classNameWithPackage = aClass.getClassNameWithPackage();
        // There is no easy way to retrieve the originally created "hashcode" because it is calculated over a CompilationUnit that soon after is modified;
        // so current "CreatedClass" contains a CompilationUnit that is different from the one used to calculate the hashcode
        String expectedPackageName = classNameWithPackage.substring(0, classNameWithPackage.lastIndexOf('.'));
        String expectedClassName = classNameWithPackage.substring(classNameWithPackage.lastIndexOf('.')+1);
        //language=JAVA
        String expectedResult = "" +
                "package PACKAGE_TOREPLACE;\n" +
                "\n" +
                "import static defaultpkg.Rules53448E6B9A07CB05B976425EF329E308.*;\n" +
                "import org.drools.modelcompiler.dsl.pattern.D;\n" +
                "\n" +
                "@org.drools.compiler.kie.builder.MaterializedLambda()\n" +
                "public enum CLASS_TOREPLACE implements org.drools.model.functions.Block2<org.drools.model.Drools, org.drools.modelcompiler.domain.Person>, org.drools.model.functions.HashedExpression {\n" +
                "\n" +
                "    INSTANCE;\n" +
                "    public static final String EXPRESSION_HASH = \"15102979E2E45F1A4617C12D3517D6B5\";\n" +
                "     public java.lang.String getExpressionHash() {\n" +
                "        return EXPRESSION_HASH;\n" +
                "    }" +
                "    private final org.drools.model.BitMask mask_$p = org.drools.model.BitMask.getPatternMask(DomainClassesMetadata53448E6B9A07CB05B976425EF329E308.org_drools_modelcompiler_domain_Person_Metadata_INSTANCE, \"age\", \"likes\");\n" +
                "\n" +
                "    @Override()\n" +
                "    public void execute(org.drools.model.Drools drools, org.drools.modelcompiler.domain.Person $p) throws java.lang.Exception {\n" +
                "        {\n" +
                "            ($p).setAge($p.getAge() + 1);\n" +
                "            ($p).setLikes(\"Cheese\");\n" +
                "            drools.update($p, mask_$p);\n" +
                "        }\n" +
                "    }\n" +
                "}";
        // Workaround to keep the "//language=JAVA" working
        expectedResult = expectedResult
                .replace("PACKAGE_TOREPLACE", expectedPackageName)
                .replace("CLASS_TOREPLACE", expectedClassName);

        verifyCreatedClass(aClass, expectedResult);
    }
}