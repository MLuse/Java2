package hr.algebra.trirp1.tictactoe.tictactoe3rp11.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameMove implements Serializable {
    private Symbol symbol;
    private Position position;
}
