package org.drools.analytics.report.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * @author Toni Rikkola
 */
abstract public class AnalyticsMessageBase implements Serializable,
		Comparable<AnalyticsMessageBase> {
	private static final long serialVersionUID = 9190003495068712452L;

	private static int index = 0;

	public static class Severity implements Comparable<Severity> {
		public static final Severity NOTE = new Severity(0, "Note", "Notes");
		public static final Severity WARNING = new Severity(1, "Warning",
				"Warnings");
		public static final Severity ERROR = new Severity(2, "Error", "Errors");

		private final int index;
		private final String singular;
		private final String tuple;

		private Severity(int i, String singular, String tuple) {
			this.index = i;
			this.singular = singular;
			this.tuple = tuple;
		}

		private int getIndex() {
			return index;
		}

		public String getSingular() {
			return singular;
		}

		public String getTuple() {
			return tuple;
		}

		public static Collection<Severity> values() {
			Collection<Severity> all = new ArrayList<Severity>();

			all.add(NOTE);
			all.add(WARNING);
			all.add(ERROR);

			return all;
		}

		@Override
		public String toString() {
			return singular;
		}

		public int compareTo(Severity s) {

			if (s.getIndex() == this.index) {
				return 0;
			}

			return (s.getIndex() < this.index ? -1 : 1);
		}
	}

	public static class MessageType {
		public static final MessageType NOT_SPECIFIED = new MessageType(0);
		public static final MessageType RANGE_CHECK = new MessageType(1);
		public static final MessageType MISSING_EQUALITY = new MessageType(2);
		public static final MessageType REDUNDANCY = new MessageType(3);
		public static final MessageType SUBSUMPTION = new MessageType(4);
		public static final MessageType MISSING_COMPONENT = new MessageType(5);
		public static final MessageType OPTIMISATION = new MessageType(6);
		public static final MessageType INCOHERENCE = new MessageType(7);

		private final int index;

		private MessageType(int i) {
			index = i;
		}
	}

	protected Severity severity;
	protected MessageType messageType;

	protected int id = index++;
	protected Cause faulty;
	protected String message;

	public int compareTo(AnalyticsMessageBase o) {
		if (id == o.getId()) {
			return 0;
		}

		return (id > o.getId() ? 1 : -1);
	}

	protected AnalyticsMessageBase(Severity severity, MessageType messageType,
			Cause faulty, String message) {
		this.severity = severity;
		this.messageType = messageType;
		this.faulty = faulty;
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Cause getFaulty() {
		return faulty;
	}

	public void setFaulty(Cause faulty) {
		this.faulty = faulty;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer(severity.singular);

		str.append(" id = ");
		str.append(id);
		str.append(":\n");

		if (faulty != null) {
			str.append("faulty : ");
			str.append(faulty);
			str.append(", ");
		}

		str.append(message);

		str.append("\t]");

		return str.toString();
	}

	public abstract Collection<? extends Cause> getCauses();
}
