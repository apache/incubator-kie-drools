import React from 'react';
import ReactDOM from 'react-dom';
import '@patternfly/react-core/dist/styles/base.css';
import { BrowserRouter } from 'react-router-dom';
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
