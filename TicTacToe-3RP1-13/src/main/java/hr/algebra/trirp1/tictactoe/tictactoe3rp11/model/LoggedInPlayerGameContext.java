package hr.algebra.trirp1.tictactoe.tictactoe3rp11.model;

import hr.algebra.trirp1.tictactoe.tictactoe3rp11.utils.GameUtils;
import javafx.scene.control.Button;

public class LoggedInPlayerGameContext {

    private PlayerType playerType;
    private Symbol turn;
    private Integer gameNumber = 0;
    private Button[][] board = new Button[GameUtils.NUMBER_OF_ROWS][GameUtils.NUMBER_OF_COLUMNS];

    public PlayerType getPlayerType() {
        return playerType;
    }

    public void setPlayerType(PlayerType playerType) {
        this.playerType = playerType;
    }

    public Symbol getTurn() {
        return turn;
    }

    public void setTurn(Symbol turn) {
        this.turn = turn;
    }

    public Integer getGameNumber() {
        return gameNumber;
    }

    public void setGameNumber(Integer gameNumber) {
        this.gameNumber = gameNumber;
    }

    public Button[][] getBoard() {
        return board;
    }

    public void setBoard(Button[][] board) {
        this.board = board;
    }
}
