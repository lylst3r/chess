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
        //System.out.println("Logged in as ");
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
        System.out.print("\n" + "[IN_GAME] >>> ");
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" ->  quit(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String quit(String... params) throws ResponseException {
        return null;
    }

    public String help() {
            return """
                - quit --> exit chess
                - help --> get possible commands
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
