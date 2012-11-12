package org.jbpm.process.workitem.google.calendar;

import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;

public class GoogleCalendarWorkItemHandler implements WorkItemHandler {
	
	private String userName = "drools.demo@gmail.com";
	private String password = "pa$$word";

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		try {
			String userName = (String) workItem.getParameter("UserName");
			if (userName == null) {
				userName = this.userName;
			}
			String password = (String) workItem.getParameter("Password");
			if (password == null) {
				password = this.password;
			}
			String title = (String) workItem.getParameter("Title");
			String content = (String) workItem.getParameter("Content");
			String start = (String) workItem.getParameter("Start");
			String end = (String) workItem.getParameter("End");
//			GoogleCalendarUtils.insertEntry(userName, password, title, content, start, end);
			manager.completeWorkItem(workItem.getId(), null);
		} catch (Throwable t) {
			manager.abortWorkItem(workItem.getId());
		}
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// Do nothing
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
