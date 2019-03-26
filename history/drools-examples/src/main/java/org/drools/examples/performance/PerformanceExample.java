package org.drools.examples.performance;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import java.util.ArrayList;

public class PerformanceExample {
    public static void main(final String[] args) throws Exception {
        final long numberOfRulesToBuild = 10;
        boolean useAccumulate = true;
        String dialect = "mvel"; //noticed performance difference between java and mvel dialects
        boolean usekjars = false;
        boolean collectionBasedRules = true;

        System.out.println("********* Numbers of rules: " + numberOfRulesToBuild + " kjars: " + usekjars + " accumulate: " + useAccumulate + " dialect: " + dialect + " *********");
        String rules = getRules(numberOfRulesToBuild, useAccumulate, dialect, collectionBasedRules);
        //System.out.println(rules);
        long startTime = System.currentTimeMillis();
        StatelessKieSession kSession;
        FactType ft;
        if (usekjars) {
            KieContainer kContainer = loadContainerFromString(rules);
            kSession = kContainer.newStatelessKieSession();
            ft = kContainer.getKieBase().getFactType("org.drools.examples.performance", "TransactionC");
        } else {
            /* Alternative way to load knowledge base without using kjars.
            Found slowness issue with internalInvalidateSegmentPrototype() when number of rules are increased.*/
            KieBase kbase = loadKnowledgeBaseFromString( rules );
            kSession = kbase.newStatelessKieSession();
            ft = kbase.getFactType("org.drools.examples.performance", "TransactionC");
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Total time to build and load knowledgebase: " + (endTime - startTime)  + " ms" );


        ArrayList output = new ArrayList();
        kSession.setGlobal("mo", output);
        Object o = ft.newInstance();
        Gson gConverter = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        Object fo = gConverter.fromJson(getFact(), o.getClass());
        kSession.execute(fo); //initial execute
        startTime = System.currentTimeMillis();
        kSession.execute(fo);
        endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (endTime - startTime)  + " ms" );
        String rulesOutput = gConverter.toJson(output);
        System.out.println(rulesOutput);

    }

    private static KieContainer loadContainerFromString(String rules) {
        long startTime = System.currentTimeMillis();
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write("src/main/resources/examples/pertest.drl", rules);

        KieBuilder kb = ks.newKieBuilder(kfs);

        kb.buildAll();
        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time to build rules : " + (endTime - startTime)  + " ms" );
        startTime = System.currentTimeMillis();
        KieContainer kContainer = ks.newKieContainer(kr.getDefaultReleaseId());
        endTime = System.currentTimeMillis();
        System.out.println("Time to load container: " + (endTime - startTime)  + " ms" );
        return kContainer;
    }

