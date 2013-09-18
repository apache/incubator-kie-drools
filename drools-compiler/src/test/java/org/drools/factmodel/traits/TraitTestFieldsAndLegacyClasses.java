package org.drools.factmodel.traits;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.type.FactType;
import org.drools.factmodel.FieldDefinition;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.*;


@RunWith(Parameterized.class)
public class TraitTestFieldsAndLegacyClasses {

    public TraitFactory.VirtualPropertyMode mode;

    @Parameterized.Parameters
        public static Collection modes() {
        return Arrays.asList( new TraitFactory.VirtualPropertyMode[][]
                                      {
                                              { TraitFactory.VirtualPropertyMode.MAP },
                                              { TraitFactory.VirtualPropertyMode.TRIPLES }
                                      } );
    }

    public TraitTestFieldsAndLegacyClasses( TraitFactory.VirtualPropertyMode m ) {
        this.mode = m;
    }


    @Test
    public void testTraitFieldUpdate0() {

        String drl = "" +
                     "package org.drools.factmodel.traits0;\n" +
                     "\n"+
                     "import org.drools.factmodel.traits.Traitable;\n"+
                     "import org.drools.factmodel.traits.Thing;\n"+
                     "import java.util.*\n"+
                     "import org.drools.factmodel.traits.TraitTestFieldsAndLegacyClasses.Parent;\n"+
                     "import org.drools.factmodel.traits.TraitTestFieldsAndLegacyClasses.Child;\n"+
                     "global java.util.List list;\n"+
                     "\n"+
                     "declare trait ParentTrait\n" +
                     "@propertyReactive\n" +
                     "    child : Child\n" +
                     "    age : int = 24\n" +
                     "end\n"+

                     "declare Parent\n" +
                     "@Traitable\n" +
                     "@propertyReactive\n" +
                     "end\n" +

                     "rule \"Init\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "    \n" +
                     "then\n" +
                     "   Parent p = new Parent(\"parent\", null);\n"+
                     "   Map map = new HashMap();\n"+
                     "   map.put( \"parent\", ParentTrait.class );\n"+
                     "   insert(p);\n"+
                     "   insert(map);\n"+
                     "   System.out.println(\"Initialized : \"+p);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"trait parent\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "   $p : Parent( name == \"parent\" )\n" +
                     "   $map : HashMap([parent] != null)\n"+
                     "then\n" +
                     "   Object p = don ( $p , (Class) $map.get(\"parent\") );\n"+
                     "   System.out.println(\"donned : \"+p);\n" +
                     "   System.out.println(\">>>>>>\"+$map.get(\"parent\"));\n" +
                     "   list.add(\"correct\");\n"+
                     "end\n"+
                     "\n"+
                     "\n";


        KnowledgeBase kBase = loadKnowledgeBaseFromString(drl);
        TraitFactory.setMode( mode, kBase );

        StatefulKnowledgeSession knowledgeSession = kBase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        knowledgeSession.setGlobal("list", list);

        knowledgeSession.fireAllRules();
        assertTrue(list.contains("correct"));
        assertEquals( 1, list.size() );
    }




    @Test
    public void testTraitFieldUpdate1() {

        String drl = "" +
                     "package org.drools.factmodel.traits;\n" +
                     "\n"+
                     "import org.drools.factmodel.traits.Traitable;\n"+
                     "import org.drools.factmodel.traits.Trait;\n" +
                     "" +
                     "global java.util.List list;\n"+
                     "\n"+
                     "declare trait ParentTrait\n" +
                     "@propertyReactive\n" +
                     "    child : ChildTrait\n" +    //<<<<<<<
                     "    age : int = 24\n" +
                     "end\n"+

                     "declare trait ChildTrait\n"+
                     "@propertyReactive\n"+
                     "   name : String = \"child\"\n"+
                     "end\n"+

                     "declare Parent\n" +
                     "@Traitable( logical = true ) \n" +
                     "@propertyReactive\n" +
                     "   name : String\n"+
                     "   child : Child\n"+
                     "end\n" +

                     "declare Child\n" +
                     "@Traitable \n" +
                     "@propertyReactive\n" +
                     "   sex : String = \"male\"\n"+
                     "end\n"+
                     "\n"+

                     "rule \"Init\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "    \n" +
                     "then\n" +
                     "   Child c = new Child();\n"+
                     "   Parent p = new Parent(\"parent\",c);\n"+
                     "   insert(c);insert(p);\n"+
                     "   System.out.println(\"Initialized : \"+p);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"trait parent\" \n" +
                     "no-loop\n" +
                     "salience -1\n"+
                     "when\n" +
                     "    $p : Parent( name == \"parent\" )\n" +
                     "then\n" +
                     "   ParentTrait p = don ( $p , ParentTrait.class );\n"+
                     "   System.out.println(\"donned : \"+p);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"trait child\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "    $c : Child( sex == \"male\" )\n" +
                     "then\n" +
                     "   ChildTrait c = don ( $c , ChildTrait.class );\n"+
                     "   System.out.println(\"donned : \"+c);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"test parent and child traits\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "    $p : ParentTrait( $c : child isA ChildTrait.class )\n" +
                     "then\n" +
                     "   //shed ( $p , ParentTrait.class );\n"+
                     "   System.out.println(\"ParentTrait( child isA ChildTrait.class ) \"+$c);\n" +
                     "   list.add(\"correct\");\n"+
                     "end\n"+
                     "\n"+
                     "\n";

        KnowledgeBase kBase = loadKnowledgeBaseFromString(drl);
        TraitFactory.setMode( mode, kBase );

        StatefulKnowledgeSession knowledgeSession = kBase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        knowledgeSession.setGlobal("list", list);


        knowledgeSession.fireAllRules();
        assertTrue(list.contains("correct"));
        assertEquals( 1, list.size() );

    }

