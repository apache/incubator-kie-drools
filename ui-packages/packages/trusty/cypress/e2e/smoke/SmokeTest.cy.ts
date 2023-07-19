/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

describe('E2E - smoke test', () => {
  beforeEach(() => {
    cy.visit('/');
  });

  it('menu button is visible', () => {
    cy.get('button#nav-toggle').should('be.visible');
  });

  it('Menu button shows/hides options', () => {
    cy.ouiaId('audit-item').then(($item) => {
      //toggle menu side bar - both directions
      if ($item.is(':visible')) {
        cy.get('button#nav-toggle').click();
        cy.ouiaId('audit-item').should('not.be.visible');
        cy.get('button#nav-toggle').click();
        cy.ouiaId('audit-item').should('be.visible');
      } else {
        cy.get('button#nav-toggle').click();
        cy.ouiaId('audit-item').should('be.visible');
        cy.get('button#nav-toggle').click();
        cy.ouiaId('audit-item').should('not.be.visible');
      }
    });
  });

  it('Search is visible', () => {
    cy.ouiaId('search-input').should('be.visible');
    cy.ouiaId('search-input').type('someId');
    cy.ouiaId('search-button').should('be.visible');
  });

  it('Refresh is visible', () => {
    cy.ouiaId('refresh-button').should('be.visible');
  });

  it('Date inputs are visible', () => {
    cy.get('#audit-from-date+input').should('be.visible');
    cy.get('#audit-to-date+input').should('be.visible');
  });

  it('Top paging is visible', () => {
    cy.ouiaId('top-pagination').should('be.visible');
  });
});
