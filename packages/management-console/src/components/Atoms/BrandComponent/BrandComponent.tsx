import React from 'react';
import { Brand } from '@patternfly/react-core';
import { withRouter } from 'react-router-dom';
import { RouteComponentProps } from 'react-router';

type combinedProps = RouteComponentProps & IOwnProps;
interface IOwnProps {}

const BrandComponent: React.FC<combinedProps> = ({ history }) => {
  const logo = require('../../../static/kogito_logo_rgb.png');
  const onLogoClick = () => {
    history.push('/ProcessInstances');
  };
  return <Brand src={logo} alt="Kogito Logo" onClick={onLogoClick} />;
};

export default withRouter(BrandComponent);
