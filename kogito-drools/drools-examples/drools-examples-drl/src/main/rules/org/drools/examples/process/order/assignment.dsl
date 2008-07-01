[condition][HumanTaskWorkItem]- with actor id "{actorId}"=parameters['ActorId'] == "{actorId}"
[condition][HumanTaskWorkItem]- without actor id=parameters['ActorId'] == null
[condition][HumanTaskWorkItem]- with task name "{taskName}"=parameters['TaskName'] == "{taskName}"
[condition][HumanTaskWorkItem]There is a human task=workItemNodeInstance: WorkItemNodeInstance( ) workItem: WorkItemImpl( name == "Human Task" ) from workItemNodeInstance.workItem
[consequence][]Set actor id "{actorId}"=workItem.setParameter("ActorId", "{actorId}"); update(workItemNodeInstance);
[condition][HumanTaskWorkItem]- with priority {priority}=parameters['Priority'] == {priority}
[condition][HumanTaskWorkItem]Process "{processId}" contains a human task=workItemNodeInstance: WorkItemNodeInstance( processInstance.processId == "{processId}" ) workItem: WorkItemImpl( name == "Human Task" ) from workItemNodeInstance.workItem
