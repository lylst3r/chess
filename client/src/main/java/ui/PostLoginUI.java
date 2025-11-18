package ui;

import exception.ResponseException;
import model.GameData;
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

                if (result.equals("logout")) {
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
        System.out.print("\n" + "[LOGGED_IN] >>> ");
    }


    public String eval(String input) {
        try {
            String[] tokens = input.split(" ");
            String cmd = tokens.length > 0 ? tokens[0].toLowerCase() : "help";

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
        if (uiHelper.getAuth().authToken() == null) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: Unauthorized");
        }
        server.logout(uiHelper.getAuth().authToken());
        uiHelper.setAuth(null);
        uiHelper.setState(State.LOGGEDOUT);
        System.out.println("Logging out...");
        return "logout";
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(ResponseException.Code.ClientError,
                    "Expected: create <gameName>");
        }
        try {
            String gameName = String.join(" ", params);
            GameData created = server.createGame(uiHelper.getAuth().authToken(), gameName);

            return "Created game: " + gameName;
        } catch (ResponseException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, "Unable to create game.");
        }
    }

    public String listGames(String... params) throws ResponseException {
        if (uiHelper.getAuth().authToken() == null) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: Unauthorized");
        }
        if (params.length != 0) {
            throw new ResponseException(ResponseException.Code.ClientError, "Expected: list");
        }

        GameData[] games = server.listGames(uiHelper.getAuth().authToken());

        if (games == null || games.length == 0) {
            return "No games available.";
        }

        StringBuilder sb = new StringBuilder("Games:\n");
        for (int i = 0; i < games.length; i++) {
            GameData g = games[i];

            sb.append(String.format("%d) %s\n", i + 1, g.gameName()));
        }

        return sb.toString();
    }

    public String playGame(String... params) throws ResponseException {
        if (uiHelper.getAuth().authToken() == null) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: Unauthorized");
        }
        if (params.length != 2) {
            throw new ResponseException(ResponseException.Code.ClientError,
                    "Expected: join <gameNumber> <LIGHT|DARK>");
        }
        if (!params[1].equals("LIGHT") && !params[1].equals("DARK")
                && !params[1].equals("light") && !params[1].equals("dark")) {
            throw new ResponseException(ResponseException.Code.ClientError,
                    "Must enter 'LIGHT' or 'DARK' for team color.");
        }

        int gameNumber;
        try {
            gameNumber = Integer.parseInt(params[0]) - 1;
        } catch (NumberFormatException e) {
            return "Invalid game number. Pick a different game number.";
        }

        String color = params[1].toUpperCase();
        GameData[] games = server.listGames(uiHelper.getAuth().authToken());

        if (gameNumber < 0 || gameNumber >= games.length) {
            return "Game doesn't exist. Pick a different game number.";
        }
        GameData joined = server.joinGame(uiHelper.getAuth().authToken(),
                games[gameNumber].gameID(), color);

        uiHelper.setGame(joined);
        uiHelper.setState(State.INGAME);
        uiHelper.setColor(color);
        System.out.print("Joining game " + games[gameNumber].gameName() + " as " + color + "\n");
        new GameplayUI(serverUrl, uiHelper).run();

        if (uiHelper.getState() == State.INGAME) {
            return String.format("Joined game " + joined.gameName() + " as " + color);
        } else {
            System.out.print(help());
            return "";
        }
    }

    public String observeGame(String... params) throws ResponseException {
        if (uiHelper.getAuth().authToken() == null) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: Unauthorized");
        }
        if (params.length != 1) {
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
        uiHelper.setState(State.INGAME);
        System.out.print("Observing game " + games[gameNumber].gameName() + "\n");
        new GameplayUI(serverUrl, uiHelper).run();

        if (uiHelper.getState() == State.INGAME) {
            return String.format("Observing game " + uiHelper.getGameName(gameNumber));
        } else {
            System.out.print(help());
            return "";
        }
    }

    public String quit(String... params) throws ResponseException {
        return "quit";
    }

    public String help() {
            return """
                - create <gameName>              create a new game
                - list                           get chess games
                - join <gameNumber> [LIGHT|DARK] join as a player
                - observe <gameNumber>           watch a game
                - logout                         return to login screen
                - quit                           exit chess
                - help                           get possible commands
            """;
    }
}
