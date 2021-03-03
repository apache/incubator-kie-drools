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
        .within($header => {
          // eslint-disable-next-line cypress/require-data-selectors
          cy.get('img')
            .should('have.attr', 'alt')
            .should('contains', 'Management Console');
          cy.ouiaComponentType('page-toolbar').should('exist');
        });
    });
    it('Check navigation panel', () => {
      cy.get('[data-ouia-navigation=true]')
        .ouiaComponentType('PF4/Nav')
        .ouiaComponentId('navigation-list')
        .should('exist')
        .within($navigation => {
          cy.ouiaNavigationName()
            .should('not.be.empty')
            .and('have.length', 3);
          cy.ouiaNavigationName('process-instances')
            .should('exist')
            .should('have.attr', 'class')
            .should('contains', 'current');
          cy.ouiaNavigationName('domain-explorer')
            .should('exist')
            .should('have.attr', 'class')
            .should('not.contains', 'current');
          cy.ouiaNavigationName('jobs-management')
            .should('exist')
            .should('have.attr', 'class')
            .should('not.contains', 'current');
        });
    });
    it('Check main content', () => {
      cy.get('[data-ouia-main=true]')
        .should('exist')
        .ouiaComponentType('process-list-page')
        .should('exist')
        .within($page => {
          cy.ouiaComponentType('page-title')
            .should('exist')
            .and('contain.text', 'Process Instances');
          cy.ouiaComponentType('PF4/Breadcrumb')
            .should('exist')
            .within($nav => {
              // eslint-disable-next-line cypress/require-data-selectors
              cy.get('li')
                .should('have.length', 2)
                .within($items => {
                  expect($items.eq(0)).to.contain.text('Home');
                  expect($items.eq(1)).to.contain.text('Process instances');
                });
            });
          cy.ouiaComponentType('process-list-toolbar').should('be.visible');
          cy.ouiaComponentType('process-list-table').should('be.visible');
          cy.ouiaComponentType('load-more')
            .scrollIntoView()
            .should('be.visible');
        });
    });
  });
  describe('Data presentation', () => {
    it('Table Layout', () => {
      cy.ouiaComponentType('process-list-page').within($page => {
        cy.ouiaComponentType('process-list-table')
          .ouiaComponentType('process-list-table-item')
          .should('have.length', 10)
          .eq(0)
          .within($item => {
            cy.ouiaComponentType('datalist-expand-toggle').should('be.visible');
            cy.ouiaComponentType('datalist-checkbox')
              .should('be.visible')
              .and('be.enabled');
            cy.ouiaComponentType('datalist-cell').then($cells => {
              cy.wrap($cells)
                .ouiaComponentId('endpoint')
                .should('be.visible');
              cy.wrap($cells)
                .ouiaComponentId('status')
                .should('be.visible')
                .and('contain.text', 'Active');
              cy.wrap($cells)
                .ouiaComponentId('created')
                .should('be.visible');
              cy.wrap($cells)
                .ouiaComponentId('updated')
                .should('be.visible');
            });
          });
      });
    });
    it('Process-list-item expanded.', () => {
      cy.ouiaComponentType('process-list-page').within($page => {
        cy.ouiaComponentType('process-list-table')
          .ouiaComponentType('process-list-table-item')
          .ouiaComponentId('process-8035b580-6ae4-4aa8-9ec0-e18e19809e0b')
          .within($item => {
            cy.ouiaComponentType('process-list-table-item-expand').should(
              'not.be.visible'
            );
            cy.ouiaComponentType('datalist-expand-toggle')
              .scrollIntoView()
              .should('be.visible')
              .click({ force: true });
            cy.ouiaComponentType('process-list-table-item-expand')
              .should('be.visible')
              .within($expanded => {
                cy.ouiaComponentType('process-list-table-item').should(
                  'have.length',
                  4
                );
                cy.ouiaComponentId(
                  'process-c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
                  'process-list-table-item'
                )
                  .should('be.visible')
                  .within($it => {
                    cy.ouiaComponentType('datalist-expand-toggle').should(
                      'not.exist'
                    );
                    cy.ouiaComponentType('datalist-checkbox').should(
                      'not.be.visible'
                    );
                    cy.ouiaComponentId('endpoint', 'datalist-cell')
                      .should('be.visible')
                      .and('contain.text', 'FlightBooking');
                    cy.ouiaComponentId('status', 'datalist-cell')
                      .should('be.visible')
                      .and('contain.text', 'Completed');
                  });
              });
            cy.ouiaComponentId(
              'process-c54ca5b0-b975-46e2-a9a0-6a86bf7ac21eaccd',
              'process-list-table-item'
            )
              .should('be.visible')
              .within($it => {
                cy.ouiaComponentType('datalist-expand-toggle').should(
                  'not.exist'
                );
                cy.ouiaComponentType('datalist-checkbox').should(
                  'not.be.visible'
                );
                cy.ouiaComponentId('endpoint', 'datalist-cell')
                  .should('be.visible')
                  .and('contain.text', 'FlightBooking test 1');
                cy.ouiaComponentId('status', 'datalist-cell')
                  .should('be.visible')
                  .and('contain.text', 'Suspended');
              });
          });
      });
    });
    it('Load More', () => {
      cy.ouiaComponentType('process-list-page').within($page => {
        cy.ouiaComponentType('process-list-table')
          .ouiaComponentType('process-list-table-item')
          .should('have.length', 10);
        cy.ouiaComponentType('load-more')
          .scrollIntoView()
          .should('be.visible')
          .ouiaComponentType('PF4/Dropdown')
          .click();
        cy.ouiaComponentType('process-list-table')
          .ouiaComponentType('process-list-table-item')
          .should('have.length', 13);
      });
    });
  });
});
