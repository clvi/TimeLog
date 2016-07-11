package eu.leneurone.timelog.services.impl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.leneurone.timelog.R;
import eu.leneurone.timelog.model.Marker;
import eu.leneurone.timelog.model.Time;
import eu.leneurone.timelog.services.StorageService;

/**
 * Implements storage on private local storage
 */
public class StorageServiceImpl implements StorageService {

    /** the filename prefix of the data files */
    private static final String FILENAME_PREFIX = "timeLog_";

    @Override
    public void storeDayWorklog(@NonNull Date day, @NonNull Map<Marker, Time> times, @NonNull Context context) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(context.openFileOutput(buildFilename(day), Context.MODE_PRIVATE))) {
            objectOutputStream.writeObject(times);
        } catch (IOException ex) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.technical_error)
                    .setMessage(R.string.technical_error_msg)
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    @Override
    @NonNull
    public Map<Marker, Time> loadDayWorklog(@NonNull Date day, @NonNull Context context) {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(context.openFileInput(buildFilename(day)))) {
            //noinspection unchecked
            return  (Map<Marker, Time>) objectInputStream.readObject();
        } catch (FileNotFoundException e) {
            // do nothing : this is normal if no data has been saved for this day
            return new HashMap<>();
        } catch (ClassNotFoundException | IOException ex) {
            Logger.getLogger(StorageServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return new HashMap<>();
        }
    }

    private static String buildFilename(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.FRANCE);
        return FILENAME_PREFIX + format.format(date);
    }
}
