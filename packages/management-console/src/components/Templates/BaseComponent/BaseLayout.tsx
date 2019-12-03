import * as React from 'react';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import Dashboard from '../DashboardComponent/Dashboard';
import './BaseLayout.css';

const BaseLayout: React.FC<{}> = () => {
  return (
    <BrowserRouter>
      <Switch>
        <Route path="/" component={Dashboard} />
      </Switch>
    </BrowserRouter>
  );
};

export default BaseLayout;
