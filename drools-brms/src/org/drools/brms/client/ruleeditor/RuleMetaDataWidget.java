package org.drools.brms.client.ruleeditor;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This displays the metadata for a rule.
 * 
 * @author Michael Neale
 */
public class RuleMetaDataWidget extends Composite {

	private VerticalPanel mainPanel;
	
	
	
	public RuleMetaDataWidget(String ruleName, String author, String subject) {
		mainPanel = new VerticalPanel();
		
		doName(ruleName);
		doAuthor(author);
		doSubject(subject);
		
		setWidget(mainPanel);
	}



	private void doSubject(String subject) {
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(new Label("Subject:"));
		panel.add(new Label(subject));
		mainPanel.add(panel);	
		
	}



	private void doAuthor(String author) {
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(new Label("Author:"));
		panel.add(new Label(author));
		mainPanel.add(panel);	
	}
	




	private void doName(String ruleName) {
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(new Label("Rule name:"));
		panel.add(new Label(ruleName));
		mainPanel.add(panel);
	}
	
	
	
}
