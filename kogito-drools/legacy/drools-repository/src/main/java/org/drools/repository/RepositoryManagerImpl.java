package org.drools.repository;

import java.security.Principal;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;


import org.drools.repository.db.ISaveHistory;
import org.drools.repository.db.IVersionable;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * The repository manager takes care of storing and sychronising the repository
 * data with the repository database, using hibernate.
 * 
 * This should not be access directly, but via a proxy to configure the session correctly.
 * 
 * Use RepositoryFactory to get an instance of a repository manager.
 * 
 * @author <a href ="mailto:sujit.pal@comcast.net"> Sujit Pal</a>
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class RepositoryManagerImpl
    implements
    RepositoryManager {

    private Session session;
    private Principal currentUser;
    
    /**
     * Session is injected by the proxy.
     */
    public void injectSession(Session session) {
        this.session = session;        
    }
    
    /* (non-Javadoc)
     * @see org.drools.repository.db.RepositoryManager#loadRule(java.lang.String, long)
     */
    public RuleDef loadRule(String ruleName,
                            long versionNumber) {
        RuleDef result = (RuleDef) session.createQuery( "from RuleDef where name = :name and versionNumber = :version" )
                            .setString( "name", ruleName )
                            .setLong( "version", versionNumber )
                            .uniqueResult();

        return result;
    }

    /* (non-Javadoc)
     * @see org.drools.repository.db.RepositoryManager#listRuleVersions(java.lang.String)
     */
    public List listRuleVersions(String ruleName) {
        List result = (List) session.createQuery( "from RuleDef where name = :name order by versionNumber" )
                                .setString( "name", ruleName ).list();
        return result;
    }

    public List listSaveHistory(ISaveHistory asset) {
        disableHistoryFilter( session );

//        List result = (List) session.createQuery( "from RuleDef where historicalId = :id" ).setLong( "id",
//                                                                                                     rule.getId().longValue() ).list();
        String query = "from " + asset.getClass().getName() + " where historicalId = :id";
        List result = (List) session.createQuery(query)
                            .setLong("id", asset.getId().longValue()).list();
        enableHistoryFilter( session );
        return result;
    }

    /* (non-Javadoc)
     * @see org.drools.repository.db.RepositoryManager#findRulesByTag(java.lang.String)
     */
    public List findRulesByTag(String tag) {
        List result = session.createQuery( "select rule from RuleDef as rule " + 
                                           "join rule.tags as tags " + 
                                           "where tags.tag = :tag" ).setString( "tag", tag ).list();
        return result;
    }

    /** We will "manually" cascade changes to the ruleset */
    public void save(Asset asset) {
        if (asset instanceof RuleSetDef) {
            cascadeChanges( (RuleSetDef) asset);
        }
        session.saveOrUpdate( asset );        
    }
    
    public void cascadeChanges(RuleSetDef ruleSet) {
        for ( Iterator iter = ruleSet.getModified().iterator(); iter.hasNext(); ) {
            Asset asset = (Asset) iter.next();
            session.saveOrUpdate(asset);
            iter.remove();
        } 
    }

    /* (non-Javadoc)
     * @see org.drools.repository.db.RepositoryManager#loadRuleSet(java.lang.String, long)
     */
    public RuleSetDef loadRuleSet(String ruleSetName,
                                  long workingVersionNumber) {
        session.clear(); //to make sure latest is loaded up, not stale
        RuleSetDef def = loadRuleSetFiltered( ruleSetName,
                                              workingVersionNumber );
        return def;
    }

    /** 
     * This is put here for internal re-use. Internally the public methods should
     * not be called.
     */
    private RuleSetDef loadRuleSetFiltered(String ruleSetName,
                                           long workingVersionNumber) {
        enableWorkingVersionFilter( workingVersionNumber,
                             session );        
        RuleSetDef def = loadRuleSetByName( ruleSetName,
                                            session );
        def.setWorkingVersionNumber( workingVersionNumber );

        disableWorkingVersionFilter( session );
        return def;
    }

    private RuleSetDef loadRuleSetByName(String ruleSetName,
                                         Session session) {
        RuleSetDef def = (RuleSetDef) session.createQuery( "from RuleSetDef where name = :name" ).setString( "name",
                                                                                                             ruleSetName ).uniqueResult();
        if (def == null) {
            throw new RepositoryException("Unable to find RuleSet with name: [" + ruleSetName + "]");
        }
        return def;
    }

    /* (non-Javadoc)
     * @see org.drools.repository.db.RepositoryManager#loadAttachment(java.lang.String)
     */
    public RuleSetAttachment loadAttachment(String name, long workingVersionNumber) {
        RuleSetAttachment at = (RuleSetAttachment) session.createQuery( "from RuleSetAttachment where name = :name and versionNumber = :versionNumber" )
                                .setString( "name", name )
                                .setLong( "versionNumber", workingVersionNumber)
                                .uniqueResult();
        return at;
    }



    /* (non-Javadoc)
     * @see org.drools.repository.db.RepositoryManager#listRuleSets()
     */
    public List listRuleSets() {
        List list = session.createQuery( "select distinct name from RuleSetDef where name is not null" ).list();
        return list;
    }

    /* (non-Javadoc)
     * @see org.drools.repository.db.RepositoryManager#delete(org.drools.repository.RuleDef)
     */
    public void delete(Asset rule) {
        session.delete( rule );
    }

    /* (non-Javadoc)
     * @see org.drools.repository.db.RepositoryManager#searchRulesByTag(java.lang.String, java.lang.String)
     */
    public List searchRulesByTag(String ruleSetName,
                                 String tag) {
        RuleSetDef def = loadRuleSetByName( ruleSetName,
                                            session );
        List list = session.createFilter( def.getRules(),
                                          "where this.tags.tag = :tag" ).setString( "tag",
                                                                                    tag ).list();
      
        return list;
    }
    
    public void checkOutRule(RuleDef rule) {
        
        String userId = getUserId();
        if (rule.isCheckedOut()) {
            throw new RepositoryException("Rule is already checked out to " + rule.getCheckedOutBy());
        }
        rule.setCheckedOut(true);
        rule.setCheckedOutBy(userId);
        session.update(rule);
    }



    private String getUserId() {
        if (this.currentUser == null) {
            throw new RepositoryException("No current user context was provided to the repository.");
        }
        String userId = this.currentUser.getName();
        return userId;
    }

    public void checkInRule(RuleDef rule) {
        String userId = getUserId();

        if (!userId.equals(rule.getCheckedOutBy())) {
            throw new RepositoryException("Unable to check in the rule, as it is currently checked out by " + rule.getCheckedOutBy());
        }
        rule.setCheckedOut(false);
        rule.setCheckedOutBy(null);
        session.update(rule);
    }    
    
    public void checkOutAttachment(RuleSetAttachment attachment) {
        String userId = getUserId();
        if (attachment.isCheckedOut()) {
            throw new RepositoryException("Rule is already checked out to " + attachment.getCheckedOutBy());
        }        
        attachment.setCheckedOut(true);
        attachment.setCheckedOutBy(userId);
        session.update(attachment);
        
    }

    public void checkInAttachment(RuleSetAttachment attachment) {
        String userId = getUserId();
        if (!userId.equals(attachment.getCheckedOutBy())) {
            throw new RepositoryException("Unable to check in the attachment, as it is currently checked out by " + attachment.getCheckedOutBy());
        }
        attachment.setCheckedOut(false);
        attachment.setCheckedOutBy(null);
        session.update(attachment);
    }
    

    public List query(String query,
                      Map parameters) {
        Query q = session.createQuery(query);
        
        for ( Iterator iter = parameters.keySet().iterator(); iter.hasNext(); ) {
            String key = (String) iter.next();
            q.setParameter(key, parameters.get(key));
        }
        
        return q.list();
    }    
    
    public void close() { /*implemented by the proxy */}    


    //////////////////////////
    // Filters follow
    //////////////////////////
    public void enableHistoryFilter(Session session) {
        session.enableFilter( "historyFilter" ).setParameter( "viewHistory",
                                                              Boolean.FALSE );
    }

    void disableHistoryFilter(Session session) {
        session.disableFilter( "historyFilter" );
    }

    void enableWorkingVersionFilter(long workingVersionNumber,
                                     Session session) {
        session.enableFilter( "workingVersionFilter" ).setParameter( "filteredVersionNumber",
                                                                     new Long( workingVersionNumber ) );
    }

    void disableWorkingVersionFilter(Session session) {
        session.disableFilter( "workingVersionFilter" );
    }

    /** Sets the current user principal for checkin/out purposes. */
    public void setCurrentUser(Principal user) {
        this.currentUser = user;
    }


}
