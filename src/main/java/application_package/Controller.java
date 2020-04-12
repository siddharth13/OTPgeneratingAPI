package application_package;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
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
    AmazonSNSClient snsClient = new AmazonSNSClient();
    public String sendSMS(String phoneNumber,String otp){
        // sns.eu-central-1.amazonaws.com
        String message = "Your OTP is "+otp+" .";
       // String phoneNumber = "+917397226050";
        phoneNumber="+91"+phoneNumber;
        Map<String, MessageAttributeValue> smsAttributes =
                new HashMap<String, MessageAttributeValue>();
        //<set SMS attributes>
        smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
                .withStringValue("0.0050") //Sets the max price to 0.50 USD.
                .withDataType("Number"));
        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                .withStringValue("Transactional") //Sets the type to promotional.
                .withDataType("String"));
        PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(phoneNumber)
                .withMessageAttributes(smsAttributes));
        System.out.println(result); // Prints the message ID.
        return result.toString();
     // return phoneNumber;
    }
    public static boolean isValidPhoneNumber(String s)
    {
        Pattern p = Pattern.compile("(0/91)?[7-9][0-9]{9}");
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }
    @GetMapping("/")
    public String Home(){
        return "endpoints------generatingOTP-->type=post(for inserting value";
    }
    @GetMapping(path = "/generateOTP")
    public void generate(RequestData requestData){
        Table table = dynamoDB.getTable("OTPDatabase");
        String id=requestData.getId();
        System.err.println(requestData.getId());
        Random rand = new Random();
        String otp= ""+(rand.nextInt(899999) + 100000);
        String timeinmilli=String.valueOf(System.currentTimeMillis());
        try {
            if(isValidPhoneNumber(id)) {
                PutItemOutcome outcome = table
                        .putItem(new Item().withPrimaryKey("ID", id).with("OTP", otp).with("TIMESTAMP", timeinmilli));
                 sendSMS(id,otp);
            }
            else{
                System.err.println("the phone no. is not valid");
            }
        }
        catch (Exception e){
            System.err.println("Unable to add item: " + id + " " + otp);
            System.err.println(e.getMessage());
        }

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
    @GetMapping("/regenerateOTP")
    public void regenerate(RequestData requestData){
        Table table = dynamoDB.getTable("OTPDatabase");
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("ID", requestData.getId());
        Random rand=new Random();
        String otp= ""+(rand.nextInt(899999) + 100000);
        try {
            Item outcome = table.getItem(spec);
            if(outcome==null){
                System.err.println("User Not Found");
            }
           else{
                String time=String.valueOf(System.currentTimeMillis());
                table.putItem(new Item().withPrimaryKey("ID",requestData.getId()).with("OTP",otp).with("TIMESTAMP",time));
                sendSMS(requestData.getId(),otp);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("Connection Problem");
        }
    }
    @GetMapping("/resendOTP")
    public void resend(RequestData requestData){
        Table table = dynamoDB.getTable("OTPDatabase");
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("ID", requestData.getId());


        try {
            Item outcome = table.getItem(spec);
            String otp=outcome.get("OTP").toString();
            if(outcome==null){
                System.err.println("User Not Found");
            }
            else{
                String time=String.valueOf(System.currentTimeMillis());
                table.putItem(new Item().withPrimaryKey("ID",requestData.getId()).with("OTP",otp).with("TIMESTAMP",time));
                sendSMS(requestData.getId(),otp);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("Connection Problem");
        }
    }

}
