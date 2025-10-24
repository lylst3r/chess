package exception;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {

    public enum Code {
        ServerError,
        ClientError,
        Unauthorized,
        AlreadyTaken,
        Conflict,
        BadRequest,
    }

    final private Code code;

    public ResponseException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage()));
    }

    public int toHttpStatusCode() {
        return switch (code) {
            case ServerError -> 500;
            case ClientError, BadRequest -> 400;
            case Unauthorized -> 401;
            case AlreadyTaken, Conflict -> 403;
        };
    }

}

