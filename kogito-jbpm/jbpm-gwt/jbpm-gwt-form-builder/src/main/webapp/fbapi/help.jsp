<html>
  <head>
    <title>FormBuilder REST API help page</title>
    <style type="text/css">
        a {
            text-decoration: none;
            color: #000000;
        }
        a:HOVER {
            text-decoration: none;
            color: #555555;
        }
        a:ACTIVE {
            text-decoration: none;
            color: #888888;
        }
        a:VISITED {
            text-decoration: none;
            color: #000000;
        }
        table {
            background-color: #DDDDDD;
        }
    </style>
    <script type="text/javascript">
    <!--

    var xmlhttp;
    if (typeof XMLHttpRequest!='undefined') {
        try {
          xmlhttp = new XMLHttpRequest();
        } catch (e) {
          xmlhttp = false;
        }
    }

    function loadDoc(codeElement, docname) {
        if (xmlhttp) {
            xmlhttp.open("GET", docname,true);
            xmlhttp.onreadystatechange=function() {
                if (xmlhttp.readyState==4) {
                    ds=xmlhttp.responseText;
                    ds = ds.replace(/</g, "&lt;");
                    ds = ds.replace(/>/g, "&gt;");
                    ds = ds.replace(/\n/g, "<br/>");
                    ds = ds.replace(/ /g, "&nbsp;");
                    codeElement.innerHTML = ds;
                }
            }
            xmlhttp.send(null)
        }
    }
    
    function show(idDiv) {
        var myDiv = document.getElementById(idDiv);
        if (myDiv.style.visibility == "visible") {
            myDiv.style.visibility = "collapse";
        } else {
            myDiv.style.visibility = "visible";
            if (eval('document.getElementById("code_" + idDiv)') != 'undefined') {
                var codeElement = document.getElementById("code_" + idDiv);
                loadDoc(codeElement, "<%=request.getContextPath()%>/fbapi/" + idDiv + ".xml");
            }
        }
    }

    function hide(idDiv) {
        document.location.href = "#";
        document.getElementById(idDiv).style.visibility = "collapse";
    }
    -->
    </script>
  </head>
  <body>
    <h1>FormBuilder REST API</h1>
    <ul>
      <li>
        <h4><%=request.getContextPath()%>/org.jbpm.formbuilder.FormBuilder/fbapi/menuItems/</h4>
        <ul>
          <li><a href="javascript:void(0);" onclick="show('listMenuItems');"><strong>GET</strong>: List menu items</a></li>
          <li style="visibility: collapse;" id="listMenuItems">
              <table border="0" cellspacing="0" cellpadding="0">
                  <tr>
                      <th>URL Example:</th>
                      <td>/org.jbpm.formbuilder.FormBuilder/fbapi/menuItems/</td>
                  </tr>
                  <tr>
                      <th>Response Example:</th>
                      <td><code id="code_listMenuItems"></code></td>
                  </tr>
                  <tr><td>&nbsp;</td><td><input type="button" onclick="hide('listMenuItems');" value="Hide"/></td></tr>
              </table>
          </li>
          <li><a href="javascript:void(0);" onclick="show('saveMenuItem');"><strong>POST</strong>: Saves a menu item</a></li>
          <li style="visibility: collapse;" id="saveMenuItem">
              <table border="0" cellspacing="0" cellpadding="0">
                  <tr>
                      <th>URL Example:</th>
                      <td>/org.jbpm.formbuilder.FormBuilder/fbapi/menuItems/</td>
                  </tr>
                  <tr>
                      <th>Request Example:</th>
                      <td><code id="code_saveMenuItem"></code></td>
                  </tr>
                  <tr><td>&nbsp;</td><td><input type="button" onclick="hide('saveMenuItem');" value="Hide"/></td></tr>
              </table>
          </li>
          <li><a href="javascript:void(0);" onclick="show('deleteMenuItem');"><strong>DELETE</strong>: Deletes a (custom) menu item</a></li>
          <li style="visibility: collapse;" id="deleteMenuItem">
              <table border="0" cellspacing="0" cellpadding="0">
                  <tr>
                      <th>URL Example:</th>
                      <td>/org.jbpm.formbuilder.FormBuilder/fbapi/menuItems/</td>
                  </tr>
                  <tr>
                      <th>Request Example:</th>
                      <td><code id="code_deleteMenuItem"></code></td>
                  </tr>
                  <tr><td>&nbsp;</td><td><input type="button" onclick="hide('deleteMenuItem');" value="Hide"/></td></tr>
              </table>
          </li>
        </ul>
      </li>
      <li>
        <h4><%=request.getContextPath()%>/org.jbpm.formbuilder.FormBuilder/fbapi/formItems/</h4>
        <ul>
          <li><a href="javascript:void(0);" onclick="show('listFormItems');"><strong>GET</strong>: Lists UI components available</a></li>
          <li style="visibility: collapse;" id="listFormItems">
              <table border="0" cellpadding="0" cellspacing="0">
                  <tr>
                      <th>URL Example:</th>
                      <td>/org.jbpm.formbuilder.FormBuilder/fbapi/formItems/package/defaultPackage/</td>
                  </tr>
                  <tr>
                      <th>Response Example:</th>
                      <td><code id="code_listFormItems"></code></td>
                  </tr>
                  <tr><td>&nbsp;</td><td><input type="button" onclick="hide('listFormItems');" value="Hide"/></td></tr>
              </table>
          </li>
          <li><a href="javascript:void(0);" onclick="show('saveFormItem');"><strong>POST</strong>: Saves a UI component on the server</a></li>
          <li style="visibility: collapse;" id="saveFormItem">
              <table border="0" cellpadding="0" cellspacing="0">
                  <tr>
                      <th>URL Example:</th>
                      <td>/org.jbpm.formbuilder.FormBuilder/fbapi/formItems/package/defaultPackage/</td>
                  </tr>
                  <tr>
                      <th>Request Body Example:</th>
                      <td><code id="code_saveFormItem"></code></td>
                  </tr>
                  <tr><td>&nbsp;</td><td><input type="button" onclick="hide('saveFormItem');" value="Hide"/></td></tr>
              </table>
          </li>
          <li><a href="javascript:void(0);" onclick="show('deleteFormItem')"><strong>DELETE</strong>: Deletes a UI component from the server</a></li>
          <li style="visibility: collapse;" id="deleteFormItem">
              <table border="0" cellpadding="0" cellspacing="0">
                  <tr>
                      <th>URL Example:</th>
                      <td>/org.jbpm.formbuilder.FormBuilder/fbapi/formItems/package/defaultPackage/formItemName/FORM_ITEM_TO_DELETE/</td>
                  </tr>
                  <tr>
                      <td colspan="2" style="color: red;"><strong>Attention</strong>: In storage, all form item definition ids are preceded 
                      by "formItemDefinition_" in their name. So, if you have a form item definition saved with the name "myItem", you must retrieve
                      it with the form item definition ID "formItemDefinition_myForm"</td>
                  </tr>
                  <tr><td>&nbsp;</td><td><input type="button" onclick="hide('deleteFormItem');" value="Hide"/></td></tr>
              </table>
          </li>
        </ul>
      </li>
      <li>
        <h4><%=request.getContextPath()%>/org.jbpm.formbuilder.FormBuilder/fbapi/formDefinitions/</h4>
        <ul>
          <li><a href="javascript:void(0);" onclick="show('listForms');"><strong>GET</strong>: Lists form definitions available</a></li>
          <li style="visibility: collapse;" id="listForms">
              <table border="0" cellpadding="0" cellspacing="0">
                  <tr>
                      <th>URL Example to get one form:</th>
                      <td>/org.jbpm.formbuilder.FormBuilder/fbapi/formDefinitions/package/defaultPackage/formDefinitionId/FORM_DEF_TO_GET/</td>
                  </tr>
                  <tr>
                      <th>URL Example to get all forms:</th>
                      <td>/org.jbpm.formbuilder.FormBuilder/fbapi/formDefinitions/package/defaultPackage/</td>
                  </tr>
                  <tr>
                      <td colspan="2" style="color: red;"><strong>Attention</strong>: In storage, all form definition ids are preceded 
                      by "formDefinition_" in their name. So, if you have a form definition saved with the name "myForm", you must retrieve
                      it with the form definition ID "formDefinition_myForm"</td>
                  </tr>
                  <tr><td>&nbsp;</td><td><input type="button" onclick="hide('listForms');" value="Hide"/></td></tr>
              </table>
          </li>
          <li><a href="javascript:void(0);" onclick="show('saveForm');"><strong>POST</strong>: Saves a form definition on the server</a></li>
          <li style="visibility: collapse;" id="saveForm">
              <table border="0" cellpadding="0" cellspacing="0">
                  <tr>
                      <th>URL Example:</th>
                      <td>/org.jbpm.formbuilder.FormBuilder/fbapi/formDefinitions/package/defaultPackage/</td>
                  </tr>
                  <tr>
                      <th>Request Body Example:</th>
                      <td><code id="code_saveForm"></code></td>
                  </tr>
                  <tr><td>&nbsp;</td><td><input type="button" onclick="hide('saveForm');" value="Hide"/></td></tr>
              </table>
          </li>
          <li><a href="javascript:void(0);" onclick="show('deleteForm');"><strong>DELETE</strong>: Deletes a form definition from the server</a></li>
          <li style="visibility: collapse;" id="deleteForm">
              <table border="0" cellpadding="0" cellspacing="0">
                  <tr>
                      <th>URL Example:</th>
                      <td>/org.jbpm.formbuilder.FormBuilder/fbapi/formDefinitions/package/defaultPackage/formDefinitionId/FORM_DEF_TO_DELETE/</td>
                  </tr>
                  <tr>
                      <td colspan="2" style="color: red;"><strong>Attention</strong>: In storage, all form definition ids are preceded 
                      by "formDefinition_" in their name. So, if you have a form definition saved with the name "myForm", you must delete
                      it with the form definition ID "formDefinition_myForm"</td>
                  </tr>
                  <tr><td>&nbsp;</td><td><input type="button" onclick="hide('deleteForm');" value="Hide"/></td></tr>
              </table>
          </li>
        </ul>
      </li>
      <li>
        <h4><%=request.getContextPath()%>/org.jbpm.formbuilder.FormBuilder/fbapi/menuOptions/</h4>
        <ul>
          <li><a href="javascript:void(0);" onclick="show('getMenuOptions');"><strong>GET</strong>: Lists menu bar options</a></li>
          <li style="visibility: collapse;" id="getMenuOptions">
              <table border="0" cellpadding="0" cellspacing="0">
                  <tr>
                      <th>URL Example:</th>
                      <td>/org.jbpm.formbuilder.FormBuilder/fbapi/menuOptions/</td>
                  </tr>
                  <tr>
                      <th>Request Body Example:</th>
                      <td><code id="code_getMenuOptions"></code></td>
                  </tr>
                  <tr><td>&nbsp;</td><td><input type="button" onclick="hide('getMenuOptions');" value="Hide"/></td></tr>
              </table>
          </li>
        </ul>
      </li>
      <li>
        <h4><%=request.getContextPath()%>/org.jbpm.formbuilder.FormBuilder/fbapi/tasks/</h4>
        <ul>
          <li style="color:red;"><strong>GET</strong>: explain listTasks()</li>
        </ul>
      </li>
      <li>
        <h4><%=request.getContextPath()%>/org.jbpm.formbuilder.FormBuilder/fbapi/validations/</h4>
        <ul>
          <li style="color:red;"><strong>GET</strong>: explain listValidations()</li>
        </ul>
      </li>
      <li>
        <h4><%=request.getContextPath()%>/org.jbpm.formbuilder.FormBuilder/fbapi/formTemplate/</h4>
        <ul>
          <li style="color:red;"><strong>POST</strong>: explain getFormTemplate()</li>
          <li style="color:red;"><strong>POST</strong>: explain exportTemplateFile()</li>
        </ul>
      </li>
      <li>
        <h4><%=request.getContextPath()%>/org.jbpm.formbuilder.FormBuilder/fbapi/formPreview/</h4>
        <ul>
          <li style="color:red;"><strong>POST</strong>: explain getFormPreview()</li>
        </ul>
      </li>
    </ul>
  </body>
</html>
