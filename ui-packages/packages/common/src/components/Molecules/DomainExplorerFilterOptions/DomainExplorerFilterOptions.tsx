import React, { useState, useEffect, ReactElement } from 'react';
import {
  Select,
  SelectVariant,
  SelectGroup,
  SelectOption,
  TextInput,
  Button,
  Dropdown,
  DropdownToggle,
  DropdownItem,
  InputGroup,
  Popover,
  PopoverPosition,
  ToolbarItem
} from '@patternfly/react-core';
import { GraphQL } from '../../../graphql/types';
import useGetInputFieldsFromTypeQuery = GraphQL.useGetInputFieldsFromTypeQuery;
import { QuestionCircleIcon } from '@patternfly/react-icons';
import { constructObject, removeDuplicates } from '../../../utils/Utils';
import '../../styles.css';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';

interface ResponseType {
  loading?: boolean;
  data: any;
}
interface IOwnProps {
  filterArgument: string;
  filterChips: string[];
  finalFilters: Record<string, Record<string, unknown>>;
  generateFilterQuery: () => void;
  getQueryTypes: ResponseType;
  getSchema: ResponseType;
  reset: boolean;
  runQuery: boolean;
  setFilterChips: (filterChips: (chips: string[]) => string[]) => void;
  setFinalFilters: (
    finalFilters: (filters: Record<string, unknown>) => void
  ) => void;
  setOffset: (offset: number) => void;
  setReset: (reset: boolean) => void;
  setRunQuery: (runQuery: boolean) => void;
}
const DomainExplorerFilterOptions: React.FC<IOwnProps & OUIAProps> = ({
  filterArgument,
  filterChips,
  finalFilters,
  generateFilterQuery,
  getQueryTypes,
  getSchema,
  reset,
  runQuery,
  setFilterChips,
  setFinalFilters,
  setOffset,
  setReset,
  setRunQuery,
  ouiaId,
  ouiaSafe
}) => {
  const [initData2, setInitData2] = useState<any>({
    __schema: { queryType: [] }
  });
  const [isExpanded, setIsExpanded] = useState(false);
  const [isFilterDropdownOpen, setIsFilterDropdownOpen] = useState(false);
  const [selected, setSelected] = useState('id');
  const [selectTypes, setSelectTypes] = useState('equal');
  const [textValue, setTextValue] = useState('');
  const [inputArray, setInputArray] = useState('');
  const [isOpen, setIsOpen] = useState(false);
  const [currentArgument, setCurrentArgument] = useState('');
  const [currentArgumentScalar, setCurrentArgumentScalar] = useState('String');
  const [typeParent, setTypeParent] = useState<any>([]);
  const [currentBoolean, setCurrentBoolean] = useState('boolean');
  const [stateToggle, setStateToggle] = useState(false);
  const [multiStateToggle, setMultiStateToggle] = useState(false);
  const [selectedState, setSelectedState] = useState('');
  const [multiState, setMultiState] = useState([]);
  const [enumArray, setEnumArray] = useState([]);
  const scalarArgs = [
    null,
    'Boolean',
    'BooleanArgument',
    'DateArgument',
    'DateRange',
    'IdArgument',
    'NumericRange',
    'NumericArgument',
    'String',
    'StringArgument',
    'StringArrayArgument'
  ];

  const scalarTypes = ['Boolean', 'String'];

  const nonArgs = [null, 'Boolean', 'String'];
  const enumArgTypes = ['equal', 'in'];
  useEffect(() => {
    setInitData2(getQueryTypes.data);
  }, [getQueryTypes.data]);

  const onFieldToggle = (_isExpanded) => {
    setIsExpanded(_isExpanded);
  };

  const getTypes = useGetInputFieldsFromTypeQuery({
    variables: {
      type: currentArgument
    }
  });

  const onToggle = (_isOpen) => {
    setIsFilterDropdownOpen(_isOpen);
  };
  const checkType = (innerText) => {
    const typeName =
      getTypes.data.__type &&
      getTypes.data.__type.inputFields.find((item) => {
        if (item.name === innerText) {
          return item;
        }
      });
    if (
      typeName.type.kind === 'ENUM' ||
      (typeName.type.ofType && typeName.type.ofType.kind === 'ENUM')
    ) {
      if (typeName.type.name === null) {
        setCurrentArgumentScalar('enumMultiSelection');
        setEnumArray(typeName.type.ofType.enumValues);
      } else {
        setCurrentArgumentScalar('enumSingleSelection');
        setEnumArray(typeName.type.enumValues);
      }
    } else {
      if (scalarTypes.includes(typeName.type.name)) {
        setCurrentArgumentScalar(typeName.type.name);
      } else {
        setCurrentArgumentScalar('ArrayString');
      }
    }
  };
  const onSelect = (event) => {
    setSelectTypes(event.target.innerText);
    setIsFilterDropdownOpen(false);
    checkType(event.target.innerText);
  };

  const typesMenuItems =
    !getTypes.loading &&
    getTypes.data.__type &&
    getTypes.data.__type.inputFields.map((data, index) => (
      <SelectOption key={index} value={data.name} />
    ));

  const textBoxChange = (value) => {
    setTextValue(value);
  };

  const textGroupChange = (value) => {
    setInputArray(value);
  };

  const onSelectBoolean = (event) => {
    setCurrentBoolean(event.target.innerText);
    setIsOpen(!isOpen);
  };

  const onToggleBoolean = (_isOpen) => {
    setIsOpen(_isOpen);
  };

  const dropdownItems = [
    <DropdownItem key="true" component="button">
      true{' '}
    </DropdownItem>,
    <DropdownItem key="false" component="button">
      false{' '}
    </DropdownItem>
  ];

  const fetchSchema = (option) => {
    const filteredItem =
      !getQueryTypes.loading &&
      getQueryTypes.data.__schema &&
      getQueryTypes.data.__schema.queryType.find((item) => {
        if (item.name === option.type.name) {
          return item;
        }
      });
    const fieldName = [];
    filteredItem &&
      filteredItem.inputFields.map((item) => fieldName.push(item.name));
    if (
      fieldName.length > 2 &&
      enumArgTypes.join('|') !== fieldName.join('|')
    ) {
      return filteredItem;
    }
  };
  let childItems;
  let finalResult: any = [];
  const childSelectionItems = (_data, title, ...attr) => {
    let nestedTitles = '';
    childItems =
      !getQueryTypes.loading &&
      _data.map((group) => {
        const label = title + ' / ' + attr.join();
        const childEle = (
          <SelectGroup
            label={label.replace(/,/g, '')}
            key={'kie-filter-item-' + label.replace(/,/g, '')}
            id={group.name}
            value={label.replace(/,/g, '')}
          >
            {group.inputFields !== null &&
              group.inputFields
                .filter((item) => {
                  if (!scalarArgs.includes(item.type.name)) {
                    const tempData = [];
                    const schemaObj = fetchSchema(item);
                    if (schemaObj === undefined) {
                      return item;
                    }
                    schemaObj && tempData.push(schemaObj);
                    nestedTitles = nestedTitles + ' / ' + item.name;
                    childSelectionItems(tempData, title, attr, nestedTitles);
                  } else {
                    return item;
                  }
                })
                .map((item) => {
                  return (
                    <SelectOption
                      key={'kie-filter-item-' + group.name + title + item.name}
                      value={item.name + title + group.name}
                    >
                      {item.name}
                    </SelectOption>
                  );
                })}
          </SelectGroup>
        );
        return childEle;
      });
    finalResult.push(childItems);
  };
  const rootElementsArray = [];
  const selectionItems = () => {
    !getSchema.loading &&
      getSchema.data.__type &&
      getSchema.data.__type.inputFields
        .filter((group) => {
          if (
            group.type.kind !== 'LIST' &&
            group.type.name !== filterArgument
          ) {
            return group;
          }
        })
        .map((group, index) => {
          let groupItem;
          let rootItem;
          group.type.inputFields.filter((item) => {
            if (!nonArgs.includes(item.type.name)) {
              groupItem = group;
            } else {
              rootItem = group;
            }
          });
          if (rootItem) {
            const _rootElement = (
              <SelectOption key={rootItem.name} value={rootItem.name} />
            );
            !rootElementsArray.includes(_rootElement) &&
              rootElementsArray.push(_rootElement);
          }

          let ele;
          if (groupItem) {
            ele = (
              <SelectGroup
                label={groupItem.name}
                key={index}
                id={groupItem.name}
                value={groupItem.name}
              >
                {groupItem.type.inputFields &&
                  groupItem.type.inputFields
                    .filter((item) => {
                      if (!scalarArgs.includes(item.type.name)) {
                        const tempData = [];
                        const schemaObj = fetchSchema(item);
                        schemaObj && tempData.push(schemaObj);
                        childSelectionItems(
                          tempData,
                          groupItem.name,
                          item.name
                        );
                      } else {
                        return item;
                      }
                    })
                    .map((item, _index) => {
                      return (
                        <SelectOption
                          key={_index}
                          value={item.name + groupItem.name}
                        >
                          {item.name}
                        </SelectOption>
                      );
                    })}
              </SelectGroup>
            );
          }
          ele && !finalResult.includes(ele) && finalResult.push(ele);
        });
  };

  const rootElement: ReactElement = (
    <SelectGroup label=" " key={'kie-filter-item-' + ' '} id="" value=" ">
      {rootElementsArray}
    </SelectGroup>
  );

  const getOperators = (innerText, parent) => {
    let tempParents;
    let lastEle;
    if (parent !== ' ') {
      tempParents = parent.split(' / ');
      setTypeParent(tempParents);
      lastEle = tempParents.slice(-1)[0];
    } else {
      tempParents = [innerText];
      lastEle = tempParents.slice(-1)[0];
    }
    let arg;
    if (lastEle === 'processInstances') {
      let str = lastEle.charAt(0).toUpperCase() + lastEle.slice(1);
      str = str.substring(0, str.length - 1);
      arg = str + 'MetaArgument';
    } else if (lastEle === 'userTasks') {
      let str = lastEle.charAt(0).toUpperCase() + lastEle.slice(1);
      str = str.substring(0, str.length - 1);
      arg = str + 'InstanceMetaArgument';
    } else {
      const str = lastEle.charAt(0).toUpperCase() + lastEle.slice(1);
      arg = str + 'Argument';
    }
    const argType = initData2.__schema.queryType.find((type) => {
      if (type.name === arg) {
        return type;
      }
    });

    const argField = argType.inputFields.find((data) => {
      if (data.name === innerText) {
        return data;
      }
    });

    if (argField === undefined) {
      setCurrentArgument(argType.name);
    } else {
      if (argField.type.kind === 'INPUT_OBJECT') {
        setCurrentArgument(argField.type.name);
      } else {
        setCurrentArgumentScalar(argField.type.name);
      }
    }
    setSelectTypes('');
    setIsExpanded(false);
  };
  const onChange = (event) => {
    const innerText = event.target.innerText;
    setSelected(innerText);
    const parent =
      event.nativeEvent.target.parentElement.parentElement.getAttribute(
        'value'
      );
    getOperators(innerText, parent);
  };

  const onStateToggle = (_isOpen) => {
    setStateToggle(_isOpen);
  };

  const onStateSelect = (event) => {
    const selection = event.target.innerText;
    setSelectedState(selection);
    setStateToggle(!stateToggle);
  };

  const onMultiStateToggle = (_isOpen) => {
    setMultiStateToggle(_isOpen);
  };

  const onMultiStateSelect = (event, selection) => {
    if (multiState.includes(selection)) {
      setMultiState((prev) => prev.filter((item) => item !== selection));
    } else {
      setMultiState((prev) => [...prev, selection]);
    }
  };

  const onLoad = () => {
    const innerText = 'id';
    const tempParents = [innerText];
    const lastEle = tempParents.slice(-1)[0];
    const str = lastEle.charAt(0).toUpperCase() + lastEle.slice(1);
    const arg = str + 'Argument';
    const argType = getQueryTypes.data.__schema.queryType.find((type) => {
      if (type.name === arg) {
        return type;
      }
    });
    const argField = argType.inputFields.find((data) => {
      if (data.name === innerText) {
        return data;
      }
    });
    if (argField === undefined) {
      setCurrentArgument(argType.name);
      setCurrentArgumentScalar('String');
      setTextValue('');
      setInputArray('');
      setCurrentBoolean('boolean');
      setMultiState([]);
      setSelectedState('');
    }
  };

  finalResult.unshift(rootElement);

  !getSchema.loading && selectionItems();
  finalResult = finalResult.flat();

  const setPlaceHolders = () => {
    currentArgumentScalar === 'String' && setTextValue('');
    currentArgumentScalar === 'Boolean' && setCurrentBoolean('boolean');
    currentArgumentScalar === 'ArrayString' && setInputArray('');
    currentArgumentScalar === 'enumSingleSelection' && setSelectedState('');
    currentArgumentScalar === 'enumMultiSelection' && setMultiState([]);
    setSelectTypes('');
    setSelected('');
  };
  const HandleQuery = () => {
    reset !== true && setPlaceHolders();
    generateFilterQuery();
  };
  useEffect(() => {
    runQuery && HandleQuery();
  }, [runQuery]);

  useEffect(() => {
    setReset(true);
  }, []);

  useEffect(() => {
    if (reset === true) {
      setSelected('id');
      setSelectTypes('equal');
      setTypeParent('');
      onLoad();
      setRunQuery(true);
    }
  }, [reset]);
  const obj: any = {};

  const validateChip = (parentString, _selected, _selectTypes, value) => {
    if (_selectTypes === 'equal') {
      return parentString
        ? `${parentString} / ${_selected}: ${value}`
        : `${_selected}: ${value}`;
    } else if (_selectTypes === 'isNull') {
      if (value === true) {
        return parentString
          ? `${parentString} / ${_selected}: is null`
          : `${_selected}: is null`;
      } else {
        return parentString
          ? `${parentString} / ${_selected}: is not null`
          : `${_selected}: is not null`;
      }
    } else if (_selectTypes === 'in') {
      return parentString
        ? `${parentString} / ${_selected}: is in ${value}`
        : `${_selected}: is in ${value}`;
    } else {
      return parentString
        ? `${parentString} / ${_selected}: ${_selectTypes} ${value}`
        : `${_selected}: ${_selectTypes} ${value}`;
    }
  };

  const checkChipArray = (chipSelections, chipText) => {
    let value = '';
    filterChips.forEach((item) => {
      const tempItem = item.split(':');
      if (tempItem[0] === chipSelections) {
        value = item;
      }
    });
    if (value.length > 0) {
      const index = filterChips.indexOf(value);
      setFilterChips((prev) => {
        prev.splice(index, 1);
        return [...prev, chipText];
      });
    } else {
      setFilterChips((prev) => [...prev, chipText]);
    }
  };
  const valueToValidate = (scalarType) => {
    switch (scalarType) {
      case 'Boolean':
        return currentBoolean;
      case 'ArrayString':
        return selectedState;
      case 'enumSingleSelection':
        return selectedState;
      case 'enumMultiSelection':
        return multiState;
      default:
        return textValue;
    }
  };

  const validateScalarArgument = (scalarType, objKeys) => {
    const value = valueToValidate(scalarType);
    let parentString = '';
    let chipText = '';
    typeParent &&
      typeParent.map(
        (parent) => (parentString = parentString + ' / ' + parent)
      );
    parentString = parentString.substring(3);
    if (scalarType === 'ArrayString') {
      chipText = validateChip(parentString, selected, selectTypes, inputArray);
      let chipSelections = '';
      if (typeParent) {
        chipSelections = `${parentString} / ${selected}`;
      } else {
        chipSelections = selected;
      }
      checkChipArray(chipSelections, chipText);
      const tempArray = inputArray.split(',');
      constructObject(obj, objKeys, tempArray);
    } else {
      chipText = validateChip(parentString, selected, selectTypes, value);
      let chipSelections = '';
      if (typeParent) {
        chipSelections = `${parentString} / ${selected}`;
      } else {
        chipSelections = selected;
      }
      checkChipArray(chipSelections, chipText);
      constructObject(obj, objKeys, value);
    }
    setFinalFilters(() => {
      if (Object.prototype.hasOwnProperty.call(finalFilters, typeParent)) {
        const te: any = Object.values(obj)[0];
        finalFilters[typeParent] = { ...finalFilters[typeParent], ...te };
        return finalFilters;
      } else {
        return { ...finalFilters, ...obj };
      }
    });
    setTypeParent('');
    setRunQuery(true);
  };

  const onApplyFilter = () => {
    let objKeys;
    setOffset(0);
    if (typeParent.length > 0) {
      objKeys = `${typeParent},${selected},${selectTypes}`;
    } else {
      objKeys = `${selected},${selectTypes}`;
    }
    validateScalarArgument(currentArgumentScalar, objKeys);
  };

  return (
    <ToolbarItem {...componentOuiaProps(ouiaId, 'filter-options', ouiaSafe)}>
      {!getSchema.loading && (
        <Select
          onToggle={onFieldToggle}
          onSelect={onChange}
          selections={selected}
          isOpen={isExpanded}
          id="select-field"
          placeholderText="Select a field"
          aria-labelledby="Select a field"
          maxHeight="60vh"
          isGrouped
        >
          {removeDuplicates(finalResult, 'props')}
        </Select>
      )}
      {!getTypes.loading && (
        <Select
          aria-label="Location"
          onToggle={onToggle}
          onSelect={onSelect}
          selections={selectTypes}
          isOpen={isFilterDropdownOpen}
          id="select-operator"
          placeholderText="operator"
        >
          {typesMenuItems}
        </Select>
      )}
      {currentArgumentScalar === 'String' && (
        <>
          <TextInput
            name="filterText"
            type="search"
            aria-label={`filter text for ${selected}`}
            onChange={textBoxChange}
            id="textBox-input"
            className="kogito-common--filter-options__inputs"
            placeholder="value"
            value={textValue}
          />
          <Button
            variant="primary"
            onClick={onApplyFilter}
            id="button-with-string"
            isDisabled={!(selected && selectTypes && textValue)}
          >
            Apply Filter
          </Button>
        </>
      )}
      {currentArgumentScalar === 'Boolean' && (
        <>
          <Dropdown
            onSelect={onSelectBoolean}
            toggle={
              <DropdownToggle id="toggle-id" onToggle={onToggleBoolean}>
                {currentBoolean}
              </DropdownToggle>
            }
            id="boolean-dropdown"
            isOpen={isOpen}
            dropdownItems={dropdownItems}
            className="kogito-common--filter-options__inputs"
          />
          <Button
            variant="primary"
            id="button-with-boolean"
            onClick={onApplyFilter}
            isDisabled={
              !(selected && selectTypes && currentBoolean !== 'boolean')
            }
          >
            Apply Filter
          </Button>
        </>
      )}
      {currentArgumentScalar === 'ArrayString' && (
        <>
          <InputGroup className="kogito-common--filter-options__inputs">
            <TextInput
              name="filterArrayOfInputs"
              id="filterArrayOfInputs"
              type="text"
              onChange={textGroupChange}
              aria-label="filter array of inputs"
              placeholder="value"
              value={inputArray}
            />
            <Popover
              aria-label="filter array of inputs popover"
              position={PopoverPosition.top}
              bodyContent='This field allows specifying multiple values using "," as value delimiter'
            >
              <Button variant="control" aria-label="popover for input">
                <QuestionCircleIcon />
              </Button>
            </Popover>
          </InputGroup>
          <Button
            variant="primary"
            onClick={onApplyFilter}
            id="button-with-arrayInput"
            isDisabled={!(selected && selectTypes && inputArray)}
          >
            Apply Filter
          </Button>
        </>
      )}
      {currentArgumentScalar === 'enumSingleSelection' && (
        <>
          <Select
            aria-label="Location"
            onToggle={onStateToggle}
            onSelect={onStateSelect}
            id="enumSingleSelection"
            selections={selectedState}
            isOpen={stateToggle}
            placeholderText="value"
            className="kogito-common--filter-options__inputs"
          >
            {enumArray.map((item, index) => (
              <SelectOption key={index} value={item.name} />
            ))}
          </Select>
          <Button
            variant="primary"
            onClick={onApplyFilter}
            id="button-with-enumSingleSelection"
            isDisabled={!(selected && selectTypes && selectedState)}
          >
            Apply Filter
          </Button>
        </>
      )}
      {currentArgumentScalar === 'enumMultiSelection' && (
        <>
          <Select
            variant={SelectVariant.checkbox}
            aria-label="Select Input"
            onToggle={onMultiStateToggle}
            onSelect={onMultiStateSelect}
            id="enumMultiSelection"
            selections={multiState}
            isOpen={multiStateToggle}
            placeholderText="value"
            className="kogito-common--filter-options__inputs"
          >
            {enumArray.map((item, index) => (
              <SelectOption key={index} value={item.name} />
            ))}
          </Select>
          <Button
            variant="primary"
            onClick={onApplyFilter}
            id="button-with-enumMultiSelection"
            isDisabled={!(selected && selectTypes && multiState.length !== 0)}
          >
            Apply Filter
          </Button>
        </>
      )}
    </ToolbarItem>
  );
};

export default React.memo(DomainExplorerFilterOptions);
