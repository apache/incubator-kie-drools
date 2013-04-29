
        <div class="form-content">
            
            <input type="hidden" name="taskId" value="${task.id}"/>

            <div class="form-row clearfix">
                <label>Report</label>
		<textarea readonly="true" name="in_test_report">${inputs['in_test_report']}</textarea>

            </div>
            <div class="form-row clearfix">
                <label>Due Date</label>

	                <input type="text" disabled="true" name="in_dueDate" id="in_dueDate" value="${inputs['in_dueDate']}"/>

            </div>
            <div class="form-row clearfix">
                <label>Notify</label>
                <#if task.taskData.status = 'InProgress'>
                	<input type="text" name="out_users_list" id="out_users_list" value=""/>
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

       
    