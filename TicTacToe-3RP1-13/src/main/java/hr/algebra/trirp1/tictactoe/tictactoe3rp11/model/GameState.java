package hr.algebra.trirp1.tictactoe.tictactoe3rp11.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameState implements java.io.Serializable {
    private String[][] board;
    private Symbol turn;
    private Boolean newGame;
    private Integer numberOfTurns;
}
