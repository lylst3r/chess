package ui;

import exception.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class GameplayUI {
    private final ServerFacade server;
    private final UIHelper uiHelper;

    public GameplayUI(String serverUrl, UIHelper uiHelper) {
        server = new ServerFacade(serverUrl);
        this.uiHelper = uiHelper;
    }

    public void run() {
        //System.out.println(uiHelper.getGame().gameName());
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
                case "leave" -> leave(params);
                case "quit" ->  quit(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return "Error: " + ex.getMessage();
        }
    }

    public String printWhiteBoard(String... params) {

        return "";
    }

    public String printBlackBoard(String... params) {

        return "";
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
