const executionIdBase = require('./executionIdBase');
const DMN_1 = `
  <dmn:definitions xmlns:dmn="http://www.omg.org/spec/DMN/20180521/MODEL/" xmlns="https://kiegroup.org/dmn/_84627CF5-6B15-47D8-A943-40555270CCEC" xmlns:di="http://www.omg.org/spec/DMN/20180521/DI/" xmlns:kie="http://www.drools.org/kie/dmn/1.2" xmlns:dmndi="http://www.omg.org/spec/DMN/20180521/DMNDI/" xmlns:dc="http://www.omg.org/spec/DMN/20180521/DC/" xmlns:feel="http://www.omg.org/spec/DMN/20180521/FEEL/" id="_66A7B1AB-61EF-4E13-B41E-46C928698EE2" name="example" typeLanguage="http://www.omg.org/spec/DMN/20180521/FEEL/" namespace="https://kiegroup.org/dmn/_84627CF5-6B15-47D8-A943-40555270CCEC">
    <dmn:extensionElements/>
    <dmn:inputData id="_70F1A759-16EA-44EE-98C1-E9B3726382FF" name="Name">
      <dmn:extensionElements/>
      <dmn:variable id="_544B578A-CB1D-4DE2-AB3C-22622689011A" name="Name" typeRef="string"/>
    </dmn:inputData>
    <dmn:decision id="_22CF5537-8C35-4EAB-B770-97BB5E58EEA8" name="Fred?">
      <dmn:extensionElements/>
      <dmn:variable id="_5B829B9A-CA62-4174-B3A3-968EDF4F5C6D" name="Fred?" typeRef="boolean"/>
      <dmn:informationRequirement id="_96ED21AE-BF0E-44A0-862C-790215C6FA54">
        <dmn:requiredInput href="#_70F1A759-16EA-44EE-98C1-E9B3726382FF"/>
      </dmn:informationRequirement>
      <dmn:decisionTable id="_D6AF6546-5995-4936-932E-93FE014AAA46" hitPolicy="UNIQUE" preferredOrientation="Rule-as-Row">
        <dmn:input id="_38911DC6-5410-49E3-9693-FBDCD73286E4">
          <dmn:inputExpression id="_C005FDF1-908D-44F7-BF84-DFC26743700B" typeRef="string">
            <dmn:text>Name</dmn:text>
          </dmn:inputExpression>
        </dmn:input>
        <dmn:output id="_96858CA5-E457-4F6F-BFDB-CF5D2DBEC837"/>
        <dmn:annotation name="annotation-1"/>
        <dmn:rule id="_2D490F65-BBEE-4FAE-ACDB-AB0900F20B3E">
          <dmn:inputEntry id="_AE1EE04D-1FE1-41F5-B47E-1C2C8A4D4FC5">
            <dmn:text>"Fred"</dmn:text>
          </dmn:inputEntry>
          <dmn:outputEntry id="_644F9F13-FC26-4239-9D64-CB3658703090">
            <dmn:text>true</dmn:text>
          </dmn:outputEntry>
          <dmn:annotationEntry>
            <dmn:text/>
          </dmn:annotationEntry>
        </dmn:rule>
        <dmn:rule id="_6B69BA8C-F416-4735-8EA3-69F6CCB572B7">
          <dmn:inputEntry id="_65F12800-FACD-47E7-81C8-78233543B8CB">
            <dmn:text>-</dmn:text>
          </dmn:inputEntry>
          <dmn:outputEntry id="_90D601DD-D8BF-4978-B423-8F6DE61786E5">
            <dmn:text>false</dmn:text>
          </dmn:outputEntry>
          <dmn:annotationEntry>
            <dmn:text/>
          </dmn:annotationEntry>
        </dmn:rule>
      </dmn:decisionTable>
    </dmn:decision>
    <dmndi:DMNDI>
      <dmndi:DMNDiagram>
        <di:extension>
          <kie:ComponentsWidthsExtension>
            <kie:ComponentWidths dmnElementRef="_D6AF6546-5995-4936-932E-93FE014AAA46">
              <kie:width>50</kie:width>
              <kie:width>100</kie:width>
              <kie:width>100</kie:width>
              <kie:width>100</kie:width>
            </kie:ComponentWidths>
          </kie:ComponentsWidthsExtension>
        </di:extension>
        <dmndi:DMNShape id="dmnshape-_70F1A759-16EA-44EE-98C1-E9B3726382FF" dmnElementRef="_70F1A759-16EA-44EE-98C1-E9B3726382FF" isCollapsed="false">
          <dmndi:DMNStyle>
            <dmndi:FillColor red="255" green="255" blue="255"/>
            <dmndi:StrokeColor red="0" green="0" blue="0"/>
            <dmndi:FontColor red="0" green="0" blue="0"/>
          </dmndi:DMNStyle>
          <dc:Bounds x="636" y="304" width="100" height="50"/>
          <dmndi:DMNLabel/>
        </dmndi:DMNShape>
        <dmndi:DMNShape id="dmnshape-_22CF5537-8C35-4EAB-B770-97BB5E58EEA8" dmnElementRef="_22CF5537-8C35-4EAB-B770-97BB5E58EEA8" isCollapsed="false">
          <dmndi:DMNStyle>
            <dmndi:FillColor red="255" green="255" blue="255"/>
            <dmndi:StrokeColor red="0" green="0" blue="0"/>
            <dmndi:FontColor red="0" green="0" blue="0"/>
          </dmndi:DMNStyle>
          <dc:Bounds x="785" y="305" width="100" height="50"/>
          <dmndi:DMNLabel/>
        </dmndi:DMNShape>
        <dmndi:DMNEdge id="dmnedge-_96ED21AE-BF0E-44A0-862C-790215C6FA54" dmnElementRef="_96ED21AE-BF0E-44A0-862C-790215C6FA54">
          <di:waypoint x="686" y="329"/>
          <di:waypoint x="835" y="305"/>
        </dmndi:DMNEdge>
      </dmndi:DMNDiagram>
    </dmndi:DMNDI>
  </dmn:definitions>`;

