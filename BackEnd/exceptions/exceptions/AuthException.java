package exceptions;

public class AuthException extends Throwable {
    public AuthException(String s) {
    }

    public static class UserAlreadyExistsException extends Exception {
      public UserAlreadyExistsException(String msg) {super (msg); }
      
   }
   
   public static class InvalidCredentialsException extends Exception {
      public InvalidCredentialsException (String msg) {super(msg);}
   }
}