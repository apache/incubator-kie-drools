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
import * as React from 'react';
import { mount } from 'enzyme';
import ModelDiagram from '../ModelDiagram';
import { ModelData } from '../../../../types';

const mockOpenFunction = jest.fn();
jest.mock('@kie-tools/kie-editors-standalone/dist/dmn', () => ({
  open: () => mockOpenFunction()
}));

afterAll(() => jest.resetAllMocks());

describe('ModelDiagram', () => {
  test('renders a DMN model', () => {
    mount(<ModelDiagram model={modelDataDMN} />);

    // investigating other ways to test for the embedded dmn viewer
    expect(mockOpenFunction).toBeCalledTimes(1);
  });

  test('renders a message if the model type is not supported', () => {
    const wrapper = mount(<ModelDiagram model={modelDataUnknown} />);

    expect(wrapper.find('h4').text()).toEqual('Unsupported model type');
  });
});

const modelDataDMN = {
  executionId: 'b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000',
  deploymentDate: '01012020',
  modelId: '1234567890',
  name: 'myMortgage',
  namespace: 'modelNameSpace',
  dmnVersion: 'http://www.omg.org/spec/DMN/20151101/dmn.xsd',
  modelVersion: '',
  serviceIdentifier: {
    groupId: 'groupId',
    artifactId: 'artifactId',
    modelVersion: 'version'
  },
  model:
    '<dmn:definitions xmlns:dmn="http://www.omg.org/spec/DMN/20180521/MODEL/" xmlns="https://kiegroup.org/dmn/_84627CF5-6B15-47D8-A943-40555270CCEC" xmlns:di="http://www.omg.org/spec/DMN/20180521/DI/" xmlns:kie="http://www.drools.org/kie/dmn/1.2" xmlns:dmndi="http://www.omg.org/spec/DMN/20180521/DMNDI/" xmlns:dc="http://www.omg.org/spec/DMN/20180521/DC/" xmlns:feel="http://www.omg.org/spec/DMN/20180521/FEEL/" id="_66A7B1AB-61EF-4E13-B41E-46C928698EE2" name="example" typeLanguage="http://www.omg.org/spec/DMN/20180521/FEEL/" namespace="https://kiegroup.org/dmn/_84627CF5-6B15-47D8-A943-40555270CCEC">\n    <dmn:extensionElements/>\n    <dmn:inputData id="_70F1A759-16EA-44EE-98C1-E9B3726382FF" name="Name">\n      <dmn:extensionElements/>\n      <dmn:variable id="_544B578A-CB1D-4DE2-AB3C-22622689011A" name="Name" typeRef="string"/>\n    </dmn:inputData>\n    <dmn:decision id="_22CF5537-8C35-4EAB-B770-97BB5E58EEA8" name="Fred?">\n      <dmn:extensionElements/>\n      <dmn:variable id="_5B829B9A-CA62-4174-B3A3-968EDF4F5C6D" name="Fred?" typeRef="boolean"/>\n      <dmn:informationRequirement id="_96ED21AE-BF0E-44A0-862C-790215C6FA54">\n        <dmn:requiredInput href="#_70F1A759-16EA-44EE-98C1-E9B3726382FF"/>\n      </dmn:informationRequirement>\n      <dmn:decisionTable id="_D6AF6546-5995-4936-932E-93FE014AAA46" hitPolicy="UNIQUE" preferredOrientation="Rule-as-Row">\n        <dmn:input id="_38911DC6-5410-49E3-9693-FBDCD73286E4">\n          <dmn:inputExpression id="_C005FDF1-908D-44F7-BF84-DFC26743700B" typeRef="string">\n            <dmn:text>Name</dmn:text>\n          </dmn:inputExpression>\n        </dmn:input>\n        <dmn:output id="_96858CA5-E457-4F6F-BFDB-CF5D2DBEC837"/>\n        <dmn:annotation name="annotation-1"/>\n        <dmn:rule id="_2D490F65-BBEE-4FAE-ACDB-AB0900F20B3E">\n          <dmn:inputEntry id="_AE1EE04D-1FE1-41F5-B47E-1C2C8A4D4FC5">\n            <dmn:text>"Fred"</dmn:text>\n          </dmn:inputEntry>\n          <dmn:outputEntry id="_644F9F13-FC26-4239-9D64-CB3658703090">\n            <dmn:text>true</dmn:text>\n          </dmn:outputEntry>\n          <dmn:annotationEntry>\n            <dmn:text/>\n          </dmn:annotationEntry>\n        </dmn:rule>\n        <dmn:rule id="_6B69BA8C-F416-4735-8EA3-69F6CCB572B7">\n          <dmn:inputEntry id="_65F12800-FACD-47E7-81C8-78233543B8CB">\n            <dmn:text>-</dmn:text>\n          </dmn:inputEntry>\n          <dmn:outputEntry id="_90D601DD-D8BF-4978-B423-8F6DE61786E5">\n            <dmn:text>false</dmn:text>\n          </dmn:outputEntry>\n          <dmn:annotationEntry>\n            <dmn:text/>\n          </dmn:annotationEntry>\n        </dmn:rule>\n      </dmn:decisionTable>\n    </dmn:decision>\n    <dmndi:DMNDI>\n      <dmndi:DMNDiagram>\n        <di:extension>\n          <kie:ComponentsWidthsExtension>\n            <kie:ComponentWidths dmnElementRef="_D6AF6546-5995-4936-932E-93FE014AAA46">\n              <kie:width>50</kie:width>\n              <kie:width>100</kie:width>\n              <kie:width>100</kie:width>\n              <kie:width>100</kie:width>\n            </kie:ComponentWidths>\n          </kie:ComponentsWidthsExtension>\n        </di:extension>\n        <dmndi:DMNShape id="dmnshape-_70F1A759-16EA-44EE-98C1-E9B3726382FF" dmnElementRef="_70F1A759-16EA-44EE-98C1-E9B3726382FF" isCollapsed="false">\n          <dmndi:DMNStyle>\n            <dmndi:FillColor red="255" green="255" blue="255"/>\n            <dmndi:StrokeColor red="0" green="0" blue="0"/>\n            <dmndi:FontColor red="0" green="0" blue="0"/>\n          </dmndi:DMNStyle>\n          <dc:Bounds x="636" y="304" width="100" height="50"/>\n          <dmndi:DMNLabel/>\n        </dmndi:DMNShape>\n        <dmndi:DMNShape id="dmnshape-_22CF5537-8C35-4EAB-B770-97BB5E58EEA8" dmnElementRef="_22CF5537-8C35-4EAB-B770-97BB5E58EEA8" isCollapsed="false">\n          <dmndi:DMNStyle>\n            <dmndi:FillColor red="255" green="255" blue="255"/>\n            <dmndi:StrokeColor red="0" green="0" blue="0"/>\n            <dmndi:FontColor red="0" green="0" blue="0"/>\n          </dmndi:DMNStyle>\n          <dc:Bounds x="785" y="305" width="100" height="50"/>\n          <dmndi:DMNLabel/>\n        </dmndi:DMNShape>\n        <dmndi:DMNEdge id="dmnedge-_96ED21AE-BF0E-44A0-862C-790215C6FA54" dmnElementRef="_96ED21AE-BF0E-44A0-862C-790215C6FA54">\n          <di:waypoint x="686" y="329"/>\n          <di:waypoint x="835" y="305"/>\n        </dmndi:DMNEdge>\n      </dmndi:DMNDiagram>\n    </dmndi:DMNDI>\n  </dmn:definitions>'
} as ModelData;

const modelDataUnknown = {
  executionId: 'b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000',
  deploymentDate: '01012020',
  modelId: '1234567890',
  name: 'unknown model',
  namespace: 'modelNameSpace',
  dmnVersion: '????',
  modelVersion: '',
  serviceIdentifier: {
    groupId: 'groupId',
    artifactId: 'artifactId',
    modelVersion: 'version'
  },
  model: '<?xml version="1.0" ?> \n <metadata>\n </metadata>'
} as ModelData;
