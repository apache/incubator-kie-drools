package org.drools.analytics.result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

import org.drools.analytics.components.AnalyticsClass;
import org.drools.analytics.components.AnalyticsRule;
import org.drools.analytics.components.Field;
import org.drools.analytics.dao.AnalyticsData;
import org.drools.analytics.dao.AnalyticsDataMaps;

public class ComponentsReportModeller {

	private static String cssFile = "basic.css";
	private StringBuffer str = new StringBuffer("");
	private AnalyticsData data = AnalyticsDataMaps.getAnalyticsDataMaps();

	public String writeComponentsHTML() {
		Collection<AnalyticsRule> rules = data.getAllRules();
		Collection<AnalyticsClass> classes = data.getAllClasses();
		Collection<Field> fields = data.getAllFields();

		str.append("<html>\n");
		str.append("<head>\n");
		str.append("<title>\n");
		str.append("Rule Relations\n");
		str.append("</title>\n");
		// str.append("<link rel=\"stylesheet\" type=\"text/css\"
		// href=\"basic.css\" title=\"default\">\n");

		str.append("<style type=\"text/css\">\n");
		str.append("<!--\n");
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(ComponentsReportModeller.class
							.getResourceAsStream(cssFile)));
			String cssLine = null;
			while ((cssLine = reader.readLine()) != null) {
				str.append(cssLine);
				str.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		str.append("-->\n");
		str.append("</style>\n");

		str.append("</head>\n");
		str.append("<body>\n\n");

		str.append("<br>\n");
		str.append("<h1>\n");
		str.append("Rule Relations");
		str.append("</h1>\n");
		str.append("<br>\n");

		visitRuleCollection(rules);

		str.append("<br>\n");
		str.append("<br>\n");

		visitClassCollection(classes);

		str.append("<br>\n");
		str.append("<br>\n");

		visitFieldCollection(fields);

		str.append("<br>\n");
		str.append("<br>\n");

		str.append("</body>\n");
		str.append("</html>");

		return str.toString();
	}

	public void visitRuleCollection(Collection<AnalyticsRule> rules) {

		str.append("<table class=\"rules\">\n");
		str.append("<tr>\n");
		str.append("<th>\n");

		if (rules.size() > 0) {
			str.append("Rules ( ");
			str.append(rules.size());
			str.append(" )\n");

			str.append("</th>\n");
			str.append("</tr>\n");

			for (AnalyticsRule rule : rules) {
				str.append("<tr>\n");
				str.append("<td>\n");

				visitRule(rule);

				str.append("</td>\n");
				str.append("</tr>\n");
			}
		} else {
			str.append("No rules found\n");
			str.append("</th>\n");
			str.append("</tr>\n");
		}

		str.append("</table>\n");
	}

	public void visitClassCollection(Collection<AnalyticsClass> classes) {

		str.append("<table class=\"patterns\">\n");
		str.append("<tr>\n");
		str.append("<th>\n");

		if (classes.size() > 0) {
			str.append("Patterns ( ");
			str.append(classes.size());
			str.append(" )\n");

			str.append("</th>\n");
			str.append("</tr>\n");

			for (AnalyticsClass clazz : classes) {
				str.append("<tr>\n");
				str.append("<td>\n");

				visitClass(clazz);

				str.append("</td>\n");
				str.append("</tr>\n");
			}
		} else {
			str.append("No patterns found\n");
			str.append("</th>\n");
			str.append("</tr>\n");
		}

		str.append("</table>\n");
	}

	public void visitFieldCollection(Collection<Field> fields) {

		str.append("<table class=\"fields\">\n");
		str.append("<tr>\n");
		str.append("<th>\n");

		if (fields.size() > 0) {
			str.append("Fields ( ");
			str.append(fields.size());
			str.append(" )\n");

			str.append("</th>\n");
			str.append("</tr>\n");

			for (Field field : fields) {
				str.append("<tr>\n");
				str.append("<td>\n");

				visitField(field);

				str.append("</td>\n");
				str.append("</tr>\n");
			}
		} else {
			str.append("No fields found\n");
			str.append("</th>\n");
			str.append("</tr>\n");
		}

		str.append("</table>\n");
	}

	public void visitRule(AnalyticsRule rule) {
		Collection<AnalyticsClass> classes = data.getClassesByRuleName(rule
				.getRuleName());

		str.append("Rule<br />&nbsp;&nbsp;Name:&nbsp;");
		str.append("<a name=\"Rule_id");
		str.append(rule.getId());
		str.append("\">");
		str.append(rule.getRuleName());
		str.append("</a>");

		str
				.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Patterns:&nbsp;<br />\n");
		processClassCollection(classes);
	}

	public void visitClass(AnalyticsClass clazz) {
		Collection<Field> fields = data.getFieldsByClassId(clazz.getId());
		Collection<AnalyticsRule> rules = data.getRulesByClassId(clazz.getId());

		str.append("Pattern<br />&nbsp;&nbsp;Name:&nbsp;");
		str.append("<a name=\"Pattern_id");
		str.append(clazz.getId());
		str.append("\">");
		str.append(clazz.getName());
		str.append("</a>");

		str
				.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Known fields:&nbsp;<br />\n");
		processFieldCollection(fields);
		str
				.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Rules that use this component:&nbsp;<br />\n");
		processRuleCollection(rules);
	}

	public void visitField(Field field) {
		AnalyticsClass clazz = data.getClassById(field.getClassId());
		Collection<AnalyticsRule> rules = data.getRulesByFieldId(field.getId());

		str.append("Field<br />&nbsp;&nbsp;Name:&nbsp;");
		str.append("<a name=\"Field_id");
		str.append(field.getId());
		str.append("\">");
		str.append(field.getName());
		str.append("</a>");

		str.append("<br />&nbsp;&nbsp;Type:&nbsp;");
		str.append(field.getFieldType());
		str.append("<br />&nbsp;&nbsp;Belongs to class:&nbsp;");

		str.append("<a href=\"#Pattern_id");
		str.append(clazz.getId());
		str.append("\">");
		str.append(clazz.getName());
		str.append("</a><br />");

		str
				.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Rules that use this component:&nbsp;<br />\n");
		processRuleCollection(rules);
	}

	private void processFieldCollection(Collection<Field> fields) {
		if (fields != null && !fields.isEmpty()) {
			for (Field field : fields) {
				str
						.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-&nbsp;<a href=\"#Field_id");
				str.append(field.getId());
				str.append("\">");
				str.append(field.getName());
				str.append("</a><br />\n");
			}
		} else {
			str
					.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;No fields found<br />\n");
		}
	}

	private void processRuleCollection(Collection<AnalyticsRule> rules) {

		if (rules != null && !rules.isEmpty()) {
			for (AnalyticsRule rule : rules) {
				str
						.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-&nbsp;<a href=\"#Rule_id");
				str.append(rule.getId());
				str.append("\">");
				str.append(rule.getRuleName());
				str.append("</a><br />\n");
			}
		} else {
			str
					.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;No rules found<br />\n");
		}
	}

	private void processClassCollection(Collection<AnalyticsClass> classes) {
		// Patterns
		if (classes != null && !classes.isEmpty()) {
			for (AnalyticsClass clazz : classes) {
				str
						.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-&nbsp;<a href=\"#Pattern_id");
				str.append(clazz.getId());
				str.append("\">");
				str.append(clazz.getName());
				str.append("</a><br />\n");
			}
		} else {
			str
					.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;No patterns found<br />\n");
		}
	}
}
