public static class ${invokerClassName}Invoker implements PredicateExpression
{
    public boolean evaluate(Tuple tuple,
                            FactHandle factHandle,
                            Declaration declaration, 
                            Declaration[] declarations, 
                            WorkingMemory workingMemory) {
        ${declaration.objectType.classType.name} ${declaration.identifier} = ( ${declaration.objectType.classType.name} ) workingMemory.getObject( factHandle );                              
        
        <#list declarations as declr>
        ${declr.objectType.classType.name} ${declr.identifier} = ( ${declr.objectType.classType.name} ) declarations[${declr_index}].getValue( workingMemory.getObject( tuple.get( declarations[${declr_index}] ) ) );
        </#list>

        <#list globals as identifier>
        ${globalTypes[identifier].name} ${identifier} = ( ${globalTypes[identifier].name} ) workingMemory.get( "${identifier}" );
	    </#list>
        
        return ${ruleClassName}.${methodName}(${declaration.identifier}, <#list declarations as item>${item.identifier}<#if item_has_next>, </#if></#list><#list globals as identifier>, ${identifier}</#list> );
    }        
}                        
      