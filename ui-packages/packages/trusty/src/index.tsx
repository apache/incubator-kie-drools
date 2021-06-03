import React from 'react';
import ReactDOM from 'react-dom';
import '@patternfly/react-core/dist/styles/base.css';
import { BrowserRouter } from 'react-router-dom';
// manually importing Red Hat Mono font because not yet provided by PatternFly
// see: https://github.com/patternfly/patternfly/issues/4021
// when updating PF to a version that will include it, it will be
// important to remove this duplication
import '../static/fonts/RedHatMono/RedHatMono.css';
import './index.css';
import TrustyApp from './components/Templates/TrustyApp/TrustyApp';
import { datePickerSetup } from './components/Molecules/DatePicker/DatePicker';

datePickerSetup();

ReactDOM.render(
  <BrowserRouter>
    <TrustyApp />
  </BrowserRouter>,
  document.getElementById('root')
);
