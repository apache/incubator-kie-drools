package org.drools.decisiontable;

import java.util.Locale;

import org.junit.After;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A class to test decimal separated value in action column with different locales.
 */

public class DecimalSeparatorTest {

    private KieSession ksession;

    public void init() {
        final KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        KieModuleModel kmodule = ks.newKieModuleModel();

        KieBaseModel baseModel = kmodule.newKieBaseModel("defaultKBase").setDefault(true);
        baseModel.newKieSessionModel("defaultKSession").setDefault(true);

        kfs.writeKModuleXML(kmodule.toXML());
        kfs.write(ks.getResources().newClassPathResource("decimalSeparator.drl.xls",
                                                           this.getClass())); // README when path is set then test works
        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        assertThat(kieBuilder.getResults().getMessages(org.kie.api.builder.Message.Level.ERROR)).isEmpty();

        ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
    }

    @After
    public void tearDown() {
        if (ksession != null) {
            ksession.dispose();
        }
    }

    @Test
    public void testDecimalSeparatorInFrench() {
        Locale.setDefault(Locale.FRENCH);
        init();
        
        ksession.insert("Hello");
        
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testDecimalSeparatorInEnglish() {
        Locale.setDefault(Locale.ENGLISH);
        init();

        ksession.insert("Hello");
        
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

}
