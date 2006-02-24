package ${package};
public static class ${invokerClassName} implements org.drools.spi.ReturnValueExpression
{
    public boolean evaluate(org.drools.spi.Tuple tuple,
                            org.drools.rule.Declaration[] declarations, 
                            org.drools.WorkingMemory workingMemory) {                               
        <#list declarations as declr>
        ${declr.objectType.classType.name?replace("$", ".")} ${declr.identifier} = ( ${declr.objectType.classType.name?replace("$", ".")} ) declarations[${declr_index}].getValue( workingMemory.getObject( tuple.get( declarations[${declr_index}] ) ) );
        </#list>

        <#list globals as identifier>
        ${globalTypes[identifier].name?replace("$", ".")} ${identifier} = ( ${globalTypes[identifier].name?replace("$", ".")} ) workingMemory.get( "${identifier}" );
        </#list>
        
        return ${ruleClassName}.${methodName}( <#list declarations as decl>${decl.identifier}<#if decl_has_next>, </#if></#list><#list globals as identifier>, ${identifier}</#list> );
    }
}                        
      