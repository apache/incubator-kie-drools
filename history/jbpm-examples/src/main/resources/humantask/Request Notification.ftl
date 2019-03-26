<html>
<body>

<h2>Request Notification</h2>
<hr>
UserId: ${userId} <br/>
Description: ${description} <br/>
Date: ${date?date} ${date?time} <br/>
Result: ${result} <br/>
Comment: ${comment} <br/>
User response: ${resultUser} <br/>
Manager response: ${resultManager}
<form action="complete" method="POST" enctype="multipart/form-data">
<input type="submit" value="OK">
</form>
</body>
</html>