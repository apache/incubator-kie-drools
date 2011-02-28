<html>
<body>
<h2>Employee evaluation</h2>
<hr>
${task.descriptions[0].text}<br/>
<br/>
Reason:${reason}<br/> 
<br/>
Please fill in the following evaluation form: 
<form action="complete" method="POST" enctype="multipart/form-data">
Rate the overall performance: <select name="performance">
<#if performance?exists && performance == "outstanding">
  <option value="outstanding" selected="selected">Outstanding</option>
<#else>
  <option value="outstanding">Outstanding</option>
</#if>
<#if 
performance?exists && performance == "exceeding">
  <option value="exceeding" selected="selected">Exceeding expectations</option>
<#else>
  <option value="exceeding">Exceeding expectations</option>
</#if>
<#if performance?exists && performance == "acceptable">
  <option value="acceptable" selected="selected">Acceptable</option>
<#else>
  <option value="acceptable">Acceptable</option>
</#if>
<#if performance?exists && performance == "below">
  <option value="below" selected="selected">Below average</option>
<#else>
  <option value="below">Below average</option>
</#if>
</select><br/>
<br/>
Check any that apply:<br/>
<input type="checkbox" name="initiative" value="initiative">Displaying initiative<br/>
<input type="checkbox" name="change" value="change">Thriving on change<br/>
<input type="checkbox" name="communication" value="communication">Good communication skills<br/>
<br/>
<input type="submit" value="Complete">
</form>
</body>
</html>