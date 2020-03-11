import { BrowserRouter, Route, Switch } from 'react-router-dom';
import Dashboard from '../DashboardComponent/Dashboard';
import './BaseComponent.css';
import React from 'react';

const BaseComponent: React.FC = () => {
  return (
    <BrowserRouter>
      <Switch>
        <Route path="/" component={Dashboard} />
      </Switch>
    </BrowserRouter>
  );
};

export default BaseComponent;
