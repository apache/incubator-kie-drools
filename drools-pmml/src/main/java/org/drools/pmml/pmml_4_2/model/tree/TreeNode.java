package org.drools.pmml.pmml_4_2.model.tree;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
 * declare @{ pmmlPackageName }.TreeNode
@propertyReactive
    context     : String            @key
    id          : String            @key
    parent      : String
    children    : java.util.List
    outcome     : String
    recordCount : int
    counts      : java.util.Map
    confidence  : java.util.Map
    defaultChld : String
end
 */
public class TreeNode {
	private String context;
	private String id;
	private String parent;
	private List children;
	private String outcome;
	private int recordCount;
	private Map counts;
	private Map confidence;
	private String defaultChld;

	
	
	public TreeNode(String context, String id, String parent, List children, String outcome, int recordCount,
			Map counts, Map confidence, String defaultChld) {
		this.context = context;
		this.id = id;
		this.parent = parent;
		this.children = children;
		this.outcome = outcome;
		this.recordCount = recordCount;
		this.counts = counts;
		this.confidence = confidence;
		this.defaultChld = defaultChld;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public List getChildren() {
		return children;
	}
	public void setChildren(List children) {
		this.children = children;
	}
	public String getOutcome() {
		return outcome;
	}
	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}
	public int getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}
	public Map getCounts() {
		return counts;
	}
	public void setCounts(Map counts) {
		this.counts = counts;
	}
	public Map getConfidence() {
		return confidence;
	}
	public void setConfidence(Map confidence) {
		this.confidence = confidence;
	}
	public String getDefaultChld() {
		return defaultChld;
	}
	public void setDefaultChld(String defaultChld) {
		this.defaultChld = defaultChld;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TreeNode other = (TreeNode) obj;
		if (context == null) {
			if (other.context != null) {
				return false;
			}
		} else if (!context.equals(other.context)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("TreeNode( ");
		builder.append("context=").append(this.context).append(", ");
		builder.append("id=").append(this.id).append(", ");
		builder.append("parent=").append(this.parent).append(", ");
		builder.append("children=[");
		Iterator lstIter = this.children.iterator();
		while (lstIter.hasNext()) {
			Object object = lstIter.next();
			builder.append(object.toString());
			if (lstIter.hasNext()) {
				builder.append(", ");
			}
		}
		builder.append("], ");
		builder.append("outcome=").append(this.outcome).append(", ");
		builder.append("recordCount=").append(this.recordCount).append(", ");
		builder.append("counts=[");
		Iterator keyIter = this.counts.keySet().iterator();
		while (keyIter.hasNext()) {
			Object key = keyIter.next();
			Object value = this.counts.get(key);
			builder.append("(").append(key.toString()).append("->").append(value != null ? value.toString() : "null").append(")");
			if (keyIter.hasNext()) {
				builder.append(", ");
			}
		}
		builder.append("], ");
		builder.append("confidence=[");
		keyIter = this.confidence.keySet().iterator();
		while (keyIter.hasNext()) {
			Object key = keyIter.next();
			Object value = this.confidence.get(key);
			builder.append("(").append(key.toString()).append("->").append(value != null ? value.toString() : "null").append(")");
			if (keyIter.hasNext()) {
				builder.append(", ");
			}
		}
		builder.append("], ");
		builder.append("defaultChild=").append(this.defaultChld).append(" )");
		return builder.toString();
	}
	
}
