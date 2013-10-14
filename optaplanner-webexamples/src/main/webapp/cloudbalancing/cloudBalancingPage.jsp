<%@ page import="org.optaplanner.webexamples.cloudbalancing.CloudWebAction, java.util.Iterator, java.util.Set, java.util.Map, java.util.TreeMap" %>

<%
  Map tm = new TreeMap();
  CloudWebAction cloudwebaction = new CloudWebAction();
  //bestSolution = (Hashtable) cloudwebaction.toDisplayString(session);
  tm = (TreeMap) cloudwebaction.toDisplayString(session);
  String key;
  String[] valueArray = new String[9];
  String value;
%>

<table border='1'>
  <caption>Cloud Balance</caption>
  <thead>
  <tr>
    <th></th>
    <th>Computer Name</th>
    <th>Computer Resources</th>
    <th>Process Name</th>
    <th>Process Resources</th>
  </tr>
  </thead>
  <tfoot>
  <tr>
    <td></td>
  </tr>
  </tfoot>
  <tbody>
  <%
    //Enumeration k = bestSolution.keys();
//while (k.hasMoreElements()) {
//	key = (String) k.nextElement();
//        valueArray = (String[ ]) bestSolution.get(key); 

//Set set = tm.entrySet();
//Iterator i = set.iterator();
//while(i.hasNext()) {
//	Map.Entry me = (Map.Entry)i.next();
//	key = (String) me.getKey();
// valueArray = (String[ ]) me.getValue()

//   for (key : tm.KeySet() {
//	valueArray = (String[ ]) tm.get(key);

    Set keys = tm.keySet();
    for (Iterator i = keys.iterator(); i.hasNext(); ) {
      key = (String) i.next();
      valueArray = (String[]) tm.get(key);


  %>
  <tr>
    <th rowspan='4'>
      <img src='cloudComputer.png' alt='My Physical Machine'>
    </th>
    <th rowspan='4'>
      <%= valueArray[0]  %>
    </th>
    <td>
      CPU : <%= valueArray[1] %>
    </td>
    <th rowspan='4'>
      <%= valueArray[8]  %>
    </th>
    <td>
      CPU : <%= valueArray[5] %>
    </td>
  </tr>
  <tr>
    <td>
      RAM : <%= valueArray[2] %>
    </td>
    <td>
      MEM : <%= valueArray[6] %>
    </td>
  </tr>
  <tr>
    <td>
      NET : <%= valueArray[3] %>
    </td>
    <td>
      NET : <%= valueArray[7] %>
    </td>
  </tr>
  <tr>
    <td>
      $ : <%= valueArray[4] %>
    </td>
    <td>
      ID : <%= key %>
    </td>
  </tr>

  <%
      //System.out.println(" +++++++++++ " + valueArray[0] + " -> CPU : " + valueArray[1]);
      valueArray = null;
    }
  %>
  </tbody>
</table>
