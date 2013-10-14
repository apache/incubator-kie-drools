<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="org.optaplanner.examples.cloudbalancing.domain.CloudComputer" %>
<%@ page import="java.util.List" %>
<%@ page import="org.optaplanner.examples.cloudbalancing.domain.CloudProcess" %>
<%@ page import="org.optaplanner.examples.cloudbalancing.domain.CloudBalance" %>
<%@ page import="org.optaplanner.webexamples.cloudbalancing.CloudBalancingSessionAttributeName" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore" %>

<%
  CloudBalance solution = (CloudBalance) session.getAttribute(CloudBalancingSessionAttributeName.SHOWN_SOLUTION);
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
      int cpuPowerCapacity = computer == null ? 0 : computer.getCpuPower();
      int memoryCapacity = computer == null ? 0 : computer.getMemory();
      int networkBandwidthCapacity = computer == null ? 0 : computer.getNetworkBandwidth();
      boolean used = processList.size() > 0;
  %>
  <tr <%=used ? "" : "class=\"disabled\""%>>
    <%
      if (computer == null) {
    %>
      <th>Unassigned</th>
    <%
      } else {
    %>
      <th><img src="cloudComputer.png" alt=""/>&nbsp;<%=computer.getLabel()%></th>
    <%
      }
    %>
    <td style="text-align: center;"><%=cpuPowerUsage%> GHz / <%=cpuPowerCapacity%> GHz</td>
    <td style="text-align: center;"><%=memoryUsage%> GB / <%=memoryCapacity%> GB</td>
    <td style="text-align: center;"><%=networkBandwidthUsage%> GB / <%=networkBandwidthCapacity%> GB</td>
    <%
      if (computer == null) {
    %>
    <td></td>
    <%
    } else {
    %>
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
