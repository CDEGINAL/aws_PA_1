import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TextRecognition {
    private static final String BUCKET_NAME = "njit-cs-643";
    private static final String SQS_URL = "YOUR_SQS_QUEUE_URL"; // Replace with your SQS queue URL
    
    public static void main(String[] args) {
        // Initialize AWS clients
        BasicAWSCredentials awsCreds = new BasicAWSCredentials("YOUR_ACCESS_KEY", "YOUR_SECRET_KEY");
        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion("us-east-1")
                .build();
        AmazonSQS sqsClient = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion("us-east-1")
                .build();

        recognizeText(rekognitionClient, sqsClient);
    }

    public static void recognizeText(AmazonRekognition rekognitionClient, AmazonSQS sqsClient) {
        while (true) {
            // Poll SQS for messages
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(SQS_URL);
            List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).getMessages();

            if (messages.isEmpty()) continue;

            for (Message message : messages) {
                String imageIndex = message.getBody();
                if ("-1".equals(imageIndex)) {
                    return; // End signal
                }
                // Call Rekognition to detect text
                DetectTextRequest request = new DetectTextRequest()
                        .withImage(new Image().withS3Object(new S3Object().withBucket(BUCKET_NAME).withName(imageIndex)));
                DetectTextResult result = rekognitionClient.detectText(request);
                
                String recognizedText = result.getTextDetections().stream()
                        .map(TextDetection::getDetectedText)
                        .collect(Collectors.joining(", "));

                writeToFile(imageIndex, recognizedText);
                // Delete the message from SQS after processing
                sqsClient.deleteMessage(SQS_URL, message.getReceiptHandle());
            }
        }
    }

    private static void writeToFile(String imageIndex, String recognizedText) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
            writer.write("Image: " + imageIndex + ", Text: " + recognizedText);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