    @Test
    public void testTraitFieldUpdate2() {

        String drl = "" +
                     "package org.drools.factmodel.traits2;\n" +
                     "\n"+
                     "import org.drools.factmodel.traits.Traitable;\n"+
                     "global java.util.List list;\n"+
                     "\n"+
                     "declare trait ParentTrait\n" +
                     "@propertyReactive\n" +
                     "    child : ChildTrait \n" +    //><><><><><
                     "    age : int = 24\n" +
                     "end\n"+

                     "declare trait ChildTrait\n"+
                     "@propertyReactive\n"+
                     "   name : String = \"child\"\n"+
                     "end\n"+

                     "declare Parent\n" +
                     "@Traitable( logical=true )\n" +   //><><><><><
                     "@propertyReactive\n" +
                     "   name : String\n"+
                     "   child : Child\n"+
                     "end\n" +

                     "declare Child\n" +
                     "@Traitable\n" +
                     "@propertyReactive\n" +
                     "   sex : String = \"male\"\n"+
                     "end\n"+
                     "\n"+

                     "rule \"Init\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "    \n" +
                     "then\n" +
                     "   Child c = new Child();\n"+
                     "   Parent p = new Parent(\"parent\", null);\n"+    //<<<<<
                     "   insert(c);insert(p);\n"+
                     "   System.out.println(\"Initialized : \"+p);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"trait parent\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "   $p : Parent( name == \"parent\" )\n" +
                     "then\n" +
                     "   ParentTrait p = don ( $p , ParentTrait.class );\n"+
                     "   System.out.println(\"donned : \"+p);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"trait child\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "   $c : Child( sex == \"male\" )\n" +
                     "then\n" +
                     "   ChildTrait c = don ( $c , ChildTrait.class );\n"+
                     "   System.out.println(\"donned : \"+c);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"assign child to parent\" \n" +          //<<<<<<
                     "no-loop\n" +
                     "when\n" +
                     "   $c : Child( sex == \"male\" )\n" +
                     "   $p : Parent( name == \"parent\" )\n" +
                     "   ParentTrait( child not isA ChildTrait.class )\n" +
                     "   ChildTrait()\n"+
                     "then\n" +
                     "   " +
                     "   modify ( $p ) { \n" +
                     "       setChild($c);\n"+
                     "   }\n"+
                     "   System.out.println(\"child assigned to the parent : \"+$p);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"test parent and child traits\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "    $p : ParentTrait( child isA ChildTrait.class )\n" +
                     "then\n" +
                     "   //shed ( $p , ParentTrait.class );\n"+
                     "   System.out.println(\"::ParentTrait( child isA ChildTrait.class ) \");\n" +
                     "   list.add(\"correct\");\n"+
                     "end\n"+
                     "\n"+
                     "\n";


        KnowledgeBase kBase = loadKnowledgeBaseFromString(drl);
        StatefulKnowledgeSession knowledgeSession = kBase.newStatefulKnowledgeSession();
        TraitFactory.setMode( mode, kBase );

        List list = new ArrayList();
        knowledgeSession.setGlobal("list", list);


        knowledgeSession.fireAllRules();
        assertTrue(list.contains("correct"));
        assertEquals( 1, list.size() );
    }

    @Test
    public void testTraitFieldUpdate3() {

        String drl = "" +
                     "package org.drools.factmodel.traits3;\n" +
                     "\n"+
                     "import org.drools.factmodel.traits.Traitable;\n"+
                     "global java.util.List list;\n"+
                     "\n"+
                     "declare trait ParentTrait\n" +
                     "@propertyReactive\n" +
                     "    child : ChildTrait\n" +
                     "    age : int = 24\n" +
                     "end\n"+

                     "declare trait ChildTrait\n"+
                     "@propertyReactive\n"+
                     "   name : String = \"child\"\n"+
                     "end\n"+

                     "declare Parent\n" +
                     "@Traitable( logical = true )\n" +
                     "@propertyReactive\n" +
                     "   name : String\n"+
                     "   child : Child\n"+
                     "end\n" +

                     "declare Child\n" +
                     "@Traitable\n" +
                     "@propertyReactive\n" +
                     "   sex : String = \"male\"\n"+
                     "end\n"+
                     "\n"+

                     "rule \"Init\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "    \n" +
                     "then\n" +
                     "   Child c = new Child();\n"+
                     "   Parent p = new Parent(\"parent\", null);\n"+
                     "   insert(c);insert(p);\n"+
                     "   System.out.println(\"Initialized : \"+p);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"trait parent\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "   $p : Parent( name == \"parent\" )\n" +
                     "then\n" +
                     "   ParentTrait p = don ( $p , ParentTrait.class );\n"+
                     "   System.out.println(\"donned : \"+p);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"trait child\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "   $c : Child( sex == \"male\" )\n" +
                     "then\n" +
                     "   ChildTrait c = don ( $c , ChildTrait.class );\n"+
                     "   System.out.println(\"donned : \"+c);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"assign child to parent\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "   Child( sex == \"male\" )\n" +
                     "   $p : Parent( name == \"parent\" )\n" +
                     "   ParentTrait( child not isA ChildTrait.class )\n" +
                     "   $c : ChildTrait()\n"+             //<<<<<
                     "then\n" +
                     "   $p.setChild((Child)$c.getCore());\n"+     //<<<<<
                     "   update($p);\n"+
                     "   System.out.println(\"child assigned to the parent : \"+$p);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"test parent and child traits\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "    $p : ParentTrait( child isA ChildTrait.class )\n" +
                     "then\n" +
                     "   //shed ( $p , ParentTrait.class );\n"+
                     "   System.out.println(\"::ParentTrait( child isA ChildTrait.class ) \");\n" +
                     "   list.add(\"correct\");\n"+
                     "end\n"+
                     "\n"+
                     "\n";


        KnowledgeBase kBase = loadKnowledgeBaseFromString(drl);
        TraitFactory.setMode( mode, kBase );
        StatefulKnowledgeSession knowledgeSession = kBase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        knowledgeSession.setGlobal("list", list);


        knowledgeSession.fireAllRules();
        assertTrue(list.contains("correct"));
        assertEquals( 1, list.size() );
    }