    protected static KieBase loadKnowledgeBaseFromString(String... drlContentStrings) {
        long startTime = System.currentTimeMillis();
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for (String drlContentString : drlContentStrings) {
            kbuilder.add(ResourceFactory.newByteArrayResource(drlContentString
                    .getBytes()), ResourceType.DRL);
        }

        if (kbuilder.hasErrors()) {
            throw new RuntimeException("Build Errors:\n" + kbuilder.getErrors());
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time to build rules: " + (endTime - startTime)  + " ms" );

        startTime = System.currentTimeMillis();
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        endTime = System.currentTimeMillis();
        System.out.println("Time to create knowledgebase: " + (endTime - startTime)  + " ms" );

        return kbase;
    }

    private static String getFact() {
        return "{\n" +
                "\"TransactionNumber\": \"88882\",\n" +
                "\"TrackingID\": \"T001\",\n" +
                "\"CurrencyCode\": \"USD\",\n" +
                "\"TransactionNetTotal\" : 100.0,\n" +
                "\"StoreCode\": \"D001\",\n" +
                "\"CardNumber\": \"3614838386\",\n" +
                "\"TransactionDetails\": [\n" +
                "{\n" +
                "\"Quantity\": 25,\n" +
                "\"ItemNumber\": \"SKU1_0\",\n" +
                "\"BrandID\": \"Nike\",\n" +
                "\"SKU\": \"SKU1\",\n" +
                "\"ProductCategoryCode\" : \"Clothing\"\n" +
                "}]\n" +
                "}";
    }

    private static String getRules(long numberofRules, boolean useAccumulate, String dialect, boolean collectionBasedRules) {
        final long startTime = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder("package org.drools.examples.performance;\n");
        sb.append(getImportStatements());
        sb.append("global ArrayList<Outcome> mo;");
        sb.append(getDeclareStatements());
        for (long l =0; l <numberofRules; l++) {
                sb.append(createRule(l, useAccumulate, dialect, collectionBasedRules));
        }
        //sb.append(createRules2("mvel"));
        final long endTime = System.currentTimeMillis();
        System.out.println("Time to generate: " + (endTime - startTime) + " ms");
        return sb.toString();
    }

    private static String createRule(long number, boolean useAccumulate, String dialect, boolean collectionBasedRules) {
        if (collectionBasedRules) {
            return createCollectionRule( number, useAccumulate, dialect );
        } else {
            return createRule( number, useAccumulate, dialect );
        }
    }

    private static String createRule(long number, boolean useAccumulate, String dialect) {
        String s = "" +
                "rule \"rule" + number + "\" \n";
                if (!dialect.isEmpty()) {
                    s = s + "dialect \"" + dialect + "\"\n";
                }
                s = s + "when   t : TransactionC(CurrencyCode == \"USD" + number + "\") \n";
                if (useAccumulate) {
                    s = s + "accumulate($item:TransactionDetailsC() from t.TransactionDetails, $totQty: collectList($item.getQuantity()))\n";
                }
                s = s + "then \n" +
                "mo.add(new Outcome(\"rule" + number + "\", t.getTransactionNumber()));\n" +
                "end \n" ;
        return s;
    }

    private static String createCollectionRule(long number, boolean useAccumulate, String dialect) {
        long NumOfSKU = 10;
        String sku = "";
        String prefix = "";
        for (long l =0; l <NumOfSKU; l++) {
            sku += prefix + "\"SKU" + number + "_" + l + "\"";
            prefix = ",";
        }

        String s = "" +
                "rule \"rule" + number + "\" \n";
                if (!dialect.isEmpty()) {
                    s = s + "dialect \"" + dialect + "\"\n";
                }
                s = s + "when   t : TransactionC() \n" +
                //"d: TransactionDetailsC(ItemNumber == \"SKU" + number + "\") from t.TransactionDetails \n";
                "d: TransactionDetailsC(ItemNumber in (" + sku  + ")) from t.TransactionDetails \n";
                if (useAccumulate) {
                    s = s + "accumulate($item:TransactionDetailsC(ItemNumber in (" + sku  + ")) from t.TransactionDetails, $totQty: collectList($item.getQuantity()))\n";
                }
                s = s + "then \n" +
                "mo.add(new Outcome(\"rule" + number + "\", d.getBrandID()));\n" +
                "end \n" ;
        return s;
    }

    private static String createRules2(String dialect) {
        return "" +
                "rule \"r1\"\n" +
                "dialect \"" + dialect + "\"\n" +
                "when   t : TransactionC(CurrencyCode == \"USD\") \n" +
                "then \n" +
                "mo.add(new Outcome(\"r1\" , t.getTransactionNumber()));\n" +
                "end \n" +
                "rule \"r2\"\n" +
                "dialect \"" + dialect + "\"\n" +
                "when   t : TransactionC(CurrencyCode == \"USD\") \n" +
                "then \n" +
                "mo.add(new Outcome(\"r2\" , t.getTransactionNumber()));\n" +
                "end \n" +
                "rule \"r3\"\n" +
                "dialect \"" + dialect + "\"\n" +
                "when   t : TransactionC(CurrencyCode == \"CAD\") \n" +
                "then \n" +
                "mo.add(new Outcome(\"r3\", t.getTransactionNumber()));\n" +
                "end \n" +
                "rule \"r4\"\n" +
                "dialect \"" + dialect + "\"\n" +
                "when   t : TransactionC(CurrencyCode == \"USD\") \n" +
                "then \n" +
                "mo.add(new Outcome(\"r4\", t.getTransactionNumber()));\n" +
                "end \n";

    }

    private static String getDeclareStatements() {
        return "" +
                "declare TransactionC \n" +
                "CardNumber : String \n" +
                "StoreCode : String \n" +
                "TrackingID : String \n" +
                "CurrencyCode : String \n" +
                "TransactionNetTotal : Double \n" +
                "TransactionNumber : String \n" +
                "TransactionDetails : TransactionDetailsC[] \n" +
                "end \n" +
                "declare TransactionDetailsC \n" +
                "ItemNumber : String \n" +
                "BrandID : String \n" +
                "SKU : String \n" +
                "ProductCategoryCode : String \n" +
                "Quantity : Double \n" +
                "end\n" +
                "declare Outcome \n" +
                "RuleId : String \n" +
                "OutcomeValue : String \n" +
                "end \n";
    }

    private static String getImportStatements() {
        return "import java.util.ArrayList \n" +
                "import java.util.List \n";
    }
}
