import { BrowserRouter, Route, Switch } from 'react-router-dom';
import PageLayoutComponent from '../PageLayoutComponent/PageLayoutComponent';
import './BaseComponent.css';
import React from 'react';

const BaseComponent: React.FC = () => {
  return (
    <BrowserRouter>
      <Switch>
        <Route path="/" component={PageLayoutComponent} />
      </Switch>
    </BrowserRouter>
  );
};

export default BaseComponent;
