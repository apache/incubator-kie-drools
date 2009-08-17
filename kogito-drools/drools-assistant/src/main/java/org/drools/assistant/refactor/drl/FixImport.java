package org.drools.assistant.refactor.drl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.assistant.info.drl.RuleBasicContentInfo;
import org.drools.assistant.info.drl.RuleDRLContentInfo;
import org.drools.assistant.info.drl.RuleLineContentInfo;

public class FixImport {
	
	private static final String CLASS_PATTERN = "[\\s\\t:,]+[a-zA-Z]*\\(";
	
	private static final String[] KEYWORDS = {"new", "update", "insert"};
	
	private static final Pattern pattern = Pattern.compile(CLASS_PATTERN);
	private static Matcher matcher;
	
	private static List<String> classes = new ArrayList<String>();
	private static List<String> classloaderClasses = new ArrayList<String>();
	
	// detect all the Class Name and compare with the current imports
	// how detect the Classes loaded into the ClassLoader?
	public static void execute(RuleBasicContentInfo contentInfo, List<RuleBasicContentInfo> imports) {
		classloaderClasses.clear();
		classes.clear();
		RuleDRLContentInfo ruleInfo = ((RuleLineContentInfo)contentInfo).getRule();
		String rule = "";
		for (RuleLineContentInfo ruleLineContentInfo : ruleInfo.getLHSRuleLines())
			rule = rule.concat(ruleLineContentInfo.getContent() + "\n");
		for (RuleLineContentInfo ruleLineContentInfo : ruleInfo.getRHSRuleLines())
			rule = rule.concat(ruleLineContentInfo.getContent() + "\n");
		matcher = pattern.matcher(rule);
		String className;
		while (matcher.find()) {
			className = matcher.group().replaceAll(":", "").replaceAll("\\(", "").replaceAll("\\t", "").replaceAll("\\n", "").trim();
			addClass(className);
		}
		hookClassLoader(ClassLoader.getSystemClassLoader());
	}
	
	private static void addClass(String className) {
		for (int i = 0; i < KEYWORDS.length; i++)
			if (KEYWORDS[i].equals(className))
				return;
		if (!classes.contains(className))
			classes.add(className);
	}
	
	private static void hookClassLoader(ClassLoader currLoader) {
		try {
			Field field = ClassLoader.class.getDeclaredField ("classes");
			field.setAccessible(true);
			Vector<?> currClasses = (Vector<?>)field.get( currLoader );
			for (int position = 0; position < currClasses.size(); position++) {
				Class<?> object = (Class<?>) currClasses.get(position);
				if (!classloaderClasses.contains(object.getCanonicalName()))
					classloaderClasses.add(object.getCanonicalName());
			}
		}
		catch ( java.lang.Exception ex ) {
			System.out.println( "Can't hook " + currLoader + ": " + ex );
		}
	}
	
}
