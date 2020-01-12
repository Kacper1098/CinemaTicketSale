package stefanowicz.kacper.converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.help.LocalDateDeserializer;
import stefanowicz.kacper.help.LocalDateSerializer;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Optional;

public abstract class JsonConverter<T> {
    private final String jsonFileName;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
            .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
            .setPrettyPrinting().create();

    private final Type type = ((ParameterizedType)(getClass().getGenericSuperclass())).getActualTypeArguments()[0];

    public JsonConverter(String jsonFileName){this.jsonFileName = jsonFileName;}

    public void toJson(final T element){
        if( element  == null){
            throw new AppException("Json converter exception - to json method - element is null");
        }
        try(FileWriter writer = new FileWriter(jsonFileName)){
            gson.toJson( element, writer);
        }
        catch (Exception e){
            throw new AppException("Json converter exception - to json method - " + e.getMessage());
        }
    }

    public Optional<T> fromJson(){
        try(FileReader reader = new FileReader(jsonFileName)){
            return Optional.of(gson.fromJson(reader, type));
        }
        catch (Exception e){
            throw new AppException("Json converter exception - from json method - " + e.getMessage());
        }
    }
}
