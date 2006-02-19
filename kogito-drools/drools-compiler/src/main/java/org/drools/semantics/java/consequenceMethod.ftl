    public void {methodName}(KnowledgeHelper knowledgeHelper,
                             <#list declarations as declr>${declr.objectType.classType.name} ${declr.identifier}, org.drools.rule.Declaration declarations[${declr_index}]<#if declr_has_next>, </#if>
                             </#list><#list globals as identifier>, ${identifier}</#list>) {                             
        ${text}	
    }                        
      