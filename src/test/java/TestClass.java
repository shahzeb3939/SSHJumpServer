import postgresql.tasks.AuthDb;

public class TestClass {

    public static void main(String[] args) {
        String authToken = AuthDb.getAuthToken("user.one@gmail.com");
        System.out.println(authToken);
        String pin = AuthDb.getPin("user.two@gmail.com");
        System.out.println(pin);
    }
}
