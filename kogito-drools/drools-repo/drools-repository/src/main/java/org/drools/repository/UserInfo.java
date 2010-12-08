/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.repository;

import java.util.ArrayList;
import java.util.List;

import org.drools.repository.security.PermissionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

import static org.drools.repository.security.PermissionManager.getNode;
import static org.drools.repository.security.PermissionManager.getUserInfoNode;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.NodeIterator;


/**
 * Manage access to misc. user info that we might want to store. 
 * @author Michael Neale
 */
public class UserInfo {
    private static final Logger log                   = LoggerFactory.getLogger( UserInfo.class );

    Node userInfoNode;

    /**
     * Use the current sessions userName to get to the info node.
     */
    public UserInfo(RulesRepository repo) throws RulesRepositoryException {
		try {
			init(repo, repo.getSession().getUserID());
		} catch (RepositoryException e) {
			log.error("Unable to init UserInfo", e);
			throw new RulesRepositoryException(e);
		}
	}

    UserInfo() {}

    /**
     * Use the given userName to select the node.
     */
    public UserInfo(RulesRepository repo, String userName)
			throws RulesRepositoryException {
		try {
			init(repo, userName);
		} catch (RepositoryException e) {
			log.error("Unable to init UserInfo", e);
			throw new RulesRepositoryException(e);
		}
	}

    void init(RulesRepository repo, String userName) throws RepositoryException {
        this.userInfoNode = getUserInfoNode(userName, repo);
    }

    public List<InboxEntry> readEntries(String fileName, String propertyName)
			throws RulesRepositoryException {
		try {
			Val property = getProperty(fileName, propertyName);
			if (!(property.value == null || property.value.equals(""))) {
				return (List<InboxEntry>) getXStream().fromXML(property.value);
			} else {
				return new ArrayList<InboxEntry>();
			}
		} catch (RepositoryException e) {
			log.error("Unable to readEntries", e);
			throw new RulesRepositoryException(e);
		}
	}
    
    public void writeEntries(String fileName, String boxName,
			List<InboxEntry> entries) throws RulesRepositoryException {
		try {
			String entry = getXStream().toXML(entries);

			setProperty(fileName, boxName, new UserInfo.Val(entry));
		} catch (RepositoryException e) {
			log.error("Unable to writeEntries", e);
			throw new RulesRepositoryException(e);
		}
	}   
    
    public void clear(String fileName, String boxName) {
		try {
			setProperty(fileName, boxName, new UserInfo.Val(""));
		} catch (RepositoryException e) {
			log.error("Unable to clear", e);
			throw new RulesRepositoryException(e);
		}
	}
    
    /**
     * And entry in an inbox.
     */
    public static class InboxEntry {
        public String from;

        public InboxEntry() {}
        public InboxEntry(String assetId, String note, String userFrom) {
            this.assetUUID = assetId;
            this.note = note;
            this.timestamp = System.currentTimeMillis();
            this.from = userFrom;
        }
        public String assetUUID;
        public String note;
        public long timestamp;
    }    

    private XStream getXStream() {
        XStream xs = new XStream();
        xs.alias("inbox-entries", List.class);
        xs.alias("entry", InboxEntry.class);
        return xs;
    }
    
    public void setProperty(String fileName, String propertyName, Val value) throws RepositoryException {
        Node inboxNode = getNode(userInfoNode, fileName, "nt:file");
        if (inboxNode.hasNode("jcr:content")) {
            inboxNode.getNode("jcr:content").setProperty(propertyName, value.value);
        } else {
            inboxNode.addNode("jcr:content", "nt:unstructured").setProperty(propertyName, value.value);
        }
    }
   
    public Val getProperty(String fileName, String propertyName) throws RepositoryException {
        Node inboxNode = getNode(userInfoNode, fileName, "nt:file");

        if (inboxNode.hasNode("jcr:content")) {
            if (inboxNode.getNode("jcr:content").hasProperty(propertyName)) {
                return new Val(inboxNode.getNode("jcr:content").getProperty(propertyName).getString());
            } else {
                return new Val("");
            }
        } else {
            inboxNode.addNode("jcr:content", "nt:unstructured"); //needed to make it consistent on save
            return new Val("");
        }
    }

    public static class Val {
        public String value;
        public Val(String s) {
            this.value = s;
        }
    }

    /**
     * Do something for each user.
     * @param c
     */
    public static void eachUser(RulesRepository repository, Command c)
			throws RulesRepositoryException {
		try {

			NodeIterator nit = PermissionManager.getUsersRootNode(
					PermissionManager.getRootNode(repository)).getNodes();
			while (nit.hasNext()) {
				c.process(nit.nextNode().getName());
			}
		} catch (RepositoryException e) {
			log.error("Unable to eachUser", e);
			throw new RulesRepositoryException(e);
		}
	}

    public static interface Command {
        public void process(String toUser) throws RulesRepositoryException;
    }

    /**
     * Persists the change (if not in a transaction of course, if in a transaction, it will wait until the boundary is hit,
     * as per JCR standard.
     * @throws RepositoryException
     */
    public void save() throws RulesRepositoryException {
		try {
			userInfoNode.getParent().getParent().save();
			// userInfoNode.getParent().save();
		} catch (RepositoryException e) {
			log.error("Unable to save", e);
			throw new RulesRepositoryException(e);
		}
	}

}
