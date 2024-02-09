/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.validation.dtanalysis.mcdc;

import java.io.StringWriter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.validation.dtanalysis.mcdc.MCDCAnalyser.PosNegBlock;
import org.kie.dmn.validation.dtanalysis.mcdc.MCDCAnalyser.Record;
import org.kie.dmn.validation.dtanalysis.mcdc.dmntck.ObjectFactory;
import org.kie.dmn.validation.dtanalysis.mcdc.dmntck.TestCases;
import org.kie.dmn.validation.dtanalysis.mcdc.dmntck.TestCases.TestCase;
import org.kie.dmn.validation.dtanalysis.mcdc.dmntck.TestCases.TestCase.InputNode;
import org.kie.dmn.validation.dtanalysis.mcdc.dmntck.TestCases.TestCase.ResultNode;

public class MCDC2TCKGenerator {

    public static String mcdc2tck(DecisionTable dt, List<PosNegBlock> selectedBlocks) throws JAXBException {
        ObjectFactory factory = new ObjectFactory();

        TestCases testCases = factory.createTestCases();

        Set<Record> mcdcRecords = new LinkedHashSet<>();
        int testCaseId = 1;
        for (PosNegBlock b : selectedBlocks) {
            boolean add = mcdcRecords.add(b.posRecord);
            if (add) {
                appendRecordToTestCases(dt, testCases, String.valueOf(testCaseId), b.posRecord);
                testCaseId++;
            }
            for (Record negRecord : b.negRecords) {
                add = mcdcRecords.add(negRecord);
                if (add) {
                    appendRecordToTestCases(dt, testCases, String.valueOf(testCaseId), negRecord);
                    testCaseId++;
                }
            }
        }

        JAXBContext jaxbContext = JAXBContext.newInstance(TestCases.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(testCases, sw);
        return sw.toString();
    }

    private static void appendRecordToTestCases(DecisionTable dt, TestCases testCases, String withId, Record record) {
        ObjectFactory factory = new ObjectFactory();
        TestCase testCase = factory.createTestCasesTestCase()
                                   .withId("mcdc_" + withId)
                                   .withName("Test case " + withId);
        testCase.withDescription(record.toString());
        for (int i = 0; i < record.enums.length; i++) {
            Object en = record.enums[i];
            String inputName = dt.getInput().get(i).getInputExpression().getText();
            InputNode inputNode = factory.createTestCasesTestCaseInputNode().withName(inputName);
            testCase.withInputNode(inputNode);
            JAXBElement<Object> jaxbElement = factory.createValueTypeValue(en);
            inputNode.withValue(jaxbElement);
        }
        if (record.output.size() == 1) {
            Object out = record.output.get(0);
            String outputName = dt.getOutputLabel();
            ResultNode resultNode = factory.createTestCasesTestCaseResultNode().withName(outputName);
            testCase.withResultNode(resultNode);
            JAXBElement<Object> jaxbElement = factory.createValueTypeValue(out);
            resultNode.withExpected(factory.createValueType().withValue(jaxbElement));
        } else {
            throw new UnsupportedOperationException();
        }
        testCases.withTestCase(testCase);
    }
}
