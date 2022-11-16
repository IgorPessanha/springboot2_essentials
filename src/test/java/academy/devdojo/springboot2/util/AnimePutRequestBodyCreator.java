package academy.devdojo.springboot2.util;

import academy.devedojo.springboot2.requests.AnimePutRequestBody;

public class AnimePutRequestBodyCreator {
	
	public static AnimePutRequestBody createAnimePutRequestBody() {
		return AnimePutRequestBody.builder()
				.id(AnimeCreator.createValidUpdatedAnime().getId())
				.name(AnimeCreator.createValidUpdatedAnime().getName())
				.build();
	}
	
}
