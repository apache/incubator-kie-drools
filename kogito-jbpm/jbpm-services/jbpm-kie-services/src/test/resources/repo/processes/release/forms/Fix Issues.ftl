
        <div class="form-content">
            
            <input type="hidden" name="taskId" value="${task.id}"/>

            <div class="form-row clearfix">
                <label>Report</label>
                <textarea readonly="true" name="in_test_report">${inputs['in_test_report']}</textarea>
            </div>
            <div class="form-row clearfix">
                <label>Selected Files</label>
                <input type="text" disabled="true" name="in_files" id="in_files" value="${inputs['in_files']}"/>
            </div>
            <div class="form-row clearfix">
                <label>Fixed Files</label>
                <#if task.taskData.status = 'InProgress'>
                <input type="text" name="out_fixed_file_list" id="out_fixed_file_list" value="${inputs['in_files']}"/>
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

       
    