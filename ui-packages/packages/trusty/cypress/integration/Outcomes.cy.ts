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
describe('Outcomes - verify mocked data', () => {
  beforeEach(() => {
    cy.visit('/');
  });

  it('Simple outcome', () => {
    cy.ouiaId('exec-table', 'PF4/Table')
      .ouiaId('ac6d2f5f-4eba-4557-9d78-22b1661a876a', 'PF4/TableRow', {
        timeout: 20000
      })
      .ouiaId('show-detail', 'link')
      .click();
    cy.ouiaId('outcomes-gallery', 'outcomes').within(() => {
      cy.ouiaId('Mortgage Approval', 'PF4/Card').within(() => {
        cy.ouiaId('card-title', 'title').should(
          'has.text',
          'Mortgage Approval'
        );
        cy.ouiaId('Mortgage Approval', 'simple-property-value').should(
          'has.text',
          'Null'
        );
        cy.ouiaId('view-detail', 'PF4/Button').should('be.visible');
      });
      cy.ouiaId('Risk Score', 'PF4/Card').within(() => {
        cy.ouiaId('card-title', 'title').should('has.text', 'Risk Score');
        cy.ouiaId('Risk Score', 'simple-property-value').should(
          'has.text',
          '21.7031851958099'
        );
        cy.ouiaId('view-detail', 'PF4/Button').should('be.visible');
      });
    });
  });
});
