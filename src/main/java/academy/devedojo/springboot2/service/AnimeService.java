package academy.devedojo.springboot2.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import academy.devedojo.springboot2.domain.Anime;
import academy.devedojo.springboot2.exception.BadRequestException;
import academy.devedojo.springboot2.respository.AnimeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnimeService {

	private final AnimeRepository animeRepository;

	public Page<Anime> listAll(Pageable pageable) {
		return animeRepository.findAll(pageable);
	}
	
	public List<Anime> listAllNonPageable() {
		return animeRepository.findAll();
	}

	public List<Anime> findByName(String name) {
		return animeRepository.findByName(name);
	}

	public Anime findByIdOrThrowBadResquestException(Long id) {
		return animeRepository.findById(id)
				.orElseThrow(() -> new BadRequestException("Anime not found"));
	}
	
	@Transactional //Nao leva em consideracao as tranasacoes para exeções checked
	public Anime save(Anime anime) {
		return animeRepository.save(anime);
	}

	public void delete(Long id) {
		animeRepository.delete(findByIdOrThrowBadResquestException(id));
	}

	public void replace(Anime anime) {
		findByIdOrThrowBadResquestException(anime.getId());
		animeRepository.save(anime);
	}

}
