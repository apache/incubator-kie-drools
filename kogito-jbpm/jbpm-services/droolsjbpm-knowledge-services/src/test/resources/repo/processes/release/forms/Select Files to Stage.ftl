
        <div class="form-content">
            
            <input type="hidden" name="taskId" value="${task.id}"/>

            <div class="form-row clearfix">
                <label>Release</label>
                
                  <input type="text" disabled="true" name="release_name" value="${inputs['release_name']}" />
                
            </div>

            <div class="form-row clearfix">
                <label>Files</label>
                <#if task.taskData.status = 'InProgress'>
		  
                    <input type="text" name="files_output" id="files_output" value=""/>
                  
                </#if>
            </div>

                

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

       
    