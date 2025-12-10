package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import exception.ResponseException;
import server.ServerFacade;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

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
            case ERROR -> System.out.println("Server error: " + message.getErrorMessage());
            case NOTIFICATION -> System.out.println(((NotificationMessage) message).getMessage());
            default -> System.out.println("Unknown message: " + message);
        }
    }


    private String printBoard(boolean isWhitePerspective) {
        //String[][] board = initialBoard();
        String[][] board = getBoardFromGame();

        // Column labels
        String[] cols = isWhitePerspective ?
                new String[]{"a","b","c","d","e","f","g","h"} :
                new String[]{"h","g","f","e","d","c","b","a"};

        System.out.print("   ");
        for (String c : cols) System.out.print(" " + c + "  ");
        System.out.println();

        if (isWhitePerspective) {
            for (int row = 7; row >= 0; row--) {
                System.out.print((row + 1) + " ");

                for (int col = 0; col < 8; col++) {
                    printBoardHelper(row, col, board);
                }

                System.out.println(" " + (row + 1));
            }
        } else {
            for (int row = 0; row < 8; row++) {
                System.out.print((row + 1) + " ");

                for (int col = 7; col >= 0; col--) {
                    printBoardHelper(row, col, board);
                }

                System.out.println(" " + (row + 1));
            }
        }

        System.out.print("   ");
        for (String c : cols) {
            System.out.print(" " + c + "  ");
        }
        System.out.println("\n");

        return "";
    }

    private String[][] getBoardFromGame() {
        if (uiHelper.getGame() == null || uiHelper.getGame().game() == null) {
            String[][] b = new String[8][8];
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    b[r][c] = EscapeSequences.EMPTY;
                }
            }
            return b;
        }

        ChessGame game = uiHelper.getGame().game();
        String[][] b = new String[8][8];

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                var piece = game.getPieceAt(r + 1, c + 1); // ChessGame uses 1–8 indexing
                b[r][c] = (piece == null) ? EscapeSequences.EMPTY : piece.getUnicodeSymbol();
            }
        }
        return b;
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


    public void printBoardHelper(int row, int col, String[][] board) {
        boolean lightSquare = (row + col) % 2 == 0;
        String bg = lightSquare ?
                EscapeSequences.SET_BG_COLOR_LIGHT_PINK :
                EscapeSequences.SET_BG_COLOR_DARK_PINK;

        System.out.print(bg + pad(board[row][col]) + EscapeSequences.RESET_BG_COLOR);
    }

    public String printWhiteBoard(String... params) {
        return printBoard(true);
    }

    public String printBlackBoard(String... params) {
        return printBoard(false);
    }

    public String makeMove() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter move (e.g. e2 e4): ");
        String moveText = sc.nextLine();

        try {
            String[] parts = moveText.split(" ");
            if (parts.length != 2) {
                return "Invalid move format";
            }

            var from = uiHelper.toPosition(parts[0]);
            var to   = uiHelper.toPosition(parts[1]);

            var move = new ChessMove(from, to, null);

            ws.send(new MakeMoveCommand(uiHelper.getAuthToken(),
                    uiHelper.getGameID(),
                    move));

            return "Move sent.";
        } catch (Exception e) {
            return "Invalid move.";
        }
    }

    public String resign() {
        return null;
    }


    public String reprintBoard() throws ResponseException {
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
        System.out.print("Enter the piece to highlight (e.g., e2): ");
        String input = sc.nextLine();

        try {
            ChessPosition pos = uiHelper.toPosition(input);
            ChessGame game = uiHelper.getGame().game();
            if (game == null) {
                return "Game not loaded.";
            }

            var piece = game.getPieceAt(pos);
            if (piece == null) {
                return "No piece at that position.";
            }

            var moves = game.validMoves(pos);
            if (moves.isEmpty()) {
                return "No valid moves for this piece.";
            }

            String[][] board = getBoardFromGame();
            for (var move : moves) {
                int r = move.getStartPosition().getRow() - 1;
                int c = move.getStartPosition().getColumn() - 1;
                board[r][c] = EscapeSequences.SET_TEXT_COLOR_YELLOW + board[r][c] + EscapeSequences.RESET_TEXT_COLOR;
            }

            printBoardWithHighlights(board);
            return "";
        } catch (Exception e) {
            return "Invalid input: " + e.getMessage();
        }
    }

    private void printBoardWithHighlights(String[][] board) {
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
                    printBoardHelper(row, col, board);
                }
                System.out.println(" " + (row + 1));
            }
        } else {
            for (int row = 0; row < 8; row++) {
                System.out.print((row + 1) + " ");
                for (int col = 7; col >= 0; col--) {
                    printBoardHelper(row, col, board);
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
