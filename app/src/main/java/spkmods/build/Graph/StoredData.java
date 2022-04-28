package spkmods.build.Graph;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 9/27/2016.
 */

public class StoredData {

    public static List<Long> downloadList = new ArrayList<>();
    public static List<Long> uploadList = new ArrayList<>();

    public static long downloadSpeed = 0;
    public static long uploadSpeed = 0;
    public static boolean isSetData = false;

    public static void setZero() {
        isSetData = true;
        // return if listed is full
        for (int i = 0; i < 300; i++) {
            downloadList.add(0L);
            uploadList.add(0L);

        }

    }
}

