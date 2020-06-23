import React from 'react';
import { Button } from '@patternfly/react-core';
import { ExternalLinkAltIcon } from '@patternfly/react-icons';

interface IOwnProps {
  serviceUrl: string;
  isLinkShown: boolean;
}

const EndpointLink: React.FC<IOwnProps> = ({ serviceUrl, isLinkShown }) => {
  return (
    <>
      {serviceUrl !== null ? (
        <Button
          component={'a'}
          variant={'link'}
          target={'_blank'}
          href={`${serviceUrl}`}
          isInline={true}
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
