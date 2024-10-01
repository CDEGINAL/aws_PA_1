import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

public class ObjectDetection {
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

        detectCars(rekognitionClient, sqsClient);
    }

    public static void detectCars(AmazonRekognition rekognitionClient, AmazonSQS sqsClient) {
        String[] imageList = {"2.jpg", "3.jpg"}; // Add all your image filenames here
        for (String image : imageList) {
            // Call Rekognition to detect labels
            DetectLabelsRequest request = new DetectLabelsRequest()
                    .withImage(new Image().withS3Object(new S3Object().withBucket(BUCKET_NAME).withName(image)))
                    .withMaxLabels(10)
                    .withMinConfidence(90F);
            
            DetectLabelsResult result = rekognitionClient.detectLabels(request);
            boolean carDetected = result.getLabels().stream()
                    .anyMatch(label -> "Car".equals(label.getName()));

            if (carDetected) {
                // Send image index to SQS
                sqsClient.sendMessage(SQS_URL, image);
            }
        }
        // Signal completion
        sqsClient.sendMessage(SQS_URL, "-1");
    }
}
