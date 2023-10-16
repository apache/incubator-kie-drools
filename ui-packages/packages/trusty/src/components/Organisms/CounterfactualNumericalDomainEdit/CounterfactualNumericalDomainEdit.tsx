/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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

  const handleMinChange = (value) => {
    onUpdate(value === '' ? undefined : Number(value), max);
  };

  const handleMaxChange = (value) => {
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