const modelData = [
  {
    executionId: executionIdBase + 1000,
    deploymentDate: '01012020',
    modelId: '1234567890',
    name: 'myMortgage',
    namespace: 'modelNameSpace',
    type: 'http://www.omg.org/spec/DMN/20151101/dmn.xsd',
    serviceIdentifier: {
      groupId: 'groupId',
      artifactId: 'artifactId',
      version: 'version'
    },
    model: DMN_1
  },
  {
    executionId: executionIdBase + 1001,
    deploymentDate: '01012020',
    modelId: '1234567890',
    name: 'myMortgage',
    namespace: 'modelNameSpace',
    type: 'http://www.omg.org/spec/DMN/20151101/dmn.xsd',
    serviceIdentifier: {
      groupId: 'groupId',
      artifactId: 'artifactId',
      version: 'version'
    },
    model: DMN_1
  },
  {
    executionId: executionIdBase + 1002,
    deploymentDate: '01012020',
    modelId: '1234567890',
    name: 'myMortgage',
    namespace: 'modelNameSpace',
    type: 'http://www.omg.org/spec/DMN/20151101/dmn.xsd',
    serviceIdentifier: {
      groupId: 'groupId',
      artifactId: 'artifactId',
      version: 'version'
    },
    model: DMN_1
  },
  {
    executionId: executionIdBase + 1003,
    deploymentDate: '01012020',
    modelId: '1234567890',
    name: 'myMortgage',
    namespace: 'modelNameSpace',
    type: 'http://www.omg.org/spec/DMN/20151101/dmn.xsd',
    serviceIdentifier: {
      groupId: 'groupId',
      artifactId: 'artifactId',
      version: 'version'
    },
    model: DMN_1
  },
  {
    executionId: executionIdBase + 1004,
    deploymentDate: '01012020',
    modelId: '1234567890',
    name: 'myMortgage',
    namespace: 'modelNameSpace',
    type: 'http://www.omg.org/spec/DMN/20151101/dmn.xsd',
    serviceIdentifier: {
      groupId: 'groupId',
      artifactId: 'artifactId',
      version: 'version'
    },
    model: DMN_1
  },
  {
    executionId: executionIdBase + 1005,
    deploymentDate: '01012020',
    modelId: '1234567890',
    name: 'fraud-scoring',
    namespace: 'modelNameSpace',
    type: 'http://www.omg.org/spec/DMN/20151101/dmn.xsd',
    serviceIdentifier: {
      groupId: 'groupId',
      artifactId: 'artifactId',
      version: 'version'
    },
    model: DMN_1
  },
  {
    executionId: executionIdBase + 1006,
    deploymentDate: '01012020',
    modelId: '1234567890',
    name: 'fraud-scoring',
    namespace: 'modelNameSpace',
    type: 'http://www.omg.org/spec/DMN/20151101/dmn.xsd',
    serviceIdentifier: {
      groupId: 'groupId',
      artifactId: 'artifactId',
      version: 'version'
    },
    model: DMN_1
  },
  {
    executionId: executionIdBase + 1007,
    deploymentDate: '01012020',
    modelId: '1234567890',
    name: 'fraud-scoring',
    namespace: 'modelNameSpace',
    type: 'http://www.omg.org/spec/DMN/20151101/dmn.xsd',
    serviceIdentifier: {
      groupId: 'groupId',
      artifactId: 'artifactId',
      version: 'version'
    },
    model: DMN_1
  },
  {
    executionId: executionIdBase + 1008,
    deploymentDate: '01012020',
    modelId: '1234567890',
    name: 'fraud-scoring',
    namespace: 'modelNameSpace',
    type: 'http://www.omg.org/spec/DMN/20151101/dmn.xsd',
    serviceIdentifier: {
      groupId: 'groupId',
      artifactId: 'artifactId',
      version: 'version'
    },
    model: DMN_1
  },
  {
    executionId: executionIdBase + 1009,
    deploymentDate: '01012020',
    modelId: '1234567890',
    name: 'fraud-scoring',
    namespace: 'modelNameSpace',
    type: 'http://www.omg.org/spec/DMN/20151101/dmn.xsd',
    serviceIdentifier: {
      groupId: 'groupId',
      artifactId: 'artifactId',
      version: 'version'
    },
    model: DMN_1
  }
];

module.exports = modelData;
