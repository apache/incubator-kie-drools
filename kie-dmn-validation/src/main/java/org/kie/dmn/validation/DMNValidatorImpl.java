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
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.drools.core.util.Drools;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.core.impl.DMNMessageImpl;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.Definitions;
import org.kie.dmn.core.util.KieHelper;
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
    private List<DMNMessage> failedInitMsg;

    public DMNValidatorImpl() {
        final KieServices ks = KieServices.Factory.get();
        final KieContainer kieContainer = KieHelper.getKieContainer(
                ks.newReleaseId( "org.kie", "kie-dmn-validation", Drools.getFullVersion() ),
                ks.getResources().newClassPathResource("dmn-validation-rules.drl", getClass() ) );
        if( kieContainer != null ) {
            this.kieContainer = Optional.of( kieContainer );
        } else {
            this.kieContainer = Optional.empty();
            LOG.error("Unable to load embedded DMN validation rules file." );
            String message = MsgUtil.createMessage( Msg.FAILED_VALIDATOR );
            failedInitMsg.add(new DMNMessageImpl(DMNMessage.Severity.ERROR, message, Msg.FAILED_VALIDATOR.getType(), null ) );
        }
    }
    
    public void dispose() {
        kieContainer.ifPresent( KieContainer::dispose );
    }

    @Override
    public List<DMNMessage> validateSchema(File xmlFile) {
        List<DMNMessage> problems = new ArrayList<>();
        Source s = new StreamSource(xmlFile);
        try {
            schema.newValidator().validate(s);
        } catch (SAXException | IOException e) {
            problems.add(new DMNMessageImpl(DMNMessage.Severity.ERROR, MsgUtil.createMessage(Msg.FAILED_XML_VALIDATION), Msg.FAILED_VALIDATOR.getType(), null, e));
        }
        // TODO detect if the XSD is not provided through schemaLocation, and validate against embedded
        return problems;
    }
    
    @Override
    public List<DMNMessage> validateModel(Definitions dmnModel) {
        if (!kieContainer.isPresent()) {
            return failedInitMsg;
        }
        
        StatelessKieSession kieSession = kieContainer.get().newStatelessKieSession();
        MessageReporter reporter = new MessageReporter();
        kieSession.setGlobal( "reporter", reporter );
        
        kieSession.execute(allChildren(dmnModel).collect(toList()));

        if ( LOG.isDebugEnabled() ) {
            for ( DMNMessage m : reporter.getMessages() ) {
                LOG.debug("{}", m);
            }
        }
        
        return reporter.getMessages();
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
