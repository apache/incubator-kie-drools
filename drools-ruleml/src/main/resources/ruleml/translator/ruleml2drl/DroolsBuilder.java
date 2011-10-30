package ruleml.translator.ruleml2drl;

import java.util.ArrayList;
import java.util.List;

import ruleml.translator.ruleml2drl.DroolsBuilder.Query;

public class DroolsBuilder {
	private static final String NAME = "#rulename#";
	private static final String WHEN_PART = "#whenpart#";
	private static final String THEN_PART = "#thenpart#";
	private static final String PACKAGE_PART = "#packagepart#";
	private static final String IMPORT_PART = "#importpart#";
	private static final String RULE_PART = "#rulepart#";
	private static final String QUERY_PART = "#querypart#";

	public static final void main(String[] args) {
		Drl drl = new Drl();

		drl.setPackage_("com.simple");
		drl.setImports(new String[] {
				"com.sample.TestDataModel.*", "asdfd.sss" });
		
		Rule rule = new Rule();
		rule.setRuleName("test");
		rule.setWhenPart(new String[] { "........" });
		rule.setThenPart(new String[] { "---------" });
		drl.addRule(rule);

		rule.setRuleName("test");
		rule.setWhenPart(new String[] { "!!!!!!!!!" });
		rule.setThenPart(new String[] { "---------" });
		drl.addRule(rule);

		System.out.println(drl);
	}

	public static class Drl {

		private String package_;
		private String[] imports;
		private List<Query> queries = new ArrayList<Query>();
		private List<Rule> rules = new ArrayList<Rule>();

		private static String drlPattern = "#packagepart#\n" + "#importpart#\n"
				+ "#querypart#\n" + "#rulepart#";

		public String getPackage_() {
			return package_;
		}

		public void setPackage_(String package_) {
			this.package_ = package_;
		}

		public String[] getImports() {
			return imports;
		}

		public void setImports(String[] imports) {
			this.imports = imports;
		}

		public List<Query> getQueries() {
			return queries;
		}

		public void setQueries(List<Query> queries) {
			this.queries = queries;
		}

		public List<Rule> getRules() {
			return rules;
		}

		public void setRules(List<Rule> rules) {
			this.rules = rules;
		}

		public static String getDrlPattern() {
			return drlPattern;
		}

		public static void setDrlPattern(String drlPattern) {
			Drl.drlPattern = drlPattern;
		}

		public void createRule(String ruleName, Object[] whenPart,
				Object[] thenPart) {
			Rule rule = new Rule();
			rule.setRuleName(ruleName);
			rule.setWhenPart(whenPart);
			rule.setThenPart(thenPart);
			rules.add(rule);
		}

		public void addRule(Rule rule) {
			rules.add(rule);
		}

		@Override
		public String toString() {
			String result = drlPattern;
			result = result.replace(PACKAGE_PART, "package " + package_);

			String importPart = "";
			for (String import_ : imports) {
				importPart += "import " + import_ + ";\n";
			}
			result = result.replace(IMPORT_PART, importPart);

			String rulePart = "";
			for (Rule rule : rules) {
				rulePart += rule.toString();
			}
			result = result.replace(RULE_PART, rulePart);
			
			String queryPart = "";
			for (Query query : queries) {
				queryPart += query.toString();
			}			
			result = result.replace(QUERY_PART, queryPart);


			return result;
		}

		public void addQuery(Query query) {
			this.queries.add(query);
		}
	}

	public static class Query {
		private String name;
		private Object[] whenPart;

		public String getName() {
			return name;
		}

		public void setRuleName(String name) {
			this.name = name;
		}

		public Object[] getWhenPart() {
			return whenPart;
		}

		public void setWhenPart(Object[] whenPart) {
			this.whenPart = whenPart;
		}

		private static String queryPattern = "query \"#rulename#\"\n"
				+ "#whenpart#\n" 
				+ "end\n";

		@Override
		public String toString() {
			String result = queryPattern;
			result = result.replace(NAME, name);

			// serialize the when part
			String when = "";
			for (Object o : whenPart) {
				when += "\t\t" + o.toString() + "\n";
			}

			// replace in the pattern
			result = result.replace(WHEN_PART, when);

			return result;
		}

	}

	public static class Rule extends Query {

		public Object[] getThenPart() {
			return thenPart;
		}

		public void setThenPart(Object[] thenPart) {
			this.thenPart = thenPart;
		}

		private Object[] thenPart;

		private static String rulePattern = "rule \"#rulename#\"\n"
				+ "\twhen\n" + "#whenpart#\n" + "\tthen\n" + "#thenpart#\n"
				+ "end\n";

		@Override
		public String toString() {
			String result = rulePattern;
			result = result.replace(NAME, getName());

			// serialize the when part
			String when = "";
			for (Object o : getWhenPart()) {
				when += "\t\t" + o.toString() + "\n";
			}

			// serialize the then part
			String then = "";
			for (Object o : thenPart) {
				then += "\t\t" + o.toString() + "\n";
			}

			// replace in the pattern
			result = result.replace(WHEN_PART, when);
			result = result.replace(THEN_PART, then);

			return result;
		}
	}
}
