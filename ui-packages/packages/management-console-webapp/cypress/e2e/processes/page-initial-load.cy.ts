/**
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
describe('Process List Page test', () => {
  beforeEach('visit page', () => {
    cy.visit('http://localhost:9000/ProcessInstances');
  });
  describe('Check page structure', () => {
    it('Check page attributes', () => {
      cy.get("[data-ouia-page-type='process-instances']")
        .should('exist')
        .and('not.have.attr', 'data-ouia-page-object-id');
    });
    it('Check header', () => {
      cy.get('[data-ouia-header=true]')
        .should('exist')
        .within(($header) => {
          // eslint-disable-next-line cypress/require-data-selectors
          cy.get('img')
            .should('have.attr', 'alt')
            .should('contains', 'Management Console');
          cy.ouiaType('page-toolbar').should('exist');
        });
    });
    it('Check navigation panel', () => {
      cy.get('[data-ouia-navigation=true]')
        .ouiaType('PF4/Nav')
        .ouiaId('navigation-list')
        .should('exist')
        .within(($navigation) => {
          cy.ouiaNavigationName().should('not.be.empty').and('have.length', 2);
          cy.ouiaNavigationName('process-instances')
            .should('exist')
            .should('have.attr', 'class')
            .should('contains', 'current');
          cy.ouiaNavigationName('jobs-management')
            .should('exist')
            .should('have.attr', 'class')
            .should('not.contains', 'current');
        });
    });
    it('Check main content', () => {
      cy.get('[data-ouia-main=true]')
        .should('exist')
        .within(($main) => {
          cy.ouiaType('page-section-header')
            .should('exist')
            .within(($header) => {
              cy.ouiaType('page-title')
                .should('exist')
                .and('contain.text', 'Process Instances');
              cy.ouiaType('PF4/Breadcrumb')
                .should('exist')
                .within(($nav) => {
                  // eslint-disable-next-line cypress/require-data-selectors
                  cy.get('li')
                    .should('have.length', 2)
                    .eq(0)
                    .should('have.text', 'Home');
                  // eslint-disable-next-line cypress/require-data-selectors
                  cy.get('li')
                    .should('have.length', 2)
                    .eq(1)
                    .should('have.text', 'Processes');
                });
            });
          cy.ouiaType('process-list')
            .should('exist')
            .within(($page) => {
              cy.ouiaType('process-list-toolbar').should('be.visible');
              cy.ouiaType('process-list-table').should('be.visible');
              cy.ouiaType('load-more').scrollIntoView().should('be.visible');
            });
        });
    });
  });
  describe('Data presentation', () => {
    it('Table Layout', () => {
      cy.ouiaType('process-list').within(($page) => {
        cy.ouiaType('process-list-table')
          .ouiaSafe()
          .ouiaType('process-list-row')
          .should('have.length', 10)
          .ouiaId('8035b580-6ae4-4aa8-9ec0-e18e19809e0b1')
          .within(($item) => {
            cy.ouiaType('process-list-cell').then(($cells) => {
              cy.ouiaId('__toggle').should('be.visible');
              cy.ouiaId('__select')
                .should('be.visible')
                .find('input')
                .should('be.enabled');
              cy.ouiaId('id').should('be.visible');
              cy.wrap($cells)
                .ouiaId('status')
                .should('be.visible')
                .and('contain.text', 'Active');
              cy.wrap($cells).ouiaId('created').should('be.visible');
              cy.wrap($cells).ouiaId('last update').should('be.visible');
              cy.wrap($cells).ouiaId('__actions').should('be.visible');
            });
          });
      });
    });
    it('Process-list-item expanded.', () => {
      cy.ouiaType('process-list').within(($page) => {
        cy.ouiaType('load-more')
          .scrollIntoView()
          .should('be.visible')
          .ouiaType('PF4/Dropdown')
          .click();
        cy.ouiaType('process-list-table')
          .ouiaSafe()
          .within(($table) => {
            cy.ouiaId(
              '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
              'process-list-row-expanded'
            )
              .scrollIntoView()
              .should('not.be.visible');
            cy.ouiaId(
              '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
              'process-list-row'
            )
              .scrollIntoView()
              .should('be.visible')
              .within(($row) => {
                cy.ouiaId('__toggle', 'process-list-cell')
                  .scrollIntoView()
                  .should('be.visible')
                  .ouiaType('PF4/Button')
                  .click();
              });
            cy.ouiaId(
              '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
              'process-list-row-expanded'
            )
              .scrollIntoView()
              .should('be.visible')
              .ouiaId(
                '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
                'process-list-child-table'
              )
              .should('be.visible')
              .within(($childTable) => {
                // 1 header and 4 items, better selectors would require major refactoring of process-list/ProcessListChildTable component.
                cy.ouiaType('PF4/TableRow').should('have.length', 5);
              });
          });
      });
    });
    it('Load More', () => {
      cy.ouiaType('process-list').within(($page) => {
        cy.ouiaType('process-list-table')
          .ouiaSafe()
          .ouiaType('process-list-row')
          .should('have.length', 10);
        cy.ouiaType('load-more')
          .scrollIntoView()
          .should('be.visible')
          .ouiaType('PF4/Dropdown')
          .click();
        cy.ouiaType('process-list-table')
          .ouiaType('process-list-row')
          .should('have.length', 13);
      });
    });
  });
});
