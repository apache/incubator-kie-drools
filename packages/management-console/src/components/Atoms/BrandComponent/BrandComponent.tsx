import React from 'react';
import { Brand } from '@patternfly/react-core';
import { withRouter } from 'react-router-dom';
import { RouteComponentProps } from 'react-router';
import managementConsoleLogo from '../../../static/managementConsoleLogo.svg';

const BrandComponent: React.FC<RouteComponentProps> = ({ history }) => {
  const onLogoClick = () => {
    history.push('/ProcessInstances');
  };
  return (
    <Brand
      src={managementConsoleLogo}
      alt="Management Console Logo"
      onClick={onLogoClick}
    />
  );
};

export default withRouter(BrandComponent);
