package org.drools.repository;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.repository.db.IVersionable;
import org.drools.repository.security.ACLResource;

/**
 * The ruleset definition contains a grouping of rules for editing/release. The
 * workingVersionNumber drives what version of rules will be included in this
 * ruleset. Changing this number will mean that different versions of ruledefs
 * are loaded etc. This number is set when you load the rulebase (it starts with
 * 1 for a new RuleSet).
 * 
 * Assets such as RuleDefs, Functions etc can be added to the RuleSet.
 * When an asset that already has an identity (eg a Rule that is 
 * already is in the repository) is added to a RuleSet, it is copied, 
 * and its version is set to to match the working version number of the RuleSet.
 * This is to stop in advertent changes effecting unrelated rulesets, in cases
 * where rules are "shared".
 * 
 * Note that as RuleDefs are taged, it is possible to load Rules based on these tags,
 * and thus build up rulesets dynamically (without using a RuleSetDef object). This
 * is possible for environments where rules are changed and managed individually,
 * rather then as part of a ruleset.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class RuleSetDef extends Asset
    implements
    Comparable, ACLResource {
    private static final long serialVersionUID = 608068118653708104L;

    private String            name;
    private MetaData          metaData;
    private Set               rules;
    private Set               tags;
    private long              workingVersionNumber;
    private Set               versionHistory;
    private Set               attachments;
    private Set               imports;
    private Set               applicationData;
    private Set               functions;
    
    private Set               modifiedAssets = new HashSet();

    
    
    public RuleSetDef(String name,
                      MetaData meta) {
        this.name = name;
        this.metaData = meta;
        this.tags = new HashSet();
        this.rules = new HashSet();
        this.attachments = new HashSet();
        this.versionHistory = new HashSet();
        this.functions = new HashSet();
        this.applicationData = new HashSet();
        this.imports = new HashSet();
        this.workingVersionNumber = 1;
        
        addNewVersionHistory("new");        
    }

    /**
     * This is not for public consumption. Use the proper constructor instead.
     */
    RuleSetDef() {
    }

    /** 
     * This returns a version history of RuleSet versions.
     * You can think of these as "major" versions. Past versions 
     * can be loaded from the repository on demand, using the versionNumber
     * from the appropriate history record that you wish to retrieve.
     */
    public Set getVersionHistory() {
        return versionHistory;
    }

    void setVersionHistory(Set versionHistory) {
        this.versionHistory = versionHistory;
    }

    /** 
     * Adds a rule to the ruleset.
     * If the rule has already been saved, then it will be copied for this ruleset.
     * 
     * If a rule is new, obviously there is no copying.
     * 
     * The owningRuleSetName property is set to the name of this ruleset.
     * 
     * @return The rule that was just added (which may be a copy).
     */
    public RuleDef addRule(RuleDef rule) {
        RuleDef returnVal = rule;
        if (rule.getId() == null) {
            rule.setOwningRuleSetName(this.name);
            addAssetToSet( rule,
                                  this.rules );
        } else {         
            //we have to make a copy
            RuleDef newRule = (RuleDef) rule.copy();  
            newRule.setOwningRuleSetName(this.name);
            addAssetToSet( newRule,
                                  this.rules );
            returnVal = newRule;
            
        }
        return returnVal;
    }

    public RuleSetDef addAttachment(RuleSetAttachment attachmentFile) {
        return addAssetToSet( attachmentFile,
                              this.attachments );
    }

    public RuleSetDef addImport(ImportDef importDef) {
        return addAssetToSet( importDef,
                              this.imports );
    }

    public RuleSetDef addApplicationData(ApplicationDataDef appData) {
        return addAssetToSet( appData,
                              this.applicationData );
    }
    
    /** 
     * Find the current working version info.
     * This includes the status of this version of the ruleset. */
    public RuleSetVersionInfo getVersionInfoWorking() {        
        for ( Iterator iter = this.versionHistory.iterator(); iter.hasNext(); ) {
            RuleSetVersionInfo info = (RuleSetVersionInfo) iter.next();
            if (info.getVersionNumber() == this.getWorkingVersionNumber()) {
                return info;
            }
        }
        return null;
    }
    
    /** 
     * Removes a rule from the current ruleset. This
     * DOES NOT delete the rule, and DOES NOT effect any other versions
     * of the ruleset. 
     * 
     * Note that assets are removed by setting their version number to
     * IVersionable.NO_VERSION (-1) so that they do not show up.
     * This may be changed so they are archived in future, and deleted.
     * 
     * The repository API has a delete(RuleDef rule) method
     * 
     */
    public void removeRule(RuleDef rule) {        
        rule.setOwningRuleSetName(null);
        removeAsset(rule);
    }
    
    public void removeFunction(FunctionDef function) {
        removeAsset(function);        
    }
    
    public void removeApplicationData(ApplicationDataDef appData) {
        removeAsset(appData);        
    }
    
    public void removeImport(ImportDef imp) {
        removeAsset(imp);
    }
    
    public void removeAttachment(RuleSetAttachment attachment) {
        removeAsset(attachment);
    }
    
    private void removeAsset(IVersionable asset) {
        asset.setVersionNumber(IVersionable.NO_VERSION);
        modifiedAssets.add(asset);
    }
    
    /** 
     * @return a rule that is associated with this ruleset by the rules name. 
     * If its not found, then it will return null.
     */
    public RuleDef findRuleByName(String name) {
        for ( Iterator iter = this.rules.iterator(); iter.hasNext(); ) {
            RuleDef rule = (RuleDef) iter.next();
            if (rule.getName().equals(name)) return rule;
        }
        return null;
        
    }

    public RuleSetDef addFunction(FunctionDef function) {
        return addAssetToSet( function,
                              this.functions );
    }
    
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }

    /**
     * This adds a new versionable asset to the specified set.
     * If an asset already exists, it will be added to this ruleset with the
     * current rulesets version (it is recommended to copy assets first).
     */
    RuleSetDef addAssetToSet(IVersionable asset,
                             Set set) {
        asset.setVersionNumber( this.workingVersionNumber );
        if ( asset.getId() == null ) {
            asset.setVersionComment( "new" );
        }
        set.add( asset );
        
        //don't forget this for cascasing !
        this.modifiedAssets.add( asset );
        return this;
    }
    
    /** 
     * Call this so the repository knows what assets to save 
     * when the time comes to sync with the repository.
     * 
     * This is only needed if you change the content of a rule for instance.
     * Adding and removing things is automatic.
     * 
     * This can be done so the ruleset temporarily remembers changes,
     * or you can save the changes one by one yourself.
     * @param A versionable asset (like a RuleDef, Function etc).
     */
    public void modify(IVersionable asset) {
        this.modifiedAssets.add( asset );
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    /** The list of rules that are currently loaded for this ruleset */
    public Set getRules() {
        return rules;
    }

    private void setRules(Set rules) {
        this.rules = rules;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public Set getTags() {
        return tags;
    }

    private void setTags(Set tags) {
        this.tags = tags;
    }

    public RuleSetDef addTag(String tag) {
        this.tags.add( new Tag( tag ) );
        return this;
    }
    
    public RuleSetDef addTag(Tag tag) {
        this.tags.add(tag);
        return this;
    }

    public long getWorkingVersionNumber() {
        return workingVersionNumber;
    }

    /**
     * This will only be set when loading the RuleSet from the repository. When
     * you load a ruleset, a version number is specified. This property is not
     * persistent, as multiple people could be working on different versions at
     * the same time.
     * 
     * DO NOT set this property MANUALLY !!!
     * 
     * @param workingVersionNumber
     */
    public void setWorkingVersionNumber(long workingVersionNumber) {
        this.workingVersionNumber = workingVersionNumber;
    }

    /**
     * This method increments the working version of the ruleset, creating a
     * brand new version. This records the event in the version history.
     * 
     * Typically you would call this method when you want to make a stable
     * version of a rule set (lock in all the related assets) and then move on
     * to an "editing" version. You can always switch back to a previous version
     * of a rulebase.
     * 
     * All rules and ruleset-attachments etc that are connected to this version
     * of the ruleset are cloned with the new workingVersionNumber.
     * 
     * This means that the previous state of the RuleSet is kept in tact (for
     * instance, as a release of rules). Rules can then be edited, removed and
     * so on without effecting any previous versions of rules and the ruleset.
     * 
     * Previous rules can be retrieved by changing the value of
     * workingVersionNumber when loading the ruleset.
     * 
     * Note that further to this, rules themselves will be versioned on save
     * (think of that versioning as "minor" versions, and this sort of ruleset
     * versions as major versions).
     * 
     * IMPORTANT: once a new version is created, the RuleSet should be saved and
     * then loaded fresh, which will hide the non working versions of the rules.
     * 
     * TODO: refactor this to only be called from the manager, and also for it to copy old permissions.
     */
    public void createNewVersion(String comment) {

        this.workingVersionNumber++;
        addNewVersionHistory( comment );

        createAndAddNewVersions( this.rules,
                                 comment,
                                 this.workingVersionNumber );

        createAndAddNewVersions( this.attachments,
                                 comment,
                                 this.workingVersionNumber );

        createAndAddNewVersions( this.functions,
                                 comment,
                                 this.workingVersionNumber );

        createAndAddNewVersions( this.applicationData,
                                 comment,
                                 this.workingVersionNumber );
        
        createAndAddNewVersions( this.imports,
                                 comment,
                                 this.workingVersionNumber );


    }
    
    private void addNewVersionHistory( String comment) {
        RuleSetVersionInfo newVersion = new RuleSetVersionInfo();
        newVersion.setVersionNumber( this.workingVersionNumber );
        newVersion.setVersionComment( comment );
        this.versionHistory.add( newVersion );
    }

    /**
     * This will work on any set of <code>IVersionable</code> objects. They
     * are copied, and then added to the original set (with null Ids). The
     * comment is added, as is the new version number.
     */
    private void createAndAddNewVersions(Set assets,
                                         String comment,
                                         long newVersionNumber) {
        // as the Ids are null, copied objects
        // will get a new identity, and have the new workingVersionNumber
        Set newVersions = new HashSet();
        for ( Iterator iter = assets.iterator(); iter.hasNext(); ) {
            IVersionable old = (IVersionable) iter.next();
            if ( old.getVersionNumber() == newVersionNumber - 1 ) {
                // we only want to clone rules that are for the version being
                // cloned
                IVersionable clone = (IVersionable) old.copy();
                clone.setVersionComment( comment );
                clone.setVersionNumber( newVersionNumber );
                newVersions.add( clone );
            }
        }
        assets.addAll( newVersions );
        modifiedAssets.addAll( newVersions );
    }

    public String toString() {
        return "{ name=" + this.name + " , workingVersionNumber=" + this.workingVersionNumber + " ruleCount:" 
                        + this.rules.size() + " }";
    }

    /** The name provides the natural ordering */
    public int compareTo(Object arg) {
        if ( arg instanceof RuleSetDef ) {
            return ((RuleSetDef) arg).name.compareTo( this.name );
        }
        return 0;
    }

    public Set getAttachments() {
        return attachments;
    }

    private void setAttachments(Set attachments) {
        this.attachments = attachments;
    }

    public Set getApplicationData() {
        return applicationData;
    }

    private void setApplicationData(Set applicationData) {
        this.applicationData = applicationData;
    }

    public Set getFunctions() {
        return functions;
    }

    private void setFunctions(Set functions) {
        this.functions = functions;
    }

    public Set getImports() {
        return imports;
    }

    private void setImports(Set imports) {
        this.imports = imports;
    }

    Set getModified() {
        return this.modifiedAssets;
    }

}
