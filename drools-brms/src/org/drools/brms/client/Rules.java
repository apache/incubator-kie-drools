package org.drools.brms.client;

import org.drools.brms.client.rulelist.RuleList;
import org.drools.brms.client.rulenav.RulesNavigatorTree;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Rules extends JBRMSFeature {

	public static ComponentInfo init() {
		return new ComponentInfo("Rules", "Find and edit rules.") {
			public JBRMSFeature createInstance() {
				return new Rules();
			}

			public Image getImage() {

				return new Image("images/rules.gif");
			}
		};
	}
	
	public Rules() {
		
		VerticalPanel  panel = new VerticalPanel();
		RulesNavigatorTree nav = new RulesNavigatorTree();
		panel.add(nav.getTree());
		panel.add(new RuleList());
		setWidget(panel);
	}

}
