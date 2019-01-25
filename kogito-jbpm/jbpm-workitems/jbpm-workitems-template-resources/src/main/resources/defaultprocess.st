<?xml version="1.0" encoding="UTF-8"?>
<bpmn2:definitions xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://www.omg.org/bpmn20" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:bpsim="http://www.bpsim.org/schemas/1.0" xmlns:color="http://www.omg.org/spec/BPMN/non-normative/color" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:drools="http://www.jboss.org/drools" id="_vj8fkfi_EeiF_4V9E7-6nA" xsi:schemaLocation="http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd http://www.jboss.org/drools drools.xsd http://www.bpsim.org/schemas/1.0 bpsim.xsd" exporter="jBPM Designer" exporterVersion="1.0" expressionLanguage="http://www.mvel.org/2.0" targetNamespace="http://www.omg.org/bpmn20" typeLanguage="http://www.java.com/javaTypes">
    $if(first(widInfo.values).parameters)$
        $first(widInfo.values).parameters.values:{k1|
            <bpmn2:itemDefinition id="_in$k1.name$Item" structureRef="$k1.runtimeType$"/>
        }$
    $endif$
    $if(first(widInfo.values).results)$
        $first(widInfo.values).results.values:{k1|
            <bpmn2:itemDefinition id="_out$k1.name$Item" structureRef="$k1.runtimeType$"/>
        }$
    $endif$
    <bpmn2:itemDefinition id="__3901989D-0D41-44E9-99B8-AA92BA7615B1_TaskNameInputXItem" structureRef="String"/>
    $if(first(widInfo.values).parameters)$
        $first(widInfo.values).parameters.values:{k1|
            <bpmn2:itemDefinition id="__3901989D-0D41-44E9-99B8-AA92BA7615B1_$k1.name$InputXItem" structureRef="$k1.runtimeType$"/>
        }$
    $endif$
    $if(first(widInfo.values).results)$
        $first(widInfo.values).results.values:{k1|
            <bpmn2:itemDefinition id="__3901989D-0D41-44E9-99B8-AA92BA7615B1_$k1.name$OutputXItem" structureRef="$k1.runtimeType$"/>
        }$
    $endif$
  <bpmn2:process id="$first(widInfo.values).name$" drools:packageName="org.jbpm" drools:version="1.0" name="$first(widInfo.values).name$" isExecutable="true">
      $if(first(widInfo.values).parameters)$
          $first(widInfo.values).parameters.values:{k1|
              <bpmn2:property id="in$k1.name$" itemSubjectRef="_in$k1.name$Item"/>
          }$
      $endif$
      $if(first(widInfo.values).results)$
          $first(widInfo.values).results.values:{k1|
              <bpmn2:property id="out$k1.name$" itemSubjectRef="_out$k1.name$Item"/>
          }$
      $endif$

    <bpmn2:startEvent id="processStartEvent" drools:selectable="true" color:background-color="#9acd32" color:border-color="#000000" color:color="#000000" name="Start">
      <bpmn2:extensionElements>
        <drools:metaData name="elementname">
          <drools:metaValue><![CDATA[Start]]></drools:metaValue>
        </drools:metaData>
      </bpmn2:extensionElements>
      <bpmn2:outgoing>_4CABEDDD-1349-4141-B4D5-E3A7EDA43071</bpmn2:outgoing>
    </bpmn2:startEvent>
    <bpmn2:task id="_3901989D-0D41-44E9-99B8-AA92BA7615B1" drools:selectable="true" drools:taskName="$first(widInfo.values).name$" color:background-color="#fafad2" color:border-color="#000000" color:color="#000000" name="$first(widInfo.values).displayName$">
      <bpmn2:extensionElements>
        <drools:metaData name="elementname">
          <drools:metaValue><![CDATA[$first(widInfo.values).displayName$]]></drools:metaValue>
        </drools:metaData>
      </bpmn2:extensionElements>
      <bpmn2:incoming>_4CABEDDD-1349-4141-B4D5-E3A7EDA43071</bpmn2:incoming>
      <bpmn2:outgoing>_F51868D7-5804-4146-929F-0FCEF6208B8E</bpmn2:outgoing>
      <bpmn2:ioSpecification id="_vj9tsPi_EeiF_4V9E7-6nA">
        <bpmn2:dataInput id="_3901989D-0D41-44E9-99B8-AA92BA7615B1_TaskNameInputX" drools:dtype="String" itemSubjectRef="__3901989D-0D41-44E9-99B8-AA92BA7615B1_TaskNameInputXItem" name="TaskName"/>
        $if(first(widInfo.values).parameters)$
            $first(widInfo.values).parameters.values:{k1|
                <bpmn2:dataInput id="_3901989D-0D41-44E9-99B8-AA92BA7615B1_$k1.name$InputX" drools:dtype="$k1.runtimeType$" itemSubjectRef="__3901989D-0D41-44E9-99B8-AA92BA7615B1_$k1.name$InputXItem" name="$k1.name$"/>
            }$
        $endif$
       $if(first(widInfo.values).results)$
           $first(widInfo.values).results.values:{k1|
               <bpmn2:dataOutput id="_3901989D-0D41-44E9-99B8-AA92BA7615B1_$k1.name$OutputX" drools:dtype="$k1.runtimeType$" itemSubjectRef="__3901989D-0D41-44E9-99B8-AA92BA7615B1_$k1.name$OutputXItem" name="$k1.name$"/>
           }$
       $endif$

        <bpmn2:inputSet id="_vj9tsfi_EeiF_4V9E7-6nA">
          <bpmn2:dataInputRefs>_3901989D-0D41-44E9-99B8-AA92BA7615B1_TaskNameInputX</bpmn2:dataInputRefs>
          $if(first(widInfo.values).parameters)$
              $first(widInfo.values).parameters.values:{k1|
                  <bpmn2:dataInputRefs>_3901989D-0D41-44E9-99B8-AA92BA7615B1_$k1.name$InputX</bpmn2:dataInputRefs>
              }$
          $endif$
        </bpmn2:inputSet>
        <bpmn2:outputSet id="_vj9tsvi_EeiF_4V9E7-6nA">
            $if(first(widInfo.values).results)$
               $first(widInfo.values).results.values:{k1|
                   <bpmn2:dataOutputRefs>_3901989D-0D41-44E9-99B8-AA92BA7615B1_$k1.name$OutputX</bpmn2:dataOutputRefs>
               }$
           $endif$
        </bpmn2:outputSet>
      </bpmn2:ioSpecification>
      <bpmn2:dataInputAssociation id="_vj-UwPi_EeiF_4V9E7-6nA">
        <bpmn2:targetRef>_3901989D-0D41-44E9-99B8-AA92BA7615B1_TaskNameInputX</bpmn2:targetRef>
        <bpmn2:assignment id="_vj-Uwfi_EeiF_4V9E7-6nA">
          <bpmn2:from xsi:type="bpmn2:tFormalExpression" id="_vj-Uwvi_EeiF_4V9E7-6nA"><![CDATA[$first(widInfo.values).name$]]></bpmn2:from>
          <bpmn2:to xsi:type="bpmn2:tFormalExpression" id="_vj-Uw_i_EeiF_4V9E7-6nA">_3901989D-0D41-44E9-99B8-AA92BA7615B1_TaskNameInputX</bpmn2:to>
        </bpmn2:assignment>
      </bpmn2:dataInputAssociation>

        $if(first(widInfo.values).parameters)$
            $first(widInfo.values).parameters.values:{k1|
                <bpmn2:dataInputAssociation id="_vj-UxPi_EeiF_4V9E7-6nA$k1.name$">
                    <bpmn2:sourceRef>in$k1.name$</bpmn2:sourceRef>
                    <bpmn2:targetRef>_3901989D-0D41-44E9-99B8-AA92BA7615B1_$k1.name$InputX</bpmn2:targetRef>
                </bpmn2:dataInputAssociation>
            }$
        $endif$

        $if(first(widInfo.values).results)$
           $first(widInfo.values).results.values:{k1|
               <bpmn2:dataOutputAssociation id="_vj-Uy_i_EeiF_4V9E7-6nA$k1.name$">
                   <bpmn2:sourceRef>_3901989D-0D41-44E9-99B8-AA92BA7615B1_$k1.name$OutputX</bpmn2:sourceRef>
                   <bpmn2:targetRef>out$k1.name$</bpmn2:targetRef>
                </bpmn2:dataOutputAssociation>
           }$
       $endif$
    </bpmn2:task>
    <bpmn2:sequenceFlow id="_4CABEDDD-1349-4141-B4D5-E3A7EDA43071" drools:selectable="true" color:background-color="#000000" color:border-color="#000000" color:color="#000000" sourceRef="processStartEvent" targetRef="_3901989D-0D41-44E9-99B8-AA92BA7615B1"/>
    <bpmn2:endEvent id="_EFDCB45C-84C5-4F49-93DA-0013521F1E20" drools:selectable="true" color:background-color="#ff6347" color:border-color="#000000" color:color="#000000" name="Finish">
      <bpmn2:extensionElements>
        <drools:metaData name="elementname">
          <drools:metaValue><![CDATA[Finish]]></drools:metaValue>
        </drools:metaData>
      </bpmn2:extensionElements>
      <bpmn2:incoming>_F51868D7-5804-4146-929F-0FCEF6208B8E</bpmn2:incoming>
    </bpmn2:endEvent>
    <bpmn2:sequenceFlow id="_F51868D7-5804-4146-929F-0FCEF6208B8E" drools:selectable="true" color:background-color="#000000" color:border-color="#000000" color:color="#000000" sourceRef="_3901989D-0D41-44E9-99B8-AA92BA7615B1" targetRef="_EFDCB45C-84C5-4F49-93DA-0013521F1E20"/>
  </bpmn2:process>
  <bpmndi:BPMNDiagram id="_vj-UzPi_EeiF_4V9E7-6nA">
    <bpmndi:BPMNPlane id="_vj-Uzfi_EeiF_4V9E7-6nA" bpmnElement="$first(widInfo.values).name$">
      <bpmndi:BPMNShape id="_vj-Uzvi_EeiF_4V9E7-6nA" bpmnElement="processStartEvent">
        <dc:Bounds height="30.0" width="30.0" x="225.0" y="248.0"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="_vj-Uz_i_EeiF_4V9E7-6nA" bpmnElement="_3901989D-0D41-44E9-99B8-AA92BA7615B1">
        <dc:Bounds height="80.0" width="100.0" x="315.0" y="223.0"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="_vj-U0Pi_EeiF_4V9E7-6nA" bpmnElement="_EFDCB45C-84C5-4F49-93DA-0013521F1E20">
        <dc:Bounds height="28.0" width="28.0" x="480.0" y="249.0"/>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNEdge id="_vj-U0fi_EeiF_4V9E7-6nA" bpmnElement="_4CABEDDD-1349-4141-B4D5-E3A7EDA43071" sourceElement="_vj-Uzvi_EeiF_4V9E7-6nA" targetElement="_vj-Uz_i_EeiF_4V9E7-6nA">
        <di:waypoint xsi:type="dc:Point" x="240.0" y="263.0"/>
        <di:waypoint xsi:type="dc:Point" x="365.0" y="263.0"/>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="_vj-U0vi_EeiF_4V9E7-6nA" bpmnElement="_F51868D7-5804-4146-929F-0FCEF6208B8E" sourceElement="_vj-Uz_i_EeiF_4V9E7-6nA" targetElement="_vj-U0Pi_EeiF_4V9E7-6nA">
        <di:waypoint xsi:type="dc:Point" x="365.0" y="263.0"/>
        <di:waypoint xsi:type="dc:Point" x="494.0" y="263.0"/>
      </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
  <bpmn2:relationship id="_vj-70Pi_EeiF_4V9E7-6nA" type="BPSimData">
    <bpmn2:extensionElements>
      <bpsim:BPSimData>
        <bpsim:Scenario xsi:type="bpsim:Scenario" id="default" name="Simulationscenario">
          <bpsim:ScenarioParameters xsi:type="bpsim:ScenarioParameters" baseTimeUnit="min"/>
          <bpsim:ElementParameters xsi:type="bpsim:ElementParameters" elementRef="_EFDCB45C-84C5-4F49-93DA-0013521F1E20" id="_vj-70fi_EeiF_4V9E7-6nA">
            <bpsim:TimeParameters xsi:type="bpsim:TimeParameters">
              <bpsim:ProcessingTime xsi:type="bpsim:Parameter">
                <bpsim:UniformDistribution max="10.0" min="5.0"/>
              </bpsim:ProcessingTime>
            </bpsim:TimeParameters>
          </bpsim:ElementParameters>
          <bpsim:ElementParameters xsi:type="bpsim:ElementParameters" elementRef="_3901989D-0D41-44E9-99B8-AA92BA7615B1" id="_vj-70vi_EeiF_4V9E7-6nA">
            <bpsim:TimeParameters xsi:type="bpsim:TimeParameters">
              <bpsim:ProcessingTime xsi:type="bpsim:Parameter">
                <bpsim:UniformDistribution max="10.0" min="5.0"/>
              </bpsim:ProcessingTime>
            </bpsim:TimeParameters>
            <bpsim:CostParameters xsi:type="bpsim:CostParameters">
              <bpsim:UnitCost xsi:type="bpsim:Parameter">
                <bpsim:FloatingParameter value="0.0"/>
              </bpsim:UnitCost>
            </bpsim:CostParameters>
          </bpsim:ElementParameters>
          <bpsim:ElementParameters xsi:type="bpsim:ElementParameters" elementRef="_F51868D7-5804-4146-929F-0FCEF6208B8E" id="_vj-70_i_EeiF_4V9E7-6nA">
            <bpsim:ControlParameters xsi:type="bpsim:ControlParameters">
              <bpsim:Probability xsi:type="bpsim:Parameter">
                <bpsim:FloatingParameter value="100.0"/>
              </bpsim:Probability>
            </bpsim:ControlParameters>
          </bpsim:ElementParameters>
          <bpsim:ElementParameters xsi:type="bpsim:ElementParameters" elementRef="_4CABEDDD-1349-4141-B4D5-E3A7EDA43071" id="_vj-71Pi_EeiF_4V9E7-6nA">
            <bpsim:ControlParameters xsi:type="bpsim:ControlParameters">
              <bpsim:Probability xsi:type="bpsim:Parameter">
                <bpsim:FloatingParameter value="100.0"/>
              </bpsim:Probability>
            </bpsim:ControlParameters>
          </bpsim:ElementParameters>
          <bpsim:ElementParameters xsi:type="bpsim:ElementParameters" elementRef="processStartEvent" id="_vj-71fi_EeiF_4V9E7-6nA">
            <bpsim:TimeParameters xsi:type="bpsim:TimeParameters">
              <bpsim:ProcessingTime xsi:type="bpsim:Parameter">
                <bpsim:UniformDistribution max="10.0" min="5.0"/>
              </bpsim:ProcessingTime>
            </bpsim:TimeParameters>
            <bpsim:ControlParameters xsi:type="bpsim:ControlParameters">
              <bpsim:Probability xsi:type="bpsim:Parameter">
                <bpsim:FloatingParameter value="100.0"/>
              </bpsim:Probability>
            </bpsim:ControlParameters>
          </bpsim:ElementParameters>
        </bpsim:Scenario>
      </bpsim:BPSimData>
    </bpmn2:extensionElements>
    <bpmn2:source>_vj8fkfi_EeiF_4V9E7-6nA</bpmn2:source>
    <bpmn2:target>_vj8fkfi_EeiF_4V9E7-6nA</bpmn2:target>
  </bpmn2:relationship>
</bpmn2:definitions>