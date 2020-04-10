package application_package;
import javafx.scene.control.RadioMenuItem;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static ch.qos.logback.core.joran.action.ActionConst.NULL;

@RestController
public class Controller {
    private List<Data> list=new ArrayList<>();
    @GetMapping("/")
    public String Home(){
        return "HELLO";
    }
    @PostMapping(path = "/generatingOTP")
    public void generate(@RequestBody RequestData requestData){
      RequestData rd=requestData;
        String s=rd.getId();
        Random rand = new Random();
        int n=rand.nextInt(999999);
       list.add(new Data(s,n+""));
    }
    @PostMapping("/getOTP")
    public List<Data> verify(@RequestBody Data input){

       return list;
    }



}
