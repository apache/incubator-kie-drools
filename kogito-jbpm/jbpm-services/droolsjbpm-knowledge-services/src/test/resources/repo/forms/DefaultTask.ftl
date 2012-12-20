
        <div class="form-content">
            
            <input type="hidden" name="taskId" value="${task.id}"/>

                <#if inputs?size != 0>
                    <h2>Inputs</h2><br/>
                </#if>    
                <#list inputs?keys as key>
                    <#assign value = inputs[key]>
                    <div class="form-row clearfix">
                        <label>${key}</label>
                        <div class="input-button">
                            <input type="text" name="${key}" value="${value}"/>
                        </div>
                    </div>

                </#list>
                <#if outputs?size != 0>
                    <h2>Outputs</h2><br/>
                </#if>
                
                <#list outputs?keys as key>
                    <#assign value = outputs[key]>
                    <#if task.taskData.status = 'Reserved'>
                        
                        <div class="form-row clearfix">
                            <label>${key}</label>
                            <div class="input-button">
                            </div>
                        </div>

                    </#if>
                    <#if task.taskData.status = 'InProgress'>
                         <div class="form-row clearfix">
                            <label>${key}</label>
                            <div class="input-button">
                                <input type="text" name="${key}" value="${value}"/>
                            </div>
                        </div>
                    </#if>
                </#list>
                 <div class="form-row submit clearfix">
                     <#if task.taskData.status = 'Reserved'>
                         <input type="button" class="button main" name="btn_Start" value="Start" onClick="startTask(getFormValues(form));"/>
                     </#if>
                     <#if task.taskData.status = 'InProgress'>
                          <input type="button" class="button main" name="btn_Save" value="Save" onClick="saveTaskState(getFormValues(form));"/>
                          <input type="button" class="button main" name="btn_Complete" value="Complete" onClick="completeTask(getFormValues(form));"/>
                          
                    </#if>  
                </div>
      
              
        </div>

       
    