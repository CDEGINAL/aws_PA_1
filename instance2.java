// Sample pseudocode for Instance B
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.sqs.AmazonSQS;

public void recognizeText() {
    // Initialize AWS clients
    AmazonRekognition rekognitionClient = ...; // initialize client
    AmazonSQS sqsClient = ...; // initialize client
    while (true) {
        // Poll SQS for messages
        String message = sqsClient.receiveMessage(queueUrl).getMessages().get(0);
        if (message.equals("-1")) break; // End signal
        // Download image from S3 and recognize text
        String text = recognizeTextInImage(message);
        // Write results to file
        writeToFile(message, text);
    }
}
