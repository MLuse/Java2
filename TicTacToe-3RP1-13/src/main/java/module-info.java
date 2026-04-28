module hr.algebra.trirp1.tictactoe.tictactoe3rp11 {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.jfr;
    requires static lombok;
    requires org.slf4j;
    requires java.desktop;
    requires java.rmi;
    requires java.naming;
    requires java.logging;


    opens hr.algebra.trirp1.tictactoe.tictactoe3rp11 to javafx.fxml;
    exports hr.algebra.trirp1.tictactoe.tictactoe3rp11;
    exports hr.algebra.trirp1.tictactoe.tictactoe3rp11.controller;
    opens hr.algebra.trirp1.tictactoe.tictactoe3rp11.controller to javafx.fxml;
    exports hr.algebra.trirp1.tictactoe.tictactoe3rp11.rmi;
}