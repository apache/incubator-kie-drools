import React from 'react';
import ReactDOM from 'react-dom';
import '@patternfly/react-core/dist/styles/base.css';
import { TrustyApp } from '../src/index';
import './index.css';

ReactDOM.render(
  <TrustyApp counterfactualEnabled={true} explanationEnabled={true} />,
  document.getElementById('root')
);
