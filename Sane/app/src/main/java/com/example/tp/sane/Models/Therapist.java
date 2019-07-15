package com.example.tp.sane.Models;

public class Therapist {

    int ID, Therapist_ID;
    int valid;
    private String name, surname, phoneNumber, DoB, discipline;

    public Therapist(int ID, int Therapist_ID, String name, String surname, String DoB, String phoneNumber, String discipline, int valid) {
        this.ID = ID;
        this.Therapist_ID = Therapist_ID;
        this.name = name;
        this.surname = surname;
        this.DoB = DoB;
        this.phoneNumber = phoneNumber;
        this.discipline = discipline;
        this.valid = valid;
    }

    public int getID() {
        return ID;
    }
    public int getTherapist_ID() {
        return Therapist_ID;
    }

    public int getValid() {
        return valid;
    }

    private String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDiscipline() { return discipline; }

    public void changeText1(String text) { name = text; }

    public String getDoB() {
        return DoB;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
}
