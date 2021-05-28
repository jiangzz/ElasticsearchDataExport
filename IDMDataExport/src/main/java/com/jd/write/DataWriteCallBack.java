package com.jd.write;


import java.util.List;

public interface DataWriteCallBack<T> {
    public void writeData(List<T> dataList);
}
