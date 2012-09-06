package org.drools.kproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenerateKBaseProjectFiles {
    public static String generateQualifier(KBase kbase) {
        String s = "package " + kbase.getNamespace() + ";\n" +
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
                   "public @interface " + kbase.getName() + " {\n" +
                   "\n" +
                   "}\n" +
                   "";
        return s;
    }

    public static String generateProducer(KBase kbase) {
//        String s = "package " + kbase.getNamespace() + ";\n" +
//                   "import java.util.Properties;\n" +
//                   "import java.io.IOException;\n" +
//                   "import java.io.InputStream;\n" +
//                   "import javax.enterprise.inject.Produces;\n" +
//                   "import javax.inject.Named;\n" +
//                   "import org.drools.KnowledgeBase;\n" +
//                   "import org.drools.KnowledgeBaseConfiguration;\n" +
//                   "import org.drools.KnowledgeBaseFactory;\n" +
//                   "import org.drools.builder.CompositeKnowledgeBuilder;\n" +
//                   "import org.drools.builder.KnowledgeBuilder;\n" +
//                   "import org.drools.builder.KnowledgeBuilderFactory;\n" +
//                   "import org.drools.builder.ResourceType;\n" +
//                   "import org.drools.io.ResourceFactory;\n" +
//                   "public class " + kbase.getName() + "Producer {\n" +
//                   "    @Produces \n" +
//                   "    @" + kbase.getName() + "\n" +
//                   "    public KnowledgeBase newKnowledgeBase() {\n" +
//                   "        String fileStr = null;\n" +
//                   "        InputStream is = null;\n" +
//                   "        try {\n" +
//                   "            is = getClass().getResourceAsStream( \"/" + kbase.getQName() + ".files.dat\" );\n" +
//                   "            fileStr = org.drools.core.util.StringUtils.toString( is );\n" +
//                   "        } catch ( IOException e ) {\n" +
//                   "            throw new RuntimeException( \"Unable to fine files for KnowledgeBase " + kbase.getQName() + "\" );\n" +
//                   "        } finally {\n" +
//                   "            if ( is != null ) {\n" +
//                   "                try {\n" +
//                   "                    is.close();\n" +
//                   "                } catch (IOException e) {\n" +
//                   "                    throw new RuntimeException( \"Unable to fine files for KnowledgeBase " + kbase.getQName() + "\" );\n" +
//                   "                }\n" +
//                   "            }\n" +
//                   "        }\n" +
//                   "        \n" +
//                   "        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();\n" +
//                   "        CompositeKnowledgeBuilder ckbuilder = kbuilder.batch();\n" +
//                   "        \n" +
//                   "        String[] files = fileStr.split( \",\" );\n" +
//                   "        if ( files.length > 0 ) {\n" +
//                   "            for ( String file : files ) {\n" +
//                   "                if ( file.endsWith(\".drl\" ) ) {\n" +
//                   "                    ckbuilder.add( ResourceFactory.newUrlResource( getClass().getResource( \"/\" + file.trim() ) ), ResourceType.DRL );\n" +
//                   "                }\n" +
//                   "            }\n" +
//                   "        }\n" +
//                   "        ckbuilder.build();\n" +
//                   "\n" +
//                   "        \n" +
//                   "        if ( kbuilder.hasErrors() ) {\n" +
//                   "            throw new RuntimeException( \"Unable to compile " + kbase.getQName() + ":\\n\" + kbuilder.getErrors() );\n" +
//                   "        }\n" +
//                   "        \n" +
//                   "        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();\n";
//
//                   switch ( kbase.getEventProcessingMode() ) {
//                       case CLOUD:
//                           s += "        kconf.setOption( " + kbase.getEventProcessingMode().getClass().getName() + ".CLOUD );\n";
//                           break;
//                       case STREAM:
//                           s += "        kconf.setOption( " + kbase.getEventProcessingMode().getClass().getName() + ".STREAM );\n";
//                           break;
//                   }
//
//                   switch( kbase.getEqualsBehavior() ) {
//                       case EQUALITY:
//                           s += "        kconf.setOption( " + kbase.getEqualsBehavior().getClass().getName() + ".EQUALITY );\n";
//                           break;
//                       case IDENTITY:
//                           s += "        kconf.setOption( " + kbase.getEqualsBehavior().getClass().getName() + ".IDENTITY );\n";
//                           break;
//                   }
//
//                   s += "        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);" +
//                   "        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );\n" +
//                   "        return kbase; \n" +
//                   "    }\n" +
//                   "}\n";

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
//                   "    public " + kbase.getName() + "Producer() {\n" +
//                   "        setKBaseQName( \"" + kbase.getQName() + "\" );\n";
//                   switch ( kbase.getEventProcessingMode() ) {
//                       case CLOUD:
//                           s += "        setEventProcessingMode( " + kbase.getEventProcessingMode().getClass().getName() + ".CLOUD );\n";
//                           break;
//                       case STREAM:
//                           s += "        setEventProcessingMode( " + kbase.getEventProcessingMode().getClass().getName() + ".STREAM );\n";
//                           break;
//                   }
//
//                   switch( kbase.getEqualsBehavior() ) {
//                       case EQUALITY:
//                           s += "        setEqualsBehavior( " + kbase.getEqualsBehavior().getClass().getName() + ".EQUALITY );\n";
//                           break;
//                       case IDENTITY:
//                           s += "        setEqualsBehavior( " + kbase.getEqualsBehavior().getClass().getName() + ".IDENTITY );\n";
//                           break;
//                   }
//                   s +=
//                   "    } \n" +
                   "    @Produces \n" +
                   "    @" + kbase.getName() + "\n" +
                   "    public KnowledgeBase newKnowledgeBase() {\n" +
//                   "        return super.newKnowledgeBase(); \n" +
                   "        return " + KBaseBuilder.class.getName() +".fluent()\n" +
                   "                    .setKBaseQName( \"" + kbase.getQName() + "\" )\n";
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
                   s +=
                   "                    .build(getClass());\n" +
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
