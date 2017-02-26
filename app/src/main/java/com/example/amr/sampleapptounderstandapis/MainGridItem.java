package com.example.amr.sampleapptounderstandapis;

/**
 * Created by Amr on 26/02/2017.
 */
public class MainGridItem {
    public String getIDD() {
        return IDD;
    }

    public void setIDD(String IDD) {
        this.IDD = IDD;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String IDD;
    private String name;
    private String number;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public MainGridItem() {
        super();
    }
}
