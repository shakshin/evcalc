package com.shakshin.evcalc;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

public class Cmdline {

    private static String getArg(List<String> src) {
        if (src.size() < 1) return null;
        String res = src.get(0);
        src.remove(0);
        return res;
    }

    private static Event getEvent() {
        Config cfg = Config.get();
        if (cfg.getActiveEvent() == null || cfg.getEvents() == null || cfg.getEvents().size() < 1) {
            return null;
        }
        for (Event ev : cfg.getEvents())
            if (ev.getId() == cfg.getActiveEvent())
                return ev;

        return null;
    }

    public static void process(String[] args) {

        List<String> largs = new LinkedList<String>();
        for (int i = 0; i < args.length; i++) largs.add(args[i]);

        String mod = getArg(largs);

        if (mod == null) {
            help();
            return;
        }

        switch (mod.toLowerCase()) {
            case "event":
                event(largs);
                break;

            case "entry":
                entry(largs);
                break;

            case "party":
                party(largs);
                break;

            case "expense":
                expense(largs);
                break;

            case "help":
                help();
                break;

            default:
                System.err.println("Wrong argument: " + mod);
                break;
        }

    }

    private static void event(List<String> args) {
        String cmd = getArg(args);
        if (cmd == null) {
            System.err.println("Command expected");
            return;
        }
        switch (cmd.toLowerCase()) {
            case "add":
                addEvent(args);
                break;

            case "drop":
                dropEvent(args);
                break;

            case "list":
                listEvents();
                break;

            case "select":
                selectEvent(args);
                break;

            case "calc":
                calc();
                break;

            case "info":
                eventInfo(args);
                break;

            default:
                System.err.println("Wrong argument: " + cmd);
                break;
        }
    }

    private static void party(List<String> args) {
        String cmd = getArg(args);
        if (cmd == null) {
            System.err.println("Command expected");
            return;
        }
        switch (cmd.toLowerCase()) {
            case "add":
                addParty(args);
                break;

            case "drop":
                dropParty(args);
                break;

            case "info":
                partyInfo(args);
                break;

            case "use":
                partyUse(args);
                break;

            case "unuse":
                partyUnuse(args);
                break;

            case "merge":
                partyMerge(args);
                break;

            case "unmerge":
                partyUnmerge(args);
                break;

            default:
                System.err.println("Wrong argument: " + cmd);
                break;
        }
    }

    private static void entry(List<String> args) {
        String cmd = getArg(args);
        if (cmd == null) {
            System.err.println("Command expected");
            return;
        }
        switch (cmd.toLowerCase()) {
            case "add":
                addEntry(args);
                break;

            case "drop":
                dropEntry(args);
                break;

            case "list":
                listEntries();
                break;

            default:
                System.err.println("Wrong argument: " + cmd);
                break;
        }
    }

    private static void expense(List<String> args) {
        String cmd = getArg(args);
        if (cmd == null) {
            System.err.println("Command expected");
            return;
        }
        switch (cmd.toLowerCase()) {
            case "add":
                addExpense(args);
                break;

            case "drop":
                dropExpense(args);
                break;

            default:
                System.err.println("Wrong argument: " + cmd);
                break;
        }
    }

    private static void listEvents() {
        Config cfg = Config.get();
        if (cfg.getEvents().size() < 1) {
            System.out.println("Event list is empty");
            return;
        }

        for (Event ev : cfg.getEvents())  {
            System.out.printf("%d: %s (%s) %s %n", ev.getId(), ev.getTitle(), ev.getDate(), (ev.getId() == cfg.getActiveEvent() ? "<-- active" : " "));
        }
    }

