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
import java.util.Calendar;
import java.util.List;

import javax.jcr.InvalidItemStateException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.lock.LockException;
import javax.jcr.version.VersionManager;

import org.drools.repository.events.StorageEventManager;

/**
 * This is the parent class for versionable assets.
 * Contains standard fields based on Dublin Core, and
 * stuff required for versioning.
 * For dublin core, refer to <a href="http://dublincore.org/documents/dces/">Here</a>
 *
 * @see CategorisableItem for more attributes to do with BRMS resources.
 * @author Ben Truitt, Michael Neale
 *
 */
public abstract class VersionableItem extends Item {

    /**
     * Property names for this node type.
     */
    public static final String TITLE_PROPERTY_NAME            = "drools:title";
    public static final String DESCRIPTION_PROPERTY_NAME      = "drools:description";
    public static final String LAST_MODIFIED_PROPERTY_NAME    = "drools:lastModified";
    public static final String FORMAT_PROPERTY_NAME           = "drools:format";
    public static final String CHECKIN_COMMENT                = "drools:checkinComment";
    public static final String VERSION_NUMBER_PROPERTY_NAME   = "drools:versionNumber";
    public static final String CONTENT_PROPERTY_ARCHIVE_FLAG  = "drools:archive";

    /** Dublin core based fields. */
    public static final String LAST_CONTRIBUTOR_PROPERTY_NAME = "drools:lastContributor";
    public static final String CREATOR_PROPERTY_NAME          = "drools:creator";
    public static final String TYPE_PROPERTY_NAME             = "drools:type";
    public static final String SOURCE_PROPERTY_NAME           = "drools:source";
    public static final String SUBJECT_PROPERTY_NAME          = "drools:subject";
    public static final String RELATION_PROPERTY_NAME         = "drools:relation";
    public static final String RIGHTS_PROPERTY_NAME           = "drools:rights";
    public static final String COVERAGE_PROPERTY_NAME         = "drools:coverage";
    public static final String PUBLISHER_PROPERTY_NAME        = "drools:publisher";

    /**
     * The name of the state property on the rule node type
     */
    public static final String STATE_PROPERTY_NAME            = "drools:stateReference";

    /**
     * The name of the tag property on the rule node type
     */
    public static final String CATEGORY_PROPERTY_NAME         = "drools:categoryReference";

    /**
     * The possible formats for the format property of the node
     */
    public static final String DEFAULT_CONTENT_FORMAT         = "txt";

    /** this is what is referred to when reading content from a versioned node */
    private Node               contentNode                    = null;

    /**
     * Sets this object's node attribute to the specified node
     *
     * @param rulesRepository the RulesRepository object that this object is being created from
     * @param node the node in the repository that this item corresponds to
     */
    public VersionableItem(RulesRepository rulesRepository,
                           Node node) {
        super( rulesRepository,
               node );
    }

    /**
     * @return A unique identifier for this items content node.
     * This UUID is constant even with new versions, it represents the asset, and
     * ALL its historical versions.
     */
    public String getUUID() {
        try {
            return this.getVersionContentNode().getIdentifier();
        } catch (  RepositoryException e ) {
            throw new RulesRepositoryException(e);
        }
    }

    /**
     * This will return true if the current entity is actually a
     * historical version (which means is effectively read only).
     */
    public boolean isHistoricalVersion() throws RepositoryException {
        return this.node.getPrimaryNodeType().getName().equals( "nt:version" ) || node.getPrimaryNodeType().getName().equals( "nt:frozenNode" );
    }




    /**
     * @return the predecessor node of this node in the version history, or null if no predecessor version exists
     * @throws RulesRepositoryException
     */
	protected Node getPrecedingVersionNode() throws RulesRepositoryException {
		try {
			Node versionNode;
			if (this.node.getPrimaryNodeType().getName().equals("nt:version")) {
				versionNode = this.node;
			} else {
				versionNode = getVersionManager(this.node).getBaseVersion(this.node.getPath());
			}

			Property predecessorsProperty = versionNode.getProperty("jcr:predecessors");
			Value[] predecessorValues = predecessorsProperty.getValues();

			if (predecessorValues.length > 0) {
				Node predecessorNode = this.node.getSession().getNodeByIdentifier(predecessorValues[0].getString());

				// we don't want to return the root node - it isn't a true
				// predecessor
				if (predecessorNode.getName().equals("jcr:rootVersion")) {
					return null;
				}

				return predecessorNode;
			}
		} catch (PathNotFoundException e) {
			// do nothing - this will happen if no predecessors exits
		} catch (Exception e) {
			log.error("Caught exception", e);
			throw new RulesRepositoryException(e);
		}
		return null;
	}

