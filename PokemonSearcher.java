package finalEx.game;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PokemonSearcher {
    PokemonResponse response;
    String print;

    public PokemonSearcher() {
        try {
            // サーバからやってくるデータをInputStreamとして取得
            InputStreamReader inputStream = new InputStreamReader(new FileInputStream("pokemon_data.json"), "UTF-8");

            // JSON の読み込み
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            response  = mapper.readValue(inputStream, PokemonResponse.class);

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

    public String getPrint() {
        return print;
    }

    public ArrayList<PokemonItem> getItemList(){
        ArrayList<PokemonItem> itemList = new ArrayList<PokemonItem>();
        String name;
        for(int i = 0; i < 807; i++) {
            name = response.data.get(i).name;
            itemList.add(new PokemonItem(name));
        }
        return itemList;
    }

    public static void main(String[] args) {
        PokemonSearcher searcher = new PokemonSearcher();
        ArrayList<PokemonItem> itemList = searcher.getItemList();
        for(PokemonItem item: itemList) {
            System.out.println(item.name + "\n") ;
        }
    }
}
