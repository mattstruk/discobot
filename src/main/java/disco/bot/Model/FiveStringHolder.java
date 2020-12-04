package disco.bot.Model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FiveStringHolder {
    private String first;
    private String second ;
    private String third;
    private String fourth;
    private String fifth;
}
