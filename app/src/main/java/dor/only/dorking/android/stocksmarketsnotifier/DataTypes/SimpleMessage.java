package dor.only.dorking.android.stocksmarketsnotifier.DataTypes;

import java.util.ArrayList;
import java.util.List;


public class SimpleMessage {

    private String mTheMessage;
    List<Link> links=new ArrayList();

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public SimpleMessage(String mTheMessage){
        this.mTheMessage=mTheMessage;

    }

    public SimpleMessage(){
        mTheMessage="";

    }

    public String getTheMessage() {
        return mTheMessage;
    }

    public void setTheMessage(String mTheMessage) {
        this.mTheMessage = mTheMessage;
    }

    public void  addLink(String rel,String url){
        Link theLink=new Link();
        theLink.setRel(rel);
        theLink.setUrl(url);
        links.add(theLink);

    }


}
