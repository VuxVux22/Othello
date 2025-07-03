import java.util.Collections;

public class SimpleAI {
 
    private GameGrid gameGrid;


    public SimpleAI(GameGrid gameGrid) {
        this.gameGrid = gameGrid;
    }

    public Position chooseMove() {
        Collections.shuffle(gameGrid.getAllValidMoves());
        return gameGrid.getAllValidMoves().get(0);
    }
}
