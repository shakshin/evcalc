package com.shakshin.evcalc;

import java.util.*;

public class Event {
    private Date date;
    private List<Participant> participants = new ArrayList<Participant>();
    private List<String> expenses = new ArrayList<String>();
    private List<Entry> entries = new ArrayList<Entry>();
    private Integer id;
    private List<PartyMerge> merges = new ArrayList<PartyMerge>();

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Participant> getParticipants() {
        if (participants == null)
            participants = new ArrayList<Participant>();
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public List<String> getExpenses() {
        if (expenses == null)
            expenses = new ArrayList<String>();
        return expenses;
    }

    public void setExpenses(List<String> expenses) {
        this.expenses = expenses;
    }

    public List<Entry> getEntries() {
        if (entries == null)
            entries = new ArrayList<Entry>();
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<PartyMerge> getMerges() {
        return merges;
    }

    public void setMerges(List<PartyMerge> merges) {
        this.merges = merges;
    }

    public void printInfo() {
        System.out.println("Event id: " + id);
        System.out.println("Event title: " + title);
        System.out.println("Event date: " + date);
        System.out.println("----");
        System.out.println("Participants (" + getParticipants().size() + "): ");

        for (Participant p : getParticipants()) {
            System.out.println("    " + p.getName());
        }

        System.out.println("----");
        System.out.println("Expenses: ");

        for (String ex : getExpenses()) {
            System.out.println("    " + ex);
        }
   }

    public boolean checkEntryPart(String part) {
        if (part.length() < 2)
            return false;

        String type = part.substring(0, 1);
        String id = part.substring(1, part.length());

        switch (type) {
            case "@":
                for (Participant p : getParticipants())
                    if (p.getName().equals(id))
                        return true;

                for (PartyMerge m : getMerges())
                    if (m.getMeta().equals(id))
                        return true;

                return false;

            case "%":
                return part.equals("%cashier") || getExpenses().contains(id);

            default:
                return false;
        }
   }

    public void printCalculation() {
        HashMap<String, Integer> expUsers = new HashMap<String, Integer>();
        for (Participant p : getParticipants())
            for (String exp : getExpenses())
                if (p.getUsedExpences().contains(exp)) {
                    Integer val = expUsers.get(exp);
                    if (val == null) val = 0;
                    val++;
                    expUsers.put(exp, val);
                }

        HashMap<String, Float> values = new HashMap<String, Float>();
        for (Entry e : getEntries()) {
            Float fromVal = values.get(e.getFrom());
            Float toVal = values.get(e.getTo());
            if (fromVal == null) fromVal = new Float(0);
            if (toVal == null) toVal = new Float(0);
            fromVal -= e.getAmount();
            toVal += e.getAmount();
            values.put(e.getFrom(), fromVal);
            values.put(e.getTo(), toVal);

        }

        HashMap<String, Float> perPerson = new HashMap<String, Float>();
        for (String exp : expUsers.keySet()) {
            Float totalExp = values.get("%" + exp);
            if (totalExp == null) totalExp = new Float(0);
            Float perP = totalExp / expUsers.get(exp);
            perPerson.put(exp, perP);
        }

        for (Participant p : getParticipants()) {
            Float val = values.get("@" + p.getName());
            if (val == null) val = new Float(0);

            for (String exp : p.getUsedExpences()) {
                if (perPerson.keySet().contains(exp)) {
                    val += perPerson.get(exp);
                }
            }
            values.put("@" + p.getName(), val);
        }

        class MergedRecord {
            public String name;
            public Map<String, Float> subrecs = new HashMap<String, Float>();
            public Float debt = new Float(0);
        }

        List<MergedRecord> mData = new ArrayList<MergedRecord>();
        HashMap<String, MergedRecord> mMap = new HashMap<String, MergedRecord>();
        for (PartyMerge merge : getMerges()) {
            MergedRecord r = mMap.get(merge.getMeta());
            if (r == null)
                r = new MergedRecord();

            r.name = merge.getMeta();
            r.debt = values.get("@"+r.name);
            if (r.debt == null)
                r.debt = new Float(0);
            for (String party : getPartiesByMerge(r.name)) {
                r.subrecs.put(party, values.get("@"+party));
                if (values.get("@"+party) != null)
                    r.debt += values.get("@"+party);
            }
            if (mMap.get(merge.getMeta()) == null) {
                mData.add(r);
                mMap.put(merge.getMeta(), r);
            }
        }



        for (Participant party : getParticipants()) {
            if (partyMerged(party.getName())) {
                continue;
            }
            MergedRecord r = new MergedRecord();
            r.name = party.getName();
            r.debt = values.get("@"+party.getName());
            mData.add(r);
        }



        System.out.println("Expenses (total / per person): ");
        for (String key : values.keySet()) {
            if (key.substring(0, 1).equals("%") && !key.equals("%cashier"))
                System.out.println(String.format("%s: %.2f / %.2f", key,  values.get(key), perPerson.get(key.substring(1, key.length()))));
        }

        System.out.println("\nDebts:");
        for (MergedRecord r : mData) {
            System.out.print(String.format("%s: %.2f", r.name,  r.debt));
            if (r.subrecs.isEmpty()) {
                System.out.println();
                continue;
            }
            System.out.println(String.format(" (%d participants)", r.subrecs.size()));

        }

        System.out.println("\nCashier:");
        for (String key : values.keySet()) {
            if (key.equals("%cashier"))
                System.out.println(String.format("%s: %.2f", key,  values.get(key)));
        }

        for (String k : values.keySet())
            switch (k.substring(0,1)) {
                case "@":
                    boolean f = false;
                    for (Participant p : getParticipants()) {
                        if (p.getName().equals(k.substring(1, k.length()))) {
                            f = true;
                            break;
                        }
                    }
                    if (!f)
                        for (PartyMerge merge : getMerges()) {
                            if (merge.getMeta().equals(k.substring(1, k.length()))) {
                                f = true;
                                break;
                            }
                        }
                    if (!f) System.out.println("WARNING: Unknown party in entries: " + k);
                    break;
                case "%":
                    if (!getExpenses().contains(k.substring(1, k.length())) && !k.equals("%cashier"))
                        System.out.println("WARNING: Unknown expense in entries: " + k);
                    break;
                default:
                    System.out.println("WARNING: unknown entry part: " + k);
            }
   }

   @Override
   public String toString() {
        return String.format("%s (%s)", getTitle(), getDate());
   }

   public boolean mergeExists(String party, String merge) {
        for (PartyMerge m : getMerges()) {
            if (m.getParty().equals(party) && m.getMeta().equals(merge)) {
                return true;
            }
        }
        return false;
   }

   public PartyMerge getMergeByParty(String party) {
       for (PartyMerge m : getMerges()) {
           if (m.getParty().equals(party)) {
               return m;
           }
       }
       return null;
   }

   public List<String> getPartiesByMerge(String merge) {
       ArrayList<String> res = new ArrayList<String>();
       for (PartyMerge m : getMerges()) {
           if (m.getMeta().equals(merge)) {
               res.add(m.getParty());
           }
       }
       return res;
   }

   public boolean partyMerged(String party) {
       for (PartyMerge m : getMerges()) {
           if (m.getParty().equals(party)) {
               return true;
           }
       }
       return false;
   }
}
