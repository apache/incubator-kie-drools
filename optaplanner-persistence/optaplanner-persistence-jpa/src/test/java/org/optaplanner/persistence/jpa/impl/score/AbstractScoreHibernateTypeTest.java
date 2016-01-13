/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.persistence.jpa.impl.score;

import org.junit.After;
import org.junit.Before;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.persistence.jpa.util.PersistenceUtil;

import javax.persistence.*;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public abstract class AbstractScoreHibernateTypeTest {

    private HashMap<String, Object> context;
    protected EntityManagerFactory entityManagerFactory;
//    private JtaTransactionManager txm;
//    private boolean useTransactions = false;
//    private boolean locking;

    @Before
    public void setUp() throws Exception {
        context = PersistenceUtil.setupWithPoolingDataSource(PersistenceUtil.ENTITY_MANAGER_FACTORY);
        entityManagerFactory = (EntityManagerFactory) context.get(PersistenceUtil.ENTITY_MANAGER_FACTORY);

//        if( useTransactions() ) {
//            useTransactions = true;
//            Environment env = createEnvironment(context);
//            Object tm = env.get( EnvironmentName.TRANSACTION_MANAGER );
//            this.txm = new JtaTransactionManager( env.get( EnvironmentName.TRANSACTION ),
//                    env.get( EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY ),
//                    tm );
//        }
    }

    @After
    public void tearDown() throws Exception {
        PersistenceUtil.cleanUp(context);
    }

    protected <S extends Score, E extends AbstractTestJpaEntity<S>> Long persistAndAssert(E jpaEntity) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(jpaEntity);
        transaction.commit();
        Long id = jpaEntity.getId();
        assertNotNull(id);
        return id;
    }

    protected <S extends Score, E extends AbstractTestJpaEntity<S>> void findAssertAndChangeScore(
            Class<E> jpaEntityClass, Long id, S oldScore, S newScore) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        E jpaEntity = entityManager.find(jpaEntityClass, id);
        assertEquals(oldScore, jpaEntity.getScore());
        jpaEntity.setScore(newScore);
        jpaEntity = entityManager.merge(jpaEntity);
        transaction.commit();
    }

    protected <S extends Score, E extends AbstractTestJpaEntity<S>> void findAndAssert(
            Class<E> jpaEntityClass, Long id, S score) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        E jpaEntity = entityManager.find(jpaEntityClass, id);
        assertEquals(score, jpaEntity.getScore());
        transaction.commit();
    }

    @MappedSuperclass
    protected static abstract class AbstractTestJpaEntity<S extends Score> {

        protected Long id;

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        @Transient
        public abstract S getScore();
        public abstract void setScore(S score);

    }
}
