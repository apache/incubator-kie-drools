import React from 'react';
import { Button } from '@patternfly/react-core';
import { ExternalLinkAltIcon } from '@patternfly/react-icons';
import { OUIAProps, componentOuiaProps } from '../../../utils/OuiaUtils';

interface IOwnProps {
  serviceUrl: string;
  isLinkShown: boolean;
}

const EndpointLink: React.FC<IOwnProps & OUIAProps> = ({
  serviceUrl,
  isLinkShown,
  ouiaId,
  ouiaSafe
}) => {
  return (
    <>
      {serviceUrl !== null ? (
        <Button
          component={'a'}
          variant={'link'}
          target={'_blank'}
          href={`${serviceUrl}`}
          isInline={true}
          {...componentOuiaProps(ouiaId, 'endpoint-link', ouiaSafe)}
        >
          {isLinkShown ? serviceUrl : 'Endpoint'}
          {<ExternalLinkAltIcon className="pf-u-ml-xs" />}
        </Button>
      ) : (
        ''
      )}
    </>
  );
};

export default EndpointLink;
