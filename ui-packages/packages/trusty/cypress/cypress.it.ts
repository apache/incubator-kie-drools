import { defineConfig } from 'cypress';

export default defineConfig({
  // setupNodeEvents can be defined in either
  // the e2e or component configuration
  e2e: {
    fixturesFolder: false,
    screenshotsFolder: 'screenshots',
    specPattern: 'integration/**/*.cy.ts',
    videosFolder: 'videos',
    supportFile: 'support/index.ts',
    baseUrl: 'http://localhost:9000',
    chromeWebSecurity: false,
    reporter: 'junit',
    reporterOptions: {
      mochaFile: 'target/surefire-reports/TEST-e2e-[hash].xml',
      toConsole: false
    },
    setupNodeEvents(on, config) {
      // bind to the event we care about
      // on('<event>', (arg1, arg2) => {
      //   // plugin stuff here
      // })
    }
  }
});
