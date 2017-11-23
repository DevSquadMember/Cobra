import java.io.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.xml.bind.*;

public class ClientRest
{
    public final statis void main(String[] args)
    {
	HttpClient httpClient = new DefaultHttpClient();
	try
	    {
		// creation d'un client
		HttpGet createClient = new HttpGet("http://localhost:8182/app/getClientId");
		HttpResponse httpResponse = httpClient.execute(createClient);

		System.out.println("http status : " + httpResponse.getStatusLine());

		int myId = (int) httpResponse.getEntity().getContent();

		System.out.println("myId : ",myId);

		//ouverture d'un compte
		HttpGet openAccount = new HttpGet("http://localhost:8182/app/getNewAccount/"+myId);
		httpResponse = httpClient.execute(openAccount);

		System.out.println("http status : " + httpResponse.getStatusLine());

		int myAccount = (int) httpResponse.getEntity().getContent();
		System.out.println("myAccount : " + myAccount);

		//depot
		TransactionObject myDeposit = new TransactionObject(myId,myAccount,50.0);
		HttpPost depositRequest = new HttpPost("http://localhost:8182/app/deposit");
		depositRequest.setHeader("Content-Type","text/xml");
	    }
	catch(Exception e)
	    {
		e.printStackTrace();
	    }
	finally
	    {
		httpClient.getConnectionManager().shutdown();
	    }
    }
}
