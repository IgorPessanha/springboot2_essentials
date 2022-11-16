package academy.devedojo.springboot2.respository;

import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devedojo.springboot2.domain.Anime;

@DataJpaTest
@DisplayName("Tests for Anime Repository")
class AnimeRepositoryTest {
	
	@Autowired
	private AnimeRepository animeRepository;
	
	@Test
	@DisplayName("Save persists anime when successful")
	void save_PersistsAnime_WhenSuccessful() {
		
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
		
		Anime animeSaved = animeRepository.save(animeToBeSaved);
		
		Assertions.assertThat(animeSaved).isNotNull();
		
		Assertions.assertThat(animeSaved.getId()).isNotNull();
		
		Assertions.assertThat(animeSaved.getName()).isEqualTo(animeToBeSaved.getName());
	}
	
	@Test
	@DisplayName("Save updates anime when successful")
	void save_UpdatesAnime_WhenSuccessful() {
		
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
		
		Anime animeSaved = animeRepository.save(animeToBeSaved);
		
		animeSaved.setName("Overlord");
		
		Anime animeUpdated = this.animeRepository.save(animeSaved);
		
		Assertions.assertThat(animeUpdated).isNotNull();
		
		Assertions.assertThat(animeUpdated.getId()).isNotNull();
		
		Assertions.assertThat(animeUpdated.getName()).isEqualTo(animeSaved.getName());
	}
	
	@Test
	@DisplayName("Delete removes anime when successful")
	void delete_RemovesAnime_WhenSuccessful() {
		
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
		
		Anime animeSaved = animeRepository.save(animeToBeSaved);
		
		this.animeRepository.delete(animeSaved);
		
		Optional<Anime> animeOptional = this.animeRepository.findById(animeSaved.getId());
		
		Assertions.assertThat(animeOptional).isEmpty();
		
	}
	
	@Test
	@DisplayName("Find by Name returns list of anime when successful")
	void findByName_ReturnsListOfAnime_WhenSuccessful() {
		
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
		
		Anime animeSaved = animeRepository.save(animeToBeSaved);
		
		String name = animeSaved.getName();
		
		List<Anime> animes = this.animeRepository.findByName(name);
		
		Assertions.assertThat(animes)
			.isNotEmpty()
			.contains(animeSaved);
	}
	
	@Test
	@DisplayName("Find by Name returns empty list when Anime is not found")
	void findByName_ReturnsEmptyList_WhenAnimeIsNotFound() {
		
		List<Anime> animes = this.animeRepository.findByName("anime");
		
		Assertions.assertThat(animes).isEmpty();
	}
	
	@Test
	@DisplayName("Save case 1 throw ConstraintViolationException when name cannot be null or empty")
	void saveCase1_ThrowConstraintViolationException_WhenNameCannotBeNullOrEmpty() {
		
		Anime anime = new Anime();
		
		Assertions.assertThatThrownBy(() -> this.animeRepository.save(anime))
			.isInstanceOf(ConstraintViolationException.class);
		
	}
	
	@Test
	@DisplayName("Save case 2 throw ConstraintViolationException when name cannot be null or empty")
	void saveCase2_ThrowConstraintViolationException_WhenNameCannotBeNullOrEmpty() {
		
		Anime anime = new Anime();
		
		Assertions.assertThatExceptionOfType(ConstraintViolationException.class)
			.isThrownBy(() -> this.animeRepository.save(anime))
			.withMessageContaining("Anime name cannot be null or empty");
	}

}
