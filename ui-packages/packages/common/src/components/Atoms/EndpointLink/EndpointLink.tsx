import React from 'react';
import { Button } from '@patternfly/react-core';
import { ExternalLinkAltIcon } from '@patternfly/react-icons';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';

interface IOwnProps {
  serviceUrl: string;
  isLinkShown: boolean;
  linkLabel?: string;
}

const EndpointLink: React.FC<IOwnProps & OUIAProps> = ({
  serviceUrl,
  isLinkShown,
  linkLabel,
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
          {isLinkShown ? serviceUrl : linkLabel || 'Endpoint'}
          {<ExternalLinkAltIcon className="pf-u-ml-xs" />}
        </Button>
      ) : (
        ''
      )}
    </>
  );
};

export default EndpointLink;
