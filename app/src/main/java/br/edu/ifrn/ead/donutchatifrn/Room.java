package br.edu.ifrn.ead.donutchatifrn;

/**
 * Created by Ale on 21/08/2017.
 */

public class Room {

    int id;
    int suapID;
    int year;
    int semestre;
    String title;

    public Room(int id, int suapID, int year, int semestre, String title){
        this.id = id;
        this.suapID = suapID;
        this.year = year;
        this.semestre = semestre;
        this.title = title;
    }
}