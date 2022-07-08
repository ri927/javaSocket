import javax.xml.namespace.NamespaceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//名前空間接頭辞とURIの対応、HTML用
public class NamespaceContextHTML implements NamespaceContext {
    private HashMap<String, String> prefixToURI;
    public NamespaceContextHTML() {
        prefixToURI = new HashMap<String, String>();
        prefixToURI.put("h", "http://www.w3.org/1999/xhtml");
    }
    public String getNamespaceURI(String prefix) {	// 接頭辞からURIを求める
        return prefixToURI.get(prefix);
    }
    public Iterator<String> getPrefixes(String uri) {
        ArrayList<String> list = new ArrayList<String>();
        for(Map.Entry<String, String> entry : prefixToURI.entrySet()) {
            if(entry.getValue().equals(uri)) {
                list.add(entry.getKey());
            }
        }
        return list.iterator();
    }
    public String getPrefix(String uri) {
        return getPrefixes(uri).next();
    }
}