package stefanowicz.kacper.converter;

import stefanowicz.kacper.model.Movie;

public class MovieJsonConverter extends JsonConverter<Movie> {
    public MovieJsonConverter(String jsonFileName) {
        super(jsonFileName);
    }
}
