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

// need to keep triple slash directive here, Cypress does not handle well imports.
// eslint-disable-next-line @typescript-eslint/triple-slash-reference
/// <reference path="../index.d.ts" />

Cypress.Commands.add(
  'ouiaId',
  { prevSubject: ['optional', 'element'] },
  (subject, id: string, type?: string, options = {}) => {
    const typeSelector = type ? ouiaAttrSelector('component-type', type) : '';
    const idSelector = ouiaAttrSelector('component-id', id);
    if (subject) {
      if (type) {
        cy.wrap(subject, options).find(typeSelector + idSelector, options);
      } else {
        cy.wrap(subject).filter(idSelector);
      }
    } else {
      // eslint-disable-next-line cypress/require-data-selectors
      cy.get(typeSelector + idSelector, options);
    }
  }
);

Cypress.Commands.add(
  'ouiaType',
  { prevSubject: ['optional', 'element'] },
  (subject, type: string, options = {}) => {
    const typeSelector = ouiaAttrSelector('component-type', type);
    if (subject) {
      cy.wrap(subject, options).find(typeSelector, options);
    } else {
      // eslint-disable-next-line cypress/require-data-selectors
      cy.get(typeSelector, options);
    }
  }
);

Cypress.Commands.add(
  'ouiaSafe',
  { prevSubject: ['element'] },
  (subject, options = {}) => {
    cy.wrap(subject).filter(ouiaSafeSelector(), options);
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
