This front end is using google GWT.

That means you can run in "hosted" mode (if your OS supports it). To run in hosted mode, there is a JBRMS.launch file,
as well as a JBRMS-shell script (depending if you want to launch from within eclipse or command line).
Otherwise, the JBRMS-compile will generate static HTML and javascript for the front end.

At present, the scripts are linux specific, but if you look at them, they should work on windows too (quite trivial really).
You just need to have GWT downloaded as appropriate.

* How to run in in "hosted" mode, download GWT:
and add a variable pointing to the home of GWT called GWT_HOME (and eclipse variable).
That should be it.


* For a good article introducing the concepts of server/client side with GWT, have a read through:
http://roberthanson.blogspot.com/2006/06/trivial-gwt-example.html