package academy.devedojo.springboot2.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import academy.devedojo.springboot2.domain.Anime;
import academy.devedojo.springboot2.requests.AnimePostRequestBody;
import academy.devedojo.springboot2.requests.AnimePutRequestBody;

@Mapper(componentModel = "spring")
public abstract class AnimeMapper {
	
	public static final AnimeMapper INST_ANIME_MAPPER = Mappers.getMapper(AnimeMapper.class);
	
	@Mapping(target = "id", ignore = true)
	public abstract Anime toAnime(AnimePostRequestBody animePostRequestBody);
	
	public abstract Anime toAnime(AnimePutRequestBody animePutRequestBody);
	
}
