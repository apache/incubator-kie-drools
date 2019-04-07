
        <div class="form-content one-col">
            
            <input type="hidden" name="processId" value="${process.id}"/>

                
                <#if outputs?size != 0>
                    <h2>Outputs</h2><br/>
                </#if>
                
                <#list outputs?keys as key>
                    <#assign value = outputs[key]>
                   
                         <div class="form-row clearfix">
                            <label>${key}</label>
                            
                                <input type="text" name="${key}" value=""/>
                            
                        </div>
                   
                </#list>
                 <div class="form-row submit clearfix">
                     
                         <input type="button" class="button main" name="btn_Start" value="Start Process" onClick="startProcess(getFormValues(form));"/>
  
                </div>
      
              
        </div>

       
    