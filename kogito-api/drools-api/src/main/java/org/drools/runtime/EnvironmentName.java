package org.drools.runtime;

public class EnvironmentName {
    public static final String TRANSACTION_MANAGER                  = "drools.transaction.TransactionManager";
    public static final String TRANSACTION_SYNCHRONIZATION_REGISTRY = "drools.transaction.TransactionSynchronizationRegistry";
    public static final String TRANSACTION                          = "drools.transaction.Transaction";

    public static final String ENTITY_MANAGER_FACTORY               = "drools.persistence.jpa.EntityManagerFactory";
    public static final String CMD_SCOPED_ENTITY_MANAGER            = "drools.persistence.jpa.CmdScopedEntityManager";
    public static final String APP_SCOPED_ENTITY_MANAGER            = "drools.persistence.jpa.AppScopedEntityManager";

    public static final String OBJECT_MARSHALLING_STRATEGIES        = "drools.marshalling.ObjectMarshallingStrategies";
    public static final String GLOBALS                              = "drools.Globals";
    public static final String CALENDARS                            = "org.drools.time.Calendars";
    public static final String DATE_FORMATS                         = "org.drools.build.DateFormats";
}
