package tutorial.firebase.com.bontsi.firapp.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ndimphiwe.bontsi on 2018/04/19.
 */
@IgnoreExtraProperties
public class Message {

    private String author;
    private  String body;
    private String time;

    public Message(String author, String body, String time) {
        this.author = author;
        this.body = body;
        this.time = time;
    }
public  Message(){

}
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Map<String, Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("author",author);
        result.put("body",body);
        result.put("time",time);
        return  result;
    }

}
