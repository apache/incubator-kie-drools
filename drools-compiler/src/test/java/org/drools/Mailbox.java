package org.drools;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mailbox {
	static public enum FolderType {INBOX, SENT, TRASH};
	static public final String TEST_EMAIL = "me@test.com";
	
	private Map<FolderType, List<Message>> folders = new HashMap<FolderType, List<Message>>();
	private Map<String, Date> recentContacts = new HashMap<String, Date>();
	private String owneremail;

	public Mailbox(String username) {
		owneremail = username;

		// create contact for self
		recentContacts.put(owneremail, new Date());

		// create default folders
		folders.put(FolderType.SENT, new ArrayList<Message>());
		folders.put(FolderType.TRASH, new ArrayList<Message>());
		folders.put(FolderType.INBOX, new ArrayList<Message>());
	}

	/** parameterized accessor */
	public List<Message> getFolder(FolderType t) {
		return folders.get(t);
	}

	public FolderType getDefaultFolderType() {
		return FolderType.INBOX;
	}

	public MailType getMailType() {
		return MailType.WORK;
	}

	public MailType getMailTypeForFolderType(FolderType pType) {
		return MailType.WORK;
	}

	public Map<FolderType, List<Message>> getFolders() {
		return folders;
	}

	public Map<String, Date> getRecentContacts() {
		return recentContacts;
	}

	public String getOwneremail() {
		return owneremail;
	}
}

