package academy.devedojo.springboot2.requests;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnimePostRequestBody {
	
	@NotBlank(message = "Anime name cannot be null or empty")
	@Schema(description = "This is the Anime's name", example = "Tensei Shitarra Slime Datta Ken", required = true)
	private String name;
}
