package application_package;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@RestController
public class Controller {
    private List<Data> list=new ArrayList<>();
    @GetMapping("/")
    public String Home(){
        return "endpoints------generatingOTP-->type=post(for inserting value";
    }
    @PostMapping(path = "/generatingOTP")
    public String generate(@RequestBody RequestData requestData){
      RequestData rd=requestData;
        String s=rd.getId();
        Random rand = new Random();
        int n=rand.nextInt(999999);
        int flag=0;
        for(Data data:list){
            if(data.getId().equals(requestData.getId())){
                data.setGeneratedOTP(n+"");
                flag=1;
                break;
            }
        }

    if(flag==0) {
    list.add(new Data(s, n + ""));
    return n + "";
    }
    else{
        return n+"";
    }
    }
    @PostMapping("/verify")
    public String verify(@RequestBody Data input){
        String OTP=input.getGeneratedOTP();
        String id=input.getId();
        for(Data data:list){
          if(data.getId().equals(id)) {
              if (data.getGeneratedOTP().equals(OTP)) {
                  return "OTP Verified";
              } else {
                  return "Wrong OTP!!! Try Again!!";
              }
          }
      }

       return "User Not Found";
    }



}
