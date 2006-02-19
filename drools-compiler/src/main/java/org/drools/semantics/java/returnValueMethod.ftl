   public Object ${methodName}(<#list declarations as decl>${decl.objectType.classType.name} ${decl.identifier}<#if decl_has_next>, </#if></#list><#list globals as identifier>, ${globalTypes[identifier].name} ${identifier}</#list> ) {        
        return (${text});            
    }                        
        