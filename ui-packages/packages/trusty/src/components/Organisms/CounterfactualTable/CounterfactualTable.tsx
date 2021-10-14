import React, {
  useCallback,
  useContext,
  useEffect,
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
import {
  Bullseye,
  Button,
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateVariant,
  Label,
  Skeleton,
  Title
} from '@patternfly/react-core';
import {
  AngleLeftIcon,
  AngleRightIcon,
  GhostIcon,
  PlusCircleIcon,
  StarIcon
} from '@patternfly/react-icons';
import { Scrollbars } from 'react-custom-scrollbars';
import CounterfactualInputDomain from '../../Molecules/CounterfactualInputDomain/CounterfactualInputDomain';
import {
  CFAnalysisResult,
  CFExecutionStatus,
  CFSearchInput,
  CFStatus
} from '../../../types';
import { CFDispatch } from '../CounterfactualAnalysis/CounterfactualAnalysis';
import FormattedValue from '../../Atoms/FormattedValue/FormattedValue';
import './CounterfactualTable.scss';
import { isInputTypeSupported } from '../../Templates/Counterfactual/counterfactualReducer';

type CounterfactualTableProps = {
  inputs: CFSearchInput[];
  results: CFAnalysisResult[];
  status: CFStatus;
  onOpenInputDomainEdit: (input: CFSearchInput, inputIndex: number) => void;
  containerWidth: number;
};

const CounterfactualTable = (props: CounterfactualTableProps) => {
  const {
    inputs,
    results,
    status,
    onOpenInputDomainEdit,
    containerWidth
  } = props;
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
  const [isInputSelectionEnabled, setIsInputSelectionEnabled] = useState<
    boolean
  >();
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
    const width = scrollbars.current.getClientWidth();
    const scrollWidth = scrollbars.current.getScrollWidth();
    const currentPosition = scrollbars.current.getScrollLeft();

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
    if (isSelected && typeof rows[rowId].value === 'boolean') {
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
      return isInputSelectionEnabled && isInputTypeSupported(input);
    },
    [isInputSelectionEnabled]
  );

  useEffect(() => {
    if (results.length > 0) {
      const ids = results.map(item => item.solutionId);
      const firstNewIndex = ids.findIndex(x => newResults.includes(x));
      const newItems = firstNewIndex > -1 ? ids.slice(0, firstNewIndex) : ids;
      if (newItems.length > 0 && newItems.join() !== newResults.join()) {
        setNewResults(newItems);
      }
    }
  }, [results, newResults]);

  useEffect(() => {
    setRows(inputs);
    setAreAllRowsSelected(inputs.find(input => input.fixed) === undefined);
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
      {containerWidth <= 880 && (
        <Bullseye>
          <EmptyState variant={EmptyStateVariant.xs}>
            <EmptyStateIcon icon={GhostIcon} />
            <Title headingLevel="h4" size="md">
              CF implementation for mobile phones
            </Title>
            <EmptyStateBody>workin on it</EmptyStateBody>
          </EmptyState>
        </Bullseye>
      )}
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
                          displayedResults[0].map(result => (
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
                            displayedResults[0].map(result => (
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
                              isSelected: row.fixed === false,
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
                              isInputTypeSupported={isInputTypeSupported}
                              onEditConstraint={onOpenInputDomainEdit}
                            />
                          </Td>
                          <Td key={`${rowIndex}_3`} dataLabel={columns[2]}>
                            {row.components === null && (
                              <FormattedValue value={row.value} />
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
                                    value !== row.value &&
                                    row.components === null
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
                                <Td className="cf-table__no-result-cell">
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
  onEditConstraint: (row: CFSearchInput, rowIndex: number) => void;
};

const ConstraintCell = (props: ConstraintCellProps) => {
  const {
    row,
    rowIndex,
    isInputSelectionEnabled,
    isInputTypeSupported,
    onEditConstraint
  } = props;

  const isTypeSupported = isInputTypeSupported(row);
  const isConstraintEditEnabled = isInputSelectionEnabled && isTypeSupported;

  return (
    <>
      {!isInputSelectionEnabled && row.domain && (
        <CounterfactualInputDomain input={row} />
      )}
      {isConstraintEditEnabled && (
        <Button
          variant={'link'}
          isInline={true}
          onClick={() => onEditConstraint(row, rowIndex)}
          icon={!row.domain && <PlusCircleIcon />}
          isDisabled={row.fixed}
          className={'counterfactual-constraint-edit'}
        >
          {row.domain ? (
            <CounterfactualInputDomain input={row} />
          ) : (
            <>Constraint</>
          )}
        </Button>
      )}
      {!isTypeSupported && <em>Not yet supported</em>}
    </>
  );
};

const convertCFResultsInputs = (results: CFAnalysisResult[]) => {
  const rows = [];
  if (results.length) {
    rows.push([]);
    results.forEach(result => {
      rows[0].push({ value: result.solutionId, stage: result.stage });
      result.inputs.forEach((input, inputIndex) => {
        if (!rows[inputIndex + 1]) {
          rows.push([]);
        }
        rows[inputIndex + 1].push(input.components === null ? input.value : '');
      });
    });
  }
  return rows;
};
