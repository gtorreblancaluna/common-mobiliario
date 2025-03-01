package common.model;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class Color {
    
    private int colorId;
    private String color;
    private String tono;
    private String comentario;

    public Color(int colorId, String color) {
        this.colorId = colorId;
        this.color = color;
    }

    @Override
    public String toString() {
        return color;
    }    
}
