package org.jbpm.examples.checklist.impl;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.examples.checklist.ChecklistItem;
import org.jbpm.examples.checklist.ChecklistManager;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.test.JBPMHelper;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.task.api.UserGroupCallback;

public class ChecklistExample {
	
	public static void main(String[] args) {
		try {
			
			JBPMHelper.startH2Server();
			JBPMHelper.setupDataSource();
			RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getDefault()
	            .userGroupCallback(new UserGroupCallback() {
	    			public List<String> getGroupsForUser(String userId, List<String> groupIds, List<String> allExistingGroupIds) {
	    				List<String> result = new ArrayList<String>();
	    				if ("actor4".equals(userId)) {
	    					result.add("group1");
	    				}
	    				return result;
	    			}
	    			public boolean existsUser(String arg0) {
	    				return true;
	    			}
	    			public boolean existsGroup(String arg0) {
	    				return true;
	    			}
	    		})
	            .addAsset(ResourceFactory.newClassPathResource("checklist/SampleChecklistProcess.bpmn"), ResourceType.BPMN2)
	            .get();
			ChecklistManager checklistManager = new DefaultChecklistManager(environment);
	
			long c1 = checklistManager.createContext("org.jbpm.examples.checklist.sample1");
			List<ChecklistItem> items = checklistManager.getTasks(c1, null);
			printChecklistItems(items, c1);
			
			System.out.println("Completing Task1");
			ChecklistItem item1 = findChecklistItem(items, "Task1");
			checklistManager.completeTask("actor1", item1.getTaskId());
			items = checklistManager.getTasks(c1, null);
			printChecklistItems(items, c1);
			
			System.out.println("Adding Extra Task");
			String[] actorIds = new String[] {
				"actor5"
			};
			ChecklistItem itemExtra = checklistManager.addTask("actor5", actorIds, new String[0], "TaskExtra", "2+", c1);
			items = checklistManager.getTasks(c1, null);
			printChecklistItems(items, c1);
			
			System.out.println("Completing Task2");
			ChecklistItem item2 = findChecklistItem(items, "Task2");
			checklistManager.claimTask("actor4", item2.getTaskId());
			checklistManager.completeTask("actor4", item2.getTaskId());
			items = checklistManager.getTasks(c1, null);
			printChecklistItems(items, c1);
			
			System.out.println("Completing Task3b");
			ChecklistItem item3b = findChecklistItem(items, "Task3b");
			checklistManager.claimTask("actor3", item3b.getTaskId());
			checklistManager.completeTask("actor3", item3b.getTaskId());
			items = checklistManager.getTasks(c1, null);
			printChecklistItems(items, c1);
			System.out.println("Completing Task3a");
			ChecklistItem item3a = findChecklistItem(items, "Task3a");
			checklistManager.completeTask("actor1", item3a.getTaskId());
			items = checklistManager.getTasks(c1, null);
			printChecklistItems(items, c1);
			
			System.out.println("Completing Extra Task");
			itemExtra = findChecklistItem(items, "TaskExtra");
			checklistManager.completeTask("actor5", itemExtra.getTaskId());
			items = checklistManager.getTasks(c1, null);
			printChecklistItems(items, c1);

			System.out.println("Completing Task4");
			ChecklistItem item4 = findChecklistItem(items, "Task4");
			checklistManager.completeTask("actor1", item4.getTaskId());
			items = checklistManager.getTasks(c1, null);
			printChecklistItems(items, c1);
			
		} catch (Throwable t) {
			t.printStackTrace();
		}
		System.exit(0);
	}
	
	private static void printChecklistItems(List<ChecklistItem> items, long processInstanceId) {
		System.out.println("Checklist " + processInstanceId);
		for (ChecklistItem item: items) {
			String orderingNb = item.getOrderingNb();
			if (orderingNb == null) {
				orderingNb = "";
			} else if (orderingNb.endsWith("+")) {
				orderingNb = "*";
			}
			System.out.println(
				fixedLength(orderingNb, 4) + " "
				+ fixedLength(item.getName(), 20) + " "
				+ fixedLength(item.getStatus().toString(), 10) + " "
				+ fixedLength(item.getActors(), 25)
				+ fixedLength(item.getTaskId() == null ? "" : item.getTaskId() + "", 3));
		}
	}
	
	private static ChecklistItem findChecklistItem(List<ChecklistItem> items, String name) {
		for (ChecklistItem item: items) {
			if (name.equals(item.getName())) {
				return item;
			}
		}
		return null;
	}
		
	private static String fixedLength(String s, int length) {
		if (s == null) {
			s = "";
		}
		if (s.length() > length) {
			return s.substring(0, length - 1);
		} else {
			int l = s.length();
			for (int i = l; i <= length; i++) {
				s += " ";
			}
			return s;
		}
	}
	
}
