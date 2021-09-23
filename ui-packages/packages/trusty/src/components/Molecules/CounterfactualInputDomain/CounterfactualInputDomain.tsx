import React from 'react';
import { CFSearchInput } from '../../../types';

type CounterfactualInputDomainProps = {
  input: CFSearchInput;
};

const CounterfactualInputDomain = ({
  input
}: CounterfactualInputDomainProps) => {
  let domain;
  switch (input.domain.type) {
    case 'RANGE':
      domain = (
        <span>
          {input.domain.lowerBound}-{input.domain.upperBound}
        </span>
      );
      break;
    case 'CATEGORICAL':
      domain =
        // special treatment for boolean values that have a 'fake' categorical
        // domain
        typeof input.value === 'boolean' ? (
          <></>
        ) : (
          <span>
            {input.domain.categories.map((category, index, list) => (
              <span key={index}>
                {category}
                {index === list.length - 1 ? '' : ','}{' '}
              </span>
            ))}
          </span>
        );

      break;
    default:
      domain = '';
  }
  return <>{domain}</>;
};

export default CounterfactualInputDomain;
