package com.jansen.sander.arduinorgb;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Sander on 20/12/2017.
 */
@Entity
public class CustomColor {
    @PrimaryKey(autoGenerate = true)
    private int cid;

    @ColumnInfo(name = "rgb_red")
    private int red;

    @ColumnInfo(name = "rgb_green")
    private int green;

    @ColumnInfo(name = "rgb_blue")
    private int blue;

    public int getCid() {
        return cid;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public CustomColor(int red, int green, int blue){
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
}
