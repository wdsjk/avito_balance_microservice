package wdsjk.project.avitobalancemicroservice.exception;

public class InternalErrorException extends RuntimeException {
    String message = "Something went wrong on the server side! Please, try again later";

    @Override
    public String getMessage() {
        return message;
    }
}
