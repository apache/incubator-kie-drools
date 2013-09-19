        <div class="form-content">
            <input type="hidden" name="taskId" value="${task.id}"/>
                <#if inputs?has_content>
                    <h2>Inputs</h2><br/>
                    <#list inputs?keys as key>
                        <div class="form-row clearfix">
                            <label>${key}</label>
                            <div class="input-button">
                                <input type="text" name="${key}" value="${inputs[key]!""}"/>
                            </div>
                        </div>

                    </#list>
                </#if>    
                
                <#if outputs?has_content>
                    <h2>Outputs</h2><br/>
                    <#list outputs?keys as key>
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
                                    <input type="text" name="${key}" value="${outputs[key]!""}"/>
                                </div>
                            </div>
                        </#if>
                    </#list>   
                </#if>
                <i>* Automatically generated form, only supports simple text strings *</i>
        </div>
