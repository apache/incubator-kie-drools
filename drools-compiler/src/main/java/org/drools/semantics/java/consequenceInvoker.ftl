    public static class ${className}Invoker implements ReturnValueExpression
    {
        public Object evaluate(Tuple tuple,
                               Declaration[] declarations, 
                               KnowledgeHelper knowledgeHelper,
                               WorkingMemory workingMemory) {
	        <#list declarations as item>
	        ${item.objectType.classType.name} ${item.identifier} = ( ${item.objectType.classType.name} ) declarations[${item_index}].getValue( workingMemory.getObject( tuple.get( declarations[${item_index}] ) ) );
	        </#list>
	
	        <#list usedApplicationData as key>
	        ${applicationData[key].name} ${key} = ( ${applicationData[key].name} ) workingMemory.get( "${key}" );
	        </#list>
	        
	        $ruleName.$methodName( knowledgeHelper, <#list declarations as item>${item.identifier}<#if item_has_next>, </#if></#list><#list usedApplicationData as key>, ${key}</#list> );
        }
    }                        
      