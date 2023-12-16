package common.model.providers;

import java.util.List;
import lombok.Data;

@Data
public class OrderProviderCopyParameter {
    private List<String> orders;
    private Integer usuarioId;
}
