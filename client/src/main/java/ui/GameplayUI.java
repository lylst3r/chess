package ui;

import chess.ChessMove;
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

        if (uiHelper.getColor().equals("LIGHT") ||  uiHelper.getColor().equals("light")) {
            printWhiteBoard();
        }
        if (uiHelper.getColor().equals("DARK") ||  uiHelper.getColor().equals("dark")) {
            printBlackBoard();
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

    private String[][] initialBoard() {
        String[][] b = new String[8][8];
        System.out.print(SET_TEXT_COLOR_OFF_WHITE);

        // White pieces
        b[0][0] = WHITE_ROOK;
        b[0][1] = WHITE_KNIGHT;
        b[0][2] = WHITE_BISHOP;
        b[0][3] = WHITE_QUEEN;
        b[0][4] = WHITE_KING;
        b[0][5] = WHITE_BISHOP;
        b[0][6] = WHITE_KNIGHT;
        b[0][7] = WHITE_ROOK;
        for (int c = 0; c < 8; c++) {
            b[1][c] = WHITE_PAWN;
        }

        // Black pieces
        b[7][0] = BLACK_ROOK;
        b[7][1] = BLACK_KNIGHT;
        b[7][2] = BLACK_BISHOP;
        b[7][3] = BLACK_QUEEN;
        b[7][4] = BLACK_KING;
        b[7][5] = BLACK_BISHOP;
        b[7][6] = BLACK_KNIGHT;
        b[7][7] = BLACK_ROOK;
        for (int c = 0; c < 8; c++) {
            b[6][c] = BLACK_PAWN;
        }

        for (int r = 2; r <= 5; r++) {
            for (int c = 0; c < 8; c++) {
                b[r][c] = EscapeSequences.EMPTY;
            }
        }
        return b;
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> updateBoard(((LoadGameMessage) message).toString());
            case ERROR -> System.out.println("Server error: " + message.getErrorMessage());
            case NOTIFICATION -> System.out.println(((NotificationMessage) message).getMessage());
            default -> System.out.println("Unknown message: " + message);
        }
    }


    private String printBoard(boolean isWhitePerspective) {
        String[][] board = initialBoard();

        String[] columns = isWhitePerspective ?
                new String[]{"a","b","c","d","e","f","g","h"} :
                new String[]{"h","g","f","e","d","c","b","a"};

        System.out.print("  ");
        for (String c : columns){
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

        return "";
    }

    public void printBoardHelper(int row, int col, String[][] board) {
        boolean light = (row + col) % 2 != 0;
        String bg = light ? EscapeSequences.SET_BG_COLOR_LIGHT_PINK : EscapeSequences.SET_BG_COLOR_DARK_PINK;
        System.out.print(bg + board[row][col] + EscapeSequences.RESET_BG_COLOR);
    }

    public void updateBoard(String json) {
        try {
            LoadGameMessage m = new Gson().fromJson(json, LoadGameMessage.class);
            uiHelper.setGame(m.getGame());
            reprintBoard();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
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
            if (parts.length != 2) return "Invalid move format";

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
        return null;
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
