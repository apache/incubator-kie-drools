    public static class ${invokerClassName} implements ReturnValueExpression
    {
        public Object evaluate(Tuple tuple,
                               Declaration[] declarations, 
                               WorkingMemory workingMemory) {                               
	        <#list declarations as declr>
	        ${declr.objectType.classType.name} ${declr.identifier} = ( ${declr.objectType.classType.name} ) declarations[${declr_index}].getValue( workingMemory.getObject( tuple.get( declarations[${declr_index}] ) ) );
	        </#list>
	
	        <#list globals as identifier>
	        ${globalTypes[identifier].name} ${identifier} = ( ${globalTypes[identifier].name} ) workingMemory.get( "${identifier}" );
	        </#list>
	        
	        return ${ruleClassName}.${methodName}( <#list declarations as decl>${decl.identifier}<#if decl_has_next>, </#if></#list><#list globals as identifier>, ${identifier}</#list> );
        }
    }                        
      