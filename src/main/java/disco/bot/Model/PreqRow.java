package disco.bot.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PreqRow {
    String pos;
    String driversName;
    String car;
    String numberOfLaps;
    String times;
    String gapToBest;

    public String getFormatted() {
        return String.format("%-2s %-20s %-25s %-3s %-20s %-7s",
                this.pos,
                this.driversName,
                this.car,
                this.numberOfLaps,
                this.times,
                this.gapToBest)
                + "\n";
    }
}
