package org.drools.modelcompiler.util.lambdareplace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import static org.drools.modelcompiler.util.lambdareplace.Util.newLine;

public class MaterializedLambdaConsequenceTest {

    PostProcessedCompare postProcessedCompare = new PostProcessedCompare();

    @Test
    public void createConsequence() {
        CreatedClass aClass = new MaterializedLambdaConsequence("org.drools.modelcompiler.util.lambdareplace", "rulename", new ArrayList<>())
                .create("(org.drools.modelcompiler.domain.Person p1, org.drools.modelcompiler.domain.Person p2) -> result.setValue( p1.getName() + \" is older than \" + p2.getName())", new ArrayList<>(), new ArrayList<>());

        //language=JAVA
        String expectedResult = "" +
                "package org.drools.modelcompiler.util.lambdareplace;" + newLine()  +
                "import static rulename.*; " +
                "import org.drools.modelcompiler.dsl.pattern.D; " +
                "" + newLine() +
                "@org.drools.compiler.kie.builder.MaterializedLambda() " +
                "public enum LambdaConsequenceF20037424A777A005A60E661AB21E036 implements org.drools.model.functions.Block2<org.drools.modelcompiler.domain.Person, org.drools.modelcompiler.domain.Person>  {" + newLine()  +
                "INSTANCE;" + newLine()  +
                "public static final String EXPRESSION_HASH = \"8305FF24AC76CB49E7AAE2C10356A105\";" +
                "        @Override()" + newLine()  +
                "        public void execute(org.drools.modelcompiler.domain.Person p1, org.drools.modelcompiler.domain.Person p2) throws java.lang.Exception {" + newLine()  +
                "            result.setValue(p1.getName() + \" is older than \" + p2.getName());" + newLine()  +
                "        }" + newLine()  +
                "    }" + newLine() ;

        postProcessedCompare.compareIgnoringHash(aClass.getCompilationUnitAsString(), expectedResult);

    }