    @Test
    public void testTraitFieldUpdate4() {

        String drl = "" +
                     "package org.drools.factmodel.traits4;\n" +
                     "\n"+
                     "import org.drools.factmodel.traits.Traitable;\n"+
                     "global java.util.List list;\n"+
                     "\n"+
                     "declare trait ParentTrait\n" +
                     "@propertyReactive\n" +
                     "    child : ChildTrait\n" +
                     "    age : int = 24\n" +
                     "end\n"+

                     "declare trait ChildTrait\n"+
                     "@propertyReactive\n"+
                     "   name : String = \"child\"\n"+
                     "end\n"+

                     "declare Parent\n" +
                     "@Traitable(logical=true)\n" +          //<<<<<<   @propertyReactive is removed
                     "   name : String\n"+
                     "   child : Child\n"+
                     "end\n" +

                     "declare Child\n" +
                     "@Traitable\n" +
                     "@propertyReactive\n" +
                     "   sex : String = \"male\"\n"+
                     "end\n"+
                     "\n"+

                     "rule \"Init\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "    \n" +
                     "then\n" +
                     "   Child c = new Child();\n"+
                     "   Parent p = new Parent(\"parent\", c);\n"+   //<<<<<
                     "   insert(c);insert(p);\n"+
                     "   System.out.println(\"Initialized : \"+p);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"trait child\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "   $p : Parent( $c := child )\n"+
                     "   $c := Child( sex == \"male\" )\n" +
                     "then\n" +
                     "   ChildTrait c = don ( $c , ChildTrait.class );\n" +
                     "   modify ( $p ) {}; \n"+
                     "   System.out.println(\"donned : \"+c);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"test parent and a child trait\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "    $p : Parent( child isA ChildTrait.class ) \n" +    //<<<<<
                     "then\n" +
                     "   System.out.println(\"::Parent( child isA ChildTrait.class ) \");\n" +
                     "   list.add(\"correct\");\n"+
                     "end\n"+
                     "\n"+
                     "\n";


        KnowledgeBase kBase = loadKnowledgeBaseFromString(drl);
        TraitFactory.setMode( mode, kBase );

        StatefulKnowledgeSession knowledgeSession = kBase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        knowledgeSession.setGlobal("list", list);


        knowledgeSession.fireAllRules();
        assertTrue(list.contains("correct"));
        assertEquals( 1, list.size() );
    }

    @Test
    public void testTraitFieldUpdate5() {

        String drl = "" +
                     "package org.drools.factmodel.traits5;\n" +
                     "\n"+
                     "import org.drools.factmodel.traits.Traitable;\n"+
                     "import org.drools.factmodel.traits.Trait;\n"+

                     "global java.util.List list;\n"+
                     "\n" +
                     "" +
                     ""+
                     "declare trait ParentTrait\n" +
                     "" +
                     "@propertyReactive\n" +
                     "    child : ChildTrait\n" +
                     "    age : int = 24\n" +
                     "end\n"+

                     "declare trait ChildTrait\n" +
                     "@Trait(logical=true) \n"+
                     "@propertyReactive\n"+
                     "   name : String = \"child\"\n"+
                     "end\n"+

                     "declare Parent\n" +
                     "@Traitable(logical=true)\n" +
                     "   name : String\n"+
                     "   child : Child\n"+
                     "end\n" +

                     "declare Child\n" +
                     "@Traitable\n" +
                     "@propertyReactive\n" +
                     "   sex : String = \"male\"\n"+
                     "end\n"+
                     "\n"+

                     "rule \"Init\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "    \n" +
                     "then\n" +
                     "   Child c = new Child();\n"+
                     "   Parent p = new Parent(\"parent\", c);\n"+
                     "   insert(c);insert(p);\n"+
                     "   System.out.println(\"Initialized : \"+p);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"trait parent\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "   $p : Parent( name == \"parent\" )\n" +
                     "then\n" +
                     "   ParentTrait p = don ( $p , ParentTrait.class );\n"+
                     "   System.out.println(\"donned : \"+p);\n" +
                     "end\n"+
                     "\n"+


                     "rule \"test parent and child traits\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "    $p : ParentTrait( $c : child isA ChildTrait.class ) \n" +     //<<<<<
                     "then\n" +
                     "   System.out.println(\"::ParentTrait( child isA ChildTrait.class ) \"+$c);\n" +
                     "   list.add(\"correct\");\n"+
                     "end\n"+
                     "\n"+
                     "\n";


        KnowledgeBase kBase = loadKnowledgeBaseFromString(drl);
        TraitFactory.setMode( mode, kBase );

        StatefulKnowledgeSession knowledgeSession = kBase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        knowledgeSession.setGlobal("list", list);


        knowledgeSession.fireAllRules();
        assertTrue(list.contains("correct"));
        assertEquals( 1, list.size() );
    }

    @Test
    public void testTraitFieldUpdate6() {

        String drl = "" +
                     "package org.drools.factmodel.traits6;\n" +
                     "\n"+
                     "import org.drools.factmodel.traits.Traitable;\n"+
                     "import org.drools.factmodel.traits.Trait;\n"+
                     "import org.drools.factmodel.traits.Thing;\n"+
                     "import org.drools.factmodel.traits.TraitTestFieldsAndLegacyClasses.Child;\n"+       //<<<<<<
                     "global java.util.List list;\n"+
                     "\n"+
                     "declare trait ParentTrait\n" +
                     "@propertyReactive\n" +
                     "    child : ChildTrait\n" +
                     "    age : int = 24\n" +
                     "end\n"+

                     "declare trait ChildTrait\n"+
                     "@Trait(logical=true) \n"+
                     "@propertyReactive\n"+
                     "   name : String = \"child\"\n"+
                     "end\n"+

                     "declare Parent\n" +
                     "@Traitable(logical=true)\n" +
                     "@propertyReactive\n" +
                     "   name : String\n"+
                     "   child : Child\n"+
                     "end\n" +

                     "declare Child\n" +               //<<<<<
                     "@Traitable(logical=true)\n" +
                     "@propertyReactive\n" +
                     //"   sex : String = \"male\"\n"+
                     "end\n"+
                     "\n"+

                     "rule \"Init\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "    \n" +
                     "then\n" +
                     "   Child c = new Child();\n"+
                     "   Parent p = new Parent(\"parent\", c);\n"+
                     "   insert(c);insert(p);\n"+
                     "   System.out.println(\"Initialized : \"+p);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"trait parent\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "   $p : Parent( name == \"parent\" )\n" +
                     "then\n" +
                     "   ParentTrait p = don ( $p , ParentTrait.class );\n"+
                     "   System.out.println(\"donned : \"+p);\n" +
                     "   System.out.println(\"original: \"+$p);\n" +
                     "end\n"+
                     "\n" +
                     "" +
                     "rule \"Side effect\" \n" +
                     "when \n" +
                     "  $p : Parent( child isA ChildTrait ) \n" +
                     "then \n" +
                     "  System.out.println( 10000000 ); \n" +
                     "   list.add(\"correct2\");\n"+
                     "end \n"+



//                     "rule \"trait and assign the child\" \n" +
//                     "no-loop\n" +
//                     "when\n" +
//                     "   $c : Child( sex == \"male\" )\n" +
//                     "   $p : Parent( this isA ParentTrait )\n" +       //<<<<<
//                     "then\n" +
//                     "   ChildTrait c = don ( $c , ChildTrait.class );\n"+
//                     "   modify($p){\n"+
//                     "       setChild((Child)c.getCore());}\n"+
//                     "   System.out.println(\"donned : \"+c);\n" +
//                     "end\n"+
//                     "\n"+

                     "rule \"test parent and child traits\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "    $p : ParentTrait( child isA ChildTrait.class )\n" +
                     "then\n" +
                     "   //shed ( $p , ParentTrait.class );\n"+
                     "   System.out.println(\"::ParentTrait( child isA ChildTrait.class ) \");\n" +
                     "   list.add(\"correct\");\n"+
                     "end\n"+
                     "\n"+
                     "\n"+
                     "\n";


        KnowledgeBase kBase = loadKnowledgeBaseFromString(drl);
        TraitFactory.setMode( mode, kBase );

        StatefulKnowledgeSession knowledgeSession = kBase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        knowledgeSession.setGlobal("list", list);


        knowledgeSession.fireAllRules();

        for ( Object o : knowledgeSession.getObjects() ) {
            System.out.print( o );
        }
        assertTrue(list.contains("correct"));
        assertTrue(list.contains("correct2"));
        assertEquals( 2, list.size() );
    }


