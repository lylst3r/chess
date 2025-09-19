package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.PieceMovesCalculator.addMove;
import static chess.PieceMovesCalculator.addMoves;

public class KingMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        int i = position.getRow();
        int j = position.getColumn();

        moves = addMove(i, j, -1, -1, moves, board); //down, left
        moves = addMove(i, j, -1, 1, moves, board); //down, right
        moves = addMove(i, j, 1, -1, moves, board); //up, left
        moves = addMove(i, j, 1, 1, moves, board); //up, right
        moves = addMove(i, j, -1, 0, moves, board); //down
        moves = addMove(i, j, 0, -1, moves, board); //left
        moves = addMove(i, j, 1, 0, moves, board); //up
        moves = addMove(i, j, 0, 1, moves, board); //right


        return moves;
    }
}