    /**
     * @return the successor node of this node in the version history
     * @throws RulesRepositoryException
     */
    protected Node getSucceedingVersionNode() throws RulesRepositoryException {
        try {
            Property successorsProperty = this.node.getProperty( "jcr:successors" );
            Value[] successorValues = successorsProperty.getValues();

            if ( successorValues.length > 0 ) {
                Node successorNode = this.node.getSession().getNodeByIdentifier( successorValues[0].getString() );
                return successorNode;
            }
        } catch ( PathNotFoundException e ) {
            //do nothing - this will happen if no successors exist
        } catch ( Exception e ) {
            log.error( "Caught exception",
                       e );
            throw new RulesRepositoryException( e );
        }
        return null;
    }

    /**
     * @return an Iterator over VersionableItem objects encapsulating each successor node of this
     *         Item's node
     * @throws RulesRepositoryException
     * @Deprecated Until I can work out why it isn't quite kosher.
     */
    ItemVersionIterator getSuccessorVersionsIterator() throws RulesRepositoryException {
        return new ItemVersionIterator( this,
                                        ItemVersionIterator.ITERATION_TYPE_SUCCESSOR );
    }

    /**
     * @return an Iterator over VersionableItem objects encapsulating each predecessor node of this
     *         Item's node
     * @throws RulesRepositoryException
     * @Deprecated Until I can work out why it isn't quite kosher.
     */
    ItemVersionIterator getPredecessorVersionsIterator() throws RulesRepositoryException {
        return new ItemVersionIterator( this,
                                        ItemVersionIterator.ITERATION_TYPE_PREDECESSOR );
    }

    /**
     * Clients of this method can cast the resulting object to the type of object they are
     * calling the method on (e.g.
     *         <pre>
     *           RuleItem item;
     *           ...
     *           RuleItem predcessor = (RuleItem) item.getPrecedingVersion();
     *         </pre>
     * @return a VersionableItem object encapsulating the predessor node of this node in the
     *         version history, or null if no predecessor version exists
     * @throws RulesRepositoryException
     */
    public abstract VersionableItem getPrecedingVersion() throws RulesRepositoryException;

    /**
     * Clients of this method can cast the resulting object to the type of object they are
     * calling the method on (e.g.
     *         <pre>
     *           RuleItem item;
     *           ...
     *           RuleItem successor = (RuleItem) item.getSucceedingVersion();
     *         </pre>
     *
     * @return a VersionableItem object encapsulating the successor node of this node in the
     *         version history.
     * @throws RulesRepositoryException
     */
    public abstract VersionableItem getSucceedingVersion() throws RulesRepositoryException;

