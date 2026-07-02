/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.persistence.jpa.impl;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Persistence;

import org.junit.jupiter.api.BeforeEach;
import org.optaplanner.core.api.score.Score;

public abstract class AbstractScoreJpaTest {

    @BeforeEach
    void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("test");
    }

    EntityManagerFactory entityManagerFactory;

    protected <Score_ extends Score<Score_>, E extends AbstractTestJpaEntity<Score_>> Long persistAndAssert(E jpaEntity) {
        try {
            EntityManager em = entityManagerFactory.createEntityManager();
            // To avoid importing javax.transaction.*, which OpenRewrite fails to migrate.
            em.getTransaction().begin();
            em.persist(jpaEntity);
            em.getTransaction().commit();
        } catch (Exception e) {
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
            EntityManager em = entityManagerFactory.createEntityManager();
            // To avoid importing javax.transaction.*, which OpenRewrite fails to migrate.
            em.getTransaction().begin();
            E jpaEntity = em.find(jpaEntityClass, id);
            em.persist(jpaEntity);
            assertThat(jpaEntity.getScore()).isEqualTo(oldScore);
            jpaEntity.setScore(newScore);
            jpaEntity = em.merge(jpaEntity);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Transaction failed.", e);
        }
    }

    protected <Score_ extends Score<Score_>, E extends AbstractTestJpaEntity<Score_>> void findAndAssert(
            Class<E> jpaEntityClass, Long id, Score_ score) {
        try {
            EntityManager em = entityManagerFactory.createEntityManager();
            // To avoid importing javax.transaction.*, which OpenRewrite fails to migrate.
            em.getTransaction().begin();
            E jpaEntity = em.find(jpaEntityClass, id);
            assertThat(jpaEntity.getScore()).isEqualTo(score);
            em.getTransaction().commit();
        } catch (Exception e) {
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
