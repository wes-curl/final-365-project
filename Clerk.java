public class Clerk {
    private String login;
    private String name;

    public Clerk(String login, String name, String password){
        this.login = login;
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }
}
