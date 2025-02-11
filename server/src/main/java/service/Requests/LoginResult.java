package service.Requests;

public record LoginResult(String username, String authToken, String message) {
}