    /**
     * Gets the Title of the versionable node.  See the Dublin Core documentation for more
     * explanation: http://dublincore.org/documents/dces/
     *
     * @return the title of the node this object encapsulates
     * @throws RulesRepositoryException
     */
    public String getTitle() throws RulesRepositoryException {
        try {
            Node theNode = getVersionContentNode();

            Property data = theNode.getProperty( TITLE_PROPERTY_NAME );
            return data.getValue().getString();
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * See the Dublin Core documentation for more
     * explanation: http://dublincore.org/documents/dces/
     *
     * @param title the new title for the node
     * @throws RulesRepositoryException
     */
    public void updateTitle(String title) throws RulesRepositoryException {
        updateStringProperty( title,
                              TITLE_PROPERTY_NAME );
    }


    public void updateType(String type) {
        updateStringProperty( type,
                              TYPE_PROPERTY_NAME );
    }

    public void updateExternalSource(String source) {
        updateStringProperty( source,
                              SOURCE_PROPERTY_NAME );
    }

    public void updateSubject(String sub) {
        updateStringProperty( sub,
                              SUBJECT_PROPERTY_NAME );
    }

    public void updateExternalRelation(String rel) {
        updateStringProperty( rel,
                              RELATION_PROPERTY_NAME );
    }

    public void updateRights(String rights) {
        updateStringProperty( rights,
                              RIGHTS_PROPERTY_NAME );
    }

    public void updateCoverage(String cov) {
        updateStringProperty( cov,
                              COVERAGE_PROPERTY_NAME );
    }

    public void updatePublisher(String pub) {
        updateStringProperty( pub,
                              PUBLISHER_PROPERTY_NAME );
    }



    /**
     * update a text field. This is a convenience method that just
     * uses the JCR node to set a property.
     * This will also update the timestamp.
     */
    public void updateStringProperty(String value,
                                      String prop) {
        updateStringProperty(value, prop, true);
    }

    /**
     * optionally update last updated... 
     */
    public void updateStringProperty(String value,
                                      String prop, boolean setLastUpdated) {
        try {
            checkIsUpdateable();

            if (value == null) {
                return;
            }

            this.checkout();
            node.setProperty( prop,
                              value );
            if (setLastUpdated) {
                Calendar lastModified = Calendar.getInstance();
                this.node.setProperty( LAST_MODIFIED_PROPERTY_NAME,
                                       lastModified );
            }

        } catch ( Exception e ) {
            if ( e instanceof RuntimeException ) {
                throw (RuntimeException) e;
            }
            throw new RulesRepositoryException( e );
        }
    }

	/**
	 * optionally update last updated...
	 */
	public void updateStringArrayProperty(String[] value, String prop, boolean setLastUpdated) {
		try {
			checkIsUpdateable();

			if (value == null) {
				return;
			}

			this.checkout();
			node.setProperty(prop, value);
			if (setLastUpdated) {
				Calendar lastModified = Calendar.getInstance();
				this.node.setProperty(LAST_MODIFIED_PROPERTY_NAME, lastModified);
				this.node.setProperty(LAST_CONTRIBUTOR_PROPERTY_NAME, node.getSession().getUserID());
			}

		} catch (RulesRepositoryException e) {
			throw new RulesRepositoryException(e);
		} catch (UnsupportedRepositoryOperationException e) {
			throw new RulesRepositoryException(e);
		} catch (LockException e) {
			throw new RulesRepositoryException(e);
		} catch (RepositoryException e) {
			throw new RulesRepositoryException(e);
		}
	}

    /**
     * See the Dublin Core documentation for more
     * explanation: http://dublincore.org/documents/dces/
     *
     * @return the description of this object's node.
     * @throws RulesRepositoryException
     */
    public String getDescription() throws RulesRepositoryException {
            return getStringProperty( DESCRIPTION_PROPERTY_NAME );
    }

    /**
     * get this version number (default is incrementing integer, but you
     * can provide an implementation of VersionNumberGenerator if needed).
     */
    public long getVersionNumber() {
//        try {
//            if ( getVersionContentNode().hasProperty( VERSION_NUMBER_PROPERTY_NAME ) ) {
//                return getVersionContentNode().getProperty( VERSION_NUMBER_PROPERTY_NAME ).getString();
//            } else {
//                return null;
//            }
//        } catch ( RepositoryException e ) {
//            throw new RulesRepositoryException( e );
//        }

        return getLongProperty( VERSION_NUMBER_PROPERTY_NAME );
    }

    /**
     * This will return the checkin comment for the latest revision.
     */
    public String getCheckinComment() throws RulesRepositoryException {
        return getStringProperty( CHECKIN_COMMENT );
    }

    /**
     * @return the date the function node (this version) was last modified
     * @throws RulesRepositoryException
     */
    public Calendar getLastModified() throws RulesRepositoryException {
        try {
        	Node n = getVersionContentNode();
        	if (n.hasProperty(LAST_MODIFIED_PROPERTY_NAME)) {
	            Property lastModifiedProperty = getVersionContentNode().getProperty( LAST_MODIFIED_PROPERTY_NAME );
	            return lastModifiedProperty.getDate();
        	} else {
        		return null;
        	}
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * Creates a new version of this object's node, updating the description content
     * for the node.
     * <br>
     * See the Dublin Core documentation for more
     * explanation: http://dublincore.org/documents/dces/
     *
     * @param newDescriptionContent the new description content for the rule
     * @throws RulesRepositoryException
     */
    public void updateDescription(String newDescriptionContent) throws RulesRepositoryException {
        try {
            this.checkout();
            //this.node.setProperty(arg0, arg1);

            this.node.setProperty( DESCRIPTION_PROPERTY_NAME,
                                   newDescriptionContent );

            Calendar lastModified = Calendar.getInstance();
            this.node.setProperty( LAST_MODIFIED_PROPERTY_NAME,
                                   lastModified );

        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }
    
    /**
     * This returns the format of an item.
     * This is analogous to a file extension
     * if the resource was a file (it may contain more information
     * then a pure file extension could, however).
     *
     * See the Dublin Core documentation for more
     * explanation: http://dublincore.org/documents/dces/
     *
     * @return the format of this object's node
     * @throws RulesRepositoryException
     */
	public String getFormat() throws RulesRepositoryException {
		try {
			Node theNode = getVersionContentNode();

			Property data = theNode.getProperty(FORMAT_PROPERTY_NAME);
			return data.getValue().getString();
		} catch (Exception e) {
			log.error("Caught Exception", e);
			throw new RulesRepositoryException(e);
		}
	}

    /**
     * This sets the format (or "file extension" of the resource).
     * In some cases this is critical, and generally should not be changed
     * after the initial version is checked in.
     *
     * @param newFormat
     */
    public void updateFormat(String newFormat) {
        this.updateStringProperty( newFormat,
                                   FORMAT_PROPERTY_NAME );
    }

    /**
     * When retrieving content, if we are dealing with a version in the history,
     * we need to get the actual content node to retrieve values.
     *
     */
    public Node getVersionContentNode() throws RepositoryException,
                                       PathNotFoundException {
        if ( this.contentNode == null ) {
            this.contentNode = getRealContentFromVersion(this.node);
        }
        return contentNode;
    }

    /**
     * This deals with a node which *may* be a version, if it is, it grabs the frozen copy.
     */
	protected Node getRealContentFromVersion(Node node) throws RepositoryException, PathNotFoundException {
		if (node.getPrimaryNodeType().getName().equals("nt:version")) {
			return node.getNode("jcr:frozenNode");
		} else {
			return node;
		}
	}

    /**
     * Need to get the name from the content node, not the version node
     * if it is in fact a version !
     */
	public String getName() {
		try {
			return getVersionContentNode().getName();
		} catch (RepositoryException e) {
			throw new RulesRepositoryException(e);
		}
	}

    /**
     * This will check out the node prior to editing.
     */
    public void checkout() {
        checkout(this.node);
    }

    /**
     * This will check out the node prior to editing.
     * @param targetNode the node to be checked out.
     */
    public static void checkout(Node targetNode) {
        try {
        	getVersionManager(targetNode).checkout(targetNode.getPath());
        } catch ( UnsupportedRepositoryOperationException e ) {
            String message = "";
            try {
                message = "Error: Caught UnsupportedRepositoryOperationException when attempting to checkout rule: " + targetNode.getName() + ". Are you sure your JCR repository supports versioning? ";
                log.error( message,
                           e );
            } catch ( RepositoryException e1 ) {
                log.error( "Caught Exception",
                           e );
                throw new RulesRepositoryException( e1 );
            }
            throw new RulesRepositoryException( message,
                                                e );
        } catch ( InvalidItemStateException e ) {
        	String message = "Your operation was failed because it conflicts with a change made through another user. Please try again.";
            log.error( "Caught Exception", e );
            throw new RulesRepositoryException( message, e );
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }
    /**
     * This will save the content (if it hasn't been already) and
     * then check it in to create a new version.
     * It will also set the last modified property.
     */
    public void checkin(String comment) {
        checkIsUpdateable();
        try {
        	
            this.node.setProperty( LAST_MODIFIED_PROPERTY_NAME, Calendar.getInstance() );
            this.node.setProperty( CHECKIN_COMMENT, comment );
            this.node.setProperty( LAST_CONTRIBUTOR_PROPERTY_NAME, this.node.getSession().getUserID() );
            long nextVersion = getVersionNumber() + 1;
            this.node.setProperty( VERSION_NUMBER_PROPERTY_NAME,  nextVersion );
            this.node.getSession().save();
            
            getVersionManager(this.node).checkin(this.node.getPath());

            if (StorageEventManager.hasSaveEvent()) {
                if (this instanceof AssetItem) {
                    StorageEventManager.getSaveEvent().onAssetCheckin((AssetItem) this);
                }
            }
            StorageEventManager.doCheckinEvents(this);

        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( "Unable to checkin.",
                                                e );
        }
    }

    /**
     * This will check to see if the node is the "head" and
     * so can be updated (you can't update historical nodes ).
     * @throws RulesRepositoryException if it is not allowed
     * (means a programming error !).
     */
    protected void checkIsUpdateable() {
        try {
            if ( this.node.getPrimaryNodeType().getName().equals( "nt:version" ) ) {
                String message = "Error. Tags can only be added to the head version of a rule node";
                log.error( message );
                throw new RulesRepositoryException( message );
            }
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * Sets this object's rule node's state property to refer to the specified state node
     *
     * @param stateName the name of the state to set the rule node to
     * @throws RulesRepositoryException
     */
    public void updateState(String stateName) throws RulesRepositoryException {
        try {

            //now set the state property of the rule
            checkout();

            StateItem stateItem = this.rulesRepository.getState( stateName );
            this.updateState( stateItem );
        } catch ( Exception e ) {
            log.error( "Caught exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * Sets this object's rule node's state property to refer to the specified StateItem's node
     *
     * @param stateItem the StateItem encapsulating the node to refer to from this object's node's state
     *                  property
     * @throws RulesRepositoryException
     */
    public void updateState(StateItem stateItem) throws RulesRepositoryException {
        checkIsUpdateable();
        try {

            //now set the state property of the rule
            checkout();
            this.node.setProperty( STATE_PROPERTY_NAME,
                                   stateItem.getNode() );
        } catch ( Exception e ) {
            log.error( "Caught exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * Gets StateItem object corresponding to the state property of this object's node
     *
     * @return a StateItem object corresponding to the state property of this object's node, or null
     *         if the state property is not set
     * @throws RulesRepositoryException
     */
    public StateItem getState() throws RulesRepositoryException {
        try {
            Node content = getVersionContentNode();
            Property stateProperty = content.getProperty( STATE_PROPERTY_NAME );
            Node stateNode = this.rulesRepository.getSession().getNodeByIdentifier( stateProperty.getString() );
            return new StateItem( this.rulesRepository,
                                  stateNode );
        } catch ( PathNotFoundException e ) {
            //not set
            return null;
        } catch ( Exception e ) {
            log.error( "Caught exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * This will return the current state item as a displayable thing.
     * If there is no state, it will be an empty string.
     */
    public String getStateDescription() {
        StateItem state = this.getState();
        if ( state == null ) {
            return "";
        } else {
            return state.getName();
        }
    }

    /** Compare this rules state with some other state */
    public boolean sameState(StateItem other) {
        StateItem thisState = getState();
        if ( thisState == other ) {
            return true;
        } else if ( thisState != null ) {
            return thisState.equals( other );
        } else {
            return false;
        }
    }

    /**
     * Returns the last contributors name.
     */
    public String getLastContributor() {
        return getStringProperty( LAST_CONTRIBUTOR_PROPERTY_NAME );
    }

    /**
     * This is the person who initially created the resource.
     */
    public String getCreator() {
        return getStringProperty( CREATOR_PROPERTY_NAME );
    }

    /**
     * This is the Dublin Core field of type (a broad classification of resource type).
     */
    public String getType() {
        return getStringProperty( TYPE_PROPERTY_NAME );
    }

    /**
     * This is the source of the asset/rule. Ie a human description of where it came from.
     */
    public String getExternalSource() {
        return getStringProperty( SOURCE_PROPERTY_NAME );
    }

    /**
     * Typically,
     * Subject will be expressed as keywords,
     * key phrases or classification codes that describe a topic of the resource.
     */
    public String getSubject() {
        return getStringProperty( SUBJECT_PROPERTY_NAME );
    }

    /**
     * A reference to a EXTERNAL related resource.
     */
    public String getExternalRelation() {
        return getStringProperty( RELATION_PROPERTY_NAME );
    }

    /**
     * Optionally contains any copyright/ownership rights for the asset.
     */
    public String getRights() {
        return getStringProperty( RIGHTS_PROPERTY_NAME );
    }

    /**
     * Typically, Coverage will include spatial location
     * (a place name or geographic coordinates), temporal period (a period label, date, or date range) or jurisdiction (such as a named administrative entity). Recommended best practice is to select a value from a controlled vocabulary (for example, the Thesaurus of Geographic Names [TGN]) and to use, where appropriate, named places or time periods in preference to numeric identifiers such as sets of coordinates or date ranges.
     */
    public String getCoverage() {
        return getStringProperty( COVERAGE_PROPERTY_NAME );
    }

    /**
     *  Examples of Publisher include a person, an organization, or a service.
     *  Typically, the name of a Publisher should be used to indicate the entity.
     */
    public String getPublisher() {
        return getStringProperty( PUBLISHER_PROPERTY_NAME );
    }

    /**
     * This returns the date/time on which the asset was "ORIGINALLY CREATED".
     * Kinda handy if you want to know how old something is.
     */
    public Calendar getCreatedDate() {
        Property prop;
        try {
            prop = this.node.getProperty( "jcr:created" );
            return prop.getDate();
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        }

    }

    public String getStringProperty(String property) {
        try {
            Node theNode = getVersionContentNode();
            if ( theNode.hasProperty( property ) ) {
                Property data = theNode.getProperty( property );
                return data.getValue().getString();
            } else {
                return "";
            }
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        }
    }

    public String[] getStringPropertyArray(String property) {
        try {
            Node theNode = getVersionContentNode();
            if ( theNode.hasProperty( property ) ) {
                Property data = theNode.getProperty( property );
                Value[] values = data.getValues();
               
    		    List<String> list = new ArrayList<String>();
                for (Value value : values) {
                	list.add(value.getString());
					
				}  
                return list.toArray(new String[0]);
            } else {
                return new String[0];
            }
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        }
    }
    protected long getLongProperty(String property) {
        try {
            Node theNode = getVersionContentNode();
            if ( theNode.hasProperty( property ) ) {
                Property data = theNode.getProperty( property );
                return data.getValue().getLong();
            } else {
                return 0;
            }
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * This returns the id of the exact version node (as opposed to the "main" node).
     * Note that each asset has only one UUID the whole time, but there are also UUIDs
     * for each item in the history.
     * So while the main UUID version remains constant, the version UUIDs change on each
     * checkin, which is what this method provides.
     */
    public String getVersionSnapshotUUID() {
        try {
            if ( isHistoricalVersion() ) {
                return this.node.getIdentifier();
            } else {
                throw new RulesRepositoryException( "This is the current version of the asset." );
            }
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException(e);
        }

    }


    public VersionableItem archiveItem(boolean data) {
    	checkout();

    	try {
    		this.node.setProperty(CONTENT_PROPERTY_ARCHIVE_FLAG, data);
    		return this;
    	} catch (RepositoryException e) {
    		log.error("Unable to update this VersionableItem binary archive flag");
    		throw new RulesRepositoryException(e);
    	}
    }

    /**
     * Test if the VersionableItem is archived
     */
    public boolean isArchived() {
    	try {
    		return this.node.getProperty(CONTENT_PROPERTY_ARCHIVE_FLAG)
    				.getBoolean();
    	} catch (RepositoryException e) {
    		log.error("Unable to check this asset");
    		throw new RulesRepositoryException(e);
    	}
    }
    
    public static VersionManager getVersionManager(Node targetNode) throws RepositoryException {
        return targetNode.getSession().getWorkspace().getVersionManager();
    }

}
