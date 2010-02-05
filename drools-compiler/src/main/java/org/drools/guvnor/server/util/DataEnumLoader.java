package org.drools.guvnor.server.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.mvel2.MVEL;

/**
 * Use mvel to load up map/list of valid items for fields - used by the Guided rule editor.
 */
public class DataEnumLoader {

	private final List<String> errors;
	private final Map<String, String[]> data;

	/**
	 * This is the source of the asset, which is an MVEL map (minus the outer "[") of course.
	 */
	public DataEnumLoader(String mvelSource) {
		errors = new ArrayList<String>();
		this.data = loadEnum(mvelSource);
	}

	private Map<String, String[]> loadEnum(String mvelSource) {

        if (mvelSource == null || (mvelSource.trim().equals( "" ))) {
            return Collections.emptyMap();
        }
        if (mvelSource.startsWith("=")) {
        	mvelSource = mvelSource.substring(1);
        } else {
        	mvelSource = "[ " + addCommasForNewLines(mvelSource) + " ]";
        }
		final Object mvelData;
		try {
			mvelData = MVEL.eval(mvelSource, new HashMap<String, Object>());
		} catch (RuntimeException e) {
			addError("Unable to load enumeration data.");
			addError(e.getMessage());
			addError("Error type: " + e.getClass().getName());
			return Collections.emptyMap();
		}
		if (!(mvelData instanceof Map<?, ?>)) {
			addError("The expression is not a map, it is a " + mvelData.getClass().getName());
			return Collections.emptyMap();
		}
		Map<String, Object> map = (Map<String, Object>) mvelData;
        Map<String, String[]> newMap = new HashMap<String, String[]>();
		for (Map.Entry<String, Object> entry: map.entrySet()) {
			String key = entry.getKey();
			Object list = entry.getValue();
			if (!(list instanceof List<?> || list instanceof String)) {
				if (list == null) {
					addError("The item with " + key + " is null.");
				} else {
					addError("The item with " + key + " is not a list or a string, it is a " + list.getClass().getName());
				}
				return Collections.emptyMap();
			} else if (list instanceof String) {
				newMap.put(key, new String[] {(String)list});
			} else {
				List<?> items = (List<?>) list;
				String[] newItems = new String[items.size()];
				for (int i = 0; i < items.size(); i++) {
					Object listItem = items.get(i);
					if (!(listItem instanceof String)) {
						newItems[i] = listItem.toString();
					} else {
						newItems[i] = (String) listItem;
					}
				}
				newMap.put(key, newItems);
			}
		}
		return newMap;
	}

	public static String addCommasForNewLines(String mvelSource) {
        StringTokenizer st = new StringTokenizer(mvelSource, "\r\n");
        StringBuilder buf = new StringBuilder();
        while(st.hasMoreTokens()) {
            String line = st.nextToken().trim();
            if (st.hasMoreTokens() && line.endsWith( "," )) {
                buf.append( line );
            } else {
                buf.append( line );
                if (st.hasMoreTokens()) {
                    buf.append( "," );
                }
            }
            if (st.hasMoreTokens()) {
                buf.append( "\n" );
            }
        }
        return buf.toString();
    }

    private void addError(String string) {
		this.errors.add(string);
	}

	/**
	 * Return a list of any errors found.
	 */
	public List<String> getErrors() {
		return this.errors;
	}

    public boolean hasErrors() {
        return this.errors.size() > 0;
    }

	/**
	 * Return the map of Fact.field to List (of Strings).
	 */
	public Map<String, String[]> getData() {
		return this.data;
	}

}
