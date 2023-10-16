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
import React, {
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useRef,
  useState
} from 'react';
import {
  TableComposable,
  Tbody,
  Td,
  Th,
  Thead,
  Tr
} from '@patternfly/react-table';
import { Button, Label, Skeleton } from '@patternfly/react-core';
import {
  AngleLeftIcon,
  AngleRightIcon,
  PlusCircleIcon,
  StarIcon
} from '@patternfly/react-icons';
import { Scrollbars } from 'react-custom-scrollbars';
import CounterfactualInputDomain from '../../Molecules/CounterfactualInputDomain/CounterfactualInputDomain';
import {
  CFAnalysisResult,
  CFExecutionStatus,
  CFSearchInput,
  CFSearchInputUnit,
  CFStatus
} from '../../../types';
import { CFDispatch } from '../CounterfactualAnalysis/CounterfactualAnalysis';
import FormattedValue from '../../Atoms/FormattedValue/FormattedValue';
import './CounterfactualTable.scss';
import {
  isInputConstraintSupported,
  isSearchInputTypeSupportedForCounterfactual
} from '../../Templates/Counterfactual/counterfactualReducer';

type CounterfactualTableProps = {
  inputs: CFSearchInput[];
  results: CFAnalysisResult[];
  status: CFStatus;
  onOpenInputDomainEdit: (input: CFSearchInput, inputIndex: number) => void;
  containerWidth: number;
};

