package com.kurata.hotelmanagement.data.model;

import java.util.ArrayList;

public class Popula {

    private String name;
    private ArrayList<String> pop_id;

    public Popula() {

    }

    public Popula(String name, ArrayList<String> pop_id) {

        this.name = name;
        this.pop_id = pop_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getPop_id() {
        return pop_id;
    }

    public void setPop_id(ArrayList<String> pop_id) {
        this.pop_id = pop_id;
    }

}

