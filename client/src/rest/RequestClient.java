package client.src.rest;

import BankIDL.TransactionResult;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import server.src.rest.AccountsObject;
import server.src.rest.TransactionResultObject;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class RequestClient {
    private static String path = "http://localhost:8182/app/";

    private static CloseableHttpClient httpClient;

    static void open(int port) {
        path = "http://localhost:" + port + "/app/";
        httpClient = HttpClientBuilder.create().build();
    }

    static void close() {
        try {
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HttpEntity executeRequest(HttpUriRequest request) {
        HttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(request);
            return httpResponse.getEntity();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    private static int executeIntRequest(HttpUriRequest request) {
        HttpEntity entity = executeRequest(request);
        if (entity != null) {
            try {
                return Integer.parseInt(EntityUtils.toString(entity));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    private static double executeDoubleRequest(HttpUriRequest request) {
        HttpEntity entity = executeRequest(request);
        if (entity != null) {
            try {
                return Double.parseDouble(EntityUtils.toString(entity));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    private static TransactionResult executeTransactionResultRequest(HttpUriRequest request) {
        HttpEntity entity = executeRequest(request);
        if (entity != null) {
            try {
                InputStream inputStream = entity.getContent();
                JAXBContext jc = JAXBContext.newInstance(TransactionResultObject.class);
                Unmarshaller u = jc.createUnmarshaller();
                Object o = u.unmarshal(inputStream);
                int val =  ((TransactionResultObject) o).result;
                return TransactionResult.from_int(val);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return TransactionResult.ERROR_ACCESS_DENIED;
    }

    static List<Integer> executeAccountsRequest(HttpUriRequest request) {
        HttpEntity entity = executeRequest(request);
        if (entity != null) {
            try {
                InputStream inputStream = entity.getContent();
                JAXBContext jc = JAXBContext.newInstance(AccountsObject.class);
                Unmarshaller u = jc.createUnmarshaller();
                Object o = u.unmarshal(inputStream);
                return ((AccountsObject) o).accounts;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<Integer>() {};
    }

    private static String getUri(String route, String params) {
        return path + route + (params.isEmpty() ? "" : "?" + params);
    }

    static int getIntResponse(String route, String params) {
        HttpGet httpGet = new HttpGet(getUri(route, params));
        return executeIntRequest(httpGet);
    }

    static double getDoubleResponse(String route, String params) {
        HttpGet httpGet = new HttpGet(getUri(route, params));
        return executeDoubleRequest(httpGet);
    }

    static int postIntResponse(String route, String params) {
        return executeIntRequest(new HttpPost(getUri(route, params)));
    }

    static TransactionResult deleteResponse(String route, String params) {
        return executeTransactionResultRequest(new HttpDelete(getUri(route, params)));
    }

    static TransactionResult postTransactionResultResponse(String route, String params) {
        return executeTransactionResultRequest(new HttpPost(getUri(route, params)));
    }

    static void postResponse(String route, String params) {
        executeRequest(new HttpPost(getUri(route, params)));
    }

    static List<Integer> getAccountsResponse(String route, String params) {
        return executeAccountsRequest(new HttpGet(getUri(route, params)));
    }
}
