    public boolean ${methodName}(${declaration.objectType.classType.name} ${declaration.identifier}<#list declarations as declr>, ${declr.objectType.classType.name} ${declr.identifier}</#list><#list globals as identifier>, ${globalTypes[identifier].name} ${identifier}</#list> ) {        
        return (${text});
    }              