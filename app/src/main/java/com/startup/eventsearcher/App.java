package com.startup.eventsearcher;

import android.app.Application;
import android.content.res.TypedArray;
import android.util.Log;

import com.startup.eventsearcher.main.ui.events.model.Category;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.events.model.EventsList;
import com.startup.eventsearcher.main.ui.profile.model.CurrentPerson;
import com.startup.eventsearcher.main.ui.profile.model.Person;

import java.util.ArrayList;

public class App extends Application {
    
    public static ArrayList<Category> categoryArrayList = new ArrayList<>();

    public static ArrayList<Category> getCategoryArrayList() {
        return categoryArrayList;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Создание пользователя
        CurrentPerson.setPerson(new Person(
                "qwerty",
                "123456",
                "Harry",
                "Potter",
                "JacqueFresco@mail.ru"
        ));

        initArrayListCategory();
        initArrayListEvent();
    }

    private void initArrayListEvent() {
        //Считывание эвентов из SharedPreference
        ArrayList<Event> eventArrayListFromJSON = EventsList.getEventArrayListFromJSON(this);
        if (eventArrayListFromJSON == null){
            //Сохранение эвентов (пустого списка) в SharedPreference
            EventsList.saveEventArrayListInJSON(this);
        }
    }

    private void initArrayListCategory() {
        //массив идентификаторов картинок-категорий
        TypedArray categoryImageResources = getResources()
                .obtainTypedArray(R.array.category_images);

        //список названий категорий
        String[] arrayListNameCategory = getResources().getStringArray(R.array.category);

        //заполнение списка объектами Category(название категории, идентификатор картинки)
        for (int i = 0; i < arrayListNameCategory.length; i++) {
            categoryArrayList.add(new Category(arrayListNameCategory[i], categoryImageResources.getResourceId(i, 0)));
        }
        categoryImageResources.recycle();
    }

    @Override
    public void onTerminate() {
        Log.d("myMap", "App onTerminate()");
        super.onTerminate();
    }
}
