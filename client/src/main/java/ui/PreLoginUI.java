package ui;

import exception.ResponseException;
import model.AuthData;
import model.UserData;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class PreLoginUI {

    private final ServerFacade server;
    private final UIHelper uiHelper;
    private final String serverUrl;

    public PreLoginUI(String serverUrl) {
        //System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_BROWN);
        server = new ServerFacade(serverUrl);
        uiHelper = new UIHelper();
        this.serverUrl = serverUrl;
    }

    public void run() {
        System.out.println("Welcome to Chess! Sign in to start.");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!"quit".equals(result)) {
            printPrompt();
            String line = scanner.nextLine().trim();

            try {
                result = eval(line);
                if (result != null && !"quit".equals(result)) {
                    System.out.println(result);
                }
            } catch (Throwable e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void printPrompt() {
        System.out.print("\n" + "[LOGGED_OUT] >>> ");
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> quit();
                case "help" -> help();
                default -> "Unknown command. Type 'help' to see commands.";
            };

        } catch (ResponseException ex) {
            return "Error: " + ex.getMessage();
        }
    }

    public String quit() {
        System.out.print("Goodbye!\n");
        return "quit";
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            try {
                AuthData auth = server.login(username, password);
                uiHelper.setAuth(auth);
                uiHelper.setState(State.LOGGEDIN);

                new PostLoginUI(serverUrl, uiHelper).run();

                if (uiHelper.getState() == State.LOGGEDIN) {
                    return String.format("You signed in as %s.", username);
                } else {
                    System.out.print(help());
                    return null;
                }
            } catch (Exception e) {
                return "Login failed: " + e.getMessage();
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: login <username> <password>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length != 3) {
            throw new ResponseException(ResponseException.Code.ClientError, "Expected: register <username> <password> <email>");
        }
        if (!params[2].contains(String.valueOf("@"))) {
            throw new ResponseException(ResponseException.Code.ClientError, "Please enter a valid email.");
        }
        String username = params[0];
        String password = params[1];
        String email = params[2];
        try {
            UserData user = new UserData(username, password, email);
            AuthData auth = server.register(user);
            uiHelper.setState(State.LOGGEDIN);
            uiHelper.setAuth(auth);
            System.out.println("Registered user: " + username);
            new PostLoginUI(serverUrl, uiHelper).run();

            if (uiHelper.getState() == State.LOGGEDIN) {
                return String.format("Registered and signed in as %s.", username);
            } else {
                System.out.print(help());
                return null;
            }
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError, "Registration failed: " + e.getMessage());
        }
    }

    public String help() {
            return """
                - register <username> <password> <email>  create an account
                - login <username> <password>             login to chess
                - quit                                    exit chess
                - help                                    get possible commands
            """;
    }

    private void assertLoggedIn() throws ResponseException {
        State state = uiHelper.getState();
        if (state == State.LOGGEDOUT) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        }
    }
}
