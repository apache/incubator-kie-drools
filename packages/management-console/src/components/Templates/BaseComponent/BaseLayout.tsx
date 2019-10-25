import * as React from 'react';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import Dashboard from '../DashboardComponent/Dashboard';
import './BaseLayout.css';

const BaseLayout: React.FC<{}> = () => {
  return (
    <div>
      <BrowserRouter>
        <Switch>
          <Route path="/" component={Dashboard} />
        </Switch>
      </BrowserRouter>
    </div>
  );
};

export default BaseLayout;
