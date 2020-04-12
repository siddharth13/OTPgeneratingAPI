package application_package;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.model.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;
@RestController
public class Controller {
    private List<Data> list=new ArrayList<>();
    private static final AWSCredentials AWS_CREDENTIALS;
    static {
        AWS_CREDENTIALS = new BasicAWSCredentials(
                "AKIA6J4QTU2L2CUFSGWI",
                "RUVsw0wAoOgagbueE5mXhoj0PexFWndZvKRUNh8f"
        );
    }
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("https://dynamodb.us-west-2.amazonaws.com", "us-west-2"))
            .build();
    DynamoDB dynamoDB = new DynamoDB(client);
    @GetMapping("/")
    public String Home(){
        return "endpoints------generatingOTP-->type=post(for inserting value";
    }
    @PostMapping(path = "/generateOTP")
    public String generate(@RequestBody RequestData requestData){
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

        try {
            Item outcome = table.getItem(spec);
            if(outcome==null){
                return "User not found";
            }
           else{

                String time=String.valueOf(System.currentTimeMillis());
                table.putItem(new Item().withPrimaryKey("ID",requestData.getId()).with("OTP",outcome.get("OTP").toString()).with("TIMESTAMP",time));
                return outcome.get("OTP").toString();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return "Connection Error";
        }
    }

}
