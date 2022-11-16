package academy.devedojo.springboot2.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devedojo.springboot2.domain.Anime;
import academy.devedojo.springboot2.exception.BadRequestException;
import academy.devedojo.springboot2.respository.AnimeRepository;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for Anime Service")
class AnimeServiceTest {

	@InjectMocks
	private AnimeService animeService;
	@Mock
	private AnimeRepository animeRepositoryMock;
	
	@BeforeEach
	void setUp() {
		
		PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
		
		BDDMockito.when(animeRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class)))
			.thenReturn(animePage);
		
		BDDMockito.when(animeRepositoryMock.findAll())
			.thenReturn(List.of(AnimeCreator.createValidAnime()));
		
		BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
			.thenReturn(Optional.of(AnimeCreator.createValidAnime()));
		
		BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
			.thenReturn(List.of(AnimeCreator.createValidAnime()));
		
		BDDMockito.when(animeRepositoryMock.save(ArgumentMatchers.any(Anime.class)))
			.thenReturn(AnimeCreator.createValidAnime());
		
		BDDMockito.doNothing().when(animeRepositoryMock).delete(ArgumentMatchers.any(Anime.class));
		
	} 
	
	@Test
	@DisplayName("listAll returns list of anime inside page object when successful")
	void listAll_ReturnsListOfAnimeInsidePageObject_WhenSuccessful() {
		
		String expectedName = AnimeCreator.createValidAnime().getName();
		
		Page<Anime> animePage = animeService.listAll(PageRequest.of(1, 1));
		
		Assertions.assertThat(animePage).isNotEmpty();
		
		Assertions.assertThat(animePage.toList())
			.isNotEmpty()
			.hasSize(1);
		
		Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
	}
	
	@Test
	@DisplayName("listAllNonPageable returns list of anime when successful")
	void listAllNonPageable_ReturnsListOfAnime_WhenSuccessful() {
		
		String expectedName = AnimeCreator.createValidAnime().getName();
		
		List<Anime> animes = animeService.listAllNonPageable();
		
		Assertions.assertThat(animes)
			.isNotEmpty()
			.isNotEmpty()
			.hasSize(1);
		
		Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
	}
	
	@Test
	@DisplayName("findByIdOrThrowBadResquestException returns anime when successful")
	void findByIdOrThrowBadResquestException_ReturnsAnime_WhenSuccessful() {
		
		Long expectedId = AnimeCreator.createValidAnime().getId();
		
		Anime anime = animeService.findByIdOrThrowBadResquestException(1L);
		
		Assertions.assertThat(anime).isNotNull();
		
		Assertions.assertThat(anime.getId())
			.isNotNull()
			.isEqualTo(expectedId);
	}
	
	@Test
	@DisplayName("findByIdOrThrowBadResquestException throws BadRequestException when anime is not found")
	void findByIdOrThrowBadResquestException_ThrowsBadRequestException_WhenAnimeIsNotFound	() {
		
		BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
		.thenReturn(Optional.empty());
		
		Assertions.assertThatThrownBy(() -> animeService.findByIdOrThrowBadResquestException(1L))
			.isInstanceOf(BadRequestException.class);
	}
	
	@Test
	@DisplayName("findByName returns list of anime when successful")
	void findByName_ReturnsListOfAnime_WhenSuccessful() {
		
		String expectedName = AnimeCreator.createValidAnime().getName();
		
		List<Anime> animes = animeService.findByName("anime");
		
		Assertions.assertThat(animes)
			.isNotEmpty()
			.isNotEmpty()
			.hasSize(1);
		
		Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
	}
	
	@Test
	@DisplayName("findByName returns an empty list of anime is not found")
	void findByName_ReturnsEmptyListOfAnime_WhenAnimeIsNotFound() {
		
		BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
			.thenReturn(Collections.emptyList());
		
		List<Anime> animes = animeService.findByName("anime");
		
		Assertions.assertThat(animes)
			.isNotNull()
			.isEmpty();
	}
	
	@Test
	@DisplayName("save returns anime when successful")
	void save_ReturnsAnime_WhenSuccessful() {
		
		Anime anime = animeService.save(AnimeCreator.createValidAnime());
		
		Assertions.assertThat(anime)
			.isNotNull()
			.isEqualTo(AnimeCreator.createValidAnime());
	}
	
	@Test
	@DisplayName("replace updates anime when successful")
	void replace_UpdatesAnime_WhenSuccessful() {
		
		Assertions.assertThatCode(() -> animeService.replace(AnimeCreator.createValidUpdatedAnime()))
			.doesNotThrowAnyException();
		
	}
	
	@Test
	@DisplayName("delete removes anime when successful")
	void delete_RemovesAnime_WhenSuccessful() {
		
		Assertions.assertThatCode(() -> animeService.delete(1L))
			.doesNotThrowAnyException();
		
	}

}
