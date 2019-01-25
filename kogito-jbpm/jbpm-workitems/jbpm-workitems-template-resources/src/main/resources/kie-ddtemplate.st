<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<deployment-descriptor xsi:schemaLocation="http://www.jboss.org/jbpm deployment-descriptor.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <persistence-unit>org.jbpm.domain</persistence-unit>
    <audit-persistence-unit>org.jbpm.domain</audit-persistence-unit>
    <audit-mode>JPA</audit-mode>
    <persistence-mode>JPA</persistence-mode>
    <runtime-strategy>SINGLETON</runtime-strategy>
    <marshalling-strategies/>
    <event-listeners/>
    <task-event-listeners/>
    <globals/>
    <work-item-handlers>
    $widInfo:{k|
      <work-item-handler>
        <resolver>mvel</resolver>
        <identifier>$widInfo.(k).defaultHandlerNoTypeUnEscaped$</identifier>
        <parameters/>
        <name>$widInfo.(k).name$</name>
      </work-item-handler>
    }; separator=""$
    </work-item-handlers>
    <environment-entries/>
    <configurations/>
    <required-roles/>
    <remoteable-classes/>
    <limit-serialization-classes>true</limit-serialization-classes>
</deployment-descriptor>