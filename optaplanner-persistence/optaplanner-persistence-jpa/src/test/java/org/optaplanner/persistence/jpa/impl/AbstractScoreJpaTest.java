package org.optaplanner.persistence.jpa.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.kie.test.util.db.PersistenceUtil;
import org.optaplanner.core.api.score.Score;

public abstract class AbstractScoreJpaTest {

    private static final String DATASOURCE_PROPERTIES = "/datasource.properties";

    protected Map<String, Object> context;
    protected EntityManagerFactory entityManagerFactory;
    protected TransactionManager transactionManager;

    @BeforeEach
    public void setUp() throws Exception {
        createH2DatabaseIfNeeded();
        context = PersistenceUtil.setupWithPoolingDataSource("org.optaplanner.persistence.jpa.test");
        entityManagerFactory = (EntityManagerFactory) context.get(PersistenceUtil.ENTITY_MANAGER_FACTORY);
        transactionManager = InitialContext.doLookup("java:comp/TransactionManager");
    }

    private void createH2DatabaseIfNeeded() throws Exception {
        Properties databaseProperties = readDatabaseProperties();
        String jdbcUrl = databaseProperties.getProperty("url", "");
        if (jdbcUrl.startsWith("jdbc:h2")) {
            final String regex = ":\\/\\/\\w+\\/(.*)";

            final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            final Matcher matcher = pattern.matcher(jdbcUrl);

            if (!matcher.find()) {
                throw new IllegalStateException("Unable to detect H2 database name. Cannot setup the test environment.");
            }

            String embeddedH2Url = "jdbc:h2:" + matcher.group(1);
            try (Connection connection = DriverManager.getConnection(embeddedH2Url)) {
                // Opening a connection to an embedded H2 creates a database that can be used in the Server mode later.
            }
        }
    }

    private Properties readDatabaseProperties() {
        Properties properties = new Properties();
        try (InputStream propertiesInputStream = PersistenceUtil.class.getResourceAsStream(DATASOURCE_PROPERTIES)) {
            if (propertiesInputStream == null) {
                throw new IllegalStateException("Unable to locate " + DATASOURCE_PROPERTIES
                        + ". Cannot setup the test environment.");
            }
            properties.load(propertiesInputStream);
            return properties;
        } catch (IOException ioe) {
            throw new IllegalStateException("Unable to open " + DATASOURCE_PROPERTIES
                    + ". Cannot setup the test environment.");
        }
    }

    @AfterEach
    public void tearDown() {
        PersistenceUtil.cleanUp(context);
    }

    protected <Score_ extends Score<Score_>, E extends AbstractTestJpaEntity<Score_>> Long persistAndAssert(E jpaEntity) {
        try {
            transactionManager.begin();
            EntityManager em = entityManagerFactory.createEntityManager();
            em.persist(jpaEntity);
            transactionManager.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicRollbackException
                | HeuristicMixedException e) {
            throw new RuntimeException("Transaction failed.", e);
        }
        Long id = jpaEntity.getId();
        assertThat(id).isNotNull();
        return id;
    }

    @SafeVarargs
    protected final <Score_ extends Score<Score_>, E extends AbstractTestJpaEntity<Score_>> void persistAndMerge(
            E jpaEntity, Score_... newScores) {
        Long id = persistAndAssert(jpaEntity);
        Class<? extends AbstractTestJpaEntity> jpaEntityClass = jpaEntity.getClass();
        Score_ oldScore = jpaEntity.getScore();
        for (Score_ newScore : newScores) {
            findAssertAndChangeScore(jpaEntityClass, id, oldScore, newScore);
            findAndAssert(jpaEntityClass, id, newScore);
            oldScore = newScore;
        }
    }

    protected <Score_ extends Score<Score_>, E extends AbstractTestJpaEntity<Score_>> void findAssertAndChangeScore(
            Class<E> jpaEntityClass, Long id, Score_ oldScore, Score_ newScore) {
        try {
            transactionManager.begin();
            EntityManager em = entityManagerFactory.createEntityManager();
            E jpaEntity = em.find(jpaEntityClass, id);
            em.persist(jpaEntity);
            assertThat(jpaEntity.getScore()).isEqualTo(oldScore);
            jpaEntity.setScore(newScore);
            jpaEntity = em.merge(jpaEntity);
            transactionManager.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicRollbackException
                | HeuristicMixedException e) {
            throw new RuntimeException("Transaction failed.", e);
        }
    }

    protected <Score_ extends Score<Score_>, E extends AbstractTestJpaEntity<Score_>> void findAndAssert(
            Class<E> jpaEntityClass, Long id, Score_ score) {
        try {
            transactionManager.begin();
            EntityManager em = entityManagerFactory.createEntityManager();
            E jpaEntity = em.find(jpaEntityClass, id);
            assertThat(jpaEntity.getScore()).isEqualTo(score);
            transactionManager.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException
                | HeuristicRollbackException e) {
            throw new RuntimeException("Transaction failed.", e);
        }
    }

    @MappedSuperclass
    protected static abstract class AbstractTestJpaEntity<Score_ extends Score<Score_>> {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        protected Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public abstract Score_ getScore();

        public abstract void setScore(Score_ score);

    }
}
