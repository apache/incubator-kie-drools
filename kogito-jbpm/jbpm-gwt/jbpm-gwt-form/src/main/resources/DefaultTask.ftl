<html>
<body>

<h2>${task.names[0].text}</h2>
<hr>
Description: ${task.descriptions[0].text}
<form action="complete" method="POST" enctype="multipart/form-data">
<input type="submit" value="Complete">
</form>
</body>
</html>