import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameGrid extends Rectangle {
    private GridCell[][] grid;
    private int moveNumber;
    private List<Position> validMoves;

    public GameGrid(Position position, int width, int height, int gridWidth, int gridHeight) {
        super(position, width, height);
        grid = new GridCell[gridWidth][gridHeight];
        int cellWidth = (width-position.x)/gridWidth;
        int cellHeight = (height-position.y)/gridHeight;
        for(int x = 0; x < gridWidth; x++) {
            for(int y = 0; y < gridHeight; y++) {
                grid[x][y] = new GridCell(new Position(position.x+cellWidth*x, position.y+cellHeight*y),
                        cellWidth, cellHeight);
            }
        }
        moveNumber = 0;
        validMoves = new ArrayList<>();
        updateValidMoves(1);
    }

    public void reset() {
        for(int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                grid[x][y].reset();
            }
        }
        moveNumber = 0;
        updateValidMoves(1);
    }

    public List<Position> getAllValidMoves() {
        return validMoves;
    }

    public void playMove(Position position, int player) {
        moveNumber++;
        grid[position.x][position.y].setCellState(player);
        List<Position> changeCellPositions = getChangedPositionsForMove(position, player);
        for(Position swapPosition : changeCellPositions) {
            grid[swapPosition.x][swapPosition.y].setCellState(player);
        }
        updateValidMoves(player == 1 ? 2 : 1);
    }

    public Position convertMouseToGridPosition(Position mousePosition) {
        int gridX = (mousePosition.x- position.x)/grid[0][0].width;
        int gridY = (mousePosition.y- position.y)/grid[0][0].height;
        if(gridX >= grid.length || gridX < 0 || gridY >= grid[0].length || gridY < 0) {
            return new Position(-1,-1);
        }
        return new Position(gridX,gridY);
    }

    public boolean isValidMove(Position position) {
        return getAllValidMoves().contains(position);
    }

    public int getWinner(boolean stillValidMoves) {
        int[] counts = getPieceCounts();
        if(stillValidMoves && counts[0] > 0) return 0;
        else if(counts[1] == counts[2]) return 3;
        else return counts[1] > counts[2] ? 1 : 2;
    }

    public int[] getPieceCounts() {
        int[] counts = new int[3];
        for (int y = 0; y < grid[0].length; y++) {
            for (int x = 0; x < grid.length; x++) {
                counts[grid[x][y].getCellState()]++;
            }
        }
        return counts;
    }

    public void paint(Graphics g) {
        drawGridLines(g);
        for(int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                grid[x][y].paint(g);
            }
        }
    }

    private void drawGridLines(Graphics g) {
        g.setColor(new Color(30, 90, 50));
        int y2 = position.y+height;
        int y1 = position.y;
        for(int x = 0; x < grid.length+1; x++)
            g.drawLine(position.x+x * grid[0][0].width, y1, position.x+x * grid[0][0].width, y2);

        int x2 = position.x+width;
        int x1 = position.x;
        for(int y = 0; y < grid[0].length+1; y++)
            g.drawLine(x1, position.y+y * grid[0][0].height, x2, position.y+y * grid[0][0].height);
    }

    public void updateValidMoves(int playerID) {
        for(Position validMove : validMoves) {
            grid[validMove.x][validMove.y].setHighlight(false);
        }
        validMoves.clear();
        if(moveNumber < 4) {
            int midX = grid.length/2-1;
            int midY = grid[0].length/2-1;
            for (int x = midX; x < midX+2; x++) {
                for (int y = midY; y < midY+2; y++) {
                    if (grid[x][y].getCellState() == 0) {
                        validMoves.add(new Position(x, y));
                    }
                }
            }
        } else {
            for (int x = 0; x < grid.length; x++) {
                for (int y = 0; y < grid[0].length; y++) {
                    if (grid[x][y].getCellState() == 0 && getChangedPositionsForMove(new Position(x,y),playerID).size()>0) {
                        validMoves.add(new Position(x, y));
                    }
                }
            }
        }
        for(Position validMove : validMoves) {
            grid[validMove.x][validMove.y].setHighlight(true);
        }
    }

    public List<Position> getChangedPositionsForMove(Position position, int playerID) {
        List<Position> result = new ArrayList<>();
        result.addAll(getChangedPositionsForMoveInDirection(position, playerID, Position.DOWN));
        result.addAll(getChangedPositionsForMoveInDirection(position, playerID, Position.LEFT));
        result.addAll(getChangedPositionsForMoveInDirection(position, playerID, Position.UP));
        result.addAll(getChangedPositionsForMoveInDirection(position, playerID, Position.RIGHT));
        result.addAll(getChangedPositionsForMoveInDirection(position, playerID, new Position(1, 1)));
        result.addAll(getChangedPositionsForMoveInDirection(position, playerID, new Position(-1, 1)));
        result.addAll(getChangedPositionsForMoveInDirection(position, playerID, new Position(1, -1)));
        result.addAll(getChangedPositionsForMoveInDirection(position, playerID, new Position(-1, -1)));

        return result;
    }

    private List<Position> getChangedPositionsForMoveInDirection(Position position, int playerID, Position direction) {
        List<Position> result = new ArrayList<>();
        Position movingPos = new Position(position);
        int otherPlayer = playerID == 1 ? 2 : 1;
        movingPos.add(direction);
        while(inBounds(movingPos) && grid[movingPos.x][movingPos.y].getCellState() == otherPlayer) {
            result.add(new Position(movingPos));
            movingPos.add(direction);
        }
        if(!inBounds(movingPos) || grid[movingPos.x][movingPos.y].getCellState() != playerID) {
            result.clear();
        }
        return result;
    }

    private boolean inBounds(Position position) {
        return !(position.x < 0 || position.y < 0 || position.x >= grid.length || position.y >= grid[0].length);
    }
}