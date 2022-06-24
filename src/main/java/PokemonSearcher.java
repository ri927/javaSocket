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
        for(int i = 0; i < response.data.size(); i++) {
            name = response.data.get(i).name;
            itemList.add(new PokemonItem(name));
        }
        return itemList;
    }

    public static String getRandomPokemon(ArrayList<PokemonItem> itemList){
        Random rand = new Random();
        int num = rand.nextInt(itemList.size()) + 100;

        String name = itemList.get(num).name;
        return name;
    }

    public static void main(String[] args) {
        PokemonSearcher searcher = new PokemonSearcher();
        ArrayList<PokemonItem> itemList = searcher.getItemList();
        System.out.println(getRandomPokemon(itemList));

//        for(PokemonItem item: itemList) {
//            System.out.println(item.name + "\n") ;
//        }
    }
}