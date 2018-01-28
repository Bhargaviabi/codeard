package com.jansen.sander.arduinorgb;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;


import java.util.List;

/**
 * Created by Sander on 20/12/2017.
 */

@Dao
public interface ColorDB {

    @Insert
    void insertAllColors(CustomColor...customColors);

    @Delete
    void delete(CustomColor customColor);

    @Query("DELETE FROM customcolor")
    void reset();

    @Query("DELETE FROM customcolor WHERE cid LIKE :cid")
    void deleteById(int cid);

    @Query("SELECT * FROM customcolor WHERE cid LIKE :cid")
    List<CustomColor> colorByCid(int cid);

    @Query("SELECT * FROM customcolor")
    List<CustomColor> getStoredColors();

    @Query("SELECT * FROM customcolor WHERE cid IN (:customColorIds)")
    List<CustomColor> loadAllByIds(int[] customColorIds);
}
