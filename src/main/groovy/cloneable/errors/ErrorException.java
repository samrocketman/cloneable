package cloneable.errors;

class ErrorException extends Exception {
    public ErrorException(String message) {
        super("ERROR: " + message);
    }
}
