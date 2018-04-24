package tutorial.firebase.com.bontsi.firapp.model;

/**
 * Created by ndimphiwe.bontsi on 2018/04/19.
 */

public class User {

    private String name;
    private String email;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;

    public User(){

    }
    public User(String name,String email,String password){

        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
