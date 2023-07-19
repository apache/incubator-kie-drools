/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

describe('Audit Details Header', () => {
  beforeEach(() => {
    cy.visit('/');
  });

  it(`Single Outcome`, () => {
    checkAuditHeader('unsuppor-ted--all--stri-913ac955b90b', [
      'Outcome',
      'Input data',
      'Model lookup',
      'Counterfactual analysis'
    ]);
  });

  it(`Outcomes`, () => {
    checkAuditHeader('strings--4979-4e03-8692-0ec45cfca6ac', [
      'Outcomes',
      'Outcomes details',
      'Input data',
      'Model lookup',
      'Counterfactual analysis'
    ]);
  });
});

function checkAuditHeader(reqId: string, thirdNavBar: string[]) {
  cy.ouiaId('exec-table', 'PF4/Table')
    .ouiaId(reqId, 'PF4/TableRow', { timeout: 20000 })
    .ouiaId('show-detail', 'link')
    .click();
  const title = 'Execution #' + String(reqId).substring(0, 8);

  cy.ouiaType('PF4/Breadcrumb')
    .should('exist')
    .within(($nav) => {
      cy.wrap($nav)
        .find('li>a')
        .should('have.length', 3)
        .within(($items) => {
          expect($items.eq(0)).have.text('Audit investigation');
          expect($items.eq(1)).have.text(title);
          expect($items.eq(2)).have.text(thirdNavBar[0]);
        });
    });
  cy.ouiaId('execution-header').within(() => {
    cy.ouiaId('title').should('be.visible').should('have.text', title);
    cy.ouiaId('status').should('be.visible').should('have.text', 'Completed');
  });

  cy.ouiaId('nav-audit-detail')
    .should('exist')
    .within(($nav) => {
      cy.wrap($nav)
        .find('li>a')
        .should('have.length', thirdNavBar.length)
        .within(($item) => {
          for (let value = 0; value < thirdNavBar.length; value++) {
            expect($item.eq(value)).have.text(thirdNavBar[value]);
            expect($item.eq(value)).be.visible;
          }
        });
    });
  cy.get('section.outcomes').should('be.visible');
}
