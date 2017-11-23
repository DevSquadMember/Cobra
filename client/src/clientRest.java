package client.src;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import server.src.TransferObject;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

public class ClientRest {
    public final static void main(String[] args) {
        HttpClient httpClient = new DefaultHttpClient();
        try {
            // Creation d'un client
            HttpGet createClient = new HttpGet("http://localhost:8182/app/getClientId");
            HttpResponse httpResponse = httpClient.execute(createClient);

            System.out.println("http status : " + httpResponse.getStatusLine());

            InputStream inputStream = httpResponse.getEntity().getContent();
            JAXBContext jc = JAXBContext.newInstance(Integer.class);
            Unmarshaller u = jc.createUnmarshaller();
            Object o = u.unmarshal( inputStream );
            int myId =  (Integer)o;

            System.out.println("myId : " + myId);

            //ouverture d'un compte
            HttpGet openAccount = new HttpGet("http://localhost:8182/app/getNewAccount/"+myId);
            httpResponse = httpClient.execute(openAccount);

            System.out.println("http status : " + httpResponse.getStatusLine());

            inputStream = httpResponse.getEntity().getContent();
            jc = JAXBContext.newInstance(Integer.class);
            u = jc.createUnmarshaller();
            o = u.unmarshal(inputStream);
            int myAccount =  (Integer)o;
            System.out.println("myAccount : " + myAccount);

            //depot
            TransferObject myDeposit = new TransferObject(myId, myAccount, 50.0);
            HttpPost depositRequest = new HttpPost("http://localhost:8182/app/deposit");
            depositRequest.setHeader("Content-Type","text/xml");
        } catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            httpClient.getConnectionManager().shutdown();
        }
    }
}
