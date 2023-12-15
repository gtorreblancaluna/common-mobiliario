package common.model.providers;

import java.util.List;
import lombok.Data;

@Data
public class OrderProviderCopyParameter {
    private String currentFolio;
    private List<String> orders;
}
