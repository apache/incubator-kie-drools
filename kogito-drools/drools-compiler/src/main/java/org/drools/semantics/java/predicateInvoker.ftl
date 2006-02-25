package ${package};

public class ${invokerClassName} implements org.drools.spi.PredicateExpression
{
    public boolean evaluate(org.drools.spi.Tuple tuple,
                            org.drools.FactHandle factHandle,
                            org.drools.rule.Declaration declaration, 
                            org.drools.rule.Declaration[] declarations, 
                            org.drools.WorkingMemory workingMemory) {
        ${declaration.objectType.classType.name?replace("$", ".")} ${declaration.identifier} = ( ${declaration.objectType.classType.name?replace("$", ".")} ) workingMemory.getObject( factHandle );                              
        
        <#list declarations as declr>
        ${declr.objectType.classType.name?replace("$", ".")} ${declr.identifier} = ( ${declr.objectType.classType.name?replace("$", ".")} ) declarations[${declr_index}].getValue( workingMemory.getObject( tuple.get( declarations[${declr_index}] ) ) );
        </#list>

        <#list globals as identifier>
        ${globalTypes[identifier].name?replace("$", ".")} ${identifier} = ( ${globalTypes[identifier].name?replace("$", ".")} ) workingMemory.getGlobal( "${identifier}" );
	    </#list>
        
        return ${ruleClassName}.${methodName}(${declaration.identifier}, <#list declarations as item>${item.identifier}<#if item_has_next>, </#if></#list><#list globals as identifier>, ${identifier}</#list> );
    }        
}                        
      