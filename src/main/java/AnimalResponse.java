import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AnimalResponse {
    @JsonProperty("2")
    public List<String> two;
//    @JsonProperty("3")
//    public List<String> three;
}
