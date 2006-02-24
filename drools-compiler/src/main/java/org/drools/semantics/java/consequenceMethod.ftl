public static void ${methodName}(org.drools.spi.KnowledgeHelper drools,
                        <#list declarations as declr>${declr.objectType.classType.name?replace("$", ".")} ${declr.identifier}, org.drools.rule.Declaration ${declr.identifier}__Declaration__<#if declr_has_next>, </#if>
                        </#list><#list globals as identifier>, ${globalTypes[identifier].name?replace("$", ".")} ${identifier}</#list>) {                             
        ${text}	
    }                        
  