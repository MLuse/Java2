package hr.algebra.trirp1.tictactoe.tictactoe3rp11.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Position implements Serializable {
    private Integer row;
    private Integer column;
}
