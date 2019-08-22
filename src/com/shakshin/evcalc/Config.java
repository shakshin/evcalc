package com.shakshin.evcalc;

import org.omg.CORBA.INTERNAL;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Config {
    private List<Event> events;
    private Integer activeEvent;

    public List<Event> getEvents() {
        if (events == null)
            events = new ArrayList<Event>();
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public Integer getActiveEvent() {
        return activeEvent;
    }

    public void setActiveEvent(Integer activeEvent) {
        this.activeEvent = activeEvent;
    }

    private static String filename() {
        return System.getProperty("user.home") + File.separator + ".evcalc.xml";
    }

    private static Config _instance = null;

    public static Config get() {
        if (_instance == null)
        try {
            XMLDecoder decoder =
                    new XMLDecoder(new BufferedInputStream(
                            new FileInputStream(filename())));
            Object o = decoder.readObject();
            decoder.close();
            _instance = (Config) o;
        } catch (Exception e) {
            _instance = new Config();
        }
        return  _instance;
    }

    public void write() throws FileNotFoundException {
        XMLEncoder encoder =
                new XMLEncoder(
                        new BufferedOutputStream(
                                new FileOutputStream(filename())));
        encoder.writeObject(this);
        encoder.close();
    }

    public Integer nextId() {

        if (getEvents().size() < 1)
            return 1;

        Integer res = 0;
        for (Event ev: getEvents()) {
            if (ev.getId() > res)
                res = ev.getId();
        }

        return res + 1;
    }

    public Event eventById(Integer id) {
        if (id != null)
            for (Event ev : getEvents()) {
                if (ev.getId() == id) {
                    return ev;
                }
            }
        return null;
    }
}