    private static void addEvent(List<String> args) {
        String title = getArg(args);
        if (title == null) {
            System.err.println("Event title expected");
            return;
        }
        String sdt = getArg(args);
        if (sdt == null) {
            System.err.println("Event date expected");
            return;
        }

        Date dt;
        try {
            dt = Date.valueOf(sdt);
        } catch (IllegalArgumentException e) {
            System.err.println("Wrong date format");
            return;
        }

        Config cfg = Config.get();

        Event ev = new Event();
        ev.setDate(dt);
        ev.setTitle(title);
        ev.setId(cfg.nextId());

        cfg.getEvents().add(ev);
        cfg.setActiveEvent(ev.getId());
    }

    private static void dropEvent(List<String> args) {
        String dis = getArg(args);
        if (dis == null) {
            System.err.println("Event id expected");
            return;
        }

        Integer id;
        try {
            id = Integer.valueOf(dis);
        } catch (NumberFormatException e) {
            System.err.println("Wrong id format");
            return;
        }

        Config cfg = Config.get();
        Event ev = cfg.eventById(id);
        if (ev != null) {
            cfg.getEvents().remove(ev);
            cfg.setActiveEvent(null);
        } else {
            System.err.println("Event not found");
        }
    }

    private static void selectEvent(List<String> args) {
        String dis = getArg(args);
        if (dis == null) {
            System.err.println("Event id expected");
            return;
        }

        Integer id;
        try {
            id = Integer.valueOf(dis);
        } catch (NumberFormatException e) {
            System.err.println("Wrong id format");
            return;
        }

        Config cfg = Config.get();

        Event ev = cfg.eventById(id);
        if (ev != null)
            cfg.setActiveEvent(id);
        else
            System.err.println("Event not found");

    }

    private static void eventInfo(List<String> args) {
        Config cfg = Config.get();
        Integer id;
        String dis = getArg(args);
        if (dis == null) {
            if (cfg.getActiveEvent() != null) {
                id = cfg.getActiveEvent();
            } else {
                System.err.println("Event id expected");
                return;
            }

        } else {
            try {
                id = Integer.valueOf(dis);
            } catch (NumberFormatException e) {
                System.err.println("Wrong id format");
                return;
            }
        }

        Event ev = cfg.eventById(id);
        if (ev != null)
            ev.printInfo();
        else
            System.err.println("Event not found");
    }

    private static void addParty(List<String> args) {
        String name = getArg(args);
        if (name == null) {
            System.err.println("Party name expected");
            return;
        }
        Config cfg = Config.get();
        Event ev = cfg.eventById(cfg.getActiveEvent());

        if (ev == null) {
            System.err.println("No event selected");
            return;
        }

        Participant p = new Participant();
        p.setName(name);
        for (String ex : ev.getExpenses())
            p.getUsedExpences().add(ex);

        ev.getParticipants().add(p);
    }

    private static void dropParty(List<String> args) {
        String name = getArg(args);
        if (name == null) {
            System.err.println("Party name expected");
            return;
        }
        Config cfg = Config.get();
        Event ev = cfg.eventById(cfg.getActiveEvent());

        if (ev == null) {
            System.err.println("No event selected");
            return;
        }

        for (Participant p : ev.getParticipants())
            if (p.getName().equals(name)) {
                ev.getParticipants().remove(p);
                return;
            }

        System.out.println("Party not found");
    }

    private static void partyInfo(List<String> args) {
        String name = getArg(args);
        if (name == null) {
            System.err.println("Party name expected");
            return;
        }
        Config cfg = Config.get();
        Event ev = cfg.eventById(cfg.getActiveEvent());

        if (ev == null) {
            System.err.println("No event selected");
            return;
        }

        for (Participant p : ev.getParticipants())
            if (p.getName().equals(name)) {
                p.printInfo();
                return;
            }

        System.out.println("Party not found");
    }

    private static void addExpense(List<String> args) {
        String name = getArg(args);
        if (name == null) {
            System.err.println("Expense name expected");
            return;
        }
        Config cfg = Config.get();
        Event ev = cfg.eventById(cfg.getActiveEvent());

        if (ev == null) {
            System.err.println("No event selected");
            return;
        }

        if (ev.getExpenses().contains(name)) {
            System.err.println("Aleady exists");
            return;
        }

        ev.getExpenses().add(name);
        for (Participant p : ev.getParticipants())
            p.getUsedExpences().add(name);
    }

