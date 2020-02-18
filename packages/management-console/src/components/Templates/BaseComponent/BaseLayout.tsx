import { BrowserRouter, Route, Switch } from 'react-router-dom';
import Dashboard from '../DashboardComponent/Dashboard';
import './BaseLayout.css';
import React from 'react';

const BaseLayout: React.FC = () => {
  return (
    <BrowserRouter>
      <Switch>
        <Route path="/" component={Dashboard} />
      </Switch>
    </BrowserRouter>
  );
};

export default BaseLayout;
