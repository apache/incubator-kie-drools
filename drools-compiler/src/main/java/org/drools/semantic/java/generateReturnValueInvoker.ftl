    public static class ${className}Invoker implements ReturnValueExpression
    {
        public Object evaluate(Tuple tuple,
                               Declaration[] declarations, 
                               WorkingMemory workingMemory) {
	        <#list declarations as item>
	        ${item.objectType.classType.name} ${item.identifier} = ( ${item.objectType.classType.name} ) declarations[${item_index}].getValue( workingMemory.getObject( tuple.get( declarations[${item_index}] ) ) );
	        </#list>
	
	        <#list usedApplicationData as key>
	        ${applicationData[key].name} ${key} = ( ${applicationData[key].name} ) workingMemory.get( "${key}" );
	        </#list>
	        
	        return $ruleName.$methodName( <#list declarations as item>${item.identifier}<#if item_has_next>, </#if></#list><#list usedApplicationData as key>, ${key}</#list> );
        }
    }                        
      