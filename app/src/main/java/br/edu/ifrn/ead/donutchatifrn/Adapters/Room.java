package br.edu.ifrn.ead.donutchatifrn.Adapters;

/**
 * Created by Ale on 21/08/2017.
 */

public class Room {

    public int id;
    public int suapID;
    public int year;
    public int semestre;
    public String title;

    public Room(int id, int suapID, int year, int semestre, String title){
        this.id = id;
        this.suapID = suapID;
        this.year = year;
        this.semestre = semestre;
        this.title = title;
    }
}