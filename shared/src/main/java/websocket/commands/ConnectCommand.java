package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    public ConnectCommand(String authToken, int gameID) {
        super(CommandType.CONNECT, authToken, gameID);
    }
}
