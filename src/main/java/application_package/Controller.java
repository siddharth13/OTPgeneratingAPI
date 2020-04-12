package application_package;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
@RestController
public class Controller {

    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("https://dynamodb.us-west-2.amazonaws.com", "us-west-2"))
            .build();
    DynamoDB dynamoDB = new DynamoDB(client);

    @GetMapping("/")
    public String Home(){
        return "endpoints------generatingOTP-->type=post(for inserting value";
    }
    @GetMapping(path = "/generateOTP")
    public String generate( RequestData requestData){
            Table table = dynamoDB.getTable("OTPDatabase");
            RequestData rd=requestData;
            String s=rd.getId();
            Random rand = new Random();
            String otp= ""+(rand.nextInt(899999) + 100000);
            String timeinmilli=String.valueOf(System.currentTimeMillis());
            try {
                PutItemOutcome outcome = table
                        .putItem(new Item().withPrimaryKey("ID",s).with("OTP",otp).with("TIMESTAMP",timeinmilli));
            }
            catch (Exception e){
                System.err.println("Unable to add item: " + s + " " + otp);
                System.err.println(e.getMessage());
            }
     return otp;
    }
    @PostMapping("/verifyOTP")
    public String verify(@RequestBody Data input) {
        Table table = dynamoDB.getTable("OTPDatabase");
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("ID", input.getId());

            try {
                Item outcome = table.getItem(spec);
                if (outcome == null) {
                    return "User not found";
                } else {

                long currenttime=System.currentTimeMillis();
                long timestamp= Long.parseLong(outcome.get("TIMESTAMP").toString());
                if(currenttime-timestamp>180000){
                    return "OTP expired";
                }
                if (outcome.get("OTP").toString().equals(input.getGeneratedOTP())) {
                    return "OTP verified";
                } else {
                    return "Wrong OTP";
                }

            }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                return "Connection Error";
            }
    }
    @PostMapping("/regenerateOTP")
    public String regenerate(@RequestBody RequestData requestData){
        Table table = dynamoDB.getTable("OTPDatabase");
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("ID", requestData.getId());
        Random rand=new Random();
        String otp= ""+(rand.nextInt(899999) + 100000);
        try {
            Item outcome = table.getItem(spec);
            if(outcome==null){
                return "User not found";
            }
           else{
                String time=String.valueOf(System.currentTimeMillis());
                table.putItem(new Item().withPrimaryKey("ID",requestData.getId()).with("OTP",otp).with("TIMESTAMP",time));
                return otp;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return "Connection Error";
        }
    }

}
