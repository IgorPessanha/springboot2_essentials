package academy.devedojo.springboot2.integration;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devedojo.springboot2.domain.Anime;
import academy.devedojo.springboot2.domain.DevDojoUser;
import academy.devedojo.springboot2.requests.AnimePostRequestBody;
import academy.devedojo.springboot2.respository.AnimeRepository;
import academy.devedojo.springboot2.respository.DevDojoUserRepository;
import academy.devedojo.springboot2.wrapper.PageableResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Tests for Anime Controller Integration")
class AnimeControllerIT {

	@Autowired
	@Qualifier(value = "testRestTemplateRoleUser")
	private TestRestTemplate testRestTemplateRoleUser;
	@Autowired
	@Qualifier(value = "testRestTemplateRoleAdmin")
	private TestRestTemplate testRestTemplateRoleAdmin;
	@Autowired
	private AnimeRepository animeRepository;
	@Autowired
	private DevDojoUserRepository devDojoUserRepository;
	
	private static final DevDojoUser USER = DevDojoUser.builder()
			.name("DevDojo Academy")
			.password("{bcrypt}$2a$10$d58NyGM7eEQTlrBpsTh8heDEgomuU7Rx8K0BBv2Sz/zYh/TZPAgtu")
			.username("devdojo")
			.authorities("ROLE_USER")
			.build();
	
	private static final DevDojoUser ADMIN = DevDojoUser.builder()
			.name("William Suane")
			.password("{bcrypt}$2a$10$d58NyGM7eEQTlrBpsTh8heDEgomuU7Rx8K0BBv2Sz/zYh/TZPAgtu")
			.username("william")
			.authorities("ROLE_USER,ROLE_ADMIN")
			.build();
	
	
	@TestConfiguration
	@Lazy
	static class Config {
		
		@Bean(name = "testRestTemplateRoleUser")
		TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
			RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
					.rootUri("http://localhost:"+port)
					.basicAuthentication("devdojo", "academy");
			return new TestRestTemplate(restTemplateBuilder);
		}
		
		@Bean(name = "testRestTemplateRoleAdmin")
		TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {
			RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
					.rootUri("http://localhost:"+port)
					.basicAuthentication("william", "academy");
			return new TestRestTemplate(restTemplateBuilder);
		}
		
	}
	
	@Test
	@DisplayName("list returns list of anime inside page object when successful")
	void list_ReturnsListOfAnimeInsidePageObject_WhenSuccessful() {
		
		String expectedName = animeRepository.save(AnimeCreator.createAnimeToBeSaved()).getName();
		
		devDojoUserRepository.save(USER);

		PageableResponse<Anime> animePage = testRestTemplateRoleUser.exchange("/animes", HttpMethod.GET, null, 
				new ParameterizedTypeReference<PageableResponse<Anime>>() {
				}).getBody();

		Assertions.assertThat(animePage).isNotNull();

		Assertions.assertThat(animePage.toList())
			.isNotNull()
			.isNotEmpty()
			.hasSize(1);

		Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
	}

	@Test
	@DisplayName("listAll returns list of anime when successful")
	void listAll_ReturnsListOfAnime_WhenSuccessful() {
		
		String expectedName = animeRepository.save(AnimeCreator.createAnimeToBeSaved()).getName();
		
		devDojoUserRepository.save(USER);

		List<Anime> animes = testRestTemplateRoleUser.exchange("/animes/all", HttpMethod.GET, null, 
				new ParameterizedTypeReference<List<Anime>>() {
				}).getBody();

		Assertions.assertThat(animes)
			.isNotEmpty()
			.isNotEmpty()
			.hasSize(1);

		Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
	}

	
	@Test
	@DisplayName("findById returns anime when successful")
	void findById_ReturnsAnime_WhenSuccessful() {
		
		Long expectedId = animeRepository.save(AnimeCreator.createAnimeToBeSaved()).getId();
		
		devDojoUserRepository.save(USER);

		Anime anime = testRestTemplateRoleUser.getForObject("/animes/{id}", Anime.class, expectedId);

		Assertions.assertThat(anime).isNotNull();

		Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
	}
	
	@Test
	@DisplayName("findByName returns list of anime when successful")
	void findByName_ReturnsListOfAnime_WhenSuccessful() {
		
		String expectedName = animeRepository.save(AnimeCreator.createAnimeToBeSaved()).getName();
		
		devDojoUserRepository.save(USER);
		
		String url = String.format("/animes/find?name=%s", expectedName); 
		
		List<Anime> animes = testRestTemplateRoleUser.exchange(url, HttpMethod.GET, null, 
				new ParameterizedTypeReference<List<Anime>>() {
				}).getBody();

		Assertions.assertThat(animes)
			.isNotNull()
			.isNotEmpty()
			.hasSize(1);

		Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
	}
	
	@Test
	@DisplayName("findByName returns an empty list of anime is not found")
	void findByName_ReturnsEmptyListOfAnime_WhenAnimeIsNotFound() {
		
		devDojoUserRepository.save(USER);
		
		List<Anime> animes = testRestTemplateRoleUser
				.exchange("/animes/find?name=dbz", HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {
				}).getBody();

		Assertions.assertThat(animes)
			.isNotNull()
			.isEmpty();
	}

	
	@Test
	@DisplayName("save returns anime when successful")
	void save_ReturnsAnime_WhenSuccessful() {
		
		AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();
		
		devDojoUserRepository.save(USER);
		
		ResponseEntity<Anime> entity = testRestTemplateRoleUser.postForEntity("/animes", animePostRequestBody, Anime.class);
				
		Assertions.assertThat(entity).isNotNull();
		
		Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		Assertions.assertThat(entity.getBody()).isNotNull();
		
		Assertions.assertThat(entity.getBody().getId()).isNotNull();
	}
	  
	
	@Test
	@DisplayName("replace updates anime when successful")
	void replace_UpdatesAnime_WhenSuccessful() {
		
		Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		savedAnime.setName("anime");
		
		devDojoUserRepository.save(USER);
		
		ResponseEntity<Void> entity = testRestTemplateRoleUser.exchange("/animes", HttpMethod.PUT, new HttpEntity<>(savedAnime), Void.class);
				
		Assertions.assertThat(entity).isNotNull();
		
		Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		
	}
	  
	@Test
	@DisplayName("delete removes anime when successful")
	void delete_RemovesAnime_WhenSuccessful() {

		Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		
		devDojoUserRepository.save(ADMIN);
		
		ResponseEntity<Void> entity = testRestTemplateRoleAdmin.exchange("/animes/admin/{id}", 
				HttpMethod.DELETE, null, Void.class, savedAnime.getId());
				
		Assertions.assertThat(entity).isNotNull();
		
		Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}
	
	@Test
	@DisplayName("delete returns 403 Forbidden when user is not admin")
	void delete_Returns403Forbidden_WhenUserIsNotAdmin() {

		Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		
		devDojoUserRepository.save(USER);
		
		ResponseEntity<Void> entity = testRestTemplateRoleUser.exchange("/animes/admin/{id}", 
				HttpMethod.DELETE, null, Void.class, savedAnime.getId());
				
		Assertions.assertThat(entity).isNotNull();
		
		Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

}
