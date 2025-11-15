package ui;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class PostLoginUI {
    private final ServerFacade server;
    private final UIHelper uiHelper;
    private final String serverUrl;

    public PostLoginUI(String serverUrl,  UIHelper uiHelper) {
        server = new ServerFacade(serverUrl);
        this.uiHelper = uiHelper;
        this.serverUrl = serverUrl;
    }

    public void run() {
        System.out.println("Logged in as " + uiHelper.getAuth().username());
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (true) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);

                if (result.equals("LOGOUT")) {
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
                case "create" -> createGame(params);
                case "list" -> listGames(params);
                case "join" -> playGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> quit(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return "Error: " + ex.getMessage();
        }
    }

    public String logout() throws ResponseException {
        server.logout(uiHelper.getAuth().authToken());
        uiHelper.setAuth(null);
        uiHelper.setState(State.LOGGEDOUT);
        System.out.println("Logging out...");
        return "LOGOUT";
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length < 1) {
            return "Expected: create <gameName>";
        }

        String gameName = String.join(" ", params);
        GameData created = server.createGame(uiHelper.getAuth().authToken(), gameName);

        return "Created game: " + created.gameName();
    }

    public String listGames(String... params) throws ResponseException {
        GameData[] games = server.listGames(uiHelper.getAuth().authToken());

        if (games == null || games.length == 0) {
            return "No games available.";
        }

        StringBuilder sb = new StringBuilder("Games:\n");
        for (int i = 0; i < games.length; i++) {
            GameData g = games[i];
            sb.append(String.format("%d) %s (id=%d)\n", i + 1, g.gameName(), g.gameID()));
        }

        return sb.toString();
    }

    public String playGame(String... params) throws ResponseException {
        if (params.length < 2) {
            return "Expected: join <gameNumber> <LIGHT|DARK>";
        }

        int gameNumber;
        try {
            gameNumber = Integer.parseInt(params[0]) - 1;
        } catch (NumberFormatException e) {
            return "Invalid game number.";
        }

        String color = params[1].toUpperCase();
        GameData[] games = server.listGames(uiHelper.getAuth().authToken());

        if (gameNumber < 0 || gameNumber >= games.length) {
            return "Game number out of range.";
        }
        GameData joined = server.joinGame(uiHelper.getAuth().authToken(),
                games[gameNumber].gameID(), color);

        uiHelper.setGame(joined);
        new GameplayUI(serverUrl, uiHelper).run();

        return "Joined game " + joined.gameName() + " as " + color;
    }

    public String observeGame(String... params) throws ResponseException {
        if (params.length < 1) {
            return "Expected: observe <gameNumber>";
        }

        int gameNumber;
        try {
            gameNumber = Integer.parseInt(params[0]) - 1;
        } catch (NumberFormatException e) {
            return "Invalid game number.";
        }

        GameData[] games = server.listGames(uiHelper.getAuth().authToken());

        if (gameNumber < 0 || gameNumber >= games.length) {
            return "Game number out of range.";
        }

        uiHelper.setGame(games[gameNumber]);
        new GameplayUI(serverUrl, uiHelper).run();

        return "Observing game " + games[gameNumber].gameName();
    }

    public String quit(String... params) throws ResponseException {
        return "quit";
    }

    public String help() {
            return """
                - create <gameName>            create a new game
                - list                         get chess games
                - join <gameID> [LIGHT|DARK]   join as a player
                - observe <gameID>             watch a game
                - logout                       return to login screen
                - quit                         exit chess
                - help                         get possible commands
            """;
    }

    private void assertLoggedIn() throws ResponseException {
        State state = uiHelper.getState();
        if (state == State.LOGGEDOUT) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        }
    }
}
