package chess;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.PieceMovesCalculator.addMoves;

public class BishopMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        //4 directions (diagonals)
        //loop to go through every square on every direction
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        int i = position.getRow();
        int j = position.getColumn();

        moves = addMoves(i, j, -1, -1, moves, board); //down, left
        moves = addMoves(i, j, -1, 1, moves, board); //down, right
        moves = addMoves(i, j, 1, -1, moves, board); //up, left
        moves = addMoves(i, j, 1, 1, moves, board); //up, right


        return moves;
    }
}
