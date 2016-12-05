package com.lge.alt.data;

import java.io.File;

public interface IData {

    void addDataToList(Object data) throws DataTypeMismatchException;

    void makeOutPutData(File file);

    void prepareHtmlDataList();

    void clearData();

}
