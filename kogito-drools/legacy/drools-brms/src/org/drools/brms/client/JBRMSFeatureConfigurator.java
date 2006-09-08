package org.drools.brms.client;

/**
 * This contains the list of configured features for the JBRMS console.
 * 
 * Modify this to add or remove features.
 * 
 * @author Michael Neale
 * 
 */
public class JBRMSFeatureConfigurator {

	/**
	 * Adds all sinks to the list. Note that this does not create actual
	 * instances of all sinks yet (they are created on-demand). This can make a
	 * significant difference in startup time.
	 */
	public static void configure(JBRMSFeatureList list) {
		list.addSink(Info.init());
		list.addSink(Rules.init());
//		list.addSink(Buttons.init());
//		list.addSink(Menus.init());
//		list.addSink(Images.init());
//		list.addSink(Layouts.init());
//		list.addSink(Lists.init());
		list.addSink(Packages.init());
//		list.addSink(Tables.init());
		list.addSink(RuleBases.init());
		list.addSink(Deployment.init());
		list.addSink(Admin.init());
		list.addSink(Search.init());
		
		
//		list.addSink(Frames.init());
//		list.addSink(Tabs.init());
	}

}
