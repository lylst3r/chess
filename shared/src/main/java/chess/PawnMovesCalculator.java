package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.PieceMovesCalculator.addMove;
import static chess.PieceMovesCalculator.addMoves;

public class PawnMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        int i = position.getRow();
        int j = position.getColumn();

        //if first move can move two


        //promotion
        if (i+1 == 8) {
            moves = addMovePawn(i, j, 1, 0, moves, board, true); //up
            return moves;
        }

        //normal move
        moves = addMovePawn(i, j, 1, 0, moves, board, false); //up

        return moves;
    }

    public static ArrayList<ChessMove> addMovePawn(int r, int c, int dr, int dc, ArrayList<ChessMove> m, ChessBoard board, boolean promotion) {
        int startRow = r;
        int startCol = c;
        if (r+dr >= 1 && r+dr <= 8 && c+dc >= 1 && c+dc <= 8) {

            //if diagonals occupied by the opposite team move is allowed
            if (checkSpot(startRow, startCol, r+1, c-1, board) == 2) {
                m.add(new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(r+1, c-1), null));
            }
            if (checkSpot(startRow, startCol, r+1, c+1, board) == 2) {
                m.add(new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(r+1, c+1), null));
            }

            //normal move option
            r = startRow + dr;
            c = startCol + dc;
            if (checkSpot(startRow, startCol, r, c, board) != 0) {
                return m;
            }
            m.add(new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(r, c), null));

            if (startRow == 2 && checkSpot(startRow, startCol, r+1, c, board) == 0) {
                m.add(new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(r+1, c), null));
            }
        }
        return m;
    }

    //check if spot is taken and if so which team is on it
    public static int checkSpot(int startR, int startC, int r, int c, ChessBoard board) {
        //spot is taken by a piece on the same team
        if (board.isTaken(new ChessPosition(r, c)) && board.getPiece(new ChessPosition(startR, startC)).getTeamColor() == board.getPiece(new ChessPosition(r, c)).getTeamColor()) {
            return 1;
        }
        //spot is taken by a piece on the opposite team
        else if (board.isTaken(new ChessPosition(r, c)) && board.getPiece(new ChessPosition(startR, startC)).getTeamColor() != board.getPiece(new ChessPosition(r, c)).getTeamColor()) {
            return 2;
        }
        //spot is open
        else if (!board.isTaken(new ChessPosition(r, c))) {
            return 0;
        }
        return 4;
    }
}
