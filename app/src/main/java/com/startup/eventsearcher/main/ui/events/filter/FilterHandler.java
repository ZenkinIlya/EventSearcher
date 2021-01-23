package com.startup.eventsearcher.main.ui.events.filter;

import android.content.Context;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.startup.eventsearcher.utils.JsonHandler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

public class FilterHandler {

    private static final String TAG = "myFilterHandler";

    public static Filter filter;
    private static String searchText;
    private static ArrayList<String> arrayListCategory;

    public static Filter getFilter() {
        return filter;
    }

    public static void setFilter(Filter filter) {
        FilterHandler.filter = filter;
    }

    public static String getSearchText() {
        return searchText;
    }

    public static void setSearchText(String searchText) {
        FilterHandler.searchText = searchText;
    }

    public static ArrayList<String> getArrayListCategory() {
        return arrayListCategory;
    }

    public static void setArrayListCategory(ArrayList<String> arrayListCategory) {
        FilterHandler.arrayListCategory = arrayListCategory;
    }

    //Инициализация при первом запуске приложения, в дальнейшем данные будут браться из json
    static {
        filter = new Filter("",
                0,
                -1,
                0,
                0,
                0);

        setSearchText("");
        setArrayListCategory(new ArrayList<>());
    }

    public static Filter getFilterFromJSON(Context context){
        Type type = new TypeToken<Filter>(){}.getType();
        Filter filterFromJson = JsonHandler.getSavedObjectFromPreference(
                context,
                "Filter",
                "filterKey",
                type);
        if (filterFromJson != null){
            setFilter(filterFromJson);
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
