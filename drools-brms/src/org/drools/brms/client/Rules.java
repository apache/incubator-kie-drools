package org.drools.brms.client;

import org.drools.brms.client.ruleeditor.RuleView;
import org.drools.brms.client.rulelist.RuleListView;
import org.drools.brms.client.rulenav.RulesNavigatorTree;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TabPanel;


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
		TabPanel tab = new TabPanel();
		tab.setWidth("100%");
		tab.setHeight("100%");
		setWidget(tab);
		
		
		HorizontalPanel explorePanel = doExplore(tab);
		
		RuleView ruleViewer = new RuleView();
		ruleViewer.setWidth("100%");
		ruleViewer.setHeight("100%");
		
		tab.add(explorePanel, "Explore");
		tab.add(ruleViewer, "Author");
		
		tab.selectTab(0);
		
		
	}

	private HorizontalPanel doExplore(TabPanel tab) {
		HorizontalPanel  panel = new HorizontalPanel();
		RulesNavigatorTree nav = new RulesNavigatorTree();			
		panel.add(nav.getTree());
		RuleListView list = new RuleListView(tab);
		panel.add(list);
		return panel;
	}

}
