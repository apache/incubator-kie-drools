package org.drools.brms.client.ruleeditor;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;


/**
 * This is the default rule editor widget (just text editor based) - more to come later.
 * @author michael neale
 */
public class DefaultRuleContentWidget extends Composite {
	
	private TextArea text;
	
	public DefaultRuleContentWidget(String content) {
		text = new TextArea();
		text.setWidth("100%");
		text.setHeight("100%");
		text.setVisibleLines(10);
		text.setText(content);		
		setWidget(text);
	}
}
