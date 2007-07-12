#These are the DSL elements for Conways Game of Life.
[then]Kill the cell=theCell.queueNextCellState(CellState.DEAD);
[when]A live cell has fewer than {number} live neighbors=theCell: Cell(numberOfLiveNeighbors < {number}, cellState == CellState.LIVE)
[when]A live cell has more than {number} live neighbors=theCell: Cell(numberOfLiveNeighbors > {number}, cellState == CellState.LIVE)
[when]A dead cell has {number} live neighbors=theCell: Cell(numberOfLiveNeighbors == {number}, cellState == CellState.DEAD)
[then]Bring the cell to life=theCell.queueNextCellState(CellState.LIVE);
