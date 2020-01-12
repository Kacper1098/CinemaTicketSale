package stefanowicz.kacper.help;

import com.google.gson.*;
import stefanowicz.kacper.exception.AppException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @Override
    public LocalDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try{
            return LocalDateTime.parse(jsonElement.getAsString(), formatter);
        }
        catch (Exception e){
            throw new AppException("LocalDateDeserializer exception - " + e.getMessage());
        }
    }
}