const CounterfactualTable = (props: CounterfactualTableProps) => {
  const { inputs, results, status, onOpenInputDomainEdit, containerWidth } =
    props;
  const dispatch = useContext(CFDispatch);
  const columns = [
    'Input',
    'Constraint',
    'Input Value',
    'Counterfactual result'
  ];
  const [rows, setRows] = useState<CFSearchInput[]>(inputs);
  const [areAllRowsSelected, setAreAllRowsSelected] = useState(false);
  const [isScrollDisabled, setIsScrollDisabled] = useState({
    prev: true,
    next: false
  });
  const [displayedResults, setDisplayedResults] = useState(
    convertCFResultsInputs(results)
  );
  const [isInputSelectionEnabled, setIsInputSelectionEnabled] =
    useState<boolean>();
  const [newResults, setNewResults] = useState<string[]>([]);

  const scrollbars = useRef(null);

  useEffect(() => {
    setIsInputSelectionEnabled(
      status.executionStatus === CFExecutionStatus.NOT_STARTED
    );
  }, [status]);

  const slideResults = (action: 'next' | 'prev') => {
    // current scrolling position
    const currentPosition = scrollbars.current.getScrollLeft();
    // window width
    const width = window.innerWidth;
    // size of a result column
    const stepSize = width > 1600 ? 250 : 200;
    let scrollIndex;
    switch (action) {
      case 'prev':
        scrollIndex = Math.abs(Math.round(currentPosition / stepSize) - 1);
        break;
      case 'next':
        scrollIndex = Math.round(currentPosition / stepSize) + 1;
        break;
    }
    const scrollPosition = stepSize * scrollIndex;
    scrollbars.current.view.scroll({
      left: scrollPosition,
      behavior: 'smooth'
    });
  };

  const onScrollUpdate = useCallback(() => {
    const width = scrollbars.current?.getClientWidth();
    const scrollWidth = scrollbars.current?.getScrollWidth();
    const currentPosition = scrollbars.current?.getScrollLeft();

    if (scrollWidth === width) {
      setIsScrollDisabled({ prev: true, next: true });
    } else {
      if (scrollWidth - currentPosition - width < 10) {
        // disabling next button when reaching the right limit (with some tolerance)
        setIsScrollDisabled({ prev: false, next: true });
      } else if (currentPosition < 10) {
        // disabling prev button when at the start (again with tolerance)
        setIsScrollDisabled({ prev: true, next: false });
      } else {
        setIsScrollDisabled({ prev: false, next: false });
      }
    }
  }, [scrollbars]);

  const onSelectAll = (event, isSelected: boolean) => {
    dispatch({
      type: 'CF_TOGGLE_ALL_INPUTS',
      payload: { selected: isSelected }
    });
  };

  const onSelect = (event, isSelected, rowId) => {
    dispatch({
      type: 'CF_TOGGLE_INPUT',
      payload: { searchInputIndex: rowId }
    });
    // boolean search domain hack
    if (
      isSelected &&
      rows[rowId].value.kind === 'UNIT' &&
      typeof (rows[rowId].value as CFSearchInputUnit).originalValue.value ===
        'boolean'
    ) {
      dispatch({
        type: 'CF_SET_INPUT_DOMAIN',
        payload: {
          inputIndex: rowId,
          domain: {
            type: 'CATEGORICAL',
            categories: ['false']
          }
        }
      });
    }
  };

  const canSelectInput = useCallback(
    (input: CFSearchInput) => {
      return (
        isInputSelectionEnabled &&
        isSearchInputTypeSupportedForCounterfactual(input)
      );
    },
    [isInputSelectionEnabled]
  );

  useEffect(() => {
    if (results.length > 0) {
      const ids = results.map((item) => item.solutionId);
      const firstNewIndex = ids.findIndex((x) => newResults.includes(x));
      const newItems = firstNewIndex > -1 ? ids.slice(0, firstNewIndex) : ids;
      if (newItems.length > 0 && newItems.join() !== newResults.join()) {
        setNewResults(newItems);
      }
    }
  }, [results, newResults]);

  useEffect(() => {
    setRows(inputs);
    setAreAllRowsSelected(
      inputs
        .filter((input) => input.value.kind === 'UNIT')
        .find((input) => (input.value as CFSearchInputUnit).fixed) === undefined
    );
  }, [inputs]);

  useEffect(() => {
    setDisplayedResults(convertCFResultsInputs(results));
  }, [results]);

  useEffect(() => {
    onScrollUpdate();
  }, [displayedResults, onScrollUpdate]);

  const handleScrollbarRendering = (cssClass: string) => {
    return ({ style, ...trackProps }) => {
      return <div style={{ ...style }} {...trackProps} className={cssClass} />;
    };
  };

  return (
    <>
      {containerWidth > 880 && (
        <div className="cf-table-outer-container">
          <div className="cf-table-inner-container">
            {containerWidth > 0 && (
              <div
                className="cf-table-container cf-table-container--with-results"
                style={{ width: containerWidth }}
              >
                <Scrollbars
                  style={{ width: containerWidth, height: '100%' }}
                  renderTrackHorizontal={handleScrollbarRendering(
                    'cf-table__scroll-track--horizontal'
                  )}
                  renderThumbHorizontal={handleScrollbarRendering(
                    'cf-table__scroll-thumb--horizontal'
                  )}
                  renderThumbVertical={handleScrollbarRendering(
                    'cf-table__scroll-thumb--vertical'
                  )}
                  renderView={handleScrollbarRendering(
                    'box cf-table__scroll-area'
                  )}
                  onScrollStop={onScrollUpdate}
                  ref={scrollbars}
                >
                  <TableComposable
                    aria-label="Counterfactual Table"
                    className="cf-table cf-table--with-results"
                  >
                    <Thead>
                      <Tr>
                        {isInputSelectionEnabled ? (
                          <Th
                            select={{
                              onSelect: onSelectAll,
                              isSelected: areAllRowsSelected
                            }}
                          />
                        ) : (
                          <Th />
                        )}

                        <Th
                          info={{
                            tooltip: 'Inputs to the decision model.'
                          }}
                        >
                          {columns[0]}
                        </Th>
                        <Th
                          info={{
                            tooltip:
                              'Limits the data used in the counterfactual.'
                          }}
                        >
                          {columns[1]}
                        </Th>
                        <Th
                          info={{
                            tooltip:
                              'The original input value used by the decision model.'
                          }}
                        >
                          {columns[2]}
                        </Th>
                        {displayedResults.length > 1 && (
                          <Th className="cf-table__slider-cell">
                            <Button
                              variant="link"
                              isInline={true}
                              aria-label="Previous results"
                              className="cf-table__slider-cell__slider"
                              isDisabled={isScrollDisabled.prev}
                              onClick={() => slideResults('prev')}
                            >
                              <AngleLeftIcon />
                            </Button>
                          </Th>
                        )}
                        {displayedResults.length > 0 &&
                          displayedResults[0].map((result) => (
                            <Th
                              key={`result ${result.value}`}
                              className={
                                newResults.includes(result.value)
                                  ? 'cf-table__result--new'
                                  : ''
                              }
                            >
                              <span>Counterfactual Result</span>
                            </Th>
                          ))}
                        {displayedResults.length > 1 && (
                          <Th className="cf-table__slider-cell">
                            <Button
                              variant="link"
                              isInline={true}
                              aria-label="Next results"
                              className="cf-table__slider-cell__slider"
                              isDisabled={isScrollDisabled.next}
                              onClick={() => slideResults('next')}
                            >
                              <AngleRightIcon />
                            </Button>
                          </Th>
                        )}
                        {displayedResults.length === 0 &&
                          status.executionStatus ===
                            CFExecutionStatus.RUNNING && (
                            <>
                              <Th key="empty-results-1">
                                <span>Counterfactual Result</span>
                              </Th>
                              <Th key="empty-results-2">
                                <span>Counterfactual Result</span>
                              </Th>
                            </>
                          )}
                        {displayedResults.length === 0 &&
                          status.executionStatus ===
                            CFExecutionStatus.NOT_STARTED && (
                            <>
                              <Th
                                className="cf-table__no-result-cell"
                                key="empty-results-1"
                              >
                                <span>Counterfactual Result</span>
                              </Th>
                            </>
                          )}
                      </Tr>
                    </Thead>
                    <Tbody>
                      {displayedResults.length > 1 && (
                        <Tr key="id-row" className="cf-table__ids-row">
                          <Td key="id-row_0" />
                          <Td key="id-row_1" />
                          <Td key="id-row_2" />
                          <Td key="id-row_3" />
                          <Td key="id-row_4" />
                          {displayedResults.length > 0 &&
                            displayedResults[0].map((result) => (
                              <Td
                                key={`id-row_${result.value}`}
                                dataLabel={'Counterfactual Result'}
                                className={
                                  newResults.includes(result.value)
                                    ? 'cf-table__result--new'
                                    : ''
                                }
                              >
                                {result.stage === 'INTERMEDIATE' ? (
                                  <Label variant="outline" color="blue">
                                    ID #{result.value.substring(0, 7)}
                                  </Label>
                                ) : (
                                  <Label icon={<StarIcon />} color="green">
                                    ID #{result.value.substring(0, 7)}
                                  </Label>
                                )}
                              </Td>
                            ))}
                          <Td
                            key="id-row_6"
                            className={'cf-table__slider-cell'}
                          />
                        </Tr>
                      )}
                      {rows.map((row, rowIndex) => (
                        <Tr key={rowIndex}>
                          <Td
                            key={`${rowIndex}_0`}
                            select={{
                              rowIndex,
                              onSelect,
                              isSelected:
                                row.value.kind === 'UNIT' &&
                                (row.value as CFSearchInputUnit).fixed ===
                                  false,
                              disable: !canSelectInput(row)
                            }}
                          />
                          <Td key={`${rowIndex}_1`} dataLabel={columns[0]}>
                            {row.name}
                          </Td>
                          <Td key={`${rowIndex}_2`} dataLabel={columns[1]}>
                            <ConstraintCell
                              row={row}
                              rowIndex={rowIndex}
                              isInputSelectionEnabled={isInputSelectionEnabled}
                              isInputTypeSupported={
                                isSearchInputTypeSupportedForCounterfactual
                              }
                              isInputConstraintSupported={
                                isInputConstraintSupported
                              }
                              onEditConstraint={onOpenInputDomainEdit}
                            />
                          </Td>
                          <Td key={`${rowIndex}_3`} dataLabel={columns[2]}>
                            {row.value.kind === 'UNIT' && (
                              <FormattedValue
                                value={
                                  (row.value as CFSearchInputUnit).originalValue
                                    .value
                                }
                              />
                            )}
                          </Td>
                          {displayedResults.length > 1 && (
                            <Td className="cf-table__slider-cell" />
                          )}
                          {displayedResults.length > 0 &&
                            displayedResults[rowIndex + 1].map(
                              (value, index) => (
                                <Td
                                  key={displayedResults[0][index].value}
                                  dataLabel={'Counterfactual Result'}
                                  className={`cf-table__result-id${
                                    displayedResults[0][index].value
                                  } ${
                                    newResults.includes(
                                      displayedResults[0][index].value
                                    )
                                      ? 'cf-table__result--new'
                                      : ''
                                  }
                                  ${
                                    row.value.kind === 'UNIT' &&
                                    value !== row.value.originalValue.value
                                      ? 'cf-table__result-value--changed'
                                      : 'cf-table__result-value'
                                  }
                                  `}
                                >
                                  <FormattedValue value={value} round={true} />
                                </Td>
                              )
                            )}
                          {displayedResults.length > 1 && (
                            <Td className="cf-table__slider-cell" />
                          )}
                          {displayedResults.length === 0 &&
                            status.executionStatus ===
                              CFExecutionStatus.RUNNING && (
                              <>
                                <Td key="results-loading-1">
                                  <Skeleton
                                    width="85%"
                                    screenreaderText="Loading results"
                                  />
                                </Td>
                                <Td key="results-loading-2">
                                  <Skeleton
                                    width="85%"
                                    screenreaderText="Loading results"
                                  />
                                </Td>
                              </>
                            )}
                          {displayedResults.length === 0 &&
                            status.executionStatus ===
                              CFExecutionStatus.NOT_STARTED && (
                              <>
                                <Td
                                  dataLabel={'Counterfactual Result'}
                                  className="cf-table__no-result-cell"
                                >
                                  No available results
                                </Td>
                              </>
                            )}
                        </Tr>
                      ))}
                    </Tbody>
                  </TableComposable>
                </Scrollbars>
              </div>
            )}
          </div>
        </div>
      )}
    </>
  );
};

