import * as React from 'react';
import Dashboard from '../DashboardComponent/Dashboard';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import './BaseLayout.css';

interface IOwnProps {}

const BaseLayout: React.FC<IOwnProps> = () => {
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
