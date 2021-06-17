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
          cy.ouiaType('page-toolbar').should('exist');
        });
    });
    it('Check navigation panel', () => {
      cy.get('[data-ouia-navigation=true]')
        .ouiaType('PF4/Nav')
        .ouiaId('navigation-list')
        .should('exist')
        .within($navigation => {
          cy.ouiaNavigationName()
            .should('not.be.empty')
            .and('have.length', 2);
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
      cy.ouiaId('management-console', 'page')
        .find('[data-ouia-main=true]')
        .should('exist')
        .within($main => {
          cy.ouiaType('page-section-header')
            .should('exist')
            .within($header => {
              cy.ouiaType('page-title')
                .should('exist')
                .and('contain.text', 'Process Instances');
              cy.ouiaType('PF4/Breadcrumb')
                .should('exist')
                .within($nav => {
                  // eslint-disable-next-line cypress/require-data-selectors
                  cy.get('li')
                    .should('have.length', 2)
                    .within($items => {
                      expect($items.eq(0)).to.contain.text('Home');
                      expect($items.eq(1)).to.contain.text('Processes');
                    });
                });
            });
          cy.ouiaType('page-section-content')
            .should('exist')
            .within($content => {
              cy.ouiaType('process-list-toolbar').should('be.visible');
              cy.ouiaType('process-list-table')
                .should('be.visible')
                .ouiaSafe();
              cy.ouiaType('load-more')
                .scrollIntoView()
                .should('be.visible');
            });
        });
    });
  });
  describe('Data presentation', () => {
    it('Table Layout', () => {
      cy.ouiaType('page-section-content').within($page => {
        cy.ouiaType('process-list-table')
          .ouiaSafe()
          .ouiaType('process-list-table-item')
          .should('have.length', 10)
          .eq(0)
          .within($item => {
            cy.ouiaType('datalist-expand-toggle').should('be.visible');
            cy.ouiaType('datalist-checkbox')
              .should('be.visible')
              .and('be.enabled');
            cy.ouiaType('datalist-cell').then($cells => {
              cy.wrap($cells)
                .ouiaId('endpoint')
                .should('be.visible');
              cy.wrap($cells)
                .ouiaId('status')
                .should('be.visible')
                .and('contain.text', 'Active');
              cy.wrap($cells)
                .ouiaId('created')
                .should('be.visible');
              cy.wrap($cells)
                .ouiaId('updated')
                .should('be.visible');
            });
          });
      });
    });
    it('Process-list-item expanded.', () => {
      cy.ouiaType('page-section-content').within($page => {
        cy.ouiaType('process-list-table')
          .ouiaSafe()
          .ouiaType('process-list-table-item')
          .ouiaId('process-8035b580-6ae4-4aa8-9ec0-e18e19809e0b')
          .within($item => {
            cy.ouiaType('process-list-table-item-expand').should(
              'not.be.visible'
            );
            cy.ouiaType('datalist-expand-toggle')
              .scrollIntoView()
              .should('be.visible')
              .click({ force: true });
            cy.ouiaType('process-list-table-item-expand')
              .should('be.visible')
              .within($expanded => {
                cy.ouiaType('process-list-table-item').should('have.length', 4);
                cy.ouiaId(
                  'process-c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
                  'process-list-table-item'
                )
                  .should('be.visible')
                  .within($it => {
                    cy.ouiaType('datalist-expand-toggle').should('not.exist');
                    cy.ouiaType('datalist-checkbox').should('not.be.visible');
                    cy.ouiaId('endpoint', 'datalist-cell')
                      .should('be.visible')
                      .and('contain.text', 'FlightBooking');
                    cy.ouiaId('status', 'datalist-cell')
                      .should('be.visible')
                      .and('contain.text', 'Completed');
                  });
              });
            cy.ouiaId(
              'process-c54ca5b0-b975-46e2-a9a0-6a86bf7ac21eaccd',
              'process-list-table-item'
            )
              .should('be.visible')
              .within($it => {
                cy.ouiaType('datalist-expand-toggle').should('not.exist');
                cy.ouiaType('datalist-checkbox').should('not.be.visible');
                cy.ouiaId('endpoint', 'datalist-cell')
                  .should('be.visible')
                  .and('contain.text', 'FlightBooking test 1');
                cy.ouiaId('status', 'datalist-cell')
                  .should('be.visible')
                  .and('contain.text', 'Suspended');
              });
          });
      });
    });
    it('Load More', () => {
      cy.ouiaType('page-section-content').within($page => {
        cy.ouiaType('process-list-table')
          .ouiaSafe()
          .ouiaType('process-list-table-item')
          .should('have.length', 10);
        cy.ouiaType('load-more')
          .scrollIntoView()
          .should('be.visible')
          .ouiaType('PF4/Dropdown')
          .click();
        cy.ouiaType('process-list-table')
          .ouiaType('process-list-table-item')
          .should('have.length', 13);
      });
    });
  });
});
