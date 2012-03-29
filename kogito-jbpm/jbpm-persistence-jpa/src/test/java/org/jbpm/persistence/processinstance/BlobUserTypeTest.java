package org.jbpm.persistence.processinstance;

import static junit.framework.Assert.*;
import static org.drools.persistence.util.PersistenceUtil.*;
import static org.drools.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;
import gnu.trove.list.linked.TByteLinkedList;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.transaction.UserTransaction;

import org.jbpm.persistence.processinstance.objects.UserTypePropertyHolder;
import org.jbpm.persistence.session.objects.MyEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BlobUserTypeTest {

    private HashMap<String, Object> context;
    private static boolean runTests = true;

    @BeforeClass
    public static void beforeClass() {
        runTests = usingHibernate3();
    }

    @Before
    public void before() {
        if( runTests ) { 
            context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME, false);
        }
    }

    @After
    public void after() {
        if( runTests ) { 
            cleanUp(context);
        }
    }

    @Test
    public void testHolderWithBlobUserType() throws Exception {
        if( ! runTests ) { 
            return;
        }
        
        {
            MyEntity test = new MyEntity();
            EntityManager em = openTx();
            em.persist(test);
            closeTx(em);
            System.out.println("test: " + test.getId());
        }

        UserTypePropertyHolder holder = new UserTypePropertyHolder();
        String stringTest = "This is a test string.";
        byte[] blob = stringTest.getBytes();
        holder.setBlobInfo(blob);
        
        EntityManager em = openTx();

        em.persist(holder);

        closeTx(em);

        int id = holder.getId();
        assertTrue(id > 0);
    }

    private EntityManager openTx() throws Exception {
        // Persist variable
        UserTransaction ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        ut.begin();

        EntityManagerFactory emf = (EntityManagerFactory) context.get(ENTITY_MANAGER_FACTORY);
        EntityManager em = emf.createEntityManager();

        em.setFlushMode(FlushModeType.COMMIT);
        em.joinTransaction();

        return em;
    }

    private void closeTx(EntityManager em) throws Exception {
        UserTransaction ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        em.close();
        ut.commit();
    }

    private static boolean usingHibernate3() {
        boolean hibernateCreateBlobMethodExists = false;
        try {
            Class<?> LobHelperClass = Class.forName("org.hibernate.Hibernate");
            byte[] byteArray = new byte[0];
            Method createBlobMethod = LobHelperClass.getMethod("createBlob", byteArray.getClass());
            hibernateCreateBlobMethodExists = true;
        } catch (Exception e) {
            // do nothing..
        }
        return hibernateCreateBlobMethodExists;
    }
}
