package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {


    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    public void movePiece(ChessPosition startPosition, ChessPosition endPosition, ChessPiece piece) {
        squares[startPosition.getRow()-1][startPosition.getColumn()-1] = null;
        squares[endPosition.getRow()-1][endPosition.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    public boolean isTaken(ChessPosition position) {
        if (getPiece(position) == null) {
            return false;
        }
        else {
            return true;
        }
    }

    public void setPositionNull(ChessPosition position) {
        squares[position.getRow()-1][position.getColumn()-1] = null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        //empty board
        //ChessPiece[][] newBoard = new ChessPiece[8][8];
        //squares = newBoard;
        squares = new ChessPiece[8][8];

        //create white pieces
        ChessPiece whiteRook1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        ChessPiece whiteRook2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        ChessPiece whiteKnight1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        ChessPiece whiteKnight2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        ChessPiece whiteBishop1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        ChessPiece whiteBishop2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        ChessPiece whiteQueen = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        ChessPiece whiteKing = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);

        //create black pieces
        ChessPiece blackRook1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPiece blackRook2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPiece blackKnight1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        ChessPiece blackKnight2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        ChessPiece blackBishop1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        ChessPiece blackBishop2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        ChessPiece blackQueen = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        ChessPiece blackKing = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);

        //add white pieces to board
        addPiece(new ChessPosition(1, 1), whiteRook1);
        addPiece(new ChessPosition(1, 8), whiteRook2);
        addPiece(new ChessPosition(1, 2), whiteKnight1);
        addPiece(new ChessPosition(1, 7), whiteKnight2);
        addPiece(new ChessPosition(1, 3), whiteBishop1);
        addPiece(new ChessPosition(1, 6), whiteBishop2);
        addPiece(new ChessPosition(1, 4), whiteQueen);
        addPiece(new ChessPosition(1, 5), whiteKing);

        //add black pieces to board
        addPiece(new ChessPosition(8, 1), blackRook1);
        addPiece(new ChessPosition(8, 8), blackRook2);
        addPiece(new ChessPosition(8, 2), blackKnight1);
        addPiece(new ChessPosition(8, 7), blackKnight2);
        addPiece(new ChessPosition(8, 3), blackBishop1);
        addPiece(new ChessPosition(8, 6), blackBishop2);
        addPiece(new ChessPosition(8, 4), blackQueen);
        addPiece(new ChessPosition(8, 5), blackKing);

        //add white pawns
        for (int i = 1; i <= 8; i++) {
            ChessPiece whitePawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            addPiece(new ChessPosition(2, i), whitePawn);
        }

        //add black pawns
        for (int i = 1; i <= 8; i++) {
            ChessPiece blackPawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            addPiece(new ChessPosition(7, i), blackPawn);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}';
    }
}
