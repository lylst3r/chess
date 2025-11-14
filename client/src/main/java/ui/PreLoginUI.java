package ui;

import exception.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class PreLoginUI {
    private final ServerFacade server;
    private final UIHelper uiHelper;
    private final String serverUrl;

    public PreLoginUI(String serverUrl) {
        server = new ServerFacade(serverUrl);
        uiHelper = new UIHelper();
        this.serverUrl = serverUrl;
    }

    public void run() {
        System.out.println("Welcome to Chess. Sign in to start.");
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
                case "quit" -> quit(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String quit(String... params) throws ResponseException {
        return null;
    }

    public String login(String... params) throws ResponseException {
        try {
            new PostLoginUI(serverUrl).run();

        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
        return null;
    }

    public String register(String... params) throws ResponseException {
        return null;
    }

    public String help() {
            return """
                - register <username> <password> <email> --> create an account
                - login <username> <password>
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
