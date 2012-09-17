package org.drools.kproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class GenerateKBaseProjectFiles {
    public static String generateQualifier(KBase kbase) {
        String s = "package " + kbase.getNamespace() + ";\n"
                + "import static java.lang.annotation.ElementType.TYPE;\n"
                + "import static java.lang.annotation.ElementType.FIELD;\n"
                + "import static java.lang.annotation.ElementType.PARAMETER;\n"
                + "import static java.lang.annotation.ElementType.METHOD;\n"
                + "import java.lang.annotation.Retention;\n"
                + "import java.lang.annotation.RetentionPolicy;\n"
                + "import java.lang.annotation.Target;\n"
                + "import javax.inject.Qualifier;"
                + "@Retention(RetentionPolicy.RUNTIME)\n"
                + "@Target({FIELD,METHOD,PARAMETER,TYPE})\n" + "@Qualifier\n"
                + "public @interface " + kbase.getName() + " {\n" + "\n"
                + "}\n" + "";
        return s;
    }
    

    public static String generateProducer(KBase kbase) {
        String s = "package " + kbase.getNamespace() + ";\n" +
                "import java.util.Properties;\n" +
                "import java.io.IOException;\n" +
                "import java.io.InputStream;\n" +
                "import javax.enterprise.inject.Produces;\n" +
                "import javax.inject.Named;\n" +
                "import org.drools.KnowledgeBase;\n" +
                "import org.drools.KnowledgeBaseConfiguration;\n" +
                "import org.drools.KnowledgeBaseFactory;\n" +
                "import org.drools.builder.CompositeKnowledgeBuilder;\n" +
                "import org.drools.builder.KnowledgeBuilder;\n" +
                "import org.drools.builder.KnowledgeBuilderFactory;\n" +
                "import org.drools.builder.ResourceType;\n" +
                "import org.drools.io.ResourceFactory;\n" +
                "public class " + kbase.getName() + "Producer extends "+ KBaseBuilder.class.getName() + " {\n" +
                "    @Produces \n" +
                "    @" + kbase.getName() + "\n" +
                "    public KnowledgeBase newKnowledgeBase() {\n" +
                "        return " + KBaseBuilder.class.getName() +".fluent()\n";
        switch ( kbase.getEventProcessingMode() ) {
            case CLOUD:
                s += "                    .setEventProcessingMode( " + kbase.getEventProcessingMode().getClass().getName() + ".CLOUD )\n";
                break;
            case STREAM:
                s += "                    .setEventProcessingMode( " + kbase.getEventProcessingMode().getClass().getName() + ".STREAM )\n";
                break;
        }

        switch( kbase.getEqualsBehavior() ) {
            case EQUALITY:
                s += "                    .setEqualsBehavior( " + kbase.getEqualsBehavior().getClass().getName() + ".EQUALITY )\n";
                break;
            case IDENTITY:
                s += "                    .setEqualsBehavior( " + kbase.getEqualsBehavior().getClass().getName() + ".IDENTITY )\n";
                break;
        }

        s +=    "                    .build( new Class[] { " + kbase.getQName() + ".class";
        for( String kBaseQName : kbase.getIncludes() ) {
            s +=  ", " + kBaseQName + ".class";
        }
        s +=    "} );\n" +
                "    }\n" +
                "}\n";

        return s;
    }
    public static String generateKBaseFiles(KProject kproject,
                                            KBase kbase,
                                            FileSystem fs) {
        Path kbasePath = fs.getFolder( kproject.getKBasesPath() + "/" + kbase.getQName() ).getPath();

        List<String> files = new ArrayList<String>( kbase.getFiles() );
        Collections.sort( files );

        StringBuilder sbuilder = new StringBuilder();
        boolean first = true;
        for ( String file : files ) {
            if ( !first ) {
                sbuilder.append( ", " );
            }
            sbuilder.append( file );
            first = false;
        }

        return sbuilder.toString();
    }
}
