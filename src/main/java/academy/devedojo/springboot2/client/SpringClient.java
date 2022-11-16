package academy.devedojo.springboot2.client;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import academy.devedojo.springboot2.domain.Anime;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SpringClient {

	private static final String HTTP_LOCALHOST_ID = "http://localhost:8080/animes/{id}";
	private static final String HTTP_LOCALHOST = "http://localhost:8080/animes/";

	public static void main(String[] args) {
		ResponseEntity<Anime> entity = new RestTemplate().getForEntity(HTTP_LOCALHOST_ID, Anime.class,
				2);
		log.info(entity);

		Anime objectAnime = new RestTemplate().getForObject(HTTP_LOCALHOST_ID, Anime.class, 2);
		log.info(objectAnime);

		Anime[] animes = new RestTemplate().getForObject("http://localhost:8080/animes/all", Anime[].class);
		log.info(Arrays.toString(animes));

		ResponseEntity<List<Anime>> exchange = new RestTemplate().exchange("http://localhost:8080/animes/all",
				HttpMethod.GET, null, new ParameterizedTypeReference<>() {
				});
		log.info(exchange.getBody());
		
		Anime kingdom = Anime.builder()
				.name("Kingdom 1")
				.build();
		Anime postForObject = new RestTemplate().postForObject(HTTP_LOCALHOST, kingdom, Anime.class);
		log.info("saved anime {}", postForObject);
		
		Anime samuraiChamploo = Anime.builder()
				.name("Samurai Champloo 1")
				.build();
		
		ResponseEntity<Anime> exchangeSamurai = new RestTemplate().exchange(HTTP_LOCALHOST, 
				HttpMethod.POST,
				new HttpEntity<>(samuraiChamploo, createJsonHeaders()), 
				Anime.class);
		log.info("saved samurai {}", exchangeSamurai);
		
		Anime body = exchangeSamurai.getBody();
		Long idLong = null;
		if(Objects.nonNull(body)) {
			body.setName("Samurai Champloo 3");
			idLong = body.getId();
		}
		ResponseEntity<Void> exchangeSamuraiPut = new RestTemplate().exchange(HTTP_LOCALHOST, 
				HttpMethod.PUT,
				new HttpEntity<>(body, createJsonHeaders()), 
				Void.class);
		log.info("Updated {}", exchangeSamuraiPut);
		
		ResponseEntity<Void> exchangeSamuraiDelete = new RestTemplate().exchange(HTTP_LOCALHOST_ID, 
				HttpMethod.DELETE,
				null, 
				Void.class, 
				idLong);
		log.info("Deleted {}", exchangeSamuraiDelete);
	}
	
	private static HttpHeaders createJsonHeaders() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return httpHeaders;
	}

}