    private static void dropExpense(List<String> args) {
        String name = getArg(args);
        if (name == null) {
            System.err.println("Expense name expected");
            return;
        }
        Config cfg = Config.get();
        Event ev = cfg.eventById(cfg.getActiveEvent());

        if (ev == null) {
            System.err.println("No event selected");
            return;
        }

        if (!ev.getExpenses().contains(name)) {
            System.err.println("Expense not found");
            return;
        }

        ev.getExpenses().remove(name);
    }

    private static void addEntry(List<String> args) {
        String from = getArg(args);
        String to = getArg(args);
        String samt = getArg(args);

        if (from == null) {
            System.err.println("Entry source expected");
            return;
        }

        if (to == null) {
            System.err.println("Entry destination expected");
            return;
        }

        if (samt == null) {
            System.err.println("Entry amount expected");
            return;
        }

        Config cfg = Config.get();
        Event ev = cfg.eventById(cfg.getActiveEvent());

        if (ev == null) {
            System.err.println("No event selected");
            return;
        }

        if (!ev.checkEntryPart(from)) {
            System.err.println("Wrong entry source: " + from);
            return;
        }

        if (!ev.checkEntryPart(to)) {
            System.err.println("Wrong entry destination: " + to);
            return;
        }

        Float amt;

        try {
            amt = Float.valueOf(samt);
        } catch (NumberFormatException e) {
            System.err.println("Wrong entry amount: " + samt);
            return;
        }

        Entry entry = new Entry();
        entry.setFrom(from);
        entry.setTo(to);
        entry.setAmount(amt);

        ev.getEntries().add(entry);
    }

    private static void dropEntry(List<String> args) {
        String from = getArg(args);
        String to = getArg(args);
        String samt = getArg(args);

        if (from == null) {
            System.err.println("Entry source expected");
            return;
        }

        if (to == null) {
            System.err.println("Entry destination expected");
            return;
        }

        if (samt == null) {
            System.err.println("Entry amount expected");
            return;
        }

        Config cfg = Config.get();
        Event ev = cfg.eventById(cfg.getActiveEvent());

        if (ev == null) {
            System.err.println("No event selected");
            return;
        }

        Float amt;

        try {
            amt = Float.valueOf(samt);
        } catch (NumberFormatException e) {
            System.err.println("Wrong entry amount: " + samt);
            return;
        }

        for (Entry e : ev.getEntries()) {
            if (e.getFrom().equals(from) && e.getTo().equals(to) && e.getAmount().equals(amt)) {
                ev.getEntries().remove(e);
                return;
            }

        }

        System.err.println("Entry not found");
    }

    private static void listEntries() {
        Config cfg = Config.get();
        Event ev = cfg.eventById(cfg.getActiveEvent());

        if (ev == null) {
            System.err.println("No event selected");
            return;
        }

        for (Entry e : ev.getEntries())
            System.out.println( e.getFrom() + " ==== [" + e.getAmount() + "] ===> " + e.getTo());
    }

    private static void partyUse(List<String> args) {
        String name = getArg(args);
        if (name == null) {
            System.err.println("Party name expected");
            return;
        }
        String exp = getArg(args);
        if (exp == null) {
            System.err.println("Expense name expected");
            return;
        }

        Config cfg = Config.get();
        Event ev = cfg.eventById(cfg.getActiveEvent());

        if (ev == null) {
            System.err.println("No event selected");
            return;
        }

        if (!ev.getExpenses().contains(exp)) {
            System.err.println("Expense not found");
            return;
        }

        for (Participant p : ev.getParticipants())
            if (p.getName().equals(name)) {
                List<String> exps = p.getUsedExpences();
                if (!exps.contains(exp))
                    exps.add(exp);
                return;
            }

        System.out.println("Party not found");

    }

