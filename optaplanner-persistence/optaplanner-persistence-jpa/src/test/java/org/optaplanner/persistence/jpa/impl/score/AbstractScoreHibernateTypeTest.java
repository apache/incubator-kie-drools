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
import javax.transaction.TransactionManager;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public abstract class AbstractScoreHibernateTypeTest {

    private Map<String, Object> context;
    protected EntityManagerFactory entityManagerFactory;
    private TransactionManager transactionManager;

    @Before
    public void setUp() throws Exception {
        context = PersistenceUtil.setupWithPoolingDataSource(PersistenceUtil.ENTITY_MANAGER_FACTORY);
        entityManagerFactory = (EntityManagerFactory) context.get(PersistenceUtil.ENTITY_MANAGER_FACTORY);
        transactionManager = (TransactionManager) context.get(PersistenceUtil.TRANSACTION_MANAGER);
    }

    @After
    public void tearDown() throws Exception {
        PersistenceUtil.cleanUp(context);
    }

    protected <S extends Score, E extends AbstractTestJpaEntity<S>> Long persistAndAssert(E jpaEntity) {
        try {
            transactionManager.begin();
            EntityManager em = entityManagerFactory.createEntityManager();
            em.persist(jpaEntity);
            transactionManager.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage(), ex);
        }
        Long id = jpaEntity.getId();
        assertNotNull(id);
        return id;

    }

    protected <S extends Score, E extends AbstractTestJpaEntity<S>> void findAssertAndChangeScore(
            Class<E> jpaEntityClass, Long id, S oldScore, S newScore) {
        try {
            transactionManager.begin();
            EntityManager em = entityManagerFactory.createEntityManager();
            E jpaEntity = em.find(jpaEntityClass, id);
            em.persist(jpaEntity);
            assertEquals(oldScore, jpaEntity.getScore());
            jpaEntity.setScore(newScore);
            jpaEntity = em.merge(jpaEntity);
            transactionManager.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    protected <S extends Score, E extends AbstractTestJpaEntity<S>> void findAndAssert(
            Class<E> jpaEntityClass, Long id, S score) {
        try {
            transactionManager.begin();
            EntityManager em = entityManagerFactory.createEntityManager();
            E jpaEntity = em.find(jpaEntityClass, id);
            assertEquals(score, jpaEntity.getScore());
            transactionManager.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage(), ex);
        }
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
