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

    public ChessGame() {

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
        //check piece is there
        if (!mainBoard.isTaken(startPosition)) {
            return null;
        }

        if (mainBoard.isTaken(startPosition)) {
            //get piece moves
            ChessPiece piece = mainBoard.getPiece(startPosition);
            //System.out.println("PIECE: " + piece + " POSITION: " + startPosition);
            //Collection<ChessMove> moves = PieceMovesCalculator.pieceMoves(mainBoard, startPosition);
            Collection<ChessMove> moves = piece.pieceMoves(mainBoard, startPosition);
            //check if puts king in check
            Collection<ChessMove> validMoves = new ArrayList<>();

            for (ChessMove move : moves) {
                ChessBoard tempBoard = new ChessBoard();
                setAnyBoard(tempBoard, mainBoard);

                //add move to board
                ChessPosition positionStart = move.getStartPosition();
                ChessPosition positionEnd = move.getEndPosition();
                ChessPiece pieceToMove = tempBoard.getPiece(positionStart);
                tempBoard.movePiece(positionStart, positionEnd, pieceToMove);
                //System.out.println("PIECE MOVE: " + move.toString());
                if (!isInCheckDifBoard(piece.getTeamColor(), tempBoard)) {
                    validMoves.add(move);
                }
            }

            return validMoves;
        }

        return null;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        //ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = mainBoard.getPiece(startPosition);
        //mainBoard.movePiece(startPosition, endPosition, piece);

        if (piece == null) {
            throw new InvalidMoveException("No piece at position");
        }

        if (teamTurn != piece.getTeamColor()) {
            throw new InvalidMoveException("Not your turn");
        }

        Collection<ChessMove> valid = validMoves(startPosition);
        if (!valid.contains(move)) {
            throw new InvalidMoveException("Invalid move: " + move);
        }

        mainBoard.movePiece(startPosition, move.getEndPosition(), piece);

        //pawn promotion
        if (move.getPromotionPiece() != null) {
            mainBoard.setPositionNull(move.getEndPosition());
            ChessPiece promotionPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            mainBoard.addPiece(move.getEndPosition(), promotionPiece);
        }

        if (getTeamTurn() == TeamColor.WHITE) {
            teamTurn = TeamColor.BLACK;
        }
        else {
            teamTurn = TeamColor.WHITE;
        }
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
        //System.out.println("KING POSITION: " + position);
        //ChessPiece piece = mainBoard.getPiece(position);
        //get all moves from other team & check if one can move to the king's position
        //ArrayList<ChessPosition> attackingPositions = getAttackingPositions(position, teamColor);
        /*if (!attackingPositions.isEmpty()) {
            //System.out.println("IN CHECK");
            return true;
        }*/
        //System.out.println("NO CHECK");

        if (position == null) {
            return false;
        }

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = mainBoard.getPiece(pos);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    for (ChessMove move : piece.pieceMoves(mainBoard, pos)) {
                        if (move.getEndPosition().equals(position)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean isInCheckDifBoard(TeamColor teamColor, ChessBoard board) {
        ChessPosition position = getKing(teamColor, board);
        if (position == null) return false;

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    for (ChessMove move : piece.pieceMoves(board, pos)) {
                        if (move.getEndPosition().equals(position)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public ArrayList<ChessPosition> getAttackingPositions(ChessPosition position, TeamColor teamColor) {
        ArrayList<ChessPosition> attackingPositions = new ArrayList<>();
        ArrayList<ChessMove> otherTeamMoves = allTeamValidMoves(teamColor);
        for (ChessMove otherTeamMove : otherTeamMoves) {
            //System.out.println("OTHERTEAMMOVE: " + otherTeamMove.toString());
            if (otherTeamMove.getEndPosition().equals(position)) {
                attackingPositions.add(otherTeamMove.getStartPosition());
                //System.out.println("ATTACKING POSITION: " +  otherTeamMove.getStartPosition());
            }
        }
        //System.out.println("attackingPositions: " + attackingPositions);
        return attackingPositions;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //if in check
        if (isInCheck(teamColor)) {
            //can move somewhere?
            ChessPosition position = getKing(teamColor);
            ChessPiece piece = mainBoard.getPiece(position);
            Collection<ChessMove> kingMoves = validMoves(position);
            if (!kingMoves.isEmpty()) {
                return false;
            }

            //can capture attacking piece?
            ArrayList<ChessPosition> attackingPositions = getAttackingPositions(position, teamColor);
            if (attackingPositions.size() == 1) {
                TeamColor team = TeamColor.WHITE;
                if (teamColor == TeamColor.WHITE) {
                    team = TeamColor.BLACK;
                }
                ArrayList<ChessMove> currentTeamMoves = allTeamValidMoves(team);
                for (ChessMove currentTeamMove : currentTeamMoves) {
                    if (attackingPositions.contains(currentTeamMove.getEndPosition())) {
                        return false;
                    }
                }
            }

            //can block piece?

            //make a temp board
            ChessBoard tempBoard = mainBoard;
            //for each piece of current team in board
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 8; j++) {
                    ChessPosition piecePosition = new ChessPosition(i, j);
                    ChessPiece pieceToMove = mainBoard.getPiece(piecePosition);
                    //for each move for that piece
                    Collection<ChessMove> moves = validMoves(piecePosition);
                    for (ChessMove move : moves) {
                        mainBoard.movePiece(piecePosition, move.getEndPosition(), pieceToMove);
                        //will the king still be in check
                        if (!isInCheck(teamColor)) {
                            return false;
                        }
                        //mainBoard = tempBoard;
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
        if (isInCheck(teamColor)) return false;
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
        mainBoard.resetBoard();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null) {
                    ChessPiece pieceCopy = new ChessPiece(piece.getTeamColor(), piece.getPieceType());
                    mainBoard.addPiece(position, pieceCopy);
                }
                else {
                    mainBoard.setPositionNull(position);
                }
            }
        }
    }

    public void setAnyBoard(ChessBoard boardSet, ChessBoard boardSetTo) {
        boardSet.resetBoard();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = boardSetTo.getPiece(position);
                if (piece != null) {
                    ChessPiece pieceCopy = new ChessPiece(piece.getTeamColor(), piece.getPieceType());
                    boardSet.addPiece(position, pieceCopy);
                }
                else {
                    boardSet.setPositionNull(position);
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

    public ArrayList<ChessMove> allTeamValidMoves(TeamColor teamColor) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position =  new ChessPosition(i, j);
                ChessPiece piece = mainBoard.getPiece(position);
                if (piece != null) {
                    if (piece.getTeamColor() == teamColor) {
                        //validMoves.addAll(validMoves(position));
                        validMoves.addAll(piece.pieceMoves(mainBoard, position));
                        //System.out.println("PIECE: " + piece);
                    }
                }
            }
        }

        //System.out.println("ALLTEAMVALIDMOVES: " + validMoves);
        return validMoves;
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
