package org.drools.kproject;



public class GenerateKSessionProducer {
    public static String generateQualifier(KSession kSession) {
        String s = "package " + kSession.getNamespace() + ";\n"+
                "import static java.lang.annotation.ElementType.TYPE;\n" + 
                "import static java.lang.annotation.ElementType.FIELD;\n" + 
                "import static java.lang.annotation.ElementType.PARAMETER;\n" + 
                "import static java.lang.annotation.ElementType.METHOD;\n" + 
                "import java.lang.annotation.Retention;\n" + 
                "import java.lang.annotation.RetentionPolicy;\n" + 
                "import java.lang.annotation.Target;\n" + 
                "import javax.inject.Qualifier;" +
                "@Retention(RetentionPolicy.RUNTIME)\n" + 
                "@Target({FIELD,METHOD,PARAMETER,TYPE})\n" + 
                "@Qualifier\n" + 
                "public @interface " + kSession.getName() +" {\n" + 
                "\n" + 
                "}\n" + 
                "";        
        return s;
    }
    
    public static String generateProducer(KBase kBase, KSession kSession) {     
        String s = 
                "package " + kSession.getNamespace() + ";\n" + 
        		"import javax.enterprise.inject.Produces;\n" + 
        		"\n" + 
        		"import org.drools.KnowledgeBase;\n" + 
        		"import org.drools.KnowledgeBaseFactory;\n" + 
        		"import org.drools.runtime.KnowledgeSessionConfiguration;\n" + 
        		"import org.drools.runtime.StatefulKnowledgeSession;\n" + 
        		"import org.drools.runtime.StatelessKnowledgeSession;\n" +
        		"import org.drools.runtime.conf.ClockTypeOption;\n" + 
        		"import " + kBase.getQName() + ";\n" +
        		"public class " + kSession.getName() + "Producer {\n" + 
        		"    \n" + 
        		"    @Produces\n" + 
        		"    @" + kSession.getName() + "\n";
        
        		if ( kSession.getType().equals( "stateful" ) ) {
            		s = s+ 
            		"    public StatefulKnowledgeSession newStatefulKnowledgeSession(@" + kBase.getName() + " KnowledgeBase kbase) {\n" + 
            		"        KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();\n" + 
            		"        ksconf.setOption( ClockTypeOption.get( \"" + kSession.getClockType().getClockType() + "\" ) );\n" + 
            		"        \n" + 
            		"        return kbase.newStatefulKnowledgeSession( ksconf, null );\n" + 
            		"    }\n";
        		} else {
                    s = s+ 
                    "    public StatelessKnowledgeSession newStatelessKnowledgeSession(@" + kBase.getName() + " KnowledgeBase kbase) {\n" + 
                    "        KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();\n" + 
                    "        ksconf.setOption( ClockTypeOption.get( \"" + kSession.getClockType().getClockType() + "\" ) );\n" + 
                    "        \n" + 
                    "        return kbase.newStatelessKnowledgeSession( ksconf );\n" + 
                    "    }\n";        		    
        		}
        		s = s+  "\n" + 
        		"}\n" + 
        		"";
                       
        return s;
        
    }
}
