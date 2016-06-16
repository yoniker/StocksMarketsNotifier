package dor.only.dorking.android.stocksmarketsnotifier.ConnectionServer;

/**
 * Created by Yoni on 6/16/2016.
 */

//This class represents a request to the server,including header/content and the server's previous request if it exists
public class RequestToServer {

    private String content;
    private String url;
    private String httpMethod;
    private String response;
    private int status;
    private int numberOfTries;

    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String PUT = "PUT";
    public static final String DELETE="DELETE";

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getNumberOfTries() {
        return numberOfTries;
    }

    public void setNumberOfTries(int numberOfTries) {
        this.numberOfTries = numberOfTries;
    }

    RequestToServer(){numberOfTries=0;}
    RequestToServer( String content,String url, String httpMethod,String response, int status,int numberOfTries){
        this.content=content;
        this.url=url;
        this.httpMethod=httpMethod;
        this.response=response;
        this.status=status;
        this.numberOfTries=numberOfTries;

    }


}
