import React from 'react';
import ReactDOM from 'react-dom';
import '@patternfly/react-core/dist/styles/base.css';
import { BrowserRouter } from 'react-router-dom';
import './index.css';
import TrustyApp from './components/TrustyApp/TrustyApp';

ReactDOM.render(
  <BrowserRouter>
    <TrustyApp />
  </BrowserRouter>,
  document.getElementById('root')
);
