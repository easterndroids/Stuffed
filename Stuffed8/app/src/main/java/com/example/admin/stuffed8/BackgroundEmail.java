package com.example.admin.stuffed8;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import java.io.File;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.internet.MimeMultipart;

//AsyncTask to perform a networking operation
public class BackgroundEmail extends AsyncTask<Void,Void,Void> {

    //Declaring Variables
    private Context context;
    private Session session;

    //Information to send email
    private String primarymail;
    private String secondarymail;
    private String usermail;
    private String subject;
    private String message;
    private String picpath;
    File pictureFile;
    //Uri path;

    //Progressdialog to show while sending email
    private ProgressDialog progressDialog;

    //Class Constructor
    public BackgroundEmail(Context context, String primarymail, String secondarymail, String usermail,
                           String subject, String message, String picpath){
        //Initializing variables
        this.context = context;
        this.primarymail = primarymail;
        this.secondarymail = secondarymail;
        this.usermail = usermail;
        this.subject = subject;
        this.message = message;
        this.picpath = picpath;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
        progressDialog = ProgressDialog.show(context,"Sending message","Please wait...",false,false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismissing the progress dialog
        progressDialog.dismiss();
        //Showing a success message
        Toast.makeText(context,"Message Sent",Toast.LENGTH_LONG).show();

    }

    @Override
    protected Void doInBackground(Void... params) {
        //Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail - Referred from [1]
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Config.EMAIL, Config.PASSWORD);
                    }
                });

        try {
            //Creating MimeMessage object
            MimeMessage mimeMessage = new MimeMessage(session);

            //Setting sender address
            mimeMessage.setFrom(new InternetAddress(Config.EMAIL));

            //Adding recipients address
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(primarymail));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(secondarymail));
            mimeMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(usermail));

            //Adding subject
            mimeMessage.setSubject(subject);

            //Adding message
            //mm.setText(message);

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Now set the actual message
            messageBodyPart.setText(message);

            // Create a multipart message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            //adding attachment
            messageBodyPart = new MimeBodyPart();
            pictureFile = new File(picpath);
            String path = pictureFile.getAbsolutePath();
            DataSource source = new FileDataSource(path);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(path);
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            mimeMessage.setContent(multipart);

            //Sending email
            Transport.send(mimeMessage);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}

// Reference:
//1.https://www.simplifiedcoding.net/android-email-app-using-javamail-api-in-android-studio/
//2.https://www.tutorialspoint.com/javamail_api/javamail_api_send_email_with_attachment.htm