    @Test
    public void testTraitFieldUpdate7() {

        String drl = "" +
                     "package org.drools.factmodel.traits;\n" +
                     "\n"+
                     "import org.drools.factmodel.traits.Traitable;\n"+
                     "import org.drools.factmodel.traits.Trait;\n"+
                     "import org.drools.factmodel.traits.Thing;\n"+
                     "import org.drools.factmodel.traits.TraitTestFieldsAndLegacyClasses.Child;\n"+
                     "import org.drools.factmodel.traits.TraitTestFieldsAndLegacyClasses.Parent;\n"+  //<<<<<
                     "global java.util.List list;\n"+
                     "\n"+
                     "declare trait ParentTrait\n" +
                     "@propertyReactive\n" +
                     "    child : ChildTrait  @position(1)\n" +      //<<<<<
                     "    age : int = 24 @position(0)\n" +
                     "end\n"+

                     "declare trait ChildTrait\n" +
                     "@Trait( logical = true ) \n"+
                     "@propertyReactive\n"+
                     "   name : String = \"child\"\n"+
                     "   sex : String\n"+
                     "end\n"+

                     "declare Parent\n" +
                     "@Traitable( logical=true ) \n" +
                     "@propertyReactive\n" +
                     "end\n" +

                     "declare Child\n" +
                     "@Traitable( logical=true ) \n" +
                     "@propertyReactive\n" +
                     "end\n"+
                     "\n"+

                     "rule \"Init\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "    \n" +
                     "then\n" +
                     "   Child c = new Child();\n"+
                     "   Parent p = new Parent( \"parent\", c );\n"+
                     "   insert(c); insert(p);\n"+
                     "   System.out.println(\"Initialized : \"+p);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"trait parent\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "   $p : Parent( name == \"parent\" )\n" +
                     "then\n" +
                     "   ParentTrait p = don ( $p , ParentTrait.class );\n"+
                     "   System.out.println(\"donned : \"+p);\n" +
                     "end\n"+
                     "\n"+

//                     "rule \"trait and assign the child\" \n" +
//                     "no-loop\n" +
//                     "when\n" +
//                     "   $c : Child( sex == \"male\" )\n" +
//                     "   $p : Parent( this isA ParentTrait )\n" +
//                     "then\n" +
//                     "   ChildTrait c =  don ( $c , ChildTrait.class );\n"+   //<<<<<<
//                     "   modify($p){\n"+
//                     "       setChild((Child)c.getCore());}\n"+
//                     "   System.out.println(\"donned : \"+c);\n" +
//                     "end\n"+
//                     "\n"+

                     "rule \"test parent and child traits\" \n" +
                     "no-loop\n" +
                     "when\n" +
//                     "   $c : Child( $sex := sex )\n"+
                     "   $p : ParentTrait( child isA ChildTrait )\n" +    //<<<<<
                     "then\n" +
                     "   list.add(\"correct\");\n"+
                     "end\n"+
                     "\n"+
                     "\n";


        KnowledgeBase kBase = loadKnowledgeBaseFromString(drl);
        TraitFactory.setMode( mode, kBase );

        StatefulKnowledgeSession knowledgeSession = kBase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        knowledgeSession.setGlobal("list", list);

        knowledgeSession.fireAllRules();

        for ( Object o : knowledgeSession.getObjects() ) {
            System.out.println( o );
        }

        assertTrue(list.contains("correct"));
        assertEquals( 1, list.size() );
    }




    @Test
    public void testTraitFieldUpdate8() {

        String drl = "" +
                     "package org.drools.factmodel.traits8;\n" +
                     "\n"+
                     "import org.drools.factmodel.traits.Traitable;\n"+
                     "import org.drools.factmodel.traits.Thing;\n"+
                     "import org.drools.factmodel.traits.TraitTestFieldsAndLegacyClasses.Child;\n"+
                     "import org.drools.factmodel.traits.TraitTestFieldsAndLegacyClasses.Parent;\n"+  //<<<<<
                     "global java.util.List list;\n"+
                     "\n"+
                     "declare trait ParentTrait\n" +
                     "@propertyReactive\n" +
                     "    child : ChildTrait\n" +
                     "    age : int = 24\n" +
                     "end\n"+

                     "declare trait ChildTrait\n"+
                     "@propertyReactive\n"+
                     "   name : String = \"child\"\n"+
                     "end\n"+

                     "declare Parent\n" +            //<<<<<
                     "@Traitable(logical=true)\n" +
                     "@propertyReactive\n" +
                     //"   name : String\n"+
                     //"   child : Child\n"+
                     "end\n" +

                     "declare Child\n" +
                     "@Traitable(logical=true)\n" +
                     "@propertyReactive\n" +
                     //"   sex : String = \"male\"\n"+
                     "end\n"+
                     "\n"+

                     "rule \"Init\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "    \n" +
                     "then\n" +
                     "   Child c = new Child();\n"+
                     "   Parent p = new Parent(\"parent\", c);\n"+
                     "   insert(c);insert(p);\n"+
                     "   System.out.println(\"Initialized : \"+p);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"trait parent\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "   $p : Parent( name == \"parent\" )\n" +
                     "then\n" +
                     "   ParentTrait p = don ( $p , ParentTrait.class );\n"+
                     "   System.out.println(\"donned : \"+p);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"test parent and child traits\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "    $p : ParentTrait( child isA ChildTrait.class )\n" +
                     "then\n" +
                     "   //shed ( $p , ParentTrait.class );\n"+
                     "   System.out.println(\"::ParentTrait( child isA ChildTrait.class ) \");\n" +
                     "   list.add(\"correct\");\n"+
                     "end\n"+
                     "\n"+
                     "\n";


        KnowledgeBase kBase = loadKnowledgeBaseFromString(drl);
        TraitFactory.setMode( mode, kBase );

        StatefulKnowledgeSession knowledgeSession = kBase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        knowledgeSession.setGlobal("list", list);


        knowledgeSession.fireAllRules();
        assertTrue(list.contains("correct"));
        assertEquals( 1, list.size() );
    }

