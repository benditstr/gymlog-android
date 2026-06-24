package de.th.nuernberg.bme.gymlog.ui.statistics;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.th.nuernberg.bme.gymlog.model.MaxWeightPerDay;

class DateAxisFormatter extends ValueFormatter {

    private final List<MaxWeightPerDay> data;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.", Locale.GERMANY);

    DateAxisFormatter(List<MaxWeightPerDay> data) {
        this.data = data;
    }

    @Override
    public String getFormattedValue(float value) {
        int index = Math.round(value);
        if (index < 0 || index >= data.size()) return "";
        return sdf.format(new Date(data.get(index).date));
    }
}