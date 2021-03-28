package com.startup.eventsearcher.utils.dateTimeMaterialPicker;

import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeMaterialPicker {

    private Long saveDateSelection;
    private int saveHour;
    private int saveMinute;
    private final IDateTimeMaterialPicker iDateTimeMaterialPicker;
    private final FragmentManager supportFragmentManager;

    public DateTimeMaterialPicker(IDateTimeMaterialPicker iDateTimeMaterialPicker,
                                  FragmentManager supportFragmentManager) {
        this.iDateTimeMaterialPicker = iDateTimeMaterialPicker;
        this.supportFragmentManager = supportFragmentManager;
    }

    public Long getSaveDateSelection() {
        return saveDateSelection;
    }

    public void getMaterialDatePicker(){
        CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
        constraintBuilder.setValidator(DateValidatorPointForward.now());

        if (saveDateSelection == null){
            saveDateSelection = new Date().getTime();
        }

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setCalendarConstraints(constraintBuilder.build());
        builder.setSelection(saveDateSelection);

        MaterialDatePicker<Long> materialDatePicker = builder.build();
        materialDatePicker.show(supportFragmentManager, "DATE_PICKER");

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            saveDateSelection = selection;
            Date date = new Date(saveDateSelection);
            SimpleDateFormat localDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            iDateTimeMaterialPicker.onGetDate(localDateFormat.format(date));
        });
    }

    public void getMaterialTimePicker(){
        if (saveHour == 0 && saveMinute == 0){
            Calendar calendar = Calendar.getInstance();
            saveHour = calendar.get(Calendar.HOUR_OF_DAY);
            saveMinute = calendar.get(Calendar.MINUTE);
        }

        MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(saveHour)
                .setMinute(saveMinute)
                .build();

        materialTimePicker.show(supportFragmentManager, "TIME_PICKER");

        materialTimePicker.addOnPositiveButtonClickListener(view -> {
            saveHour = materialTimePicker.getHour();
            saveMinute = materialTimePicker.getMinute();
            iDateTimeMaterialPicker.onGetTime(saveHour + ":" + saveMinute);
        });
    }
}
