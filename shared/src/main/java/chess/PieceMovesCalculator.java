package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PieceMovesCalculator {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            return BishopMovesCalculator.pieceMoves(board, position);
            //return List.of(new ChessMove(new ChessPosition(5,4), new ChessPosition(1,8), null));
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            return RookMovesCalculator.pieceMoves(board, position);
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            return QueenMovesCalculator.pieceMoves(board, position);
        }
        return List.of();
    }

    public static ArrayList<ChessMove> addMoves(int r, int c, int dr, int dc, ArrayList<ChessMove> m, ChessBoard board) {
        int startRow = r;
        int startCol = c;
        while(r+dr >= 1 && r+dr <= 8 && c+dc >= 1 && c+dc <= 8) {
            r = r + dr;
            c = c + dc;
            if (board.isTaken(new ChessPosition(r, c)) && board.getPiece(new ChessPosition(startRow, startCol)).getTeamColor() == board.getPiece(new ChessPosition(r, c)).getTeamColor()) {
                break;
            }
            m.add(new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(r, c), null));
            if (board.isTaken(new ChessPosition(r, c))) {
                break;
            }

        }
        return m;
    }

}