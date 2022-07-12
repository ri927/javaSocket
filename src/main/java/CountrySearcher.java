import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Random;

public class CountrySearcher {
    CountryResponse response;

    public CountrySearcher() {
        try {
            // サーバからやってくるデータをInputStreamとして取得
            InputStreamReader inputStream = new InputStreamReader(new FileInputStream("Country_list.json"), "UTF-8");

            // JSON の読み込み
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            response  = mapper.readValue(inputStream, CountryResponse.class);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch	(MalformedURLException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public ArrayList<String> getItemList(){
        ArrayList<String> itemList = new ArrayList<String>();
        for(int i = 0; i < response.countries.size(); i++) {
            String name = response.countries.get(i).name.full;
            String shortName = response.countries.get(i).name.shortName;
            if(shortName != null){
                name = shortName;
            }
            itemList.add(name);
        }
        return itemList;
    }

    public static String getRandomCountry(ArrayList<String> itemList){
        Random rand = new Random();
        int num = rand.nextInt(itemList.size());
        String name = itemList.get(num);
        return name;
    }
}