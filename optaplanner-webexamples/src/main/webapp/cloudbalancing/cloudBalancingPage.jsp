<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="org.optaplanner.examples.cloudbalancing.domain.CloudComputer" %>
<%@ page import="java.util.List" %>
<%@ page import="org.optaplanner.examples.cloudbalancing.domain.CloudProcess" %>
<%@ page import="org.optaplanner.examples.cloudbalancing.domain.CloudBalance" %>
<%@ page import="org.optaplanner.webexamples.cloudbalancing.CloudSessionAttributeName" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore" %>

<%
  CloudBalance solution = (CloudBalance) session.getAttribute(CloudSessionAttributeName.SHOWN_SOLUTION);
  HardSoftScore score = solution.getScore();
  List<CloudComputer> computerList = solution.getComputerList();
  Map<CloudComputer, List<CloudProcess>> computerToProcessListMap = new LinkedHashMap<CloudComputer, List<CloudProcess>>(
      computerList.size());
  computerToProcessListMap.put(null, new ArrayList<CloudProcess>()); // unassigned
  for (CloudComputer computer : computerList) {
    computerToProcessListMap.put(computer, new ArrayList<CloudProcess>());
  }
  for (CloudProcess process : solution.getProcessList()) {
    computerToProcessListMap.get(process.getComputer()).add(process);
  }
%>
<p style="margin-top: 10px;">Cost: <%=score == null ? "" : score.isFeasible() ? - score.getSoftScore() + " $" : "Infeasible"%></p>
<table>
  <thead>
  <tr>
    <th>Computer Name</th>
    <th>CPU power</th>
    <th>Memory</th>
    <th>Network bandwidth</th>
    <th>Price</th>
    <th>Process count</th>
  </tr>
  </thead>
  <tbody>
  <%
    for (Map.Entry<CloudComputer, List<CloudProcess>> entry : computerToProcessListMap.entrySet()) {
      CloudComputer computer = entry.getKey();
      List<CloudProcess> processList = entry.getValue();
      int cpuPowerUsage = 0;
      int memoryUsage = 0;
      int networkBandwidthUsage = 0;
      for (CloudProcess process : processList) {
        cpuPowerUsage += process.getRequiredCpuPower();
        memoryUsage += process.getRequiredMemory();
        networkBandwidthUsage += process.getRequiredNetworkBandwidth();
      }
      boolean used = processList.size() > 0;
  %>
  <tr <%=used ? "" : "class=\"disabled\""%>>
    <%
      if (computer == null) {
    %>
      <th>Unassigned</th>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    <%
      } else {
    %>
      <th><img src="cloudComputer.png" alt=""/> <%=computer.getLabel()%></th>
      <td style="text-align: center;"><%=cpuPowerUsage%> GHz / <%=computer.getCpuPower()%> GHz</td>
      <td style="text-align: center;"><%=memoryUsage%> GB / <%=computer.getMemory()%> GB</td>
      <td style="text-align: center;"><%=networkBandwidthUsage%> GB / <%=computer.getNetworkBandwidth()%> GB</td>
      <td style="text-align: right;"><%=computer.getCost()%> $</td>
    <%
      }
    %>
    <td style="text-align: right;"><%=processList.size()%> processes</td>
  </tr>
  <%
    }
  %>
  </tbody>
</table>
