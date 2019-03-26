<html>
<body>

<h2>Request Reminder</h2>
<hr>
UserId: ${userId} <br/>
Description: ${description} <br/>
Date: ${date?date} ${date?time} <br/>
Result: ${result} <br/>
Comment: ${comment} <br/>
<br/>
Reminder, request hasn't been completed yet, please review.
<form action="complete" method="POST" enctype="multipart/form-data">
<input type="submit" value="OK">
</form>
</body>
</html>