package org.drools.analytics.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.drools.analytics.components.Field;
import org.drools.analytics.components.LiteralRestriction;
import org.drools.analytics.components.Restriction;
import org.drools.analytics.dao.AnalyticsData;
import org.drools.analytics.dao.AnalyticsDataFactory;
import org.mvel.TemplateInterpreter;

public class MissingRangesReportVisitor extends ReportVisitor {

	public static String visitRangeCheckCauseCollection(
			Collection<RangeCheckCause> causes) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("header", processHeader(UrlFactory.THIS_FOLDER));
		map.put("sourceFolder", UrlFactory.THIS_FOLDER);
		map.put("ruleFolder", UrlFactory.RULE_FOLDER);
		map.put("packageFolder", UrlFactory.PACKAGE_FOLDER);
		map.put("cssStyle", createStyleTag(UrlFactory.CSS_FOLDER + "/"
				+ UrlFactory.CSS_FILE_LIST));

		MapTree<Integer, RangeCheckCause> mapTree = new MapTree<Integer, RangeCheckCause>();
		for (RangeCheckCause cause : causes) {
			mapTree.put(cause.getField().getId(), cause);
		}

		Collection<String> lines = new ArrayList<String>();
		for (Integer i : mapTree.map.keySet()) {
			Set<RangeCheckCause> set = mapTree.map.get(i);
			lines.add(processRangeCheckCollection(set));
		}
		map.put("lines", lines);

		String myTemplate = readFile("missingRanges.htm");

		String result = TemplateInterpreter.evalToString(myTemplate, map);

		return result;
	}

	private static String processRangeCheckCollection(
			Collection<RangeCheckCause> causes) {
		AnalyticsData data = AnalyticsDataFactory.getAnalyticsData();

		Field field = causes.iterator().next().getField();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("header", processHeader(UrlFactory.THIS_FOLDER));
		map.put("sourceFolder", UrlFactory.THIS_FOLDER);
		map.put("fieldFolder", UrlFactory.FIELD_FOLDER);
		map.put("objectTypeFolder", UrlFactory.OBJECT_TYPE_FOLDER);
		map.put("packageFolder", UrlFactory.PACKAGE_FOLDER);
		map.put("cssStyle", createStyleTag(UrlFactory.CSS_FOLDER + "/"
				+ UrlFactory.CSS_FILE_LIST));

		map.put("field", field);
		map.put("objectType", data.getClassById(field.getClassId()));
		map.put("ranges", visitRanges(UrlFactory.THIS_FOLDER, data
				.getRestrictionsByFieldId(field.getId()), causes));

		String myTemplate = readFile("missingRange.htm");

		String result = TemplateInterpreter.evalToString(myTemplate, map);

		return result;
	}

	public static Collection<String> visitRestrictionsCollection(
			String sourceFolder, Collection<Restriction> restrictions,
			Collection<RangeCheckCause> causes) {
		MapTree<Object, String> map = new MapTree<Object, String>();

		for (RangeCheckCause cause : causes) {
			map.put(cause.getValueAsObject(), cause.getEvaluator() + " "
					+ cause.getValueAsString() + " is missing");
		}

		for (Restriction r : restrictions) {
			if (r instanceof LiteralRestriction) {
				try {
					LiteralRestriction restriction = (LiteralRestriction) r;

					map.put(restriction.getValueAsObject(), restriction
							.getEvaluator()
							+ " "
							+ restriction.getValueAsString()
							+ " "
							+ UrlFactory.getRuleUrl(sourceFolder, restriction
									.getRuleId(), restriction.getRuleName()));
				} catch (Exception e) {
					System.out.println(map);
					System.out.println(r);
				}
			}
		}

		return map.values();
	}

	public static String visitRanges(String sourceFolder,
			Collection<Restriction> restrictions,
			Collection<RangeCheckCause> causes) {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("lines", visitRestrictionsCollection(sourceFolder,
				restrictions, causes));

		String myTemplate = readFile("ranges.htm");

		String result = TemplateInterpreter.evalToString(myTemplate, map);

		return result;
	}

}

class MapTree<K, V> {
	protected Map<K, Set<V>> map = new TreeMap<K, Set<V>>();

	protected void put(K key, V value) {
		if (map.containsKey(key)) {
			Set<V> set = map.get(key);
			set.add(value);
		} else {
			Set<V> set = new TreeSet<V>();
			set.add(value);
			map.put(key, set);
		}
	}

	protected Collection<V> values() {
		Collection<V> values = new ArrayList<V>();

		for (Set<V> set : map.values()) {
			for (V value : set) {
				values.add(value);
			}
		}

		return values;
	}
	
	@Override
	public String toString() {
		return values().toString();
	}
}
