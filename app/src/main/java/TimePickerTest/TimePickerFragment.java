package TimePickerTest;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar user_calendar = Calendar.getInstance();
        int hour = user_calendar.get(Calendar.HOUR_OF_DAY);
        int minute = user_calendar.get(Calendar.MINUTE);
        TimePickerDialog user_time_dialog = new TimePickerDialog(
                getContext(), this, hour, minute, DateFormat.is24HourFormat(getContext())
        );
        return user_time_dialog;

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


    }
}
