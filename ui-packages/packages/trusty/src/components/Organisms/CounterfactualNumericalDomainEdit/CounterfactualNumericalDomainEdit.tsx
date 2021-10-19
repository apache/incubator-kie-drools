import React, { useEffect, useState } from 'react';
import {
  Alert,
  Form,
  FormGroup,
  Split,
  SplitItem,
  TextInput
} from '@patternfly/react-core';
import { CFConstraintValidation } from '../CounterfactualInputDomainEdit/CounterfactualInputDomainEdit';
import { CFNumericalDomain } from '../../../types';

type CounterfactualNumericalDomainEditProps = {
  inputDomain: CFNumericalDomain;
  onUpdate: (min: number | undefined, max: number | undefined) => void;
  validation: CFConstraintValidation;
};

const CounterfactualNumericalDomainEdit = (
  props: CounterfactualNumericalDomainEditProps
) => {
  const { inputDomain, onUpdate, validation } = props;
  const [min, setMin] = useState(
    inputDomain ? inputDomain.lowerBound : undefined
  );
  const [max, setMax] = useState(
    inputDomain ? inputDomain.upperBound : undefined
  );

  const handleMinChange = value => {
    onUpdate(value === '' ? undefined : Number(value), max);
  };

  const handleMaxChange = value => {
    onUpdate(min, value === '' ? undefined : Number(value));
  };

  useEffect(() => {
    setMin(inputDomain ? inputDomain.lowerBound : undefined);
    setMax(inputDomain ? inputDomain.upperBound : undefined);
  }, [inputDomain]);

  return (
    <Form>
      {!validation.isValid && (
        <Alert variant="danger" isInline title={validation.message} />
      )}
      <Split hasGutter={true}>
        <SplitItem>
          <FormGroup label="Minimum value" isRequired fieldId="min">
            <TextInput
              isRequired
              type="number"
              id="min"
              name="min"
              value={min !== undefined ? min : ''}
              onChange={handleMinChange}
            />
          </FormGroup>
        </SplitItem>
        <SplitItem>
          <FormGroup label="Maximum value" isRequired fieldId="max">
            <TextInput
              isRequired
              type="number"
              id="max"
              name="max"
              value={max !== undefined ? max : ''}
              onChange={handleMaxChange}
            />
          </FormGroup>
        </SplitItem>
      </Split>
    </Form>
  );
};

export default CounterfactualNumericalDomainEdit;
