package stefanowicz.kacper.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import stefanowicz.kacper.enums.TicketHistoryFilter;
import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.help.LocalDateSerializer;
import stefanowicz.kacper.model.User;
import stefanowicz.kacper.service.TicketHistoryService;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public final class UserDataService {
    private UserDataService(){}

    private static Scanner scn = new Scanner(System.in);

    public static int getInt(String message){
        System.out.println(message);

        String text = scn.nextLine();

        if(!text.matches("\\d+")){
            throw new AppException("This is not int value!");
        }
        return Integer.parseInt(text);
    }

    public static String getString(String message){
        System.out.println(message);
        return scn.nextLine();
    }

    public static <E extends Enum<E>> E getSortBy(Class<E> enumType){
        if( enumType == null ){
            throw new AppException("GetSortBy method - enum is null");
        }
        var counter = new AtomicInteger(0);
        var values = enumType.getEnumConstants();
        Arrays
                .stream(values)
                .forEach(value -> System.out.println(counter.incrementAndGet() + ". " + value));

        int choice = getInt("Choose sort type: ");

        if(choice < 1 || choice > values.length){
            throw new AppException("No sort type with given value!");
        }
        return values[choice - 1];
    }

    public static boolean getBoolean(String message) {
        System.out.println(message + " [ y / n ]");
        String text =  scn.nextLine();
        if( !text.toLowerCase().matches("[yn]")){
            throw new AppException("Invalid value! Permitted values are [y, n]");
        }
        return text.toLowerCase().equals("y");
    }

    public static LocalDate getDate(String message){
        System.out.println(message);
        int days = getInt("Day: ");
        int month = getInt("Month: ");
        int year = getInt("Year: ");

        try{
            return LocalDate.of(year, month, days);
        }
        catch (Exception e){
            throw new AppException("Date input exception - " + e.getMessage());
        }
    }

    public static List<Field> getFieldsToUpdate(Class c, String message){
        if(c == null){
            throw new AppException("Class argument is null");
        }
        System.out.println(message);
        List<Field> fields = Arrays.stream(c.getDeclaredFields()).filter(field -> !field.getName().toLowerCase().matches("id")).collect(Collectors.toList());
        int fieldsSize = fields.size();
        List<Field> chosenFields = new ArrayList<>();
        var counter = new AtomicInteger(0);
        int choice;
        do{
            counter.set(0);
            fields
                    .forEach(field -> System.out.println(counter.incrementAndGet() + ". " + field.getName()));
            System.out.println("0. Done");
            choice = getInt("Choose fields to update: ");
            if(choice  > fields.size()){
                throw new AppException("There is no component with given value!");
            }
           try{
               if(choice == 0 ){
                   return chosenFields;
               }
               else{
                   chosenFields.add(fields.get(choice - 1));
                   fields.remove(choice - 1);
               }
           }
           catch (Exception e){
               throw new AppException("Get fields to update method exception - " + e.getMessage());
           }
        }while(chosenFields.size() != fieldsSize);

        return chosenFields;
    }

    public static BigDecimal getBigDecimal(String message){
        System.out.println(message);

        String text = scn.nextLine();
        if( !text.matches("(\\d+\\.)?\\d+") && !text.matches("\\d+")){
            throw new AppException("Wrong value");
        }
        return new BigDecimal(text);
    }

    public static <T> String toJson(T t){
        try{
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateSerializer()).create();
            return gson.toJson(t);
        }
        catch (Exception e){
            throw new AppException("to json conversion exception in menu service");
        }
    }

    public static void close(){
        if(scn != null){
            scn.close();
            scn = null;
        }
    }
}
