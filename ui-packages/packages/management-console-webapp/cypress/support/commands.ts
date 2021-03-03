// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })

Cypress.Commands.add(
  'ouiaComponentId',
  { prevSubject: ['optional', 'element'] },
  (subject, id: string, type?: string, options = {}) => {
    const typeSelector = type ? ouiaAttrSelector('component-type', type) : '';
    const idSelector = ouiaAttrSelector('component-id', id);
    if (subject) {
      if (type) {
        cy.wrap(subject, options).find(
          typeSelector + idSelector + ouiaSafeSelector(),
          options
        );
      } else {
        cy.wrap(subject).filter(idSelector + ouiaSafeSelector());
      }
    } else {
      // eslint-disable-next-line cypress/require-data-selectors
      cy.get(typeSelector + idSelector + ouiaSafeSelector(), options);
    }
  }
);

Cypress.Commands.add(
  'ouiaComponentType',
  { prevSubject: ['optional', 'element'] },
  (subject, type: string, options = {}) => {
    const typeSelector = ouiaAttrSelector('component-type', type);
    if (subject) {
      cy.wrap(subject, options).find(
        typeSelector + ouiaSafeSelector(),
        options
      );
    } else {
      // eslint-disable-next-line cypress/require-data-selectors
      cy.get(typeSelector + ouiaSafeSelector(), options);
    }
  }
);

const ouiaAttrSelector = (name: string, value?: string): string => {
  return `[data-ouia-${name}` + (value ? `='${value}']` : ']');
};

const ouiaSafeSelector = (): string => {
  return ouiaAttrSelector('safe', 'true');
};

Cypress.Commands.add(
  'ouiaNavigationName',
  { prevSubject: ['optional', 'element'] },
  (subject, value?: string, options = {}) => {
    const selector = ouiaAttrSelector('navigation-name', value);
    if (subject) {
      cy.wrap(subject, options).find(selector, options);
    } else {
      // eslint-disable-next-line cypress/require-data-selectors
      cy.get(selector, options);
    }
  }
);
