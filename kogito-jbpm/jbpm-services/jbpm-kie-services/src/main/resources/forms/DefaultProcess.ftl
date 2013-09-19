        <div class="form-content one-col">
            <input type="hidden" name="processId" value="${process.id}"/>
                <#if outputs?has_content>
                    <h2>Outputs</h2><br/>
                    <#list outputs?keys as key>
                             <div class="form-row clearfix">
                                <label>${key}</label>
                                    <input type="text" name="${key}" value=""/>
                            </div>
                   
                    </#list>
                </#if>
                <i>* Automatically generated form, only supports simple text strings *</i>
        </div>
