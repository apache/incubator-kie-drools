    public boolean ${methodName}(${declaration.objectType.classType.name} ${declaration.identifier}<#list declarations as item>, ${item.objectType.classType.name} ${item.identifier}</#list><#list usedApplicationData as key>, ${applicationData[key].name} ${key}</#list> ) {        
        return (${text});
    }                        
      