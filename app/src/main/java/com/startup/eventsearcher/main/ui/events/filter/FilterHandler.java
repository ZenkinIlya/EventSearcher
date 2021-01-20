package com.startup.eventsearcher.main.ui.events.filter;

import android.content.Context;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.startup.eventsearcher.utils.JsonHandler;

import java.lang.reflect.Type;
import java.util.Objects;

public class FilterHandler {

    private static final String TAG = "myFilterHandler";
    
    public static Filter filter;

    public static Filter getFilter() {
        return filter;
    }

    public static void setFilter(Filter filter) {
        FilterHandler.filter = filter;
    }

    //Инициализация при первом запуске приложения, в дальнейшем данные будут браться из json
    static {
        filter = new Filter("",
                1,
                -1,
                0,
                0,
                0);
    }

    public static Filter getFilterFromJSON(Context context){
        Type type = new TypeToken<Filter>(){}.getType();
        Filter filter = JsonHandler.getSavedObjectFromPreference(
                context,
                "Filter",
                "filterKey",
                type);
        if (filter != null){
            setFilter(filter);
        }
        Log.d(TAG, "getFilterFromJSON " +context.getClass().getSimpleName()+
                " : filter = " + Objects.requireNonNull(FilterHandler.filter).toString());
        return FilterHandler.filter;
    }

    public static void saveFilterToJSON(Context context){
        JsonHandler.saveObjectToSharedPreference(
                context,
                "Filter",
                "filterKey",
                filter);
        Log.d(TAG, "saveFilterToJSON " +context.getClass().getSimpleName()+
                " : filter = " + filter.toString());
    }
}
