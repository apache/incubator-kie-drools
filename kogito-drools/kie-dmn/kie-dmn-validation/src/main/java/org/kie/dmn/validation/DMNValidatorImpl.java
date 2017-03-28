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

import org.drools.core.util.Drools;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.dmn.api.core.DMNCompiler;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.backend.marshalling.v1_1.DMNMarshallerFactory;
import org.kie.dmn.core.api.DMNMessageManager;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.impl.DMNMessageImpl;
import org.kie.dmn.core.util.KieHelper;
import org.kie.dmn.core.util.DefaultDMNMessagesManager;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.Definitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

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
    public List<DMNMessage> validate(Definitions dmnModel) {
        return validate( dmnModel, VALIDATE_MODEL );
    }

    @Override
    public List<DMNMessage> validate(Definitions dmnModel, Validation... options) {
        DMNMessageManager results = new DefaultDMNMessagesManager();
        EnumSet<Validation> flags = EnumSet.copyOf( Arrays.asList( options ) );
        if( flags.contains( VALIDATE_SCHEMA ) ) {
            MsgUtil.reportMessage( LOG,
                                   DMNMessage.Severity.ERROR,
                                   dmnModel,
                                   results,
                                   null,
                                   null,
                                   Msg.FAILED_NO_XML_SOURCE );
        }
        try {
            validateModelCompilation( dmnModel, results, flags );
        } catch ( Throwable t ) {
            MsgUtil.reportMessage( LOG,
                                   DMNMessage.Severity.ERROR,
                                   dmnModel,
                                   results,
                                   t,
                                   null,
                                   Msg.FAILED_VALIDATOR );
        }
        return results.getMessages();
    }

    @Override
    public List<DMNMessage> validate(File xmlFile) {
        return validate( xmlFile, VALIDATE_MODEL );
    }

    @Override
    public List<DMNMessage> validate(File xmlFile, Validation... options) {
        DMNMessageManager results = new DefaultDMNMessagesManager(  );
        EnumSet<Validation> flags = EnumSet.copyOf( Arrays.asList( options ) );
        if( flags.contains( VALIDATE_SCHEMA ) ) {
            results.addAll( validateSchema( xmlFile ) );
        }
        if( flags.contains( VALIDATE_MODEL ) || flags.contains( VALIDATE_COMPILATION ) ) {
            Definitions dmndefs = null;
            try {
                dmndefs = DMNMarshallerFactory.newDefaultMarshaller().unmarshal( new FileReader( xmlFile ) );
                validateModelCompilation( dmndefs, results, flags );
            } catch ( Throwable t ) {
                MsgUtil.reportMessage( LOG,
                                       DMNMessage.Severity.ERROR,
                                       null,
                                       results,
                                       t,
                                       null,
                                       Msg.FAILED_VALIDATOR );
            }
        }
        return results.getMessages();
    }

    @Override
    public List<DMNMessage> validate(Reader reader) {
        return validate( reader, VALIDATE_MODEL );
    }

    @Override
    public List<DMNMessage> validate(Reader reader, Validation... options) {
        DMNMessageManager results = new DefaultDMNMessagesManager(  );
        EnumSet<Validation> flags = EnumSet.copyOf( Arrays.asList( options ) );
        try {
            String content = readContent( reader );
            if( flags.contains( VALIDATE_SCHEMA ) ) {
                results.addAll( validateSchema( new StringReader( content ) ) );
            }
            if( flags.contains( VALIDATE_MODEL ) || flags.contains( VALIDATE_COMPILATION ) ) {
                Definitions dmndefs = DMNMarshallerFactory.newDefaultMarshaller().unmarshal( new StringReader( content ) );
                validateModelCompilation( dmndefs, results, flags );
            }
        } catch ( Throwable t ) {
            MsgUtil.reportMessage( LOG,
                                   DMNMessage.Severity.ERROR,
                                   null,
                                   results,
                                   t,
                                   null,
                                   Msg.FAILED_VALIDATOR );
        }
        return results.getMessages();
    }

    private String readContent(Reader reader)
            throws IOException {
        char[] b = new char[32 * 1024];
        StringBuilder content = new StringBuilder(  );
        int chars = -1;
        while( (chars = reader.read( b ) ) > 0 ) {
            content.append( b, 0, chars );
        }
        return content.toString();
    }


    private void validateModelCompilation(Definitions dmnModel, DMNMessageManager results, EnumSet<Validation> flags) {
        if( flags.contains( VALIDATE_MODEL ) ) {
            results.addAll( validateModel( dmnModel ) );
        }
        if( flags.contains( VALIDATE_COMPILATION ) ) {
            results.addAll( validateCompilation( dmnModel, results ) );
        }
    }

    private List<DMNMessage> validateSchema(File xmlFile) {
        Source s = new StreamSource(xmlFile);
        return validateSchema( s );
    }

    private List<DMNMessage> validateSchema(Reader reader) {
        Source s = new StreamSource(reader);
        return validateSchema( s );
    }

    private List<DMNMessage> validateSchema(Source s) {
        List<DMNMessage> problems = new ArrayList<>();
        try {
            schema.newValidator().validate(s);
        } catch (SAXException | IOException e) {
            problems.add(new DMNMessageImpl( DMNMessage.Severity.ERROR, MsgUtil.createMessage( Msg.FAILED_XML_VALIDATION, e.getMessage() ), Msg.FAILED_XML_VALIDATION.getType(), null, e));
            logDebugMessages( problems );
        }
        // TODO detect if the XSD is not provided through schemaLocation, and validate against embedded
        return problems;
    }

    private List<DMNMessage> validateModel(Definitions dmnModel) {
        if (!kieContainer.isPresent()) {
            return failedInitMsg;
        }
        
        StatelessKieSession kieSession = kieContainer.get().newStatelessKieSession();
        MessageReporter reporter = new MessageReporter();
        kieSession.setGlobal( "reporter", reporter );
        
        kieSession.execute(allChildren(dmnModel).collect(toList()));

        return reporter.getMessages().getMessages();
    }

    private List<DMNMessage> validateCompilation(Definitions dmnModel, DMNMessageManager results) {
        if( dmnModel != null ) {
            DMNCompiler compiler = new DMNCompilerImpl();
            DMNModel model = compiler.compile( dmnModel );
            if( model != null ) {
                return model.getMessages();
            } else {
                MsgUtil.reportMessage( LOG,
                                       DMNMessage.Severity.ERROR,
                                       dmnModel,
                                       results,
                                       null,
                                       null,
                                       Msg.FAILED_VALIDATOR );
            }
        }
        return Collections.emptyList();
    }

    private static Stream<DMNModelInstrumentedBase> allChildren(DMNModelInstrumentedBase root) {
        return Stream.concat( Stream.of(root),
                              root.getChildren().stream().flatMap(DMNValidatorImpl::allChildren) );
    }

    private void logDebugMessages(List<DMNMessage> messages) {
        if ( LOG.isDebugEnabled() ) {
            for ( DMNMessage m : messages ) {
                LOG.debug("{}", m);
            }
        }
    }
}
