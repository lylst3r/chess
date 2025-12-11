package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import exception.ResponseException;
import server.ServerFacade;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static websocket.commands.UserGameCommand.CommandType.MAKE_MOVE;
import static websocket.commands.UserGameCommand.CommandType.RESIGN;

public class GameplayUI implements NotificationHandler {
    private final ServerFacade server;
    private final UIHelper uiHelper;
    private WebSocketFacade ws;


    public GameplayUI(String serverUrl, UIHelper uiHelper) {
        server = new ServerFacade(serverUrl);
        this.uiHelper = uiHelper;
    }

    public void run() {
        //System.out.println(uiHelper.getGame().gameName());
        try {
            ws = new WebSocketFacade(server.getServerUrl(), this);

            ws.send(new ConnectCommand(
                    uiHelper.getAuthToken(),
                    uiHelper.getGameID()
            ));
        } catch (Exception ex) {
            System.out.println("WebSocket failed: " + ex.getMessage());
            return;
        }

        try {
            reprintBoard();
        } catch (ResponseException ex) {
            System.out.println("couldn't print board");
        }

        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (true) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);

                if (result.equals("leave")) {
                    return;
                }

                if (result.equals("quit")) {
                    System.out.println();
                    System.exit(0);
                }

                System.out.print(result);
            } catch (Throwable e) {
                System.out.print(e.getMessage());
            }
        }
    }

    private void printPrompt() {
        System.out.print("\n" + "[IN_GAME] >>> ");
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "make-move" -> makeMove();
                case "highlight-moves" -> highlightMoves();
                case "reprint" -> reprintBoard();
                case "resign" -> resign();
                case "leave" -> leave(params);
                case "quit" ->  quit(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return "Error: " + ex.getMessage();
        }
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {

            case LOAD_GAME -> {
                LoadGameMessage m = (LoadGameMessage) message;
                uiHelper.setGame(m.getGame());

                if (uiHelper.getColor() == null || uiHelper.getColor().isEmpty()) {
                    uiHelper.setColor(uiHelper.getGame().whiteUsername().equals(uiHelper.getAuth().username()) ? "LIGHT" : "DARK");
                }

                try {
                    reprintBoard();
                } catch (ResponseException e) {
                    System.out.println("Error printing board: " + e.getMessage());
                }
            }

            case NOTIFICATION -> {
                NotificationMessage m = (NotificationMessage) message;
                System.out.println(m.getMessage());

                try {
                    reprintBoard();
                } catch (ResponseException e) {
                    System.out.println("Error updating board: " + e.getMessage());
                }
            }

            case ERROR -> {
                ErrorMessage m = (ErrorMessage) message;
                System.out.println("Server error: " + m.getErrorMessage());
            }

            default -> System.out.println("Unknown message: " + message);
        }
    }



    private String printBoard(boolean isWhitePerspective, int one, int two) {
        //String[][] board = initialBoard();
        String[][] board = getBoardFromGame(isWhitePerspective, one, two);

        String[] cols = isWhitePerspective ?
                new String[]{"a","b","c","d","e","f","g","h"} :
                new String[]{"h","g","f","e","d","c","b","a"};

        System.out.print(" ");
        for (String c : cols) {
            System.out.print(" " + c + "  ");
        }
        System.out.println();

        if (isWhitePerspective) {
            for (int row = 7; row >= 0; row--) {
                System.out.print((row + 1) + " ");

                for (int col = 0; col < 8; col++) {
                    printBoardHelper(row, col, board, null, null);
                }

                System.out.println(" " + (row + 1));
            }
        } else {
            for (int row = 0; row < 8; row++) {
                System.out.print((row + 1) + " ");

                for (int col = 7; col >= 0; col--) {
                    printBoardHelper(row, col, board, null, null);
                }

                System.out.println(" " + (row + 1));
            }
        }

        System.out.print(" ");
        for (String c : cols) {
            System.out.print(" " + c + "  ");
        }
        System.out.println("\n");

        return "";
    }

    private String[][] getBoardFromGame(boolean isWhitePerspective, int highlightRow, int highlightCol) {
        String[][] board = new String[8][8];

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                board[r][c] = EscapeSequences.EMPTY;
            }
        }

        if (uiHelper.getGame() == null || uiHelper.getGame().game() == null) {
            return board;
        }

        ChessGame game = uiHelper.getGame().game();

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = game.getPieceAt(pos);
                if (piece != null) {
                    int r = row - 1;
                    int c = col - 1;

                    String uni = piece.getUnicodeSymbol();
                    String colorCode = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ?
                            EscapeSequences.SET_TEXT_COLOR_WHITE :
                            EscapeSequences.SET_TEXT_COLOR_BLACK;

                    board[r][c] = colorCode + uni + EscapeSequences.RESET_TEXT_COLOR;
                }
            }
        }

        if (highlightRow >= 0 && highlightRow < 8 && highlightCol >= 0 && highlightCol < 8) {
            board[highlightRow][highlightCol] = EscapeSequences.SET_BG_COLOR_MAGENTA +
                    board[highlightRow][highlightCol] +
                    EscapeSequences.RESET_BG_COLOR;
        }

        return board;
    }




    private String pad(String s) {
        String clean = s.replaceAll("\u001B\\[[;\\d]*m", "");

        int len = clean.length();
        if (len == 0) {
            return "   ";
        }
        if (len == 1) {
            return " " + s + " ";
        }
        if (len == 2) {
            return " " + s;
        }
        return s;
    }

    public void printBoardHelper(int row, int col, String[][] board, boolean[][] highlightYellow, boolean[][] highlightMagenta) {
        boolean lightSquare = (row + col) % 2 == 0;
        String bg = lightSquare ? EscapeSequences.SET_BG_COLOR_LIGHT_PINK : EscapeSequences.SET_BG_COLOR_DARK_PINK;

        if (highlightYellow != null && highlightYellow[row][col]) {
            bg = EscapeSequences.SET_BG_COLOR_YELLOW;
        }

        if (highlightMagenta != null && highlightMagenta[row][col]) {
            bg = EscapeSequences.SET_BG_COLOR_MAGENTA;
        }

        System.out.print(bg + pad(board[row][col]) + EscapeSequences.RESET_BG_COLOR);
    }


    public String printWhiteBoard(String... params) {
        return printBoard(true, -1 ,-1);
    }

    public String printBlackBoard(String... params) {
        return printBoard(false, -1, -1);
    }

    public String makeMove() {
        Scanner sc = new Scanner(System.in);
        if (uiHelper.getColor() == null) {
            return "You are an observer.";
        }
        System.out.print("Enter move (e.g. e2 e4): ");
        String moveText = sc.nextLine();

        try {
            String[] parts = moveText.split(" ");
            if (parts.length != 2) {
                return "Invalid move format.";
            }

            ChessPosition from = uiHelper.toPosition(parts[0]);
            ChessPosition to   = uiHelper.toPosition(parts[1]);

            ChessMove move = new ChessMove(from, to, null);

            ws.send(new MakeMoveCommand(uiHelper.getAuthToken(), uiHelper.getGameID(), move));

            ChessGame game = uiHelper.getGame().game();
            game.makeMove(move);

            reprintBoard();

            return "Moved from " + parts[0] + " to " + parts[1] + ".";
        } catch (Exception e) {
            return "Invalid move: " + e.getMessage();
        }
    }

    public String resign() {
        Scanner sc = new Scanner(System.in);
        if (uiHelper.getColor() == null) {
            return "You are an observer.";
        }
        System.out.print("Confirm resign: y or n: ");
        String moveText = sc.nextLine();

        if (moveText.equals("y")) {
            try {
                ws.send(new ResignCommand(uiHelper.getAuthToken(), uiHelper.getGameID()));
                uiHelper.getGame().game().resign(uiHelper.getColor().equalsIgnoreCase("LIGHT") ?
                        ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK);
                return "You resigned the game.";
            } catch (ResponseException e) {
                return "Error resigning: " + e.getMessage();
            }
        }
        else if (moveText.equals("n")) {
            return "Resign canceled.";
        }
        else {
            return "Please enter y or n";
        }
    }


    public String reprintBoard() throws ResponseException {
        if (uiHelper.getColor() == null || uiHelper.getColor().isEmpty()) {
            return printWhiteBoard();
        }
        if (uiHelper.getColor().equals("LIGHT") ||  uiHelper.getColor().equals("light")) {
            return printWhiteBoard();
        }
        if (uiHelper.getColor().equals("DARK") ||  uiHelper.getColor().equals("dark")) {
            return printBlackBoard();
        }
        throw new ResponseException(ResponseException.Code.ServerError, "Couldn't reprint board.");
    }

    public String highlightMoves() throws ResponseException {
        Scanner sc = new Scanner(System.in);

        boolean isObserver = uiHelper.getColor() == null || uiHelper.getColor().isEmpty();
        boolean isWhite = isObserver || uiHelper.getColor().equalsIgnoreCase("LIGHT");

        if (isObserver) {
            System.out.println("You are observing. Highlights will show from white's perspective.");
        }

        System.out.print("Enter the piece to highlight (e.g., e2): ");
        String input = sc.nextLine();

        try {
            ChessPosition pos = uiHelper.toPosition(input);
            ChessGame game = uiHelper.getGame().game();
            if (game == null) {
                return "Game not loaded.";
            }

            ChessPiece piece = game.getPieceAt(pos);
            if (piece == null) {
                return "No piece at that position.";
            }

            var moves = game.validMoves(pos);
            if (moves.isEmpty()) {
                return "No valid moves for this piece.";
            }

            // Create board and highlight arrays
            String[][] board = getBoardFromGame(isWhite, -1, -1);
            boolean[][] highlightYellow = new boolean[8][8];
            boolean[][] highlightMagenta = new boolean[8][8];

            // Helper to convert ChessPosition to array coordinates
            for (var move : moves) {
                int r = 8 - move.getEndPosition().getRow(); // always convert rank to 0-index top-down
                int c = move.getEndPosition().getColumn() - 1; // column a=0, b=1...
                highlightYellow[r][c] = true;
            }

            int row = 8 - pos.getRow();
            int col = pos.getColumn() - 1;
            highlightMagenta[row][col] = true;

            // Print the board with highlights
            printBoardWithHighlights(board, highlightYellow, highlightMagenta);

            return "";
        } catch (Exception e) {
            return "Invalid input: " + e.getMessage();
        }
    }


    private void printBoardWithHighlights(String[][] board, boolean[][] highlightYellow, boolean[][] highlightMagenta) {
        boolean isWhitePerspective = uiHelper.getColor().equalsIgnoreCase("LIGHT");

        String[] columns = isWhitePerspective ?
                new String[]{"a","b","c","d","e","f","g","h"} :
                new String[]{"h","g","f","e","d","c","b","a"};

        System.out.print("  ");
        for (String c : columns) {
            System.out.print(c + "   ");
        }
        System.out.println();

        if (isWhitePerspective) {
            for (int row = 7; row >= 0; row--) {
                System.out.print((row + 1) + " ");
                for (int col = 0; col < 8; col++) {
                    printBoardHelper(row, col, board, highlightYellow, highlightMagenta);
                }
                System.out.println(" " + (row + 1));
            }
        } else {
            for (int row = 0; row < 8; row++) {
                System.out.print((row + 1) + " ");
                for (int col = 7; col >= 0; col--) {
                    printBoardHelper(row, col, board, highlightYellow, highlightMagenta);
                }
                System.out.println(" " + (row + 1));
            }
        }

        System.out.print("  ");
        for (String c : columns) {
            System.out.print(c + "   ");
        }
        System.out.println("\n");
    }


    public String leave(String... params) {
        try {
            ws.send(new LeaveCommand(uiHelper.getAuthToken(), uiHelper.getGameID()));
            uiHelper.setState(State.LOGGEDIN);
            System.out.println("Leaving game...");
            return "leave";
        } catch (ResponseException e) {
            return "Error leaving game: " + e.getMessage();
        }
    }

    public String quit(String... params) throws ResponseException {
        return "quit";
    }

    public String help() {
            return """
                - make-move         make a move
                - highlight-moves   shows which moves you can make
                - reprint           reprint board
                - resign            lose the game
                - leave             leave game
                - quit              exit chess
                - help              get possible commands
            """;
    }
}
