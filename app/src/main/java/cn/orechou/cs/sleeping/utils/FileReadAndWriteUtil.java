package cn.orechou.cs.sleeping.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

import cn.orechou.cs.sleeping.Entity.SleepRecord;



public class FileReadAndWriteUtil {

    private String DATA_SAVE_PATH = "/data/data/cn.orechou.cs.sleeping/files/sleep_record.txt";

    private FileInputStream fis;
    private BufferedReader reader;
    private FileOutputStream fos;
    private OutputStreamWriter writer;

    public void setDataPath(String path) {
        DATA_SAVE_PATH = path;
    }

    /*判断文件是否存在*/
    public Boolean checkDataFileExists() {
        File file = new File(DATA_SAVE_PATH);
        return file.exists();
    }

    /*写*/
    public Boolean writeData(ArrayList<SleepRecord> sleepRecords) {
        try {
            fos = new FileOutputStream(DATA_SAVE_PATH, false);
            writer = new OutputStreamWriter(fos);
            Gson gson = new Gson();
            gson.toJson(sleepRecords, writer);
            writer.flush();

            fos.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*读*/
    public ArrayList<SleepRecord> readData() {
        ArrayList<SleepRecord> sleepRecords = null;
        try {
            fis = new FileInputStream(DATA_SAVE_PATH);
            reader = new BufferedReader(new InputStreamReader(fis));

            Gson gson = new Gson();
            //Code taken from http://stackoverflow.com/questions/12384064/gson-convert-from-json-to-a-typed-arraylistt Sept.22,2016
            Type listType = new TypeToken<ArrayList<SleepRecord>>(){}.getType();
            sleepRecords = gson.fromJson(reader, listType);

            fis.close();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sleepRecords;
    }

}
