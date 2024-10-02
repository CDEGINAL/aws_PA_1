# aws_PA_1

Login to Vocareum:

Go to the AWS Educate website and log into your Vocareum lab environment.
Access the AWS Credentials:

Once logged in, navigate to the Account Details section in Vocareum.
Click on Access Your Credentials. This will show the AWS Access Key ID, Secret Access Key, and Session Token.
Copy the New Credentials:

Copy the new values for:
AWS Access Key ID
AWS Secret Access Key
AWS Session Token
Update Credentials on Your EC2 Instance:

Log in to your EC2 instance via SSH:

bash
Copy code
ssh -i "your-key.pem" ec2-user@your-ec2-public-ip
Open or create your AWS credentials file located at ~/.aws/credentials:

bash
Copy code
nano ~/.aws/credentials
Update the credentials file with the new values from Vocareum. The file should look like this:

ini
Copy code
[default]
aws_access_key_id = <Your_New_Access_Key_ID>
aws_secret_access_key = <Your_New_Secret_Access_Key>
aws_session_token = <Your_New_Session_Token>
Replace <Your_New_Access_Key_ID>, <Your_New_Secret_Access_Key>, and <Your_New_Session_Token> with the new values you just copied.

Save the file by pressing Ctrl + O and then Enter, and exit the editor by pressing Ctrl + X.

Verify AWS Configuration:

Run the following command to check if the credentials are correctly set:

bash
Copy code
aws s3 ls
If the credentials are valid, you should see a list of S3 buckets associated with your account.

Repeat After 3 Hours:

Remember, these credentials are temporary and will expire after 3 hours. You will need to repeat these steps to refresh the credentials whenever you encounter the InvalidAccessKeyId or similar errors.
