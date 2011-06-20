<html>
<body>

<h2>Request Approval</h2>
<hr>
UserId: ${userId} <br/>
Description: ${description} <br/>
Date: ${date?date} ${date?time} <br/>
Result: ${result} <br/>
Comment: ${comment}
<form action="complete" method="POST" enctype="multipart/form-data">
Do you agree?<BR/>
<input type="submit" name="outcome" value="Agree">
<input type="submit" name="outcome" value="Disagree">
</form>
</body>
</html>