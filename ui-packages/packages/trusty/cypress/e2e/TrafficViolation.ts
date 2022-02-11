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
let reqId;
let auditDetailsUrl;

describe('Traffic Violation', () => {
  before(() => {
    cy.request({
      method: 'POST',
      url: 'http://localhost:8080/Traffic Violation',
      headers: {
        'Content-Type': 'application/json'
      },
      body: {
        Driver: { State: 'aa', City: 'bb', Age: '25', Points: '13' },
        Violation: { Type: 'speed', 'Actual Speed': '105', 'Speed Limit': '80' }
      }
    }).then(response => {
      // response.body is automatically serialized into JSON
      expect(response.body).to.not.be.null;
      expect(response.body).to.have.property(
        'Should the driver be suspended?',
        'No'
      );
      expect(response.body).to.have.property('Fine');
      expect(response.body.Fine).to.have.property('Points', 3);
      expect(response.body.Fine).to.have.property('Amount', 500);
      expect(response.headers).to.have.property('x-kogito-execution-id');
      reqId = response.headers['x-kogito-execution-id'];
      auditDetailsUrl = `/audit/decision/${reqId}/outcomes`;
    });
  });

  it('open Audit Details', () => {
    cy.visit('/');
    cy.ouiaId('refresh-button').click();
    cy.ouiaId(reqId, 'PF4/TableRow').within(() => {
      cy.ouiaId('status', 'execution-status').should('have.text', 'Completed');
      cy.ouiaId('show-detail', 'link').click();
    });
    cy.url().should('contains', auditDetailsUrl);
  });

  describe('verify decision results', () => {
    beforeEach(() => {
      cy.visit(auditDetailsUrl);
    });

    it('Audit Details header', () => {
      const title = 'Execution #' + String(reqId).substring(0, 8);

      cy.ouiaType('PF4/Breadcrumb')
        .should('exist')
        .within($nav => {
          cy.wrap($nav)
            .find('li>a')
            .should('have.length', 3)
            .within($items => {
              expect($items.eq(0)).have.text('Audit investigation');
              expect($items.eq(1)).have.text(title);
              expect($items.eq(2)).have.text('Outcomes');
            });
        });
      cy.ouiaId('execution-header').within(() => {
        cy.ouiaId('title')
          .should('be.visible')
          .should('have.text', title);
        cy.ouiaId('status')
          .should('be.visible')
          .should('have.text', 'Completed');
      });
      cy.ouiaId('nav-audit-detail')
        .should('exist')
        .within($nav => {
          cy.wrap($nav)
            .find('li>a')
            .should('have.length', 5)
            .within($items => {
              expect($items.eq(0)).have.text('Outcomes');
              expect($items.eq(0)).have.class('pf-m-current');
              expect($items.eq(1)).have.text('Outcomes details');
              expect($items.eq(2)).have.text('Input data');
              expect($items.eq(3)).have.text('Model lookup');
              /*
               * TODO: FAI-665
               */
              expect($items.eq(4)).to.contain('Counterfactual analysis');
            });
        });
      cy.get('section.outcomes').should('be.visible');
    });

    it('Outcomes', () => {
      cy.ouiaId('outcomes-gallery', 'outcomes')
        .ouiaId('Fine', 'PF4/Card')
        .ouiaType('outcome-property')
        .within($items => {
          expect($items).to.have.length(2);
          cy.wrap($items[0])
            .ouiaId('Points', 'property-name')
            .should('have.text', 'Points:');
          cy.wrap($items[0])
            .ouiaId('Points', 'property-value')
            .should('have.text', '3');
          cy.wrap($items[1])
            .ouiaId('Amount', 'property-name')
            .should('have.text', 'Amount:');
          cy.wrap($items[1])
            .ouiaId('Amount', 'property-value')
            .should('have.text', '500');
        });
      cy.ouiaId('outcomes-gallery', 'outcomes')
        .ouiaId('Should the driver be suspended?', 'PF4/Card')
        .ouiaType('simple-property-value')
        .within($items => {
          expect($items).to.have.length(1);
          cy.wrap($items[0]).should('have.text', 'No');
        });
    });

    it('Input Data - Violation', () => {
      cy.get('ul.pf-c-nav__list>li:contains(Input)').click();
      cy.get('div.input-browser button:contains(Violation)').click();
      cy.get(
        'ul.input-browser__data-list>li:contains(Type) div.pf-c-data-list__cell:nth-child(2)'
      ).should($value => {
        expect($value).to.have.text('speed');
      });

      cy.get(
        "ul.input-browser__data-list>li:contains('Speed Limit') div.pf-c-data-list__cell:nth-child(2)"
      ).should($value => {
        expect($value).to.have.text('80');
      });

      cy.get(
        "ul.input-browser__data-list>li:contains('Actual Speed') div.pf-c-data-list__cell:nth-child(2)"
      ).should($value => {
        expect($value).to.have.text('105');
      });

      cy.get(
        'ul.input-browser__data-list>li:contains(Code) div.pf-c-data-list__cell:nth-child(2)'
      ).should($value => {
        expect($value).to.have.text('Null');
      });

      cy.get(
        'ul.input-browser__data-list>li:contains(Date) div.pf-c-data-list__cell:nth-child(2)'
      ).should($value => {
        expect($value).to.have.text('Null');
      });
    });

    it('Input Data - Driver', () => {
      cy.get('ul.pf-c-nav__list>li:contains(Input)').click();
      cy.get('div.input-browser button:contains(Driver)').click();
      cy.get(
        'ul.input-browser__data-list>li:contains(Points) div.pf-c-data-list__cell:nth-child(2)'
      ).should($value => {
        expect($value).to.have.text('13');
      });
      cy.get(
        'ul.input-browser__data-list>li:contains(State) div.pf-c-data-list__cell:nth-child(2)'
      ).should($value => {
        expect($value).to.have.text('aa');
      });

      cy.get(
        'ul.input-browser__data-list>li:contains(City) div.pf-c-data-list__cell:nth-child(2)'
      ).should($value => {
        expect($value).to.have.text('bb');
      });

      cy.get(
        'ul.input-browser__data-list>li:contains(Age) div.pf-c-data-list__cell:nth-child(2)'
      ).should($value => {
        expect($value).to.have.text('25');
      });

      cy.get(
        'ul.input-browser__data-list>li:contains(Name) div.pf-c-data-list__cell:nth-child(2)'
      ).should($value => {
        expect($value).to.have.text('Null');
      });
    });
  });
});