export default CounterfactualTable;

type ConstraintCellProps = {
  row: CFSearchInput;
  rowIndex: number;
  isInputSelectionEnabled: boolean;
  isInputTypeSupported: (input: CFSearchInput) => boolean;
  isInputConstraintSupported: (input: CFSearchInput) => boolean;
  onEditConstraint: (row: CFSearchInput, rowIndex: number) => void;
};

const ConstraintCell = (props: ConstraintCellProps) => {
  const {
    row,
    rowIndex,
    isInputSelectionEnabled,
    isInputTypeSupported,
    isInputConstraintSupported,
    onEditConstraint
  } = props;

  const isTypeSupported = isInputTypeSupported(row);
  const isConstraintSupported = isInputConstraintSupported(row);
  const isConstraintEditEnabled =
    isInputSelectionEnabled && isConstraintSupported;

  const unit = useMemo(
    () =>
      row.value.kind === 'UNIT' ? (row.value as CFSearchInputUnit) : undefined,
    [row]
  );
  const unsupported = <em>Not yet supported</em>;

  //This should not be possible but let's not assume...
  if (row.value.kind !== 'UNIT') {
    return unsupported;
  }

  return (
    <>
      {!isInputSelectionEnabled && unit.domain && (
        <CounterfactualInputDomain input={unit} />
      )}
      {isConstraintEditEnabled && (
        <Button
          variant={'link'}
          isInline={true}
          onClick={() => onEditConstraint(row, rowIndex)}
          icon={!unit.domain && <PlusCircleIcon />}
          isDisabled={unit.fixed}
          className={'counterfactual-constraint-edit'}
        >
          {unit.domain ? (
            <CounterfactualInputDomain input={unit} />
          ) : (
            <>Constraint</>
          )}
        </Button>
      )}
      {!isTypeSupported && unsupported}
    </>
  );
};

const convertCFResultsInputs = (results: CFAnalysisResult[]) => {
  const rows = [];
  if (results.length) {
    rows.push([]);
    results.forEach((result) => {
      rows[0].push({ value: result.solutionId, stage: result.stage });
      result.inputs.forEach((input, inputIndex) => {
        if (!rows[inputIndex + 1]) {
          rows.push([]);
        }
        rows[inputIndex + 1].push(
          input.value.kind === 'UNIT' ? input.value.value : ''
        );
      });
    });
  }
  return rows;
};
