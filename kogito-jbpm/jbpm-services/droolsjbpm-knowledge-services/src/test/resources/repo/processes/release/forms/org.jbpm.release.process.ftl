
        <div class="form-content one-col">
            
            <input type="hidden" name="processId" value="${process.id}"/>

                         <div class="form-row clearfix">
                            <label>Release Name</label>
                            <input type="text" name="release_name" id="release_name" value=""/>
                         </div>
                         <div class="form-row clearfix">
                            <label>Release Path</label>
                            <input type="text" name="release_path" value=""/>
                        </div>
                   
               <div class="form-row submit clearfix">
                     
                         <input type="button" class="button main" name="btn_Start" value="Start Process" onClick="startProcess(getFormValues(form));"/>
  
                </div>
      
              
        </div>

       
    