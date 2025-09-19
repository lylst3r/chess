package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.PieceMovesCalculator.addMoves;

public class RookMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        int i = position.getRow();
        int j = position.getColumn();

        moves = addMoves(i, j, -1, 0, moves, board); //down
        moves = addMoves(i, j, 0, -1, moves, board); //left
        moves = addMoves(i, j, 1, 0, moves, board); //up
        moves = addMoves(i, j, 0, 1, moves, board); //right


        return moves;
    }
}
