package dto.manage;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor    // нужен для десериализации, можно убрать, если не читаешь обратно
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateTestRequest {
    private String name;
    private String description;
}



