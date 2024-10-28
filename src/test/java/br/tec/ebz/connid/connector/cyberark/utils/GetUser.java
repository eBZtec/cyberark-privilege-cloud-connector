package br.tec.ebz.connid.connector.cyberark.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GetUser {

    private int id;
    private List<String> groups;
    private String workStreet;
    private String workCity;
    private String workState;
    private String workZip;
    private String workCountry;
    private long lastSuccessfullLoginDate;

    public GetUser() {
        id = new Random().nextInt(1000);
        lastSuccessfullLoginDate = new Date().getTime();

        groups = new ArrayList<>();
        groups.add("Group1");
        groups.add("Group2");
        groups.add("Group3");

        workStreet = "Street Test";
        workCity = "City Test";
        workState = "State Test";
        workZip = "00000-000";
        workCountry = "Brazil";

    }

    public long getLastSuccessfullLoginDate() {
        return lastSuccessfullLoginDate;
    }

    public void setLastSuccessfullLoginDate(long lastSuccessfullLoginDate) {
        this.lastSuccessfullLoginDate = lastSuccessfullLoginDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWorkStreet() {
        return workStreet;
    }

    public void setWorkStreet(String workStreet) {
        this.workStreet = workStreet;
    }

    public String getWorkCity() {
        return workCity;
    }

    public void setWorkCity(String workCity) {
        this.workCity = workCity;
    }

    public String getWorkState() {
        return workState;
    }

    public void setWorkState(String workState) {
        this.workState = workState;
    }

    public String getWorkZip() {
        return workZip;
    }

    public void setWorkZip(String workZip) {
        this.workZip = workZip;
    }

    public String getWorkCountry() {
        return workCountry;
    }

    public void setWorkCountry(String workCountry) {
        this.workCountry = workCountry;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return "createTestUser" + id;
    }

    public String homeEmail() {
        return "test@example.com";
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public List<String> getGroups() {
        return groups;
    }
}
