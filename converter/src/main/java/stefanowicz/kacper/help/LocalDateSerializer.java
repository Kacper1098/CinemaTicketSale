package stefanowicz.kacper.help;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import stefanowicz.kacper.exception.AppException;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateSerializer implements JsonSerializer<LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext jsonSerializationContext) {
        try{
            return new JsonPrimitive(formatter.format(localDate));
        }
        catch (Exception e) {
            throw new AppException("LocalDateSerializer exception - " + e.getMessage());
        }
    }
}
