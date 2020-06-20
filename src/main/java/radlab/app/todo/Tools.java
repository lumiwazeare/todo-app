package radlab.app.todo;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Tools {

    private static final String PREF_NAME = "SHARED_PRE";

    private static String []dayName = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    public final static String []monthName = {"January","February","March","April","May","June","July","August", "September",
    "October","November", "December"};

    public static String getDayName(int day){
        return dayName[day - 1];
    }

    public static String getMonthName(int month){
        return monthName[month];
    }

    //for simplicity, shared preference is use to simulate database
    public static void addToDatabase(TodoModel todoModel, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        //confirm if data exist
        String data = sharedPreferences.getString("todo", "");

        Gson gson = new Gson();
        List<TodoModel> todoModels = new ArrayList<>();
        //empty database
        if(data.isEmpty()){
            todoModels.add(todoModel);

        }else {
            todoModels.addAll(Arrays.asList(gson.fromJson(data, TodoModel[].class)));

            //now add the new data
            todoModels.add(todoModel);
        }

        //update all record in database
        String rawData  = gson.toJson(todoModels);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("todo", rawData);
        editor.apply();

    }

    public static List<TodoModel> getAllDatabase(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        //confirm if data exist
        String data = sharedPreferences.getString("todo", "");

        Gson gson = new Gson();
        //empty database
        if(data.isEmpty()){
            return new ArrayList<>();

        }

        return new ArrayList<>(Arrays.asList(gson.fromJson(data, TodoModel[].class)));
    }

    //for simplicity, shared preference is use to simulate database
    public static void updateDatabase(List<TodoModel> todoModels, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        //confirm if data exist
        String data = sharedPreferences.getString("todo", "");

        Gson gson = new Gson();

        //update all record in database
        String rawData  = gson.toJson(todoModels);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("todo", todoModels.size() > 0 ? rawData : "");
        editor.apply();

    }

    public static String getCurrentDate(){
        final Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DATE);
        String dayOfTheWeek = Tools.getDayName(c.get(Calendar.DAY_OF_WEEK));
        String dayOfTheMonth = Tools.getMonthName(c.get(Calendar.MONTH));
        return (dayOfTheWeek + " " + day + " " + dayOfTheMonth); //Saturday 20 June
    }

}