    private static void partyUnuse(List<String> args) {
        String name = getArg(args);
        if (name == null) {
            System.err.println("Party name expected");
            return;
        }
        String exp = getArg(args);
        if (exp == null) {
            System.err.println("Expense name expected");
            return;
        }

        Config cfg = Config.get();
        Event ev = cfg.eventById(cfg.getActiveEvent());

        if (ev == null) {
            System.err.println("No event selected");
            return;
        }

        for (Participant p : ev.getParticipants())
            if (p.getName().equals(name)) {
                List<String> exps = p.getUsedExpences();
                if (exps.contains(exp))
                    exps.remove(exp);
                return;
            }

        System.out.println("Party not found");
    }

    private static void partyMerge(List<String> args) {
        String name = getArg(args);
        if (name == null) {
            System.err.println("Party name expected");
            return;
        }
        String mName = getArg(args);
        if (mName == null) {
            System.err.println("Merge name expected");
            return;
        }

        Config cfg = Config.get();
        Event ev = cfg.eventById(cfg.getActiveEvent());
        if (ev == null) {
            System.err.println("No event selected");
            return;
        }

        for (Participant p : ev.getParticipants())
            if (p.getName().equals(mName)) {
                System.err.println("Already have party named " + mName);
                return;
            }

        for (Participant p : ev.getParticipants())
            if (p.getName().equals(name)) {
                if (ev.partyMerged(name)) {
                    System.err.println("Party already merged");
                    return;
                }
                PartyMerge m = new PartyMerge();
                m.setParty(name);
                m.setMeta(mName);

                ev.getMerges().add(m);

                return;
            }

        System.out.println("Party not found");

    }

    private static void partyUnmerge(List<String> args) {
        String name = getArg(args);
        if (name == null) {
            System.err.println("Party name expected");
            return;
        }
        String mName = getArg(args);
        if (mName == null) {
            System.err.println("Merge name expected");
            return;
        }

        Config cfg = Config.get();
        Event ev = cfg.eventById(cfg.getActiveEvent());
        if (ev == null) {
            System.err.println("No event selected");
            return;
        }


        if (!ev.mergeExists(name, mName)) {
            System.err.println("Party+merge combination does not exist");
            return;
        }

        PartyMerge merge = null;
        for (PartyMerge m : ev.getMerges()) {
            if (m.getMeta().equals(mName) && m.getParty().equals(name)) {
                merge = m;
                break;
            }
        }
        if (merge != null)
            ev.getMerges().remove(merge);

        return;
    }

    private  static void calc() {
        Config cfg = Config.get();
        Event ev = cfg.eventById(cfg.getActiveEvent());

        if (ev == null) {
            System.err.println("No event selected");
            return;
        }

        ev.printCalculation();
    }

    private static void help() {
        System.out.println("" +
                "Usage examples:\n" +
                "\n" +
                "evcalc event list\n" +
                "evcalc event add <title> <date>\n" +
                "evcalc event drop <id>\n" +
                "evcalc event info\n" +
                "evcalc event select <id>\n" +
                "evcalc event calc\n" +
                "\n" +
                "evcalc party add <name>\n" +
                "evcalc party drop <name>\n" +
                "evcalc party use <name> <expense>\n" +
                "evcalc party unuse <name> <expense>\n" +
                "evcalc party info <name>\n" +
                "evcalc party merge <name> <merge_name>\n" +
                "evcalc party unmerge <name> <merge_name>\n" +
                "\n" +
                "evcalc expense add <title>\n" +
                "evcalc expense drop <title>\n" +
                "\n" +
                "evcalc entry add <from> <to> <amount>\n" +
                "evcalc entry drop <from> <to> <amount>\n" +
                "evcalc entry list\n" +
                "\n" +
                "'From' and 'To' values should be started with '@' (for party) or '%' (for expense) prefix.\n" +
                "Also '%cashier' entry part may be used\n");
    }
}
