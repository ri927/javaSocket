import nu.validator.htmlparser.dom.HtmlDocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;

public class YasaiSearcher {
    /** kurashiruのURL */
    private String yasaiUrl = "https://www.hyponex.co.jp/yasai_daijiten/vegetables";
    /** レシピのリスト */
    private static ArrayList<String> yasaiList;
    /** コンストラクタ */
    public YasaiSearcher() {
        yasaiList = new ArrayList<String>();
        // TLS v1.2 の有効化 (Java 8 以降では指定不要)
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
        try {
            // URLオブジェクトを生成
            URL url = new URL(yasaiUrl);
            // URLオブジェクトから、接続にいくURLConnectionオブジェクトを取得
            URLConnection connection = url.openConnection();
            // 接続
            connection.connect();
            // サーバからやってくるデータをInputStreamとして取得
            InputStream inputStream = connection.getInputStream();
            // 次に inputStream を読み込む InputStreamReader のインスタンス inputStreamReader を生成
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            // さらに inputStreamReader をラップする BufferedReader のインスタンス reader を生成
            BufferedReader reader = new BufferedReader(inputStreamReader);

            // DOMツリーの構築
            HtmlDocumentBuilder builder = new HtmlDocumentBuilder();
            Document document = builder.parse(new InputSource(reader));

            // XPath の表現を扱う XPath オブジェクトを生成
            XPath xPath = XPathFactory.newInstance().newXPath();
            // XPath 式内で接頭辞 h がついている要素を HTML の要素として認識
            xPath.setNamespaceContext(new NamespaceContextHTML());
            // 1つの新着商品に対応する li 要素のリストを得る
            NodeList itemList = (NodeList)xPath.evaluate("//h:section[@class='l-section section vegetables']/h:div/h:ul/h:li",
                    document, XPathConstants.NODESET);
            System.out.println("野菜name数: " + itemList.getLength());
            System.out.println();

            for(int i = 0; i < itemList.getLength(); i++) {	// 各li要素について
                Node itemNode= itemList.item(i);  // li要素
                String name = xPath.evaluate("h:a/h:p[@class='name']", itemNode);
                yasaiList.add(name);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getYasaiList() {
        return yasaiList;
    }

    public static String getRandomYasai(ArrayList<String> itemList){
        Random rand = new Random();
        int num = rand.nextInt(itemList.size());
        String name = itemList.get(num);
        return name;
    }
}

