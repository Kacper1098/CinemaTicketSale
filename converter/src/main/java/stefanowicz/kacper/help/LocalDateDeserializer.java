package stefanowicz.kacper.help;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import stefanowicz.kacper.exception.AppException;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public LocalDate deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try{
            return LocalDate.parse(jsonElement.getAsString(), formatter);
        }
        catch (Exception e){
            throw new AppException("LocalDateDeserializer exception - " + e.getMessage());
        }
    }
}
