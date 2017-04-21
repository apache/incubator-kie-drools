package org.kie.dmn.core.compiler.extensions;

import nl.rws.dso.inception.backend.extension.UitvoeringsregelRefRegister;
import nl.rws.dso.inception.backend.extension.model.UitvoeringsregelRef;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kie.dmn.api.core.DMNCompilerConfiguration;
import org.kie.dmn.api.marshalling.v1_1.DMNExtensionRegister;
import org.kie.dmn.api.marshalling.v1_1.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1_1.DMNMarshallerFactory;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.model.v1_1.DMNElement;
import org.kie.dmn.model.v1_1.Definitions;
import org.kie.dmn.model.v1_1.InputData;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.utils.ChainedProperties;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by akoufoudakis on 06/04/17.
 */
public class DMNExtensionRegisterTesting {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private void setProperties() {
        System.setProperty("foo", "bar");
        System.setProperty("org.kie.dmn.marshaller.extension.conversie", "nl.rws.dso.inception.backend.extension.ConversieregelRefRegister");
        System.setProperty("org.kie.dmn.marshaller.extension.uitvoeringsregel", "nl.rws.dso.inception.backend.extension.UitvoeringsregelRefRegister");
    }

    private Map<String, String> loadExtensionProperties() {
        ChainedProperties props = new ChainedProperties("extension.properties", this.getClass().getClassLoader(), true);
        Map<String, String> extensionClassNames = new HashMap<String, String>();
        props.mapStartsWith(extensionClassNames, "org.kie.dmn.marshaller.extension.", false);
        return extensionClassNames;
    }

    private void clearProperties() {
        System.clearProperty("foo");
        System.clearProperty("org.kie.dmn.marshaller.extension.geo");
        System.clearProperty("org.kie.dmn.marshaller.extension.question");
    }

    private DMNCompilerConfiguration loadDMNCompilerConfig() {
        DMNCompilerConfiguration compilerConfig = null;
        setProperties();
        Map<String, String> extensionClassNames = loadExtensionProperties();
        List<DMNExtensionRegister> extensionRegisters = new ArrayList<DMNExtensionRegister>();
        KnowledgeBuilder kbuilder = new KnowledgeBuilderImpl();
        try {
            for (Map.Entry<String, String> extensionProperty : extensionClassNames.entrySet()) {
                String extRegClassName = extensionProperty.getValue();
                DMNExtensionRegister extRegister = (DMNExtensionRegister) ((KnowledgeBuilderImpl) kbuilder).getRootClassLoader()
                        .loadClass(extRegClassName).newInstance();
                extensionRegisters.add(extRegister);
            }
            compilerConfig = DMNFactory.newCompilerConfiguration();
            compilerConfig.addExtensions(extensionRegisters);
        } catch(ClassNotFoundException e) {
            System.out.println( "Trying to load a non-existing extension element register " + e.getLocalizedMessage());
        } catch(Exception e) {
            System.out.println("Other exception");
        }
        return compilerConfig;
    }

    @Test
    public void checkClassesLoaded() {
        DMNCompilerConfiguration compilerConfig = loadDMNCompilerConfig();
        assertEquals(2, compilerConfig.getRegisteredExtensions().size());
        assertTrue(compilerConfig.getRegisteredExtensions().get(0) instanceof DMNExtensionRegister);
        assertEquals(UitvoeringsregelRefRegister.class, compilerConfig.getRegisteredExtensions().get(0).getClass());
        clearProperties();
    }

    @Test
    public void testUitvoeringsregelRef() {
        setProperties();
        DMNCompilerConfiguration compilerConfig = loadDMNCompilerConfig();
        final DMNMarshaller DMNMarshaller = DMNMarshallerFactory.newMarshallerWithExtensions(compilerConfig.getRegisteredExtensions());
        final InputStream is = this.getClass().getResourceAsStream( "dakkapel_uitvoering_en_conversie.xml" );
        final InputStreamReader isr = new InputStreamReader( is );
        final Definitions def = DMNMarshaller.unmarshal( isr );
        InputData inputData = (InputData)def.getDrgElement().get(3);
        DMNElement.ExtensionElements elements = inputData.getExtensionElements();
        assertTrue(elements != null);
        assertEquals(1, elements.getAny().size());
        String uitvoeringsregelRef = (String)elements.getAny().get(0);
        assertEquals(uitvoeringsregelRef, "UitvId0002");
        clearProperties();
    }

    @Test
    public void testConversieRegelRef() {
        setProperties();
        DMNCompilerConfiguration compilerConfig = loadDMNCompilerConfig();
        final DMNMarshaller DMNMarshaller = DMNMarshallerFactory.newMarshallerWithExtensions(compilerConfig.getRegisteredExtensions());
        final InputStream is = this.getClass().getResourceAsStream( "dakkapel_uitvoering_en_conversie.xml" );
        final InputStreamReader isr = new InputStreamReader( is );
        final Definitions def = DMNMarshaller.unmarshal( isr );
        InputData inputData = (InputData)def.getDrgElement().get(4);
        DMNElement.ExtensionElements elements = inputData.getExtensionElements();
        assertTrue(elements != null);
        assertEquals(1, elements.getAny().size());
        String uitvoeringsregelRef = (String)elements.getAny().get(0);
        assertEquals(uitvoeringsregelRef, "Output3");
        clearProperties();
    }

}
