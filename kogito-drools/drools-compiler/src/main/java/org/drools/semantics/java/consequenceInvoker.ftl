    public static class ${invokerClassName}Invoker implements ReturnValueExpression
    {
        public Object evaluate(Tuple tuple,
                               Declaration[] declarations, 
                               KnowledgeHelper knowledgeHelper,
                               WorkingMemory workingMemory) {
	        <#list declarations as declr>
	        ${declr.objectType.classType.name} ${declr.identifier} = ( ${declr.objectType.classType.name} ) declarations[${declr_index}].getValue( workingMemory.getObject( tuple.get( declarations[${declr_index}] ) ) );
	        </#list>
	
	        <#list globals as identifier>
	        ${globalTypes[identifier].name} ${identifier} = ( ${globalTypes[identifier].name} ) workingMemory.get( "${identifier}" );
	        </#list>
	        
            ${ruleClassName}.${methodName}( knowledgeHelper, 
                                            <#list declarations as declr>${declr.identifier}, declarations[${declr_index}]<#if declr_has_next>, 
                                            </#if></#list><#list globals as identifier>, ${identifier}</#list>);
        }
    }                        
      