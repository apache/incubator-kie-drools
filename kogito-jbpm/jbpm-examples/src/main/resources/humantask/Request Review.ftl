<html>
<body>

<h2>Request Review</h2>
<hr>
UserId: ${userId} <br/>
Description: ${description} <br/>
Date: ${date?date} ${date?time}
<form action="complete" method="POST" enctype="multipart/form-data">
Comment:<BR/>
<textarea cols="50" rows="5" name="comment"></textarea></BR>
<input type="submit" name="outcome" value="Accept">
<input type="submit" name="outcome" value="Reject">
</form>
</body>
</html>