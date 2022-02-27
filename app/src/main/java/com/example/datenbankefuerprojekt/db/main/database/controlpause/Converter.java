package com.example.datenbankefuerprojekt.db.main.database.controlpause;

import androidx.room.TypeConverter;

import java.sql.Date;

/**
 * @author Abdurrahman Azattemür
 * <p>Quelle: https://stackoverflow.com/questions/50313525/room-using-date-field </p>
 *
 * <p>Diese Klasse wird benötigt, um das Datum in die Datenbank einspeichern zu können. Es ist der Converter für Date.</p>
 */
public class Converter {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}