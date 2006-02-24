public static Object ${methodName}(<#list declarations as decl>${decl.objectType.classType.name?replace("$", ".")} ${decl.identifier}<#if decl_has_next>, </#if></#list><#list globals as identifier>, ${globalTypes[identifier].name?replace("$", ".")} ${identifier}</#list> ) {        
        return (${text});            
    }                        
        