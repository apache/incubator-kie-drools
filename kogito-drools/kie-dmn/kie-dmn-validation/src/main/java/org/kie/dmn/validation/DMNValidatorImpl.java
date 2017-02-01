/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.validation;

import static java.util.stream.Collectors.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.drools.core.util.IoUtils;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.dmn.feel.model.v1_1.DMNModelInstrumentedBase;
import org.kie.dmn.feel.model.v1_1.Definitions;
import org.kie.dmn.validation.ValidationMsg.Severity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class DMNValidatorImpl implements DMNValidator {
    public static Logger LOG = LoggerFactory.getLogger(DMNValidatorImpl.class);
    static Schema schema;
    static {
        try {
            schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * A KieContainer is normally available,
     * unless at runtime some problem prevented building it correctly.
     */
    private Optional<KieContainer> kieContainer;
    /**
     * Collect at init time the runtime issues which prevented to build the `kieContainer` correctly.
     */
    private List<ValidationMsg> failedInitMsg;
    
    public DMNValidatorImpl() {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        ReleaseId rid = ks.newReleaseId("org.kie", "kie-dmn-validation", "1");
        kfs.generateAndWritePomXML(rid);
        try {
            kfs.write("src/main/resources/rules.drl", IoUtils.readBytesFromInputStream( DMNValidatorImpl.class.getResourceAsStream("/rules.drl") ));
        } catch (IOException e) {
            LOG.error("Unable to read embedded DMN validation rule file.", e);
            failedInitMsg.add(new ValidationMsg(Severity.ERROR, Msg.FAILED_VALIDATOR, e.getMessage()));
        }
        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        Results results = kieBuilder.getResults();
        for ( Message m : results.getMessages(new Message.Level[]{Message.Level.ERROR, Message.Level.WARNING}) ) {
            if (m.getLevel() == Level.ERROR) {
                LOG.error("{}", m);
            } else if (m.getLevel() == Level.WARNING) {
                LOG.warn("{}", m);
            }
        }
        
        if (results.hasMessages(new Message.Level[]{Message.Level.ERROR})) {
            LOG.error("Errors while compiling embedded DMN validation rules.");
            results.getMessages().stream()
                .map(m -> new ValidationMsg( Severity.ERROR, Msg.FAILED_VALIDATOR, m ))
                .forEach(vm -> failedInitMsg.add(vm));
            this.kieContainer = Optional.empty();
        } else {
            this.kieContainer = Optional.of( ks.newKieContainer(rid) );
        }
    }
    
    public void dispose() {
        kieContainer.ifPresent( KieContainer::dispose );
    }

    @Override
    public List<ValidationMsg> validateSchema(File xmlFile) {
        List<ValidationMsg> problems = new ArrayList<>();
        Source s = new StreamSource(xmlFile);
        try {
            schema.newValidator().validate(s);
        } catch (SAXException | IOException e) {
            problems.add(new ValidationMsg(ValidationMsg.Severity.ERROR, Msg.FAILED_XML_VALIDATION, e));
        }
        // TODO detect if the XSD is not provided through schemaLocation, and validate against embedded
        return problems;
    }
    
    @Override
    public List<ValidationMsg> validateModel(Definitions dmnModel) {
        if (!kieContainer.isPresent()) {
            return failedInitMsg;
        }
        
        StatelessKieSession kieSession = kieContainer.get().newStatelessKieSession();
        
        List<ValidationMsg> problems = new ArrayList<>();
        
        kieSession.addEventListener(new DefaultRuleRuntimeEventListener() {
            @Override
            public void objectInserted(ObjectInsertedEvent event) {
                if ( event.getObject() instanceof ValidationMsg ) {
                    problems.add((ValidationMsg) event.getObject());
                }
            }
        });
        
        kieSession.execute(allChildren(dmnModel).collect(toList()));
        
        return problems;
    }


    
    
    
    @SuppressWarnings("unchecked")
    public static <T> Stream<T> allChildren(DMNModelInstrumentedBase root, Class<T> clazz) {
        return (Stream<T>) allChildren(root)
                .filter(dmn -> clazz.isInstance(dmn) )
                ;
    }
    
    public static Stream<DMNModelInstrumentedBase> allChildren(DMNModelInstrumentedBase root) {
        return Stream.concat( Stream.of(root),
                              root.getChildren().stream().flatMap(DMNValidatorImpl::allChildren) );
    }
    
    @Deprecated
    public static List<DMNModelInstrumentedBase> allChildrenClassic(DMNModelInstrumentedBase root) {
        List<DMNModelInstrumentedBase> result = new ArrayList<>();
        for ( DMNModelInstrumentedBase c : root.getChildren() ) {
            result.add(c);
            result.addAll(allChildrenClassic(c));
        }
        return result;
    }


}
