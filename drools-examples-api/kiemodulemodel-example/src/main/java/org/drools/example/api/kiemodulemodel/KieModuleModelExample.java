package org.drools.example.api.kiemodulemodel;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.File;
import java.io.PrintStream;

public class KieModuleModelExample {

    public void go(PrintStream out) {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        Resource ex1Res = ks.getResources().newFileSystemResource(getFile("named-kiesession"));
        Resource ex2Res = ks.getResources().newFileSystemResource(getFile("kiebase-inclusion"));

        ReleaseId rid = ks.newReleaseId("org.drools", "kiemodulemodel-example", "6.0.0-SNAPSHOT");
        kfs.generateAndWritePomXML(rid);

        KieModuleModel kModuleModel = ks.newKieModuleModel();
        kModuleModel.newKieBaseModel("kiemodulemodel")
                    .addInclude("kbase1")
                    .addInclude("kbase2")
                    .newKieSessionModel("ksession6");

        kfs.writeKModuleXML(kModuleModel.toXML());
        kfs.write("src/main/resources/kiemodulemodel/HAL6.drl", getRule());

        KieBuilder kb = ks.newKieBuilder(kfs);
        kb.setDependencies(ex1Res, ex2Res);
        kb.buildAll(); // kieModule is automatically deployed to KieRepository if successfully built.
        if (kb.getResults().hasMessages(Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        }

        KieContainer kContainer = ks.newKieContainer(rid);

        KieSession kSession = kContainer.newKieSession("ksession6");
        kSession.setGlobal("out", out);

        Object msg1 = createMessage(kContainer, "Dave", "Hello, HAL. Do you read me, HAL?");
        kSession.insert(msg1);
        kSession.fireAllRules();

        Object msg2 = createMessage(kContainer, "Dave", "Open the pod bay doors, HAL.");
        kSession.insert(msg2);
        kSession.fireAllRules();

        Object msg3 = createMessage(kContainer, "Dave", "What's the problem?");
        kSession.insert(msg3);
        kSession.fireAllRules();
    }

    public static void main(String[] args) {
        new KieModuleModelExample().go(System.out);
    }


    private static String getRule() {
        String s = "" +
                   "package org.drools.example.api.kiemodulemodel \n\n" +
                   "import org.drools.example.api.namedkiesession.Message \n\n" +
                   "rule rule6 when \n" +
                   "    Message(text == \"What's the problem?\") \n" +
                   "then\n" +
                   "    insert( new Message(\"HAL\", \"I think you know what the problem is just as well as I do.\" ) ); \n" +
                   "end \n";

        return s;
    }

    private static Object createMessage(KieContainer kContainer, String name, String text) {
        Object o = null;
        try {
            Class cl = kContainer.getClassLoader().loadClass("org.drools.example.api.namedkiesession.Message");
            o = cl.getConstructor(new Class[]{String.class, String.class}).newInstance(name, text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }

    public static File getFile(String exampleName) {
        File folder = new File("drools-examples-api").getAbsoluteFile();
        File exampleFolder = null;
        while (folder != null) {
            exampleFolder = new File(folder,
                                     exampleName);
            if (exampleFolder.exists()) {
                break;
            }
            exampleFolder = null;
            folder = folder.getParentFile();
        }

        if (exampleFolder != null) {

            File targetFolder = new File(exampleFolder,
                                         "target");
            if (!targetFolder.exists()) {
                throw new RuntimeException("The target folder does not exist, please build project " + exampleName + " first");
            }

            for (String str : targetFolder.list()) {
                if (str.startsWith(exampleName) && !str.endsWith("-sources.jar") && !str.endsWith("-tests.jar") && !str.endsWith("-javadoc.jar")) {
                    return new File(targetFolder, str);
                }
            }
        }

        throw new RuntimeException("The target jar does not exist, please build project " + exampleName + " first");
    }

}
