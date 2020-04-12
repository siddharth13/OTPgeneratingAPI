package application_package.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

@RestController
public class TestController {
	
	 AmazonSNSClient snsClient = new AmazonSNSClient();

    @GetMapping("/sns")
    public String Home11(){
    	 
    	// sns.eu-central-1.amazonaws.com
    	    String message = "Hello from SNS 2";
    	    String phoneNumber = "+917397226050";
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
    }	
}