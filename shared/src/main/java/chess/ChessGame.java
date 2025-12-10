package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn = TeamColor.WHITE;
    private final ChessBoard mainBoard = new ChessBoard();
    private ChessPosition enPassant = null;
    private boolean gameOver = false;
    private TeamColor winner = null;
    private String whitePlayer;
    private String blackPlayer;


    public ChessGame() {
        mainBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    public ChessPiece getPieceAt(int r, int c) {
        if (r < 0 || r > 7 || c < 0 || c > 7) {
            return null;
        }
        return mainBoard.getPiece(new ChessPosition(r, c));
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (!mainBoard.isTaken(startPosition)) {
            return new ArrayList<>();
        }

        ChessPiece piece = mainBoard.getPiece(startPosition);
        Collection<ChessMove> moves = piece.pieceMoves(mainBoard, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        if (moves != null) {
            for (ChessMove move : moves) {
                ChessBoard tempBoard = new ChessBoard();
                setAnyBoard(tempBoard, mainBoard);

                ChessPiece pieceToMove = tempBoard.getPiece(move.getStartPosition());
                tempBoard.movePiece(move.getStartPosition(), move.getEndPosition(), pieceToMove);

                if (!isInCheckDifBoard(piece.getTeamColor(), tempBoard)) {
                    validMoves.add(move);
                }
            }
        }

        //castling
        if (piece.getPieceType() == ChessPiece.PieceType.KING && !piece.hasMoved()) {
            int row = startPosition.getRow();

            // King-side castling
            ChessPosition pos1 = new ChessPosition(row, 6);
            ChessPosition pos2 = new ChessPosition(row, 7);
            ChessPosition pos3 = new ChessPosition(row, 8);
            ChessPiece rookKingSide = mainBoard.getPiece(pos3);

            boolean rookKingExists = rookKingSide != null && rookKingSide.getPieceType() == ChessPiece.PieceType.ROOK;
            boolean spotNotTaken = !mainBoard.isTaken(pos1) && !mainBoard.isTaken(pos2);

            if ( rookKingExists && !rookKingSide.hasMoved() && spotNotTaken && !isInCheck(piece.getTeamColor())) {

                //check?
                ChessBoard tempBoard = new ChessBoard();
                setAnyBoard(tempBoard, mainBoard);
                ChessPiece kingCopy = tempBoard.getPiece(startPosition);
                tempBoard.movePiece(startPosition, pos1, kingCopy);
                if (!isInCheckDifBoard(piece.getTeamColor(), tempBoard)) {
                    tempBoard.movePiece(pos1, pos2, kingCopy);
                    if (!isInCheckDifBoard(piece.getTeamColor(), tempBoard)) {
                        validMoves.add(new ChessMove(startPosition, pos2, null));
                    }
                }
            }

            // Queen-side castling
            ChessPosition pos4 = new ChessPosition(row, 2);
            ChessPosition pos5 = new ChessPosition(row, 3);
            ChessPosition pos6 = new ChessPosition(row, 4);
            ChessPosition pos7 = new ChessPosition(row, 1);
            ChessPiece rookQueenSide = mainBoard.getPiece(pos7);

            boolean rookQueenExists = rookQueenSide != null && rookQueenSide.getPieceType() == ChessPiece.PieceType.ROOK;
            boolean spotFree = !mainBoard.isTaken(pos4) && !mainBoard.isTaken(pos5) && !mainBoard.isTaken(pos6);

            if ( rookQueenExists && !rookQueenSide.hasMoved() && spotFree && !isInCheck(piece.getTeamColor())) {

                //check>
                ChessBoard tempBoard = new ChessBoard();
                setAnyBoard(tempBoard, mainBoard);
                ChessPiece kingCopy = tempBoard.getPiece(startPosition);
                tempBoard.movePiece(startPosition, pos6, kingCopy);
                if (!isInCheckDifBoard(piece.getTeamColor(), tempBoard)) {
                    tempBoard.movePiece(pos6, pos5, kingCopy);
                    if (!isInCheckDifBoard(piece.getTeamColor(), tempBoard)) {
                        validMoves.add(new ChessMove(startPosition, pos5, null));
                    }
                }
            }
        }

        //enpassant
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && enPassant != null) {
            int direction = -1;
            if (piece.getTeamColor() == TeamColor.WHITE) {
                direction = 1;
            }
            int pawnRow = startPosition.getRow();
            int pawnCol = startPosition.getColumn();

            //pawn can capture enpassant if it is next to the en passant target
            if (pawnRow == enPassant.getRow() - direction &&
                    Math.abs(pawnCol - enPassant.getColumn()) == 1) {
                ChessPosition capturePos = new ChessPosition(enPassant.getRow(), enPassant.getColumn());
                ChessMove enPassantMove = new ChessMove(startPosition, capturePos, null);
                validMoves.add(enPassantMove);
            }
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = mainBoard.getPiece(start);

        if (piece == null) {
            throw new InvalidMoveException("No piece at position: " + start);
        }

        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Not your turn");
        }

        Collection<ChessMove> valid = validMoves(start);
        if (!valid.contains(move)) {
            throw new InvalidMoveException("Invalid move: " + move);
        }

        //castling
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            int row = start.getRow();

            if (end.getColumn() - start.getColumn() == 2) {
                ChessPosition rookStart = new ChessPosition(row, 8);
                ChessPosition rookEnd = new ChessPosition(row, 6);
                ChessPiece rook = mainBoard.getPiece(rookStart);
                mainBoard.movePiece(rookStart, rookEnd, rook);
                rook.setHasMoved(true);
            }

            else if (start.getColumn() - end.getColumn() == 2) {
                ChessPosition rookStart = new ChessPosition(row, 1);
                ChessPosition rookEnd = new ChessPosition(row, 4);
                ChessPiece rook = mainBoard.getPiece(rookStart);
                mainBoard.movePiece(rookStart, rookEnd, rook);
                rook.setHasMoved(true);
            }
        }

        //enpassant move
        //enPassant = null;

        //check for pawn double step
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && enPassant != null) {

            if (start.getColumn() != (end.getColumn()) && end.equals(enPassant)) {
                int capturedRow = (piece.getTeamColor() == TeamColor.WHITE) ? end.getRow() - 1 : end.getRow() + 1;
                ChessPosition capturedPawnPos = new ChessPosition(capturedRow, end.getColumn());
                mainBoard.setPositionNull(capturedPawnPos);
            }
        }


        //move the piece
        mainBoard.movePiece(start, end, piece);
        piece.setHasMoved(true);

        //enpassant check if can capture
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && Math.abs(end.getRow() - start.getRow()) == 2) {
            int enPassantRow = (start.getRow() + end.getRow()) / 2;
            enPassant = new ChessPosition(enPassantRow, start.getColumn());
        } else {
            enPassant = null; // reset if not a double-step pawn
        }

        // Handle pawn promotion
        if (move.getPromotionPiece() != null) {
            mainBoard.setPositionNull(end);
            ChessPiece promotion = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            mainBoard.addPiece(end, promotion);
        }

        // Flip turn
        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //find king piece
        ChessPosition position = getKing(teamColor);

        if (position == null) {
            return false;
        }

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = mainBoard.getPiece(pos);
                boolean check = inCheckHelper(mainBoard, position, pos, piece, teamColor);
                if (check) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean inCheckHelper(ChessBoard board, ChessPosition position, ChessPosition pos, ChessPiece piece, TeamColor teamColor) {
        if (piece != null && piece.getTeamColor() != teamColor) {
            for (ChessMove move : piece.pieceMoves(board, pos)) {
                if (move.getEndPosition().equals(position)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isInCheckDifBoard(TeamColor teamColor, ChessBoard board) {
        ChessPosition position = getKing(teamColor, board);
        if (position == null) {
            return false;
        }

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                boolean check = inCheckHelper(board, position, pos, piece, teamColor);
                if (check) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = mainBoard.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(pos);
                    if (!moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = mainBoard.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (!validMoves(pos).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        //mainBoard.resetBoard();
        setAnyBoard(mainBoard, board);
    }

    public void setAnyBoard(ChessBoard boardSet, ChessBoard boardSetTo) {
        //boardSet.resetBoard();
        boardSet.squares = new ChessPiece[8][8];
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = boardSetTo.getPiece(position);
                if (piece != null) {
                    ChessPiece pieceCopy = new ChessPiece(piece.getTeamColor(), piece.getPieceType());
                    boardSet.addPiece(position, pieceCopy);
                }
            }
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return mainBoard;
    }

    public ChessPosition getKing(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position =  new ChessPosition(i, j);
                ChessPiece piece = mainBoard.getPiece(position);
                if (piece != null) {
                    if (piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                        //System.out.println("KINGCOLOR: " + position);
                        return position;
                    }
                }
            }
        }
        return null;
    }

    public ChessPosition getKing(TeamColor teamColor, ChessBoard board) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor &&
                        piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return pos;
                }
            }
        }
        return null;
    }

    public boolean isValidMove(ChessMove move) {
        if (move == null) {
            return false;
        }

        ChessPosition start = move.getStartPosition();
        ChessPiece piece = mainBoard.getPiece(start);

        if (piece == null) {
            return false;
        }

        if (piece.getTeamColor() != teamTurn) {
            return false;
        }

        Collection<ChessMove> legalMoves = validMoves(start);

        return legalMoves.contains(move);
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void resign(TeamColor color) {
        if (gameOver) return;  // already finished

        gameOver = true;
        winner = (color == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    public TeamColor getWinner() {
        return winner;
    }

    public String getWhiteUsername() {
        return whitePlayer;
    }

    public String getBlackUsername() {
        return blackPlayer;
    }

    public void setWhiteUsername(String whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public void setBlackUsername(String blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(mainBoard, chessGame.mainBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, mainBoard);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", mainBoard=" + mainBoard +
                '}';
    }
}