    @Test
    public void createConsequenceWithDrools() {
        ArrayList<String> fields = new ArrayList<>();
        fields.add("\"age\"");
        MaterializedLambda.BitMaskVariable bitMaskVariable = new MaterializedLambda.BitMaskVariableWithFields("DomainClassesMetadataA3B8DE4BEBF13D94572A10FD20BBE729.org_drools_modelcompiler_domain_Person_Metadata_INSTANCE", fields, "mask_$p");

        CreatedClass aClass = new MaterializedLambdaConsequence("defaultpkg", "defaultpkg.RulesA3B8DE4BEBF13D94572A10FD20BBE729", Collections.singletonList(bitMaskVariable))
                .create("(org.drools.model.Drools drools, org.drools.modelcompiler.domain.Person $p) -> {{($p).setAge($p.getAge() + 1); drools.update($p, mask_$p);}}", new ArrayList<>(), new ArrayList<>());

        //language=JAVA
        String expectedResult = "" +
                "package defaultpkg;" + newLine()  +
                "import static defaultpkg.RulesA3B8DE4BEBF13D94572A10FD20BBE729.*; " +
                "import org.drools.modelcompiler.dsl.pattern.D; " +
                "" + newLine() +
                "@org.drools.compiler.kie.builder.MaterializedLambda() " +
                "public enum LambdaConsequenceB120B8921B17BB89EC2989BC02FAE9FF implements org.drools.model.functions.Block2<org.drools.model.Drools, org.drools.modelcompiler.domain.Person>  {" + newLine()  +
                "        INSTANCE;" + newLine()  +
                "        public static final String EXPRESSION_HASH = \"1FE08C27A04F37AADD1A62E562519E8D\";" + newLine()  +
                "        private final org.drools.model.BitMask mask_$p = org.drools.model.BitMask.getPatternMask(DomainClassesMetadataA3B8DE4BEBF13D94572A10FD20BBE729.org_drools_modelcompiler_domain_Person_Metadata_INSTANCE, \"age\");" + newLine()  +
                "        @Override()" + newLine()  +
                "        public void execute(org.drools.model.Drools drools, org.drools.modelcompiler.domain.Person $p) throws java.lang.Exception {" + newLine()  +
                "            {" + newLine()  +
                "                ($p).setAge($p.getAge() + 1);" + newLine()  +
                "                drools.update($p, mask_$p);" + newLine()  +
                "            }" +
                "        }" + newLine()  +
                "    }" + newLine() ;

        postProcessedCompare.compareIgnoringHash(aClass.getCompilationUnitAsString(), expectedResult);
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

        //language=JAVA
        String expectedResult = "" +
                "package defaultpkg;" + newLine()  +
                "import static defaultpkg.RulesB45236F6195B110E0FA3A5447BC53274.*; " +
                "import org.drools.modelcompiler.dsl.pattern.D; " +
                "" + newLine() +
                "@org.drools.compiler.kie.builder.MaterializedLambda() " +
                "public enum LambdaConsequence15D5E14C8AF75D1EE585FFA4A0764AEE implements org.drools.model.functions.Block3<org.drools.model.Drools, org.drools.modelcompiler.domain.Pet, org.drools.modelcompiler.domain.Person> {" + newLine()  +
                "" + newLine()  +
                "    INSTANCE;" + newLine()  +
                "    public static final String EXPRESSION_HASH = \"2ABFB3D359AC0D0C1F6C1BAF91E05544\";" + newLine()  +
                "    private final org.drools.model.BitMask mask_$person = org.drools.model.BitMask.getPatternMask(DomainClassesMetadataB45236F6195B110E0FA3A5447BC53274.org_drools_modelcompiler_domain_Person_Metadata_INSTANCE, \"name\");" + newLine()  +
                "" + newLine()  +
                "    private final org.drools.model.BitMask mask_$pet = org.drools.model.BitMask.getPatternMask(DomainClassesMetadataB45236F6195B110E0FA3A5447BC53274.org_drools_modelcompiler_domain_Pet_Metadata_INSTANCE, \"age\");" + newLine()  +
                "" + newLine()  +
                "    @Override()" + newLine()  +
                "    public void execute(org.drools.model.Drools drools, org.drools.modelcompiler.domain.Pet $pet, org.drools.modelcompiler.domain.Person $person) throws java.lang.Exception {" + newLine()  +
                "        {" + newLine()  +
                "            ($person).setName(\"George\");" + newLine()  +
                "            drools.update($person, mask_$person);" + newLine()  +
                "            ($pet).setAge($pet.getAge() + 1);" + newLine()  +
                "            drools.update($pet, mask_$pet);" + newLine()  +
                "        }" + newLine()  +
                "    }" + newLine()  +
                "}";

        postProcessedCompare.compareIgnoringHash(aClass.getCompilationUnitAsString(), expectedResult);
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

        //language=JAVA
        String expectedResult = "" +
                "package defaultpkg;" + newLine()  +
                "" + newLine()  +
                "import static defaultpkg.Rules53448E6B9A07CB05B976425EF329E308.*;" + newLine()  +
                "import org.drools.modelcompiler.dsl.pattern.D;" + newLine()  +
                "" + newLine()  +
                "@org.drools.compiler.kie.builder.MaterializedLambda()" + newLine()  +
                "public enum LambdaConsequenceC18D5E4E2DE9171EE73FFB2D81B29555 implements org.drools.model.functions.Block2<org.drools.model.Drools, org.drools.modelcompiler.domain.Person> {" + newLine()  +
                "" + newLine()  +
                "    INSTANCE;" + newLine()  +
                "    public static final String EXPRESSION_HASH = \"15102979E2E45F1A4617C12D3517D6B5\";" + newLine()  +
                "    private final org.drools.model.BitMask mask_$p = org.drools.model.BitMask.getPatternMask(DomainClassesMetadata53448E6B9A07CB05B976425EF329E308.org_drools_modelcompiler_domain_Person_Metadata_INSTANCE, \"age\", \"likes\");" + newLine()  +
                "" + newLine()  +
                "    @Override()" + newLine()  +
                "    public void execute(org.drools.model.Drools drools, org.drools.modelcompiler.domain.Person $p) throws java.lang.Exception {" + newLine()  +
                "        {" + newLine()  +
                "            ($p).setAge($p.getAge() + 1);" + newLine()  +
                "            ($p).setLikes(\"Cheese\");" + newLine()  +
                "            drools.update($p, mask_$p);" + newLine()  +
                "        }" + newLine()  +
                "    }" + newLine()  +
                "}";

        postProcessedCompare.compareIgnoringHash(aClass.getCompilationUnitAsString(), expectedResult);
    }
}