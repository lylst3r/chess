package ui;

import exception.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameplayUI {
    private final ServerFacade server;
    private final UIHelper uiHelper;

    public GameplayUI(String serverUrl, UIHelper uiHelper) {
        server = new ServerFacade(serverUrl);
        this.uiHelper = uiHelper;
    }

    public void run() {
        //System.out.println(uiHelper.getGame().gameName());
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
                case "reprint" -> reprintBoard();
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


    public String printWhiteBoard(String... params) {
        String[][] board = initialBoard();

        System.out.println("   a   b   c  d   e  f   g   h");

        for (int row = 7; row >= 0; row--) {
            System.out.print((row + 1) + " ");
            for (int col = 0; col < 8; col++) {
                boolean light = (row + col) % 2 != 0;
                String bg = light ? EscapeSequences.SET_BG_COLOR_LIGHT_PINK : EscapeSequences.SET_BG_COLOR_DARK_PINK;
                System.out.print(bg + board[row][col] + EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println(" " + (row + 1));
        }
        System.out.println("   a   b   c  d   e  f   g   h\n");
        return "";
    }

    public String printBlackBoard(String... params) {
        String[][] board = initialBoard();

        System.out.println("   h   g   f  e   d  c   b   a");

        for (int row = 0; row < 8; row++) {
            System.out.print((row + 1) + " ");
            for (int col = 7; col >= 0; col--) {
                boolean light = (row + col) % 2 != 0;
                String bg = light ? EscapeSequences.SET_BG_COLOR_LIGHT_PINK : EscapeSequences.SET_BG_COLOR_DARK_PINK;
                System.out.print(bg + board[row][col] + EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println(" " + (row + 1));
        }
        System.out.println("   h   g   f  e   d  c   b   a\n");
        return "";
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

    public String leave(String... params) {
        uiHelper.setState(State.LOGGEDIN);
        System.out.println("Leaving game...");
        return "leave";
    }

    public String quit(String... params) throws ResponseException {
        return "quit";
    }

    public String help() {
            return """
                - reprint    reprint board
                - leave      leave game
                - quit       exit chess
                - help       get possible commands
            """;
    }

    private void assertLoggedIn() throws ResponseException {
        State state = uiHelper.getState();
        if (state == State.LOGGEDOUT) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        } else if  (state == State.LOGGEDIN) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must join a game");
        }
    }
}
