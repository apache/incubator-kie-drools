package ${package};

public class ${invokerClassName} implements org.drools.spi.Consequence
{
    public void invoke(org.drools.spi.Activation activation,
                       org.drools.WorkingMemory workingMemory) {
                           
        org.drools.spi.Tuple tuple = activation.getTuple();
        org.drools.rule.Rule rule = activation.getRule();
        org.drools.spi.KnowledgeHelper knowledgeHelper = new org.drools.base.DefaultKnowledgeHelper( activation.getRule(), tuple, workingMemory );
        org.drools.rule.Declaration[] declarations = rule.getDeclarations();
                                                      
        <#list declarations as declr>
        ${declr.objectType.classType.name?replace("$", ".")} ${declr.identifier} = ( ${declr.objectType.classType.name?replace("$", ".")} ) declarations[${declr_index}].getValue( workingMemory.getObject( tuple.get( declarations[${declr_index}] ) ) );
        </#list>

       <#list globals as identifier>
       ${globalTypes[identifier].name?replace("$", ".")} ${identifier} = ( ${globalTypes[identifier].name?replace("$", ".")} ) workingMemory.getGlobal( "${identifier}" );
       </#list>
        
        ${ruleClassName}.${methodName}( knowledgeHelper, 
                                        <#list declarations as declr>${declr.identifier}, declarations[${declr_index}]<#if declr_has_next>, 
                                        </#if></#list><#list globals as identifier>, ${identifier}</#list> );
    }
}                        
      