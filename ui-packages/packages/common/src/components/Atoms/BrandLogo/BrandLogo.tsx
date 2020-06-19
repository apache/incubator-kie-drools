import React from 'react';
import { Brand } from '@patternfly/react-core';

interface IOwnProps {
  src: string;
  alt: string;
  brandClick: any;
}

const BrandLogo: React.FC<IOwnProps> = props => {
  return <Brand src={props.src} alt={props.alt} onClick={props.brandClick} />;
};

export default BrandLogo;
