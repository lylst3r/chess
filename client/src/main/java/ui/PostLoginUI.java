package ui;

import exception.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class PostLoginUI {
    private final ServerFacade server;
    private final UIHelper uiHelper;
    private final String serverUrl;

    public PostLoginUI(String serverUrl) {
        server = new ServerFacade(serverUrl);
        uiHelper = new UIHelper();
        this.serverUrl = serverUrl;
    }

    public void run() {
        System.out.println("Logged in as ");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    /*public void notify(Notification notification) {
        System.out.println(RED + notification.message());
        printPrompt();
    }*/

    private void printPrompt() {
        System.out.print("\n" + "[LOGGED_IN] >>> ");
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout();
                case "createGame" -> createGame(params);
                case "listGames" -> listGames(params);
                case "playGame" -> playGame(params);
                case "observerGame" -> observeGame(params);
                case "quit" -> quit(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String logout() throws ResponseException {
        return null;
    }

    public String createGame(String... params) throws ResponseException {
        return null;
    }

    public String listGames(String... params) throws ResponseException {
        return null;
    }

    public String playGame(String... params) throws ResponseException {
        try {
            new GameplayUI(serverUrl).run();

        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
        return null;
    }

    public String observeGame(String... params) throws ResponseException {
        try {
            new GameplayUI(serverUrl).run();

        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
        return null;
    }

    public String quit(String... params) throws ResponseException {
        return null;
    }

    public String help() {
            return """
                - create <username> <password> <email> --> create a new game
                - list games --> get chess games
                - join <gameID> [LIGHT|DARK] --> join as a player
                - observe <gameID> --> watch a game
                - logout <username> <password>
                - quit --> exit chess
                - help --> get possible commands
            """;
    }

    private void assertLoggedIn() throws ResponseException {
        State state = uiHelper.getState();
        if (state == State.LOGGEDOUT) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        }
    }
}
