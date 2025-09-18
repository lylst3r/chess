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

    /*public static ArrayList<ChessMove> addMoves(int r, int c, int dr, int dc, ArrayList<ChessMove> m, ChessBoard board) {
        int startRow = r;
        int startCol = c;
        while(r > 1 && r < 8 && c > 1 && c < 8) {
            r = r + dr;
            c = c + dc;
            m.add(new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(r, c), null));
            if (board.isTaken(new ChessPosition(r,c)) == 1) {
                break;
            }

        }
        return m;
    }*/
}
