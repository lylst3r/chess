package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {

    public PieceMovesCalculator() {

    }

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            return BishopMovesCalculator.pieceMoves(board, position);
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            return RookMovesCalculator.pieceMoves(board, position);
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            return QueenMovesCalculator.pieceMoves(board, position);
        } else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            return KingMovesCalculator.pieceMoves(board, position);
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            return KnightMovesCalculator.pieceMoves(board, position);
        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            return PawnMovesCalculator.pieceMoves(board, position);
        }

        return null;
    }

    public static ArrayList<ChessMove> addMoves(int row, int col, int rowDir, int colDir, ArrayList<ChessMove> moves, ChessBoard board) {
        int startRow = row;
        int startCol = col;
        ChessPosition startPosition = new ChessPosition(startRow, startCol);
        row = row + rowDir;
        col = col + colDir;

        while (row >= 1 && row <= 8 && col >= 1 && col <= 8) {

            ChessPosition position = new ChessPosition(row, col);
            if (board.isTaken(position) && board.getPiece(position).getTeamColor() == board.getPiece(startPosition).getTeamColor()) {
                return moves;
            } else if (board.isTaken(position)) {
                moves.add(new ChessMove(startPosition, position, null));
                return moves;
            } else {
                moves.add(new ChessMove(startPosition, position, null));
                row = row + rowDir;
                col = col + colDir;
            }
        }

        return moves;
    }

    public static ArrayList<ChessMove> addMove(int row, int col, int rowDir, int colDir, ArrayList<ChessMove> moves, ChessBoard board) {
        int startRow = row;
        int startCol = col;
        ChessPosition startPosition = new ChessPosition(startRow, startCol);
        row = row + rowDir;
        col = col + colDir;

        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {

            ChessPosition position = new ChessPosition(row, col);
            if (board.isTaken(position) && board.getPiece(position).getTeamColor() == board.getPiece(startPosition).getTeamColor()) {
                return moves;
            } else {
                moves.add(new ChessMove(startPosition, position, null));
            }

        }
        return moves;
    }
}