    @Test
    public void testTraitFieldUpdate9() {

        String drl = "" +
                     "package org.drools.factmodel.traits9;\n" +
                     "\n"+
                     "import org.drools.factmodel.traits.Traitable;\n"+
                     "import org.drools.factmodel.traits.Thing;\n"+
                     "import org.drools.factmodel.traits.TraitTestFieldsAndLegacyClasses.Child;\n"+
                     "import org.drools.factmodel.traits.TraitTestFieldsAndLegacyClasses.Parent;\n"+  //<<<<<
                     "global java.util.List list;\n"+
                     "\n"+
                     "declare trait ParentTrait\n" +
                     "@propertyReactive\n" +
                     "    child : Child\n" +      //<<<<<
                     "    age : int = 24\n" +
                     "end\n"+

                     "declare trait ChildTrait\n"+
                     "@propertyReactive\n"+
                     "   name : String = \"child\"\n"+
                     "end\n"+

                     "declare Parent\n" +            //<<<<<
                     "@Traitable\n" +
                     "@propertyReactive\n" +
                     "end\n" +

                     "declare Child\n" +
                     "@Traitable\n" +
                     "@propertyReactive\n" +
                     "end\n"+
                     "\n"+

                     "rule \"Init\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "    \n" +
                     "then\n" +
                     "   Child c = new Child();\n"+
                     "   Parent p = new Parent(\"parent\", c);\n"+
                     "   insert(c);insert(p);\n"+
                     "   System.out.println(\"Initialized : \"+p);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"trait parent\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "   $p : Parent( name == \"parent\" )\n" +
                     "then\n" +
                     "   ParentTrait p = don ( $p , ParentTrait.class );\n"+
                     "   System.out.println(\"donned : \"+p);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"trait and assign the child\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "   $c : Child( sex == \"male\" )\n" +
                     "   $p : Parent( this isA ParentTrait )\n" +
                     "then\n" +
                     "   ChildTrait c =  don ( $c , ChildTrait.class );\n"+   //<<<<<<
                     "   modify($p){\n"+
                     "       setChild((Child)c.getCore());}\n"+
                     "   System.out.println(\"donned : \"+c);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"test parent and child traits\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "    $p : ParentTrait( child isA ChildTrait.class, child.sex == \"male\" )\n" +    //<<<<<
                     "then\n" +
                     "   //shed ( $p , ParentTrait.class );\n"+
                     "   System.out.println(\"::ParentTrait( child isA ChildTrait.class ) \");\n" +
                     "   list.add(\"correct\");\n"+
                     "end\n"+
                     "\n"+
                     "\n";


        KnowledgeBase kBase = loadKnowledgeBaseFromString(drl);
        TraitFactory.setMode( mode, kBase );

        StatefulKnowledgeSession knowledgeSession = kBase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        knowledgeSession.setGlobal("list", list);


        knowledgeSession.fireAllRules();
        assertTrue(list.contains("correct"));
        assertEquals( 1, list.size() );
    }

    @Test
    public void testTraitFieldUpdate10() {

        String drl = "" +
                     "package org.drools.factmodel.traits;\n" +
                     "\n"+
                     "import org.drools.factmodel.traits.Traitable;\n"+
                     "import org.drools.factmodel.traits.Thing;\n"+
                     "import org.drools.factmodel.traits.TraitTestFieldsAndLegacyClasses.Child;\n"+
                     "import org.drools.factmodel.traits.TraitTestFieldsAndLegacyClasses.Parent;\n"+  //<<<<<
                     "global java.util.List list;\n"+
                     "\n"+
                     "declare trait ParentTrait\n" +
                     "@propertyReactive\n" +
                     "    child : Child  @position(1)\n" +      //<<<<<
                     "    age : int = 24 @position(0)\n" +
                     "end\n"+

                     "declare trait ChildTrait\n"+
                     "@propertyReactive\n"+
                     "   name : String = \"child\"\n"+
                     "   sex : String\n"+        //<<<<<
                     "end\n"+

                     "declare Parent\n" +            //<<<<<
                     "@Traitable\n" +
                     "@propertyReactive\n" +
                     "end\n" +

                     "declare Child\n" +
                     "@Traitable\n" +
                     "@propertyReactive\n" +
                     "end\n"+
                     "\n"+

                     "rule \"Init\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "    \n" +
                     "then\n" +
                     "   Child c = new Child();\n"+
                     "   Parent p = new Parent(\"parent\", c);\n"+
                     "   insert(c);insert(p);\n"+
                     "   System.out.println(\"Initialized : \"+p);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"trait parent\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "   $p : Parent( name == \"parent\" )\n" +
                     "then\n" +
                     "   ParentTrait p = don ( $p , ParentTrait.class );\n"+
                     "   System.out.println(\"donned : \"+p);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"trait and assign the child\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "   $c : Child( sex == \"male\" )\n" +
                     "   $p : Parent( this isA ParentTrait )\n" +
                     "then\n" +
                     "   ChildTrait c =  don ( $c , ChildTrait.class );\n"+   //<<<<<<
                     "   modify($p){\n"+
                     "       setChild((Child)c.getCore());}\n"+
                     "   System.out.println(\"donned : \"+c);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"test parent and child traits\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "   $c : Child( $sex := sex)\n"+
                     "   $p : ParentTrait( $age, $c; )\n" +    //<<<<<
                     "then\n" +
                     "   System.out.println(\"::ParentTrait(  $age, $sex; ) \"+$age+\" \"+$sex);\n" +
                     "   list.add(\"correct\");\n"+
                     "end\n"+
                     "\n"+
                     "\n";


        KnowledgeBase kBase = loadKnowledgeBaseFromString(drl);
        TraitFactory.setMode( mode, kBase );

        StatefulKnowledgeSession knowledgeSession = kBase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        knowledgeSession.setGlobal("list", list);


        knowledgeSession.fireAllRules();
        assertTrue(list.contains("correct"));
        assertEquals( 1, list.size() );
    }

    @Test
    public void testTraitTwoParentOneChild() {

        String drl = "" +
                     "package org.drools.factmodel.traits;\n" +
                     "\n"+
                     "import org.drools.factmodel.traits.Traitable;\n"+
                     "import org.drools.factmodel.traits.Thing;\n"+
                     "global java.util.List list;\n"+
                     "\n"+
                     "declare trait ParentTrait\n" +
                     "@propertyReactive\n" +
                     "    child : Child  \n" +
                     "    age : int = 24 \n" +
                     "end\n"+

                     "\n"+
                     "declare trait GrandParentTrait\n" +   //<<<<
                     "@propertyReactive\n" +
                     "    grandChild : Child \n" +
                     "    age : int = 64 \n" +
                     "end\n"+

                     "declare trait FatherTrait extends ParentTrait, GrandParentTrait \n"+ //<<<<<
                     "@propertyReactive\n"+
                     "   name : String = \"child\"\n"+
                     "   sex : String\n"+
                     "end\n"+

                     "declare Parent\n" +
                     "@Traitable\n" +
                     "@propertyReactive\n" +
                     "   name : String\n"+
                     "   child : Child\n"+
                     "end\n" +

                     "declare Child\n" +
                     "@Traitable\n" +
                     "@propertyReactive\n" +
                     "   name : String\n" +
                     "   sex : String = \"male\"\n" +
                     "end\n"+
                     "\n"+

                     "rule \"Init\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "    \n" +
                     "then\n" +
                     "   Child c = new Child(\"C1\",\"male\");\n"+
                     "   Child c2 = new Child(\"C2\",\"male\");\n"+        //<<<<
                     "   Parent p = new Parent(\"parent\", c);\n"+
                     "   insert(c);insert(p);\n"+
                     "   insert(c2);\n"+
                     "   System.out.println(\"Initialized : \"+p);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"trait as father\" \n" +
                     "salience -1000\n" +
                     "when\n" +
                     "   $p : Parent( name == \"parent\" )\n" +
                     "then\n" +
                     "   FatherTrait p = don ( $p , FatherTrait.class );\n"+
                     "   System.out.println(\"F donned : \"+p);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"trait as parent\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "   $p : Parent( name == \"parent\" )\n" +
                     "then\n" +
                     "   ParentTrait c =  don ( $p , ParentTrait.class );\n"+   //<<<<<<
                     "   System.out.println(\"P donned : \"+c);\n" +
                     "end\n"+
                     "\n"+

                     "rule \"trait and assign the grandchild\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "   $c : Child( name == \"C1\" )\n" +
                     "   $p : Parent( child == $c )\n" +
                     "then\n" +
                     "   GrandParentTrait c =  don ( $p , GrandParentTrait.class );\n"+   //<<<<<<
                     "   System.out.println(\"G donned : \"+c);\n" +
                     "   modify(c){\n"+
                     "       setGrandChild( $c );}\n"+
                     "end\n"+
                     "\n"+

                     "rule \"test three traits\" \n" +
                     "no-loop\n" +
                     "when\n" +
                     "   $p : FatherTrait( this isA ParentTrait, this isA GrandParentTrait )\n" +    //<<<<<
                     "then\n" +
                     "   System.out.println(\"::FatherTrait( this isA ParentTrait, this isA GrandParentTrait ) \");\n" +
                     "   list.add(\"correct\");\n"+
                     "end\n"+
                     "\n"+
                     "\n";


        KnowledgeBase kBase = loadKnowledgeBaseFromString(drl);
        TraitFactory.setMode( mode, kBase );

        StatefulKnowledgeSession knowledgeSession = kBase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        knowledgeSession.setGlobal("list", list);


        knowledgeSession.fireAllRules();
        assertTrue(list.contains("correct"));
        assertEquals( 1, list.size() );
    }

    @Test
    public void testTraitWithPositionArgs(){

        String drl = "" +
                     "package org.drools.traits.test;\n" +
                     "\n" +
                     "import org.drools.factmodel.traits.Traitable;\n" +
                     "\n" +
                     "\n" +
                     "global java.util.List list;\n" +
                     "\n" +
                     "declare Person\n" +
                     "@Traitable\n" +
                     "@propertyReactive\n" +
                     "    ssn : String\n" +
                     "    pob : String\n" +
                     "    isStudent : boolean\n" +
                     "    hasAssistantship : boolean\n" +
                     "end\n" +
                     "\n" +
                     "declare trait Student\n" +
                     "@propertyReactive\n" +
                     "    studyingCountry : String @position(1)\n" +
                     "    hasAssistantship : boolean\n" +
                     "end\n" +
                     "\n" +
                     "declare trait Worker\n" +
                     "@propertyReactive\n" +
                     "    pob : String @position(0)\n" +
                     "    workingCountry : String\n" +
                     "end\n" +
                     "\n" +
                     "declare trait USCitizen\n" +
                     "@propertyReactive\n" +
                     "    pob : String = \"US\"\n" +
                     "end\n" +
                     "\n" +
                     "declare trait ITCitizen\n" +
                     "@propertyReactive\n" +
                     "    pob : String = \"IT\"\n" +
                     "end\n" +
                     "\n" +
                     "declare trait IRCitizen\n" +
                     "@propertyReactive\n" +
                     "    pob : String = \"IR\"\n" +
                     "end\n" +
                     "\n" +
                     "declare trait StudentWorker extends Student, Worker\n" +
                     "@propertyReactive\n" +
                     "    uniName : String\n" +
                     "end\n" +
                     "\n" +
                     "rule \"init\"\n" +
                     "no-loop\n" +
                     "when\n" +
                     "then\n" +
                     "    Person p = new Person(\"1234\",\"IR\",true,true);\n" +
                     "    insert( p );\n" +
                     "    System.out.println(\"init\");\n" +
                     "    list.add(\"initialized\");\n" +
                     "\n" +
                     "end\n" +
                     "\n" +
                     "rule \"check for being student\"\n" +
                     "no-loop\n" +
                     "when\n" +
                     "    $p : Person( $ssn : ssn, $pob : pob,  isStudent == true )\n" +
                     "    if($pob == \"IR\" ) do[pobIsIR]\n" +
                     "then\n" +
                     "    Student st = (Student) don( $p , Student.class );\n" +
                     "    modify( st ){\n" +
                     "        setStudyingCountry( \"US\" );\n" +
                     "    }\n" +
                     "    System.out.println(\"student\");\n" +
                     "    list.add(\"student\");\n" +
                     "then[pobIsIR]\n" +
                     "    don( $p , IRCitizen.class );\n" +
                     "    System.out.println(\"IR citizen\");\n" +
                     "    list.add(\"IR citizen\");\n" +
                     "end\n" +
                     "\n" +
                     "rule \"check for being US citizen\"\n" +
                     "no-loop\n" +
                     "when\n" +
                     "    $s : Student( studyingCountry == \"US\" )\n" +
                     "then\n" +
                     "    don( $s , USCitizen.class );\n" +
                     "    System.out.println(\"US citizen\");\n" +
                     "    list.add(\"US citizen\");\n" +
                     "end\n" +
                     "\n" +
                     "rule \"check for being worker\"\n" +
                     "no-loop\n" +
                     "when\n" +
                     "    $p : Student( hasAssistantship == true, $sc : studyingCountry )\n" +
                     "then\n" +
                     "    Worker wr = (Worker) don( $p , Worker.class );\n" +
                     "    modify( wr ){\n" +
                     "        setWorkingCountry( $sc );\n" +
                     "    }\n" +
                     "    System.out.println(\"worker\");\n" +
                     "    list.add(\"worker\");\n" +
                     "end\n" +
                     "\n" +
                     "rule \"position args 1\"\n" +
                     "when\n" +
                     "    Student( $sc : studyingCountry ) @watch( studyingCountry )\n" +
                     "    $w : Worker( $pob , $sc; )\n" +
                     "    USCitizen( )\n" +
                     "    IRCitizen( $pob := pob )\n" +
                     "then\n" +
                     "    System.out.println(\"::You are working in US as student worker.\");\n" +
                     "    list.add(\"You are working in US as student worker\");\n" +
                     "    StudentWorker sw = (StudentWorker) don( $w, StudentWorker.class );\n" +
                     "    modify(sw){\n" +
                     "        setUniName( \"ASU\" );\n" +
                     "    }\n" +
                     "    System.out.println(\"student worker \" + sw);\n" +
                     "end\n" +
                     "\n" +
                     "rule \"position args 2\"\n" +
                     "when\n" +
                     "    Student( $sc : studyingCountry ) @watch( studyingCountry )\n" +
                     "    $sw : StudentWorker( $pob , $sc; )\n" +
                     "    IRCitizen( $pob := pob )\n" +
                     "then\n" +
                     "    System.out.println(\"::You are studying and working at ASU.\" + $sw );\n" +
                     "    list.add(\"You are studying and working at ASU\");\n" +
                     "end\n";

        KnowledgeBase kBase = loadKnowledgeBaseFromString(drl);
        TraitFactory.setMode( mode, kBase );

        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        kSession.setGlobal("list", list);

        kSession.fireAllRules();
        assertTrue(list.contains("initialized"));
        assertTrue(list.contains("student"));
        assertTrue(list.contains("IR citizen"));
        assertTrue(list.contains("US citizen"));
        assertTrue(list.contains("worker"));
        assertTrue(list.contains("You are working in US as student worker"));
        assertTrue(list.contains("You are studying and working at ASU"));
    }

    @Test
    public void singlePositionTraitTest(){


        String drl = "" +
                     "package org.drools.traits.test;\n" +
                     "import org.drools.factmodel.traits.Traitable;\n" +
                     "\n" +
                     "global java.util.List list;\n" +
                     "\n" +
                     "\n" +
                     "declare Pos\n" +
                     "@propertyReactive\n" +
                     "@Traitable\n" +
                     "end\n" +
                     "\n" +
                     "declare trait PosTrait\n" +
                     "@propertyReactive\n" +
                     "    field0 : int = 100  //@position(0)\n" +
                     "    field1 : int = 101  //@position(1)\n" +
                     "    field2 : int = 102  //@position(0)\n" +
                     "end\n" +
                     "\n" +
                     "declare trait MultiInhPosTrait extends PosTrait\n" +
                     "@propertyReactive\n" +
                     "    mfield0 : int = 200 //@position(0)\n" +
                     "    mfield1 : int = 201 @position(2)\n" +
                     "end\n" +
                     "\n" +
                     "\n";
        KnowledgeBase kBase = loadKnowledgeBaseFromString(drl);
        TraitFactory.setMode( mode, kBase );
        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();

        FactType parent = kBase.getFactType("org.drools.traits.test", "PosTrait");
        assertEquals(0, ((FieldDefinition) parent.getField("field0")).getIndex());
        assertEquals(1, ((FieldDefinition) parent.getField("field1")).getIndex());
        assertEquals(2, ((FieldDefinition) parent.getField("field2")).getIndex());
        FactType child = kBase.getFactType("org.drools.traits.test", "MultiInhPosTrait");
        assertEquals(0, ((FieldDefinition) child.getField("field0")).getIndex());
        assertEquals(1, ((FieldDefinition) child.getField("field1")).getIndex());
        assertEquals(2, ((FieldDefinition) child.getField("mfield1")).getIndex());
        assertEquals(3, ((FieldDefinition) child.getField("field2")).getIndex());
        assertEquals(4, ((FieldDefinition) child.getField("mfield0")).getIndex());

        drl = "" +
              "package org.drools.traits.test;\n" +
              "import org.drools.factmodel.traits.Traitable;\n" +
              "\n" +
              "global java.util.List list;\n" +
              "\n" +
              "\n" +
              "declare Pos\n" +
              "@propertyReactive\n" +
              "@Traitable\n" +
              "end\n" +
              "\n" +
              "declare trait PosTrait\n" +
              "@propertyReactive\n" +
              "    field0 : int = 100  //@position(0)\n" +
              "    field1 : int = 101  //@position(1)\n" +
              "    field2 : int = 102  @position(1)\n" +
              "end\n" +
              "\n" +
              "declare trait MultiInhPosTrait extends PosTrait\n" +
              "@propertyReactive\n" +
              "    mfield0 : int = 200 @position(0)\n" +
              "    mfield1 : int = 201 @position(2)\n" +
              "end\n" +
              "\n" +
              "\n";
        kBase = loadKnowledgeBaseFromString(drl);
        TraitFactory.setMode( mode, kBase );

        parent = kBase.getFactType("org.drools.traits.test", "PosTrait");
        assertEquals(0, ((FieldDefinition) parent.getField("field0")).getIndex());
        assertEquals(1, ((FieldDefinition) parent.getField("field2")).getIndex());
        assertEquals(2, ((FieldDefinition) parent.getField("field1")).getIndex());
        child = kBase.getFactType("org.drools.traits.test", "MultiInhPosTrait");
        assertEquals(0, ((FieldDefinition) child.getField("mfield0")).getIndex());
        assertEquals(1, ((FieldDefinition) child.getField("field2")).getIndex());
        assertEquals(2, ((FieldDefinition) child.getField("mfield1")).getIndex());
        assertEquals(3, ((FieldDefinition) child.getField("field0")).getIndex());
        assertEquals(4, ((FieldDefinition) child.getField("field1")).getIndex());

        drl = "" +
              "package org.drools.traits.test;\n" +
              "import org.drools.factmodel.traits.Traitable;\n" +
              "\n" +
              "global java.util.List list;\n" +
              "\n" +
              "\n" +
              "declare Pos\n" +
              "@propertyReactive\n" +
              "@Traitable\n" +
              "end\n" +
              "\n" +
              "declare trait PosTrait\n" +
              "@propertyReactive\n" +
              "    field0 : int = 100  @position(5)\n" +
              "    field1 : int = 101  @position(0)\n" +
              "    field2 : int = 102  @position(1)\n" +
              "end\n" +
              "\n" +
              "declare trait MultiInhPosTrait extends PosTrait\n" +
              "@propertyReactive\n" +
              "    mfield0 : int = 200 @position(0)\n" +
              "    mfield1 : int = 201 @position(1)\n" +
              "end\n" +
              "\n" +
              "\n";
        kBase = loadKnowledgeBaseFromString(drl);
        TraitFactory.setMode( mode, kBase );

        parent = kBase.getFactType("org.drools.traits.test", "PosTrait");
        assertEquals(0, ((FieldDefinition) parent.getField("field1")).getIndex());
        assertEquals(1, ((FieldDefinition) parent.getField("field2")).getIndex());
        assertEquals(2, ((FieldDefinition) parent.getField("field0")).getIndex());
        child = kBase.getFactType("org.drools.traits.test", "MultiInhPosTrait");
        assertEquals(0, ((FieldDefinition) child.getField("field1")).getIndex());
        assertEquals(1, ((FieldDefinition) child.getField("mfield0")).getIndex());
        assertEquals(2, ((FieldDefinition) child.getField("field2")).getIndex());
        assertEquals(3, ((FieldDefinition) child.getField("mfield1")).getIndex());
        assertEquals(4, ((FieldDefinition) child.getField("field0")).getIndex());

        drl = "" +
              "package org.drools.traits.test;\n" +
              "import org.drools.factmodel.traits.Traitable;\n" +
              "\n" +
              "global java.util.List list;\n" +
              "\n" +
              "\n" +
              "declare Pos\n" +
              "@propertyReactive\n" +
              "@Traitable\n" +
              "end\n" +
              "\n" +
              "declare trait PosTrait\n" +
              "@propertyReactive\n" +
              "    field0 : int = 100  //@position(5)\n" +
              "    field1 : int = 101  //@position(0)\n" +
              "    field2 : int = 102  //@position(1)\n" +
              "end\n" +
              "\n" +
              "declare trait MultiInhPosTrait extends PosTrait\n" +
              "@propertyReactive\n" +
              "    mfield0 : int = 200 //@position(0)\n" +
              "    mfield1 : int = 201 //@position(1)\n" +
              "end\n" +
              "\n" +
              "\n";
        kBase = loadKnowledgeBaseFromString(drl);
        TraitFactory.setMode( mode, kBase );

        parent = kBase.getFactType("org.drools.traits.test", "PosTrait");
        assertEquals(0, ((FieldDefinition) parent.getField("field0")).getIndex());
        assertEquals(1, ((FieldDefinition) parent.getField("field1")).getIndex());
        assertEquals(2, ((FieldDefinition) parent.getField("field2")).getIndex());
        child = kBase.getFactType("org.drools.traits.test", "MultiInhPosTrait");
        assertEquals(0, ((FieldDefinition) child.getField("field0")).getIndex());
        assertEquals(1, ((FieldDefinition) child.getField("field1")).getIndex());
        assertEquals(2, ((FieldDefinition) child.getField("field2")).getIndex());
        assertEquals(3, ((FieldDefinition) child.getField("mfield0")).getIndex());
        assertEquals(4, ((FieldDefinition) child.getField("mfield1")).getIndex());

    }



    private KnowledgeBase buildKB( String drlPath ) {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( ResourceFactory.newClassPathResource(drlPath), ResourceType.DRL );
        if ( knowledgeBuilder.hasErrors() ) {
            fail( knowledgeBuilder.getErrors().toString() );
        }
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );
        return knowledgeBase;
    }

    private KnowledgeBase loadKnowledgeBaseFromString( String drlSource ){
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( ResourceFactory.newByteArrayResource(drlSource.getBytes()), ResourceType.DRL );
        if ( knowledgeBuilder.hasErrors() ) {
            fail( knowledgeBuilder.getErrors().toString() );
        }
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );
        return knowledgeBase;

    }

    public static class Parent {
        public String name;
        public Child child;

        public String getName() {
            return name;
        }

        public Child getChild() {
            return child;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setChild(Child child) {
            this.child = child;
        }

        public Parent(String name, Child child){
            this.name = name;
            this. child = child;
        }

        @Override
        public String toString() {
            return "Parent{" +
                   "name='" + name + '\'' +
                   ", child=" + child +
                   '}';
        }
    }


    public static class Child {
        private String sex = "male";

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        @Override
        public String toString() {
            return "Child{" +
                   "sex='" + sex + '\'' +
                   '}';
        }
    }

}
