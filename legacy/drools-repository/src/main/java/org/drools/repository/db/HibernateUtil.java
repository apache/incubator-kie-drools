package org.drools.repository.db;



import org.drools.repository.ApplicationDataDef;
import org.drools.repository.FunctionDef;
import org.drools.repository.ImportDef;
import org.drools.repository.RuleDef;
import org.drools.repository.RuleSetAttachment;
import org.drools.repository.RuleSetDef;
import org.drools.repository.RuleSetVersionInfo;
import org.drools.repository.Tag;
import org.drools.repository.security.AssetPermission;
import org.drools.repository.security.RepositoryUser;
import org.drools.repository.security.PermissionGroup;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * This is the central config point for hibernate.
 * 
 * The usual infamous hibernate helper, with a few tweaks.
 * I have made the sessionFactory non final to allow reconfiguration if necessary.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class HibernateUtil {

    public static final String DROOLS_REPOSITORY_CONFIG = "drools-repository-db.cfg.xml";
    private static SessionFactory sessionFactory;

    static {
        try {
            configureSessionFactory();
        }
        catch ( Throwable ex ) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println( "Initial SessionFactory creation failed." + ex );
            throw new ExceptionInInitializerError( ex );
        }
    }

    /**
     * This will setup the session factory with paramaters 
     * that are available.
     * May be called again to re-establish the factory if needed.
     */
    public static void configureSessionFactory() {
        Configuration cfg = getConfiguration();        
        sessionFactory = cfg.buildSessionFactory();
    }

    /** Return the hibernate configuration as it stands */
    public static Configuration getConfiguration() {
        Configuration cfg = new Configuration();            
        cfg.setInterceptor( new StoreInterceptor() );
        
        registerPersistentClasses( cfg );
        cfg.configure(DROOLS_REPOSITORY_CONFIG);
        return cfg;
    }

    /**
     * Use class based registration for refactor-friendly goodness.
     */
    private static void registerPersistentClasses(Configuration cfg) {
        cfg
            .addClass(ApplicationDataDef.class)
            .addClass(FunctionDef.class)
            .addClass(RuleDef.class)
            .addClass(Tag.class)
            .addClass(RuleSetDef.class)
            .addClass(RuleSetAttachment.class)
            .addClass(RuleSetVersionInfo.class)
            .addClass(ImportDef.class)
            .addClass(AssetPermission.class)
            .addClass(PermissionGroup.class)
            .addClass(RepositoryUser.class)
            .addClass(RepositoryConfig.class);
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}
