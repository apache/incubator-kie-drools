public static boolean ${methodName}(${declaration.objectType.classType.name?replace("$", ".")} ${declaration.identifier}<#list declarations as declr>, ${declr.objectType.classType.name?replace("$", ".")} ${declr.identifier}</#list><#list globals as identifier>, ${globalTypes[identifier].name} ${identifier}</#list> ) {        
        return (${text});
    }              