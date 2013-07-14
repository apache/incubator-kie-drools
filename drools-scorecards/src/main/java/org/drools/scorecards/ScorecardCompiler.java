/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.scorecards;

import org.dmg.pmml.pmml_4_1.descr.PMML;
import org.drools.scorecards.drl.DeclaredTypesDRLEmitter;
import org.drools.scorecards.drl.ExternalModelDRLEmitter;
import org.drools.scorecards.parser.AbstractScorecardParser;
import org.drools.scorecards.parser.ScorecardParseException;
import org.drools.scorecards.parser.xls.XLSScorecardParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.util.List;

public class ScorecardCompiler {

    private PMML pmmlDocument = null;
    public static final String DEFAULT_SHEET_NAME = "scorecards";
    private List<ScorecardError> scorecardErrors;
    private DrlType drlType;
    private final static Logger logger = LoggerFactory.getLogger(ScorecardCompiler.class);

    public ScorecardCompiler(DrlType drlType) {
        this.drlType = drlType;
    }

    public ScorecardCompiler() {
        this(DrlType.INTERNAL_DECLARED_TYPES);
    }

    public DrlType getDrlType() {
        return drlType;
    }

    public void setDrlType(DrlType drlType) {
        this.drlType = drlType;
    }

    /* method for use from Guvnor */
    protected void setPMMLDocument(PMML pmmlDocument){
        this.pmmlDocument = pmmlDocument;
    }

    public boolean compileFromExcel(final String pathToFile) {
        return compileFromExcel(pathToFile, DEFAULT_SHEET_NAME);
    }

    public boolean compileFromExcel(final String pathToFile, final String worksheetName) {
        FileInputStream inputStream = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            inputStream = new FileInputStream(pathToFile);
            bufferedInputStream = new BufferedInputStream(inputStream);
            return compileFromExcel(bufferedInputStream, worksheetName);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeStream(bufferedInputStream);
            closeStream(inputStream);
        }
        return false;
    }

    public boolean compileFromExcel(final InputStream stream) {
        return compileFromExcel(stream, DEFAULT_SHEET_NAME);
    }

    public boolean compileFromExcel(final InputStream stream, final String worksheetName) {
        try {
            AbstractScorecardParser parser = new XLSScorecardParser();
            scorecardErrors = parser.parseFile(stream, worksheetName);
            if ( scorecardErrors.isEmpty() ) {
                pmmlDocument = parser.getPMMLDocument();
                return true;
            }
        } catch (ScorecardParseException e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeStream(stream);
        }
        return false;
    }

    public PMML getPMMLDocument() {
        return pmmlDocument;
    }

    public String getPMML(){
        if (pmmlDocument == null ) {
            return null;
        }
        // create a JAXBContext for the PMML class
        JAXBContext ctx = null;
        try {
            ctx = JAXBContext.newInstance(PMML.class);
            Marshaller marshaller = ctx.createMarshaller();
            // the property JAXB_FORMATTED_OUTPUT specifies whether or not the
            // marshalled XML data is formatted with linefeeds and indentation
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            // marshal the data in the Java content tree
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(pmmlDocument, stringWriter);
            return stringWriter.toString();
        } catch (JAXBException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public String getDRL(){
        if (pmmlDocument != null) {
            if (drlType == DrlType.INTERNAL_DECLARED_TYPES) {
                return new DeclaredTypesDRLEmitter().emitDRL(pmmlDocument);
            } else if (drlType == DrlType.EXTERNAL_OBJECT_MODEL) {
                return new ExternalModelDRLEmitter().emitDRL(pmmlDocument);
            }
        }
        return null;
    }

    /* convienence method for use from Guvnor*/
    public static String convertToDRL(PMML pmml, DrlType drlType) {
        if (pmml != null) {
            ScorecardCompiler scorecardCompiler = new ScorecardCompiler(drlType);
            scorecardCompiler.setPMMLDocument(pmml);
            return scorecardCompiler.getDRL();
        }
        return null;
    }

    public List<ScorecardError> getScorecardParseErrors() {
        return scorecardErrors;
    }

    private void closeStream(final InputStream stream) {
        try {
            if ( stream != null ) {
                stream.close();
            }
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static enum DrlType {
        INTERNAL_DECLARED_TYPES, EXTERNAL_OBJECT_MODEL
    }
}
