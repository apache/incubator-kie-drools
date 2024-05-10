<!---
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
-->
# Kogito JIT Executor

Kogito JIT (Just In Time) Executor is an application that allow to execute a business model on the fly on a given context. The JIT Executor is intended to be an helpful support during **_Modeling and Development phases_**; for instance, when modeling a DMN asset, the JIT Executor can be used to support live interactions with the DMN model, while still being authored. The JIT Executor is **not** recommended for Deployment solution; when looking to deploy to the Cloud, we recommend to build and deploy a standard Kogito -based project containing the assets, in order to take advantage of the all features and full capabilities of the [Kogito platform](https://docs.kogito.kie.org/latest/html_single/#con-kogito-automation_kogito-docs).

At the moment, the application supports DMN and BPMN models.

Once the user has compiled and packaged the application for example with:

```bash
mvn clean package -DskipTests
```

run the generated application under `jitexecutor-runner/target/quarkus-app/` with 

```bash
java -jar jitexecutor-runner/target/quarkus-app/quarkus-run.jar
``` 

DMN
===

An helper HTML page is available at `localhost:8080/index.html`. You can use this page to submit a DMN model with a context and get back the results. 
Otherwise, you can make a POST request directly to the endpoint `/jitdmn` with the following body

```json

{
  "model" : "<your DMN model here>",
  "context" : "{<your context here>}"
}
``` 

For example, using a simple DMN model that sum two input numbers: 

```bash
curl -H "Content-Type: application/json" -X POST http://localhost:8080/jitdmn -d '{"context": {"n" : 1, "m" : 2}, "model": "<dmn:definitions xmlns:dmn=\"http://www.omg.org/spec/DMN/20180521/MODEL/\" xmlns=\"https://kiegroup.org/dmn/_35091C3B-6022-4D40-8982-D528940CD5F9\" xmlns:feel=\"http://www.omg.org/spec/DMN/20180521/FEEL/\" xmlns:kie=\"http://www.drools.org/kie/dmn/1.2\" xmlns:dmndi=\"http://www.omg.org/spec/DMN/20180521/DMNDI/\" xmlns:di=\"http://www.omg.org/spec/DMN/20180521/DI/\" xmlns:dc=\"http://www.omg.org/spec/DMN/20180521/DC/\" id=\"_81A31B42-A686-4ED2-81FB-C1F91A95D685\" name=\"new-file\" typeLanguage=\"http://www.omg.org/spec/DMN/20180521/FEEL/\" namespace=\"https://kiegroup.org/dmn/_35091C3B-6022-4D40-8982-D528940CD5F9\"> <dmn:extensionElements/> <dmn:inputData id=\"_6FFA48B5-FB55-4962-9E64-F08418BBFF9E\" name=\"n\"> <dmn:extensionElements/> <dmn:variable id=\"_EC4D123A-D6D4-4E5D-B369-6E99F57D9C22\" name=\"n\" typeRef=\"number\"/> </dmn:inputData> <dmn:decision id=\"_1D69C44E-D782-492A-A50D-740B444F1993\" name=\"sum\"> <dmn:extensionElements/> <dmn:variable id=\"_3AF7A705-8304-4B5E-8EC7-05D9934E6C06\" name=\"sum\" typeRef=\"number\"/> <dmn:informationRequirement id=\"_E0FE5C90-5EAF-45DB-ABFD-10D27FA97AB4\"> <dmn:requiredInput href=\"#_6FFA48B5-FB55-4962-9E64-F08418BBFF9E\"/> </dmn:informationRequirement> <dmn:informationRequirement id=\"_C52CB29E-3236-4661-8856-7276AE8ED01F\"> <dmn:requiredInput href=\"#_B8221A07-DFB5-40BC-95A9-7926A6EC55C4\"/> </dmn:informationRequirement> <dmn:literalExpression id=\"_3DB33034-AC21-45DE-A5B7-D6B09B01ED1E\"> <dmn:text>n + m</dmn:text> </dmn:literalExpression> </dmn:decision> <dmn:inputData id=\"_B8221A07-DFB5-40BC-95A9-7926A6EC55C4\" name=\"m\"> <dmn:extensionElements/> <dmn:variable id=\"_455CD571-BBD9-4762-B496-832E7EBCD07F\" name=\"m\" typeRef=\"number\"/> </dmn:inputData> <dmndi:DMNDI> <dmndi:DMNDiagram id=\"_7FC1E997-A627-409E-A6D5-9A30F2F30AB4\" name=\"DRG\"> <di:extension> <kie:ComponentsWidthsExtension> <kie:ComponentWidths dmnElementRef=\"_3DB33034-AC21-45DE-A5B7-D6B09B01ED1E\"> <kie:width>300</kie:width> </kie:ComponentWidths> </kie:ComponentsWidthsExtension> </di:extension> <dmndi:DMNShape id=\"dmnshape-drg-_6FFA48B5-FB55-4962-9E64-F08418BBFF9E\" dmnElementRef=\"_6FFA48B5-FB55-4962-9E64-F08418BBFF9E\" isCollapsed=\"false\"> <dmndi:DMNStyle> <dmndi:FillColor red=\"255\" green=\"255\" blue=\"255\"/> <dmndi:StrokeColor red=\"0\" green=\"0\" blue=\"0\"/> <dmndi:FontColor red=\"0\" green=\"0\" blue=\"0\"/> </dmndi:DMNStyle> <dc:Bounds x=\"704\" y=\"364\" width=\"100\" height=\"50\"/> <dmndi:DMNLabel/> </dmndi:DMNShape> <dmndi:DMNShape id=\"dmnshape-drg-_1D69C44E-D782-492A-A50D-740B444F1993\" dmnElementRef=\"_1D69C44E-D782-492A-A50D-740B444F1993\" isCollapsed=\"false\"> <dmndi:DMNStyle> <dmndi:FillColor red=\"255\" green=\"255\" blue=\"255\"/> <dmndi:StrokeColor red=\"0\" green=\"0\" blue=\"0\"/> <dmndi:FontColor red=\"0\" green=\"0\" blue=\"0\"/> </dmndi:DMNStyle> <dc:Bounds x=\"756\" y=\"283\" width=\"100\" height=\"50\"/> <dmndi:DMNLabel/> </dmndi:DMNShape> <dmndi:DMNShape id=\"dmnshape-drg-_B8221A07-DFB5-40BC-95A9-7926A6EC55C4\" dmnElementRef=\"_B8221A07-DFB5-40BC-95A9-7926A6EC55C4\" isCollapsed=\"false\"> <dmndi:DMNStyle> <dmndi:FillColor red=\"255\" green=\"255\" blue=\"255\"/> <dmndi:StrokeColor red=\"0\" green=\"0\" blue=\"0\"/> <dmndi:FontColor red=\"0\" green=\"0\" blue=\"0\"/> </dmndi:DMNStyle> <dc:Bounds x=\"822\" y=\"364\" width=\"100\" height=\"50\"/> <dmndi:DMNLabel/> </dmndi:DMNShape> <dmndi:DMNEdge id=\"dmnedge-drg-_E0FE5C90-5EAF-45DB-ABFD-10D27FA97AB4\" dmnElementRef=\"_E0FE5C90-5EAF-45DB-ABFD-10D27FA97AB4\"> <di:waypoint x=\"754\" y=\"389\"/> <di:waypoint x=\"806\" y=\"333\"/> </dmndi:DMNEdge> <dmndi:DMNEdge id=\"dmnedge-drg-_C52CB29E-3236-4661-8856-7276AE8ED01F\" dmnElementRef=\"_C52CB29E-3236-4661-8856-7276AE8ED01F\"> <di:waypoint x=\"872\" y=\"389\"/> <di:waypoint x=\"806\" y=\"333\"/> </dmndi:DMNEdge> </dmndi:DMNDiagram> </dmndi:DMNDI> </dmn:definitions>"}'
```

The response is 
```json
{"sum":3,"m":2,"n":1}
```

If you are interested in the full DMN result, you can use the endpoint `/jitdmn/dmnresult` with the same payload. In this case, the response would be 
```json
{
  "namespace": "https://kiegroup.org/dmn/_35091C3B-6022-4D40-8982-D528940CD5F9",
  "modelName": "new-file",
  "dmnContext": {
    "sum": 3,
    "m": 2,
    "n": 1
  },
  "messages": [],
  "decisionResults": [
    {
      "decisionId": "_1D69C44E-D782-492A-A50D-740B444F1993",
      "decisionName": "sum",
      "result": 3,
      "messages": [],
      "evaluationStatus": "SUCCEEDED"
    }
  ]
}
```

## Explainability

It is possible to _execute and explain_ a DMN model given a particular context. The endpoint `/jitdmn/executeAndExplain` accepts the same JSON object of the previous endpoints, and the response is 
```json
{
  "dmnResult": {
    "namespace": "https://kiegroup.org/dmn/_35091C3B-6022-4D40-8982-D528940CD5F9",
    "modelName": "new-file",
    "dmnContext": {
      "sum": 3,
      "m": 2,
      "n": 1
    },
    "messages": [],
    "decisionResults": [
      {
        "decisionId": "_1D69C44E-D782-492A-A50D-740B444F1993",
        "decisionName": "sum",
        "result": 3,
        "messages": [],
        "evaluationStatus": "SUCCEEDED"
      }
    ]
  },
  "saliencies": {
    "status": "SUCCEEDED",
    "saliencies": [
      {
        "outcomeId": "_1D69C44E-D782-492A-A50D-740B444F1993",
        "outcomeName": "sum",
        "featureImportance": [
          {
            "featureName": "n",
            "featureScore": 0
          },
          {
            "featureName": "m",
            "featureScore": 0
          }
        ]
      }
    ]
  }
}
```

The feature importance is calculated by the LIME algorithm, it can be configured using the following `application.properties` keys: 
- `kogito.explainability.lime.no-of-perturbation`: Number of features to be perturbed in a single sample (default is `1`).  
- `kogito.explainability.lime.sample-size`: Number of samples to be generated for the local linear model training (default is `300`).

## Validation

The standard set of capabilities from `kie-dmn-validator` as used by the Kie Maven plugin or the Kogito codegen infrastructure, is also available as part of this JIT executor.
Just sending the XML of the DMN model to the relevant endpoint, will return validation messages.

For example, using a simple DMN model that does not contain any detected issues:
```bash
curl --location --request POST 'http://localhost:8080/jitdmn/validate' --header 'Accept: application/json' --header 'Content-Type: application/xml' --data-raw '<dmn:definitions xmlns:dmn="http://www.omg.org/spec/DMN/20180521/MODEL/" xmlns="https://kiegroup.org/dmn/_79B69A7F-5A25-4B53-BD6A-3216EDC246ED" xmlns:feel="http://www.omg.org/spec/DMN/20180521/FEEL/" xmlns:kie="http://www.drools.org/kie/dmn/1.2" xmlns:dmndi="http://www.omg.org/spec/DMN/20180521/DMNDI/" xmlns:di="http://www.omg.org/spec/DMN/20180521/DI/" xmlns:dc="http://www.omg.org/spec/DMN/20180521/DC/" id="_E01B9C96-BCD5-4618-AC02-35F77D1065E2" name="loan" typeLanguage="http://www.omg.org/spec/DMN/20180521/FEEL/" namespace="https://kiegroup.org/dmn/_79B69A7F-5A25-4B53-BD6A-3216EDC246ED"> <dmn:extensionElements/> <dmn:itemDefinition id="_94619DCD-2602-4A43-97E9-9809D76F15A5" name="tLoan" isCollection="false"> <dmn:itemComponent id="_3C8953D0-F1AD-40F6-BC5E-813F3185F3F1" name="amount" isCollection="false"> <dmn:typeRef>number</dmn:typeRef> </dmn:itemComponent> <dmn:itemComponent id="_1CF9A4C0-9218-4F96-AB1A-66570002B7C5" name="years" isCollection="false"> <dmn:typeRef>number</dmn:typeRef> </dmn:itemComponent> </dmn:itemDefinition> <dmn:inputData id="_058269A1-A5AC-44CB-9479-16A04DC19D77" name="Credit score"> <dmn:extensionElements/> <dmn:variable id="_BA408840-4504-44EB-BD7F-6261F787E342" name="Credit score" typeRef="number"/> </dmn:inputData> <dmn:inputData id="_65226EA1-EE4A-41F1-91A2-B2A940A75982" name="Salary"> <dmn:extensionElements/> <dmn:variable id="_204FE05E-BCF8-4AF5-AD36-B5A04E0EB929" name="Salary" typeRef="number"/> </dmn:inputData> <dmn:inputData id="_516E0AEC-03F3-43F4-B886-C489CA82A1C9" name="Loan"> <dmn:extensionElements/> <dmn:variable id="_90456001-E9FA-4DDC-B2BA-DF1B62A25AAA" name="Loan" typeRef="tLoan"/> </dmn:inputData> <dmn:decision id="_6A3FDF72-7F17-4DC5-AC1D-1DCC972C0146" name="Preapproval"> <dmn:extensionElements/> <dmn:variable id="_2C06D150-7AB2-48C9-BFC6-F48884FD96EF" name="Preapproval" typeRef="boolean"/> <dmn:informationRequirement id="_9BFEFD1E-9830-4630-8171-B6F1D3100308"> <dmn:requiredInput href="#_058269A1-A5AC-44CB-9479-16A04DC19D77"/> </dmn:informationRequirement> <dmn:informationRequirement id="_4736D6D5-5A61-4C0E-ADBA-5AAD99221445"> <dmn:requiredDecision href="#_50635164-1A27-4B84-AE16-639A118CE44C"/> </dmn:informationRequirement> <dmn:decisionTable id="_E7994A2B-1189-4BE5-9382-891D48E87D47" hitPolicy="UNIQUE" preferredOrientation="Rule-as-Row"> <dmn:input id="_9CCC5EBB-BC59-4397-B478-BB434279EBF8"> <dmn:inputExpression id="_85A80C30-68FA-405F-BE6D-D1B6C484CD81" typeRef="number"> <dmn:text>Credit score</dmn:text> </dmn:inputExpression> </dmn:input> <dmn:input id="_D160037A-3B50-44FE-BC28-700F750D1A3A"> <dmn:inputExpression id="_2774CDB9-0B9D-4B89-9639-5D1FD7D6D61B" typeRef="number"> <dmn:text>DTI</dmn:text> </dmn:inputExpression> </dmn:input> <dmn:output id="_8220E87A-3913-4FC5-9926-4A9FD28F82EA"/> <dmn:annotation name="annotation-1"/> <dmn:rule id="_C09BF27F-E03C-4390-8719-E7411784ABCB"> <dmn:inputEntry id="_C02D6987-5B78-42ED-B48D-E3CE5844C22B"> <dmn:text>&lt;700</dmn:text> </dmn:inputEntry> <dmn:inputEntry id="_922FFAE0-9635-420B-8002-4583065E6710"> <dmn:text>&lt;=.28</dmn:text> </dmn:inputEntry> <dmn:outputEntry id="_75A30858-95A6-46F4-89D5-BFFF8B3739AD"> <dmn:text>true</dmn:text> </dmn:outputEntry> <dmn:annotationEntry> <dmn:text/> </dmn:annotationEntry> </dmn:rule> <dmn:rule id="_5CE22529-735A-43D8-9043-E342F58D1CDD"> <dmn:inputEntry id="_14C78BCF-736B-4032-8AB8-B73967479EEF"> <dmn:text>&gt;=700</dmn:text> </dmn:inputEntry> <dmn:inputEntry id="_6AA1D822-C08E-4B4E-BE8B-4C3587E6F034"> <dmn:text>-</dmn:text> </dmn:inputEntry> <dmn:outputEntry id="_CD1AFB69-DDC6-416E-B780-FE2FB3AAC6C1"> <dmn:text>true</dmn:text> </dmn:outputEntry> <dmn:annotationEntry> <dmn:text/> </dmn:annotationEntry> </dmn:rule> <dmn:rule id="_5EB4DDC3-CE49-419A-B7C8-7A6916D6334F"> <dmn:inputEntry id="_2F5D6384-4AE6-4408-88F4-4D3C86F26649"> <dmn:text>&lt;700</dmn:text> </dmn:inputEntry> <dmn:inputEntry id="_5CA8DA56-07BE-4410-85DD-800508300DB8"> <dmn:text>&gt;.28</dmn:text> </dmn:inputEntry> <dmn:outputEntry id="_8B85A627-73DD-46E7-99DA-26499B3DD9BD"> <dmn:text>false</dmn:text> </dmn:outputEntry> <dmn:annotationEntry> <dmn:text/> </dmn:annotationEntry> </dmn:rule> </dmn:decisionTable> </dmn:decision> <dmn:decision id="_50635164-1A27-4B84-AE16-639A118CE44C" name="DTI"> <dmn:extensionElements/> <dmn:variable id="_7FF18790-80C8-4124-9BFB-93383CE6A50F" name="DTI" typeRef="number"/> <dmn:informationRequirement id="_7A538A2F-562E-4E49-B5F0-572FFCFEF4CB"> <dmn:requiredInput href="#_516E0AEC-03F3-43F4-B886-C489CA82A1C9"/> </dmn:informationRequirement> <dmn:informationRequirement id="_E508941C-2DE2-41DD-8406-EA8AD646DB7F"> <dmn:requiredInput href="#_65226EA1-EE4A-41F1-91A2-B2A940A75982"/> </dmn:informationRequirement> <dmn:literalExpression id="_60A349DD-1F30-488B-BA65-74160F6496F3"> <dmn:text>(Loan.amount / Loan.years)/Salary</dmn:text> </dmn:literalExpression> </dmn:decision> <dmndi:DMNDI> <dmndi:DMNDiagram id="_E8F34173-5A17-4B9E-936F-72CFCF1210C4" name="DRG"> <di:extension> <kie:ComponentsWidthsExtension> <kie:ComponentWidths dmnElementRef="_E7994A2B-1189-4BE5-9382-891D48E87D47"> <kie:width>50</kie:width> <kie:width>100</kie:width> <kie:width>100</kie:width> <kie:width>100</kie:width> <kie:width>100</kie:width> </kie:ComponentWidths> <kie:ComponentWidths dmnElementRef="_60A349DD-1F30-488B-BA65-74160F6496F3"> <kie:width>300</kie:width> </kie:ComponentWidths> </kie:ComponentsWidthsExtension> </di:extension> <dmndi:DMNShape id="dmnshape-drg-_058269A1-A5AC-44CB-9479-16A04DC19D77" dmnElementRef="_058269A1-A5AC-44CB-9479-16A04DC19D77" isCollapsed="false"> <dmndi:DMNStyle> <dmndi:FillColor red="255" green="255" blue="255"/> <dmndi:StrokeColor red="0" green="0" blue="0"/> <dmndi:FontColor red="0" green="0" blue="0"/> </dmndi:DMNStyle> <dc:Bounds x="181" y="119" width="100" height="50"/> <dmndi:DMNLabel/> </dmndi:DMNShape> <dmndi:DMNShape id="dmnshape-drg-_65226EA1-EE4A-41F1-91A2-B2A940A75982" dmnElementRef="_65226EA1-EE4A-41F1-91A2-B2A940A75982" isCollapsed="false"> <dmndi:DMNStyle> <dmndi:FillColor red="255" green="255" blue="255"/> <dmndi:StrokeColor red="0" green="0" blue="0"/> <dmndi:FontColor red="0" green="0" blue="0"/> </dmndi:DMNStyle> <dc:Bounds x="181" y="219" width="100" height="50"/> <dmndi:DMNLabel/> </dmndi:DMNShape> <dmndi:DMNShape id="dmnshape-drg-_516E0AEC-03F3-43F4-B886-C489CA82A1C9" dmnElementRef="_516E0AEC-03F3-43F4-B886-C489CA82A1C9" isCollapsed="false"> <dmndi:DMNStyle> <dmndi:FillColor red="255" green="255" blue="255"/> <dmndi:StrokeColor red="0" green="0" blue="0"/> <dmndi:FontColor red="0" green="0" blue="0"/> </dmndi:DMNStyle> <dc:Bounds x="181" y="319" width="100" height="50"/> <dmndi:DMNLabel/> </dmndi:DMNShape> <dmndi:DMNShape id="dmnshape-drg-_6A3FDF72-7F17-4DC5-AC1D-1DCC972C0146" dmnElementRef="_6A3FDF72-7F17-4DC5-AC1D-1DCC972C0146" isCollapsed="false"> <dmndi:DMNStyle> <dmndi:FillColor red="255" green="255" blue="255"/> <dmndi:StrokeColor red="0" green="0" blue="0"/> <dmndi:FontColor red="0" green="0" blue="0"/> </dmndi:DMNStyle> <dc:Bounds x="361" y="119" width="100" height="50"/> <dmndi:DMNLabel/> </dmndi:DMNShape> <dmndi:DMNShape id="dmnshape-drg-_50635164-1A27-4B84-AE16-639A118CE44C" dmnElementRef="_50635164-1A27-4B84-AE16-639A118CE44C" isCollapsed="false"> <dmndi:DMNStyle> <dmndi:FillColor red="255" green="255" blue="255"/> <dmndi:StrokeColor red="0" green="0" blue="0"/> <dmndi:FontColor red="0" green="0" blue="0"/> </dmndi:DMNStyle> <dc:Bounds x="361" y="270" width="100" height="50"/> <dmndi:DMNLabel/> </dmndi:DMNShape> <dmndi:DMNEdge id="dmnedge-drg-_9BFEFD1E-9830-4630-8171-B6F1D3100308" dmnElementRef="_9BFEFD1E-9830-4630-8171-B6F1D3100308"> <di:waypoint x="281" y="144"/> <di:waypoint x="361" y="144"/> </dmndi:DMNEdge> <dmndi:DMNEdge id="dmnedge-drg-_4736D6D5-5A61-4C0E-ADBA-5AAD99221445" dmnElementRef="_4736D6D5-5A61-4C0E-ADBA-5AAD99221445"> <di:waypoint x="411" y="295"/> <di:waypoint x="411" y="144"/> </dmndi:DMNEdge> <dmndi:DMNEdge id="dmnedge-drg-_7A538A2F-562E-4E49-B5F0-572FFCFEF4CB" dmnElementRef="_7A538A2F-562E-4E49-B5F0-572FFCFEF4CB"> <di:waypoint x="231" y="344"/> <di:waypoint x="411" y="295"/> </dmndi:DMNEdge> <dmndi:DMNEdge id="dmnedge-drg-_E508941C-2DE2-41DD-8406-EA8AD646DB7F" dmnElementRef="_E508941C-2DE2-41DD-8406-EA8AD646DB7F"> <di:waypoint x="231" y="244"/> <di:waypoint x="411" y="295"/> </dmndi:DMNEdge> </dmndi:DMNDiagram> </dmndi:DMNDI> </dmn:definitions>'
```
the response is
```json
[
    {
        "severity": "INFO",
        "message": "Decision Table Analysis of table 'Preapproval' finished with no messages to be reported.",
        "messageType": "DECISION_TABLE_ANALYSIS_EMPTY",
        "sourceId": "_E7994A2B-1189-4BE5-9382-891D48E87D47",
        "level": "INFO"
    }
]
```

For another example, a variation of the same DMN model exhibiting a critical overlap in the decision table:
```bash
curl --location --request POST 'http://localhost:8080/jitdmn/validate' --header 'Accept: application/json' --header 'Content-Type: application/xml' --data-raw '<dmn:definitions xmlns:dmn="http://www.omg.org/spec/DMN/20180521/MODEL/" xmlns="https://kiegroup.org/dmn/_79B69A7F-5A25-4B53-BD6A-3216EDC246ED" xmlns:feel="http://www.omg.org/spec/DMN/20180521/FEEL/" xmlns:kie="http://www.drools.org/kie/dmn/1.2" xmlns:dmndi="http://www.omg.org/spec/DMN/20180521/DMNDI/" xmlns:di="http://www.omg.org/spec/DMN/20180521/DI/" xmlns:dc="http://www.omg.org/spec/DMN/20180521/DC/" id="_E01B9C96-BCD5-4618-AC02-35F77D1065E2" name="loan" typeLanguage="http://www.omg.org/spec/DMN/20180521/FEEL/" namespace="https://kiegroup.org/dmn/_79B69A7F-5A25-4B53-BD6A-3216EDC246ED"> <dmn:extensionElements/> <dmn:itemDefinition id="_94619DCD-2602-4A43-97E9-9809D76F15A5" name="tLoan" isCollection="false"> <dmn:itemComponent id="_3C8953D0-F1AD-40F6-BC5E-813F3185F3F1" name="amount" isCollection="false"> <dmn:typeRef>number</dmn:typeRef> </dmn:itemComponent> <dmn:itemComponent id="_1CF9A4C0-9218-4F96-AB1A-66570002B7C5" name="years" isCollection="false"> <dmn:typeRef>number</dmn:typeRef> </dmn:itemComponent> </dmn:itemDefinition> <dmn:inputData id="_058269A1-A5AC-44CB-9479-16A04DC19D77" name="Credit score"> <dmn:extensionElements/> <dmn:variable id="_BA408840-4504-44EB-BD7F-6261F787E342" name="Credit score" typeRef="number"/> </dmn:inputData> <dmn:inputData id="_65226EA1-EE4A-41F1-91A2-B2A940A75982" name="Salary"> <dmn:extensionElements/> <dmn:variable id="_204FE05E-BCF8-4AF5-AD36-B5A04E0EB929" name="Salary" typeRef="number"/> </dmn:inputData> <dmn:inputData id="_516E0AEC-03F3-43F4-B886-C489CA82A1C9" name="Loan"> <dmn:extensionElements/> <dmn:variable id="_90456001-E9FA-4DDC-B2BA-DF1B62A25AAA" name="Loan" typeRef="tLoan"/> </dmn:inputData> <dmn:decision id="_6A3FDF72-7F17-4DC5-AC1D-1DCC972C0146" name="Preapproval"> <dmn:extensionElements/> <dmn:variable id="_2C06D150-7AB2-48C9-BFC6-F48884FD96EF" name="Preapproval" typeRef="boolean"/> <dmn:informationRequirement id="_9BFEFD1E-9830-4630-8171-B6F1D3100308"> <dmn:requiredInput href="#_058269A1-A5AC-44CB-9479-16A04DC19D77"/> </dmn:informationRequirement> <dmn:informationRequirement id="_4736D6D5-5A61-4C0E-ADBA-5AAD99221445"> <dmn:requiredDecision href="#_50635164-1A27-4B84-AE16-639A118CE44C"/> </dmn:informationRequirement> <dmn:decisionTable id="_E7994A2B-1189-4BE5-9382-891D48E87D47" hitPolicy="UNIQUE" preferredOrientation="Rule-as-Row"> <dmn:input id="_9CCC5EBB-BC59-4397-B478-BB434279EBF8"> <dmn:inputExpression id="_85A80C30-68FA-405F-BE6D-D1B6C484CD81" typeRef="number"> <dmn:text>Credit score</dmn:text> </dmn:inputExpression> </dmn:input> <dmn:input id="_D160037A-3B50-44FE-BC28-700F750D1A3A"> <dmn:inputExpression id="_2774CDB9-0B9D-4B89-9639-5D1FD7D6D61B" typeRef="number"> <dmn:text>DTI</dmn:text> </dmn:inputExpression> </dmn:input> <dmn:output id="_8220E87A-3913-4FC5-9926-4A9FD28F82EA"/> <dmn:annotation name="annotation-1"/> <dmn:rule id="_C09BF27F-E03C-4390-8719-E7411784ABCB"> <dmn:inputEntry id="_C02D6987-5B78-42ED-B48D-E3CE5844C22B"> <dmn:text>&lt;700</dmn:text> </dmn:inputEntry> <dmn:inputEntry id="_922FFAE0-9635-420B-8002-4583065E6710"> <dmn:text>&lt;=.28</dmn:text> </dmn:inputEntry> <dmn:outputEntry id="_75A30858-95A6-46F4-89D5-BFFF8B3739AD"> <dmn:text>true</dmn:text> </dmn:outputEntry> <dmn:annotationEntry> <dmn:text/> </dmn:annotationEntry> </dmn:rule> <dmn:rule id="_5CE22529-735A-43D8-9043-E342F58D1CDD"> <dmn:inputEntry id="_14C78BCF-736B-4032-8AB8-B73967479EEF"> <dmn:text>&gt;=700</dmn:text> </dmn:inputEntry> <dmn:inputEntry id="_6AA1D822-C08E-4B4E-BE8B-4C3587E6F034"> <dmn:text>-</dmn:text> </dmn:inputEntry> <dmn:outputEntry id="_CD1AFB69-DDC6-416E-B780-FE2FB3AAC6C1"> <dmn:text>true</dmn:text> </dmn:outputEntry> <dmn:annotationEntry> <dmn:text/> </dmn:annotationEntry> </dmn:rule> <dmn:rule id="_5EB4DDC3-CE49-419A-B7C8-7A6916D6334F"> <dmn:inputEntry id="_2F5D6384-4AE6-4408-88F4-4D3C86F26649"> <dmn:text>&lt;700</dmn:text> </dmn:inputEntry> <dmn:inputEntry id="_5CA8DA56-07BE-4410-85DD-800508300DB8"> <dmn:text>&gt;=.28</dmn:text> </dmn:inputEntry> <dmn:outputEntry id="_8B85A627-73DD-46E7-99DA-26499B3DD9BD"> <dmn:text>false</dmn:text> </dmn:outputEntry> <dmn:annotationEntry> <dmn:text/> </dmn:annotationEntry> </dmn:rule> </dmn:decisionTable> </dmn:decision> <dmn:decision id="_50635164-1A27-4B84-AE16-639A118CE44C" name="DTI"> <dmn:extensionElements/> <dmn:variable id="_7FF18790-80C8-4124-9BFB-93383CE6A50F" name="DTI" typeRef="number"/> <dmn:informationRequirement id="_7A538A2F-562E-4E49-B5F0-572FFCFEF4CB"> <dmn:requiredInput href="#_516E0AEC-03F3-43F4-B886-C489CA82A1C9"/> </dmn:informationRequirement> <dmn:informationRequirement id="_E508941C-2DE2-41DD-8406-EA8AD646DB7F"> <dmn:requiredInput href="#_65226EA1-EE4A-41F1-91A2-B2A940A75982"/> </dmn:informationRequirement> <dmn:literalExpression id="_60A349DD-1F30-488B-BA65-74160F6496F3"> <dmn:text>(Loan.amount / Loan.years)/Salary</dmn:text> </dmn:literalExpression> </dmn:decision> <dmndi:DMNDI> <dmndi:DMNDiagram id="_E8F34173-5A17-4B9E-936F-72CFCF1210C4" name="DRG"> <di:extension> <kie:ComponentsWidthsExtension> <kie:ComponentWidths dmnElementRef="_E7994A2B-1189-4BE5-9382-891D48E87D47"> <kie:width>50</kie:width> <kie:width>100</kie:width> <kie:width>100</kie:width> <kie:width>100</kie:width> <kie:width>100</kie:width> </kie:ComponentWidths> <kie:ComponentWidths dmnElementRef="_60A349DD-1F30-488B-BA65-74160F6496F3"> <kie:width>300</kie:width> </kie:ComponentWidths> </kie:ComponentsWidthsExtension> </di:extension> <dmndi:DMNShape id="dmnshape-drg-_058269A1-A5AC-44CB-9479-16A04DC19D77" dmnElementRef="_058269A1-A5AC-44CB-9479-16A04DC19D77" isCollapsed="false"> <dmndi:DMNStyle> <dmndi:FillColor red="255" green="255" blue="255"/> <dmndi:StrokeColor red="0" green="0" blue="0"/> <dmndi:FontColor red="0" green="0" blue="0"/> </dmndi:DMNStyle> <dc:Bounds x="181" y="119" width="100" height="50"/> <dmndi:DMNLabel/> </dmndi:DMNShape> <dmndi:DMNShape id="dmnshape-drg-_65226EA1-EE4A-41F1-91A2-B2A940A75982" dmnElementRef="_65226EA1-EE4A-41F1-91A2-B2A940A75982" isCollapsed="false"> <dmndi:DMNStyle> <dmndi:FillColor red="255" green="255" blue="255"/> <dmndi:StrokeColor red="0" green="0" blue="0"/> <dmndi:FontColor red="0" green="0" blue="0"/> </dmndi:DMNStyle> <dc:Bounds x="181" y="219" width="100" height="50"/> <dmndi:DMNLabel/> </dmndi:DMNShape> <dmndi:DMNShape id="dmnshape-drg-_516E0AEC-03F3-43F4-B886-C489CA82A1C9" dmnElementRef="_516E0AEC-03F3-43F4-B886-C489CA82A1C9" isCollapsed="false"> <dmndi:DMNStyle> <dmndi:FillColor red="255" green="255" blue="255"/> <dmndi:StrokeColor red="0" green="0" blue="0"/> <dmndi:FontColor red="0" green="0" blue="0"/> </dmndi:DMNStyle> <dc:Bounds x="181" y="319" width="100" height="50"/> <dmndi:DMNLabel/> </dmndi:DMNShape> <dmndi:DMNShape id="dmnshape-drg-_6A3FDF72-7F17-4DC5-AC1D-1DCC972C0146" dmnElementRef="_6A3FDF72-7F17-4DC5-AC1D-1DCC972C0146" isCollapsed="false"> <dmndi:DMNStyle> <dmndi:FillColor red="255" green="255" blue="255"/> <dmndi:StrokeColor red="0" green="0" blue="0"/> <dmndi:FontColor red="0" green="0" blue="0"/> </dmndi:DMNStyle> <dc:Bounds x="361" y="119" width="100" height="50"/> <dmndi:DMNLabel/> </dmndi:DMNShape> <dmndi:DMNShape id="dmnshape-drg-_50635164-1A27-4B84-AE16-639A118CE44C" dmnElementRef="_50635164-1A27-4B84-AE16-639A118CE44C" isCollapsed="false"> <dmndi:DMNStyle> <dmndi:FillColor red="255" green="255" blue="255"/> <dmndi:StrokeColor red="0" green="0" blue="0"/> <dmndi:FontColor red="0" green="0" blue="0"/> </dmndi:DMNStyle> <dc:Bounds x="361" y="270" width="100" height="50"/> <dmndi:DMNLabel/> </dmndi:DMNShape> <dmndi:DMNEdge id="dmnedge-drg-_9BFEFD1E-9830-4630-8171-B6F1D3100308" dmnElementRef="_9BFEFD1E-9830-4630-8171-B6F1D3100308"> <di:waypoint x="281" y="144"/> <di:waypoint x="361" y="144"/> </dmndi:DMNEdge> <dmndi:DMNEdge id="dmnedge-drg-_4736D6D5-5A61-4C0E-ADBA-5AAD99221445" dmnElementRef="_4736D6D5-5A61-4C0E-ADBA-5AAD99221445"> <di:waypoint x="411" y="295"/> <di:waypoint x="411" y="144"/> </dmndi:DMNEdge> <dmndi:DMNEdge id="dmnedge-drg-_7A538A2F-562E-4E49-B5F0-572FFCFEF4CB" dmnElementRef="_7A538A2F-562E-4E49-B5F0-572FFCFEF4CB"> <di:waypoint x="231" y="344"/> <di:waypoint x="411" y="295"/> </dmndi:DMNEdge> <dmndi:DMNEdge id="dmnedge-drg-_E508941C-2DE2-41DD-8406-EA8AD646DB7F" dmnElementRef="_E508941C-2DE2-41DD-8406-EA8AD646DB7F"> <di:waypoint x="231" y="244"/> <di:waypoint x="411" y="295"/> </dmndi:DMNEdge> </dmndi:DMNDiagram> </dmndi:DMNDI> </dmn:definitions>'
```
the response is
```json
[
    {
        "severity": "ERROR",
        "message": "Overlapping rules have different output value, so the HitPolicy for decision table 'Preapproval' should be PRIORITY",
        "messageType": "DECISION_TABLE_HITPOLICY_RECOMMENDER",
        "sourceId": "_E7994A2B-1189-4BE5-9382-891D48E87D47",
        "level": "ERROR"
    },
    {
        "severity": "ERROR",
        "message": "Overlap detected: Overlap values: [ <700, 0.28 ] for rules: [1, 3]. UNIQUE hit policy decision tables can only have one matching rule.",
        "messageType": "DECISION_TABLE_OVERLAP_HITPOLICY_UNIQUE",
        "sourceId": "_E7994A2B-1189-4BE5-9382-891D48E87D47",
        "level": "ERROR"
    }
]
```

## Native application

The native mode is supported by this application. You can create the native Quarkus application for example with 

```bash
mvn clean package -DskipTests -Pnative
```

end execute with
```bash
jitexecutor-runner/target/jitexecutor-runner-999-SNAPSHOT-runner
```

## Multiple models

Each endpoint does support a multiple DMN models variant of the payload; this is helpful for the use-case where the main model to be evaluated has some DMN-import references to other DMNs.

For DMN Evaluation and eXplainability, the payload require specify `mainURI` and `resources` instead of `model`.
Example:
```json
{
    "mainURI": "/multiple/importing.dmn",
    "resources": [
        {
            "URI": "/multiple/importing.dmn",
            "content": "< ... xml ... >"
        },
        {
            "URI": "/multiple/stdlib.dmn",
            "content": "< ... xml ... >"
        }
    ],
    "model": null,
    "context": {
        "a person": {
            "age": 47,
            "full name": "John Doe"
        }
    }
}
```

For Schema and Validation, the payload require to specify a JSON payload with  `mainURI` and `resources`.
Example:
```json
{
    "mainURI": "/multiple/importing.dmn",
    "resources": [
        {
            "URI": "/multiple/importing.dmn",
            "content": "< ... xml ... >"
        },
        {
            "URI": "/multiple/stdlib.dmn",
            "content": "< ... xml ... >"
        }
    ]
}
```

BPMN
===

For the moment being, BPMN expose only a validation endpoint.
You can make a POST request directly to the endpoint `/jitbpmn/validate` with the following body

```json
{
  "mainURI": "string",
  "resources": [
    {
      "URI": "string",
      "content": "string"
    }
  ]
}
``` 

For example, validate a simple valid BPMN model:

```bash
curl -H "Content-Type: application/json" -X POST http://localhost:8080/jitbpmn/validate -d '{"mainURI":"uri","resources":[{"content":"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<definitions id=\"Definition\"\n             targetNamespace=\"http://www.example.org/EvaluationExample\"\n             typeLanguage=\"http://www.java.com/javaTypes\"\n             expressionLanguage=\"http://www.mvel.org/2.0\"\n             xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n             xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n             xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\"\n             xmlns:tns=\"http://www.example.org/EvaluationExample\">\n\n  <process id=\"Evaluation\" name=\"Evaluation Process\">\n    \n    \n  \n    <!-- nodes -->  \n    <startEvent id=\"_1\" name=\"StartProcess\"/>\n    <scriptTask id=\"_2\" name=\"Log\">\n      <script>System.out.println(\"Just outputting something\");</script>\n    </scriptTask>\n    <endEvent id=\"_3\" name=\"EndProcess\">\n      <terminateEventDefinition/>\n    </endEvent>\n    \n    <!-- connections -->\n    <sequenceFlow sourceRef=\"_1\" targetRef=\"_2\"/>\n    <sequenceFlow sourceRef=\"_2\" targetRef=\"_3\"/>\n    \n    <!-- associations -->\n    <association id=\"_1234\" sourceRef=\"_1\" targetRef=\"_2\"/>\n  </process>\n</definitions>","URI":"uri"}]}'
```

will return an empty body
````json
[]
````

On the other side, validate multiple models, some of which invalid:

```bash
curl -H "Content-Type: application/json" -X POST http://localhost:8080/jitbpmn/validate -d '{"mainURI":"mainUri","resources":[{"content":"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<definitions id=\"Definition\"\n             targetNamespace=\"http://www.example.org/EvaluationExample\"\n             typeLanguage=\"http://www.java.com/javaTypes\"\n             expressionLanguage=\"http://www.mvel.org/2.0\"\n             xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n             xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n             xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\"\n             xmlns:tns=\"http://www.example.org/EvaluationExample\">\n\n  <process id=\"Evaluation\" name=\"Evaluation Process\">\n    \n    \n  \n    <!-- nodes -->  \n    <startEvent id=\"_1\" name=\"StartProcess\"/>\n    <scriptTask id=\"_2\" name=\"Log\">\n      <script>System.out.println(\"Just outputting something\");</script>\n    </scriptTask>\n    <endEvent id=\"_3\" name=\"EndProcess\">\n      <terminateEventDefinition/>\n    </endEvent>\n    \n    <!-- connections -->\n    <sequenceFlow sourceRef=\"_1\" targetRef=\"_2\"/>\n    <sequenceFlow sourceRef=\"_2\" targetRef=\"_3\"/>\n    \n    <!-- associations -->\n    <association id=\"_1234\" sourceRef=\"_1\" targetRef=\"_2\"/>\n  </process>\n</definitions>","URI":"UriValid"},{"content":"<bpmn2:definitions xmlns:bpmn2=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:bpsim=\"http://www.bpsim.org/schemas/1.0\" xmlns:drools=\"http://www.jboss.org/drools\" id=\"_BxAKkfY4EDims_O2rjOXww\" exporter=\"jBPM Process Modeler\" exporterVersion=\"2.0\" targetNamespace=\"http://www.omg.org/bpmn20\">\n  <bpmn2:process id=\"invalid\" drools:packageName=\"com.example\" drools:version=\"1.0\" drools:adHoc=\"false\" name=\"invalid-process-id\" isExecutable=\"true\" processType=\"Public\"/>\n  <bpmndi:BPMNDiagram>\n    <bpmndi:BPMNPlane bpmnElement=\"invalid\"/>\n  </bpmndi:BPMNDiagram>\n  <bpmn2:relationship type=\"BPSimData\">\n    <bpmn2:extensionElements>\n      <bpsim:BPSimData>\n        <bpsim:Scenario id=\"default\" name=\"Simulationscenario\">\n          <bpsim:ScenarioParameters/>\n        </bpsim:Scenario>\n      </bpsim:BPSimData>\n    </bpmn2:extensionElements>\n    <bpmn2:source>_BxAKkfY4EDims_O2rjOXww</bpmn2:source>\n    <bpmn2:target>_BxAKkfY4EDims_O2rjOXww</bpmn2:target>\n  </bpmn2:relationship>\n</bpmn2:definitions>","URI":"UriInvalid"}]}'
```

would return validation errors
````json
[
  "Uri: UriInvalid - Process id: invalid - name : invalid-process-id - error : Process has no start node.",
  "Uri: UriInvalid - Process id: invalid - name : invalid-process-id - error : Process has no end node."
]
````