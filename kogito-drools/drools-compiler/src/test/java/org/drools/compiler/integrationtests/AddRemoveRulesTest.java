package org.drools.compiler.integrationtests;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.io.StringReader;
import java.util.Collection;

import static org.junit.Assert.fail;


public class AddRemoveRulesTest {

    public String ruleNormal1 = "rule 'rn1' "+
                                "when "+
                                "$c : Counter(id==1)"+
                                "then "+
                                "System.out.println('RN1 fired!!!'); \n"+
                                " end ";

    public String ruleNormal2 = "rule 'rn2' "+
                                "when "+
                                "$c : Counter(id==1)"+
                                "then "+
                                "System.out.println('RN2 fired!!!'); \n"+
                                " end ";

    public String ruleNormal3 = "rule 'rn3' "+
                                "when "+
                                "$c : Counter(id==1)"+
                                "then "+
                                "System.out.println('RN3 + fired!!!'); \n"+
                                " end ";


    String rule = "rule 'test' "+
                  "when "+
                  "$c : Counter(id==1)"+
                  "eval(Integer.parseInt(\"5\")==$c.getId()) \n"+
                  "eval(Integer.parseInt(\"10\")>5) "+
                  "then "+
                  "System.out.println('TEST 1 fired!!!');"+
                  "end ";

    public String rule2 = "rule 'test2' "+
                          "when "+
                          "$c : Counter(id==2)"+
                          "eval(Integer.parseInt(\"10\")==$c.getId()) \n"+
                          "eval(Integer.parseInt(\"20\")>10) "+
                          "then "+
                          "System.out.println('TEST 2 fired!!!'); \n"+
                          " end ";

    public String rule3 = "rule 'test3' "+
                          "when "+
                          "$c : Counter(id==3)"+
                          "eval(Integer.parseInt(\"15\")==$c.getId()) \n"+
                          "eval(Integer.parseInt(\"30\")>20) "+
                          "then "+
                          "System.out.println('TEST 2 fired!!!'); \n"+
                          " end ";

    public String rule4 = "rule 'test4' "+
                          "when "+
                          "$c : Counter(id==4)"+
                          "eval(Integer.parseInt(\"20\")==$c.getId()) \n"+
                          "eval(Integer.parseInt(\"40\")>30) "+
                          "then "+
                          "System.out.println('TEST 2 fired!!!'); \n"+
                          " end ";

    public String rule5 = "rule 'test5' "+
                          "when "+
                          "$c : Counter(id==5)"+
                          "eval(Integer.parseInt(\"25\")==$c.getId()) \n"+
                          "eval(Integer.parseInt(\"50\")>40) "+
                          "then "+
                          "System.out.println('TEST 2 fired!!!'); \n"+
                          " end ";

    public String rule6 = "rule 'test6' "+
                          "when "+
                          "$c : Counter(id==6)"+
                          "eval(Integer.parseInt(\"30\")==$c.getId()) \n"+
                          "eval(Integer.parseInt(\"60\")>50) "+
                          "then "+
                          "System.out.println('TEST 2 fired!!!'); \n"+
                          " end ";



    private KnowledgeBase base = KnowledgeBaseFactory.newKnowledgeBase();

    public final static String packageName = "com.rules";


    public String getPrefix() {
        return "package "+packageName+" \n"+
               "import java.util.Map;\n"+
               "import java.util.HashMap;\n"+
               "import org.slf4j.Logger;\n"+
               "import java.util.Date;\n"+
               "import code.*;\n"+

               "declare Counter \n"+
               "@role(event)\n"+
               " id : int \n"+
               "\n"+
               "end\n\n";
    }

    private boolean loadRule(String rule)  {
        String prefix = getPrefix();
        prefix += rule;

        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder.add( ResourceFactory.newReaderResource( new StringReader( prefix ) ), ResourceType.DRL);
        Collection<KnowledgePackage> pkgs = this.buildKnowledge(builder);
        this.addKnowledgeToBase(pkgs);

        return true;
    }

    public boolean addRuleToEngine(String rule)  {
        this.loadRule(rule);
        return true;
    }

    public boolean deleteRule(String name) {
        this.base.removeRule(packageName, name);
        for(KiePackage kp : this.base.getKiePackages())
            for( Rule r : kp.getRules())
                System.out.println(r.getName()+" "+r.getPackageName());
        return true;

    }

    private Collection<KnowledgePackage> buildKnowledge(KnowledgeBuilder builder)  {
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        return builder.getKnowledgePackages();
    }

    private void addKnowledgeToBase(Collection<KnowledgePackage> pkgs) {
        this.base.addKnowledgePackages( pkgs );
    }






    @Test
    public void test() throws Exception {
        KieSession knowledgeSession = base.newKieSession();
        knowledgeSession.fireAllRules();

        addRuleToEngine( ruleNormal1 );
        addRuleToEngine(ruleNormal2);
        addRuleToEngine(ruleNormal3);

        addRuleToEngine(rule);
        addRuleToEngine(rule2);
        addRuleToEngine(rule3);
        addRuleToEngine(rule4);
        addRuleToEngine(rule5);
        addRuleToEngine(rule6);

        System.out.println("Primary remove");
        deleteRule("test6");

        addRuleToEngine(rule6);

        System.out.println("Secondary remove");
        deleteRule("test6");
    }

    @Test
    public void testAddRemoveFromKB() {
        // DROOLS-328
        String drl = "\n" +
                     "rule A\n" +
                     "  when\n" +
                     "    Double() from entry-point \"AAA\"\n" +
                     "  then\n" +
                     "  end\n" +
                     "\n" +
                     "rule B\n" +
                     "  when\n" +
                     "    Boolean()\n" +
                     "    Float()\n" +
                     "  then\n" +
                     "  end\n" +
                     "\n" +
                     "\n" +
                     "rule C\n" +
                     "  when\n" +
                     "  then\n" +
                     "    insertLogical( new Float( 0.0f ) );\n" +
                     "  end\n" +
                     "\n" +
                     "\n" +
                     "rule D\n" +
                     "  when\n" +
                     "    Byte( )\n" +
                     "    String( )\n" +
                     "  then\n" +
                     "  end\n" +
                     "\n" +
                     "\n" +
                     "rule E\n" +
                     "  when\n" +
                     "    Float()\n" +
                     "  then\n" +
                     "    insertLogical( \"foo\" );\n" +
                     "  end\n" +
                     "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        // Create kSession and initialize it
        StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession();
        kSession.fireAllRules();

        kSession.getKieBase().addKnowledgePackages( kbuilder.getKnowledgePackages() );

    }

    @Test
    public void testAddRemoveDeletingFact() {
        // DROOLS-328
        String drl = "\n" +
                     "rule B\n" +
                     "  when\n" +
                     "    Boolean()\n" +
                     "    Float()\n" +
                     "  then\n" +
                     "  end\n" +
                     "\n" +
                     "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        // Create kSession and initialize it
        StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession();
        FactHandle fh = kSession.insert(new Float( 0.0f ) );
        kSession.fireAllRules();

        kSession.getKieBase().addKnowledgePackages( kbuilder.getKnowledgePackages() );
        kSession.delete(fh);
    }


}
