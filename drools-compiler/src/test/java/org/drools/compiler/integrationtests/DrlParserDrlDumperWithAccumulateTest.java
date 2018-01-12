package org.drools.compiler.integrationtests;

import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.lang.DrlDumper;
import org.drools.compiler.lang.descr.PackageDescr;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.conf.LanguageLevelOption;

import static org.junit.Assert.fail;

/**
 * Created by mcasalino on 09/08/2017.
 */
public class DrlParserDrlDumperWithAccumulateTest {

    private static final String RULES1_DRL = "package test\n" +
            "import java.util.List;\n" +
            "import java.util.Date;\n" +
            "declare  Flight \n" +
            "\n" +
            "    departureDate : Date  \n" +
            "    status : String  \n" +
            "end\n" +
            "\n" +
            "declare  Profile \n" +
            "\n" +
            "    id : String  \n" +
            "    flights : List  \n" +
            "end\n" +
            "\n" +
            "rule \"last flown date\"\n" +
            "     when\n" +
            "      $customer : Profile($ceid : id)\n" +
            "      accumulate( Flight( status == \"Flown\", $dptDate: departureDate.time ) from $customer.flights;\n" +
            "          $max : max( $dptDate ),\n" +
            "          $cnt : count( $dptDate );\n" +
            "          $cnt > 0 )\n" +
            "     then\n" +
            "     end";

    @Test
    public void testWithoutParserAndDumper() throws DroolsParserException {
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();

        KieModule kModule = buildKieModule("0.0.1", RULES1_DRL, false);

        KieContainer kContainer = ks.newKieContainer(kModule.getReleaseId());

        kr.removeKieModule(kModule.getReleaseId());

    }

    @Test
    public void testWithParserAndDumper() throws DroolsParserException {
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();

        KieModule kModule = buildKieModule("0.0.1", RULES1_DRL, true);

        KieContainer kContainer = ks.newKieContainer(kModule.getReleaseId());

        kr.removeKieModule(kModule.getReleaseId());

    }


    private static KieModule buildKieModule(String version, String rules, boolean useParserAndDumper) throws DroolsParserException {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        ReleaseId rid = ks.newReleaseId("org.drools", "kiemodulemodel-example", version);
        kfs.generateAndWritePomXML(rid);

        if (useParserAndDumper) {
            DrlParser parser = new DrlParser(LanguageLevelOption.DRL6);
            DrlDumper dumper = new DrlDumper();

            PackageDescr rulesPkg = parser.parse(null, rules);

            System.out.println(dumper.dump(rulesPkg));
            kfs.write("src/main/resources/rules.drl",
                    dumper.dump(rulesPkg));
        } else {
            kfs.write("src/main/resources/rules.drl",
                    rules);
        }

        KieBuilder kb = ks.newKieBuilder(kfs);
        kb.buildAll(); // kieModule is automatically deployed to KieRepository if successfully built.
        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            fail(kb.getResults().toString());
        }

        return kb.getKieModule();
    }


}
