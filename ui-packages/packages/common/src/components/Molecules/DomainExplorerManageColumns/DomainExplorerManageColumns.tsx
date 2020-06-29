import React, { useState, useEffect } from 'react';
import {
  Select,
  SelectOption,
  SelectVariant,
  SelectGroup,
  Button
} from '@patternfly/react-core';
import { SyncIcon } from '@patternfly/react-icons';
import { query } from 'gql-query-builder';
import _ from 'lodash';
import gql from 'graphql-tag';
import { useApolloClient } from 'react-apollo';

export interface IOwnProps {
  columnPickerType: any;
  setColumnFilters: any;
  setTableLoading: any;
  getQueryTypes: any;
  setDisplayTable: any;
  parameters: any;
  setParameters: any;
  selected: any;
  setSelected: any;
  data: any;
  getPicker: any;
  setError: any;
  setDisplayEmptyState: any;
  rememberedParams: any;
  enableCache: boolean;
  setEnableCache: any;
  offsetVal: number;
  pageSize: number;
  setOffsetVal: (offsetVal: number) => void;
  setPageSize: (pageSize: number) => void;
  setIsLoadingMore: (isLoadingMoreVal: boolean) => void;
  isLoadingMore: boolean;
}

const DomainExplorerManageColumns: React.FC<IOwnProps> = ({
  columnPickerType,
  setColumnFilters,
  setTableLoading,
  getQueryTypes,
  setDisplayTable,
  parameters,
  setParameters,
  selected,
  setSelected,
  data,
  getPicker,
  setError,
  setDisplayEmptyState,
  rememberedParams,
  enableCache,
  setEnableCache,
  pageSize,
  offsetVal,
  setOffsetVal,
  setPageSize,
  setIsLoadingMore,
  isLoadingMore
}) => {
  // tslint:disable: forin
  // tslint:disable: no-floating-promises
  const [isExpanded, setIsExpanded] = useState(false);
  const [enableRefresh, setEnableRefresh] = useState(true);

  const nullTypes = [null, 'String', 'Boolean', 'Int', 'DateTime'];
  const client = useApolloClient();

  const onSelect = event => {
    const selection = event.target.id;
    setEnableRefresh(false);
    if (selected.includes(selection)) {
      setSelected(prevState => prevState.filter(item => item !== selection));
      const innerText = event.nativeEvent.target.nextSibling.innerText;
      const rest = filterColumnSelection(event, innerText);
      setParameters(prevState =>
        prevState.filter(obj => {
          if (!_.isEqual(obj, rest)) {
            return obj;
          }
        })
      );
    } else {
      setSelected(prevState => [...prevState, selection]);
      const innerText = event.nativeEvent.target.nextSibling.innerText;
      const rest = filterColumnSelection(event, innerText);
      setParameters(prevState => [...prevState, rest]);
    }
  };

  const filterColumnSelection = (event, selection) => {
    const parent = event.nativeEvent.target.parentElement.parentElement.getAttribute(
      'aria-labelledby'
    );
    let res = {};
    const tempParents = parent.split('---');
    for (let i = tempParents.length - 1; i >= 0; i--) {
      if (i === tempParents.length - 1) {
        if (tempParents[i] === '-') {
          res = selection;
        } else {
          res = { [tempParents[i]]: [selection] }; // assign the value
        }
      } else {
        res = { [tempParents[i]]: [res] }; // put the prev object
      }
    }
    return res;
  };
  const onToggle = _isExpanded => {
    setIsExpanded(_isExpanded);
  };

  useEffect(() => {
    if (isLoadingMore) {
      generateQuery(parameters);
    }
  }, [isLoadingMore]);

  useEffect(() => {
    /* istanbul ignore else */
    if (
      (rememberedParams.length === 0 && parameters.length !== 1) ||
      rememberedParams.length > 0
    ) {
      generateQuery(parameters);
    }
  }, [parameters.length > 1]);

  const nestedCheck = (ele, valueObj) => {
    for (const key in ele) {
      const temp = ele[key];
      if (typeof temp[0] === 'object') {
        for (const nestedProp in temp[0]) {
          const nestedObj = {};
          const result = nestedCheck(temp[0], valueObj);
          if (valueObj.hasOwnProperty(nestedProp)) {
            valueObj[nestedProp] = result;
          } else {
            nestedObj[nestedProp] = result;
            valueObj = { ...valueObj, ...nestedObj };
          }
          return valueObj;
        }
      } else {
        const val = ele[key];
        const tempObj = {};
        tempObj[val[0]] = null;
        const firstKey = Object.keys(valueObj)[0];
        valueObj = { ...valueObj[firstKey], ...tempObj };
        return valueObj;
      }
    }
  };

  const checkFunc = (ele, valueObj) => {
    for (const key in ele) {
      const temp = ele[key];
      if (typeof temp[0] === 'object') {
        for (const nestedProp in temp[0]) {
          const nestedObj = {};
          if (valueObj.hasOwnProperty(nestedProp)) {
            const result = nestedCheck(temp[0], valueObj);
            valueObj[nestedProp] = result;
          } else {
            const result = checkFunc(temp[0], valueObj);
            nestedObj[nestedProp] = result;
            valueObj = { ...valueObj, ...nestedObj };
          }
          return valueObj;
        }
      } else {
        const val = ele[key];
        const tempObj = {};
        tempObj[val[0]] = null;
        valueObj = { ...valueObj, ...tempObj };
        return valueObj;
      }
    }
  };

  const validateResponse = (obj, paramFields) => {
    let contentObj = {};
    for (const prop in obj) {
      const arr = [];
      if (obj[prop] === null) {
        const parentObj = {};
        paramFields.map(params => {
          if (params.hasOwnProperty(prop)) {
            arr.push(params);
          }
        });
        let valueObj = {};
        arr.forEach(ele => {
          valueObj = checkFunc(ele, valueObj);
        });
        parentObj[prop] = valueObj;
        contentObj = { ...contentObj, ...parentObj };
      } else {
        const elseObj = {};
        elseObj[prop] = obj[prop];
        contentObj = { ...contentObj, ...elseObj };
      }
    }
    return contentObj;
  };

  async function generateQuery(paramFields) {
    setTableLoading(true);
    setEnableRefresh(true);
    if (columnPickerType && paramFields.length > 1) {
      const Query = query({
        operation: columnPickerType,
        fields: paramFields,
        variables: {
          pagination: {
            value: { offset: offsetVal, limit: pageSize },
            type: 'Pagination'
          }
        }
      });
      try {
        const response = await client.query({
          query: gql`
            ${Query.query}
          `,
          variables: Query.variables,
          fetchPolicy: enableCache ? 'cache-first' : 'network-only'
        });
        const firstKey = Object.keys(response.data)[0];
        if (response.data[firstKey].length > 0) {
          const resp = response.data;
          const respKeys = Object.keys(resp)[0];
          const tableContent = resp[respKeys];
          const finalResp = [];
          tableContent.map(content => {
            const finalObject = validateResponse(content, paramFields);
            finalResp.push(finalObject);
          });
          setColumnFilters(finalResp);
          setTableLoading(false);
          setDisplayTable(true);
          setEnableCache(false);
          setIsLoadingMore(false);
        } else {
          setTableLoading(false);
          setDisplayEmptyState(true);
          setEnableCache(false);
        }
      } catch (error) {
        setError(error);
      }
    } else {
      setTableLoading(false);
      setDisplayEmptyState(false);
      setDisplayTable(false);
    }
  }

  const fetchSchema = option => {
    return (
      !getQueryTypes.loading &&
      getQueryTypes.data.__schema &&
      getQueryTypes.data.__schema.queryType.find(item => {
        if (item.name === option.type.name) {
          return item;
        }
      })
    );
  };

  let childItems;
  let finalResult: any = [];

  const childSelectionItems = (_data, title, ...attr) => {
    let nestedTitles = '';
    childItems =
      !getQueryTypes.loading &&
      _data.map(group => {
        const label = title + ' / ' + attr.join();
        const childEle = (
          <SelectGroup
            label={label.replace(/\,/g, '')}
            key={Math.random()}
            id={group.name}
            value={title + group.name}
          >
            {group.fields
              .filter((item, _index) => {
                if (!nullTypes.includes(item.type.name)) {
                  const tempData = [];
                  const n = fetchSchema(item);
                  tempData.push(n);
                  nestedTitles = nestedTitles + ' / ' + item.name;
                  childSelectionItems(tempData, title, attr, nestedTitles);
                } else {
                  return item;
                }
              })
              .map(item => (
                <SelectOption
                  key={Math.random()}
                  value={item.name + title + group.name}
                >
                  {item.name}
                </SelectOption>
              ))}
          </SelectGroup>
        );
        return childEle;
      });
    finalResult.push(childItems);
  };
  const child = [];
  const selectionItems = _data => {
    !getPicker.loading &&
      _data
        .filter((group, index) => {
          if (group.type.kind !== 'SCALAR') {
            return group;
          } else {
            child.push(<SelectOption key={group.name} value={group.name} />);
          }
        })
        .map((group, index) => {
          let ele;
          ele = (
            <SelectGroup
              label={group.name}
              key={index}
              id={group.name}
              value={group.name}
            >
              {group.type.fields &&
                group.type.fields
                  .filter((item, _index) => {
                    if (!nullTypes.includes(item.type.name)) {
                      const tempData = [];
                      const _v = fetchSchema(item);
                      tempData.push(_v);
                      childSelectionItems(tempData, group.name, item.name);
                    } else {
                      if (item.type.kind !== 'LIST') {
                        return item;
                      }
                    }
                  })
                  .map((item, _index) => (
                    <SelectOption key={_index} value={item.name + group.name}>
                      {item.name}
                    </SelectOption>
                  ))}
            </SelectGroup>
          );

          !finalResult.includes(ele) && finalResult.push(ele);
        });
  };

  columnPickerType && selectionItems(data);
  const rootElement: any = (
    <SelectGroup label=" " key={Math.random()} id="" value=" ">
      {child}
    </SelectGroup>
  );
  finalResult = finalResult.flat();
  finalResult.unshift(rootElement);

  function getAllChilds(arr, comp) {
    const unique = arr
      .map(e => e[comp])
      .map((e, i, final) => final.indexOf(e) === i && i)
      .filter(e => arr[e])
      .map(e => arr[e]);

    return unique;
  }

  const onResetQuery = _parameters => {
    setOffsetVal(0);
    offsetVal = 0;
    setPageSize(10);
    pageSize = 10;
    generateQuery(_parameters);
    setIsLoadingMore(false);
  };

  const onRefresh = () => {
    if (enableRefresh && parameters.length > 1) {
      onResetQuery(parameters);
    }
  };

  return (
    <React.Fragment>
      {!getPicker.loading && columnPickerType && (
        <>
          <Select
            variant={SelectVariant.checkbox}
            aria-label="Select Input"
            onToggle={onToggle}
            onSelect={onSelect}
            selections={selected}
            isExpanded={isExpanded}
            placeholderText="Select Columns"
            ariaLabelledBy="Column Picker dropdown"
            id="columnPicker-dropdown"
            isGrouped
            maxHeight="60vh"
          >
            {getAllChilds(finalResult, 'props')}
          </Select>
          <Button
            variant="primary"
            onClick={() => {
              onResetQuery(parameters);
            }}
            id="apply-columns"
          >
            Apply columns
          </Button>
          <Button
            variant="plain"
            onClick={onRefresh}
            className="pf-u-m-md"
            id="refresh-button"
            aria-label={'Refresh list'}
          >
            <SyncIcon />
          </Button>
        </>
      )}
    </React.Fragment>
  );
};

export default DomainExplorerManageColumns;
