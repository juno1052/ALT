package com.lge.alt.data;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import android.util.Log;

public class DataManager {

    public static final String TAG = "DataManager";

    public enum DataType {
        MAIN, SYSTEM, NODE, KERNEL, DUMP
    }

    private static final DataManager mInstance = new DataManager();
    private LinkedHashMap<DataType, IData> mMap = new LinkedHashMap<DataType, IData>();

    private DataManager() {

        super();

        for (DataType dataType : DataType.values()) {
            switch (dataType) {
            case KERNEL:
                mMap.put(dataType, new KernelLogData());
                break;
            case MAIN:
                mMap.put(dataType, new MainLogData());
                break;
            case SYSTEM:
                mMap.put(dataType, new SystemLogData());
                break;
            case NODE:
                mMap.put(dataType, new NodeData());
                break;
            case DUMP:
                mMap.put(dataType, new DumpData());
                break;

            default:
                Log.e(TAG, "not support data type in DataManager");

            }
        }
    }

    public static DataManager getInstance() {

        return mInstance;
    }

    public void addDataToMap(Object data, DataType dataType)
            throws DataTypeMismatchException {

        synchronized (this) {
            mMap.get(dataType).addDataToList(data);
        }
    }

    public IData getData(DataType dataType) {

        return mMap.get(dataType);
    }

    public void clearData(DataType dataType, boolean all)
            throws DataTypeMismatchException {

        Iterator<DataType> it = mMap.keySet().iterator();

        synchronized (this) {
            if (all == true) {
                while (it.hasNext()) {
                    mMap.get(it.next()).clearData();
                }
            } else {
                mMap.get(dataType).clearData();
            }
        }
    }

    public void makeOutputDataText(File dir) {

        String fileName = "report.txt";

        File file = new File(dir, fileName);

        Iterator<DataType> it = mMap.keySet().iterator();

        synchronized (this) {
            while (it.hasNext()) {
                mMap.get(it.next()).makeOutPutData(file);
            }
        }
    }
}
