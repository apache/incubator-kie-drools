    public void {methodName}(KnowledgeHelper knowledgeHelper,
                              <#list declarations as item>
                              ${item.objectType.classType.name} ${item.identifier}<#if item_has_next>, </#if>
                              </#list>
                              <#list usedApplicationData as key>
                              <#include "generateClass.ftl">  
                              , ${applicationData[key].name} ${key} = ( ${applicationData[key].name} ) workingMemory.get( "${key}" );
                              </#list> ) {
        {consequence}

	
    }                        
      