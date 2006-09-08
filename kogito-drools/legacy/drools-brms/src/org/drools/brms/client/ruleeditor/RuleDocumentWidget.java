package org.drools.brms.client.ruleeditor;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;

/**
 * This holds the editor and viewer for rule documentation.
 * TODO: make this rich text.
 * @author Michael Neale
 *
 */
public class RuleDocumentWidget extends Composite {

	private TextArea text;
	
	public RuleDocumentWidget(String content) {
		text = new TextArea();
		text.setWidth("100%");
		text.setHeight("100%");
		text.setText(content);
		setWidget(text);
	}
	
}
