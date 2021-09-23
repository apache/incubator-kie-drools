import React, { useEffect, useRef, useState } from 'react';
import {
  Button,
  ButtonVariant,
  Flex,
  FlexItem,
  Stack,
  StackItem,
  TextInput
} from '@patternfly/react-core';
import { TrashIcon } from '@patternfly/react-icons';
import { CFCategoricalDomain } from '../../../types';

type CounterfactualCategoricalDomainEditProps = {
  inputDomain: CFCategoricalDomain;
  onUpdate: (values: string[]) => void;
};

const CounterfactualCategoricalDomainEdit = (
  props: CounterfactualCategoricalDomainEditProps
) => {
  const { inputDomain, onUpdate } = props;
  const [categories, setCategories] = useState(
    inputDomain && inputDomain.categories ? inputDomain.categories : ['']
  );
  const handleChange = (value: string, position: number) => {
    const updatedList = [...categories];
    updatedList[position] = value;
    setCategories(updatedList);
  };

  const handleDelete = (position: number) => {
    const updatedList = categories.filter(
      (category, index) => index !== position
    );
    setCategories(updatedList);
    onUpdate(updatedList);
  };

  const addOneCategory = () => {
    setCategories([...categories, '']);
    shouldFocus.current = categories.length;
  };

  const handleSave = () => {
    onUpdate(categories);
  };

  const handleEnterPress = (event: React.KeyboardEvent<HTMLInputElement>) => {
    if (event.key === 'Enter') {
      addOneCategory();
    }
  };
  const inputRef = useRef<HTMLInputElement>();
  const shouldFocus = useRef(-1);

  useEffect(() => {
    if (
      inputRef.current &&
      inputRef.current.id === `enum-value-${shouldFocus.current}`
    ) {
      inputRef.current.focus();
      shouldFocus.current = -1;
    }
  }, [categories.length]);

  return (
    <Stack hasGutter={true}>
      {categories.map((category, index) => (
        <StackItem key={index}>
          <Flex spaceItems={{ default: 'spaceItemsXs' }}>
            <FlexItem>
              <TextInput
                type="text"
                id={`enum-value-${index}`}
                name={`enum-value-${index}`}
                value={category}
                onChange={value => handleChange(value, index)}
                onBlur={handleSave}
                onKeyPress={handleEnterPress}
                autoComplete="off"
                ref={inputRef}
              />
            </FlexItem>
            <FlexItem>
              <Button
                id={`delete-enum-value-${index}`}
                variant={ButtonVariant.plain}
                isInline={true}
                onClick={() => handleDelete(index)}
                isDisabled={categories.length === 1}
              >
                <TrashIcon />
              </Button>
            </FlexItem>
          </Flex>
        </StackItem>
      ))}
      <StackItem>
        <Button
          id="enum-add"
          variant={ButtonVariant.secondary}
          onClick={addOneCategory}
        >
          Add another value
        </Button>
      </StackItem>
    </Stack>
  );
};

export default CounterfactualCategoricalDomainEdit;
