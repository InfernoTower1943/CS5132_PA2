package main;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class ModulePriorityQueue<T, S extends Comparable<S>> {
    public Map<Pair<String, Long>, PriorityQueue<T, S>> modulePQMap;
    public Map<Pair<String, Long>, String> timeSlotDescriptionMap;
    public Map<String, ArrayList<Long>> moduleTimeSlotsMap;

    public ModulePriorityQueue() {
        modulePQMap = new HashMap<>();
        timeSlotDescriptionMap = new HashMap<>();
        moduleTimeSlotsMap = new HashMap<>();
    }

    public ArrayList<Long> getTimeSlotIDs(String moduleCode){
        return moduleTimeSlotsMap.get(moduleCode);
    }

    public ArrayList<PriorityQueue<T, S>> getModulePQ(String moduleCode){
        ArrayList<PriorityQueue<T, S>> result = new ArrayList<>();
        for (Pair<String, Long> p : modulePQMap.keySet()){
            if (p.getKey().equals(moduleCode)){
                result.add(modulePQMap.get(p));
            }
        }
        return result;
    }

    public PriorityQueue<T, S> getTimeSlotPQ(String moduleCode, Long timeSlotID){
        Pair<String, Long> p = new Pair<>(moduleCode, timeSlotID);
        if (modulePQMap.containsKey(p)){
            return modulePQMap.get(p);
        }
        return null;
    }

    public boolean PQIsEmpty(PriorityQueue<T,S> priorityQueue){
        return priorityQueue.isEmpty();
    }

    public int PQLength(PriorityQueue<T,S> priorityQueue){
        return priorityQueue.tree.count;
    }

    public void enqueueToTimeSlot(String moduleCode, Long timeSlotID, T item, S priority){
        Pair<String, Long> p = new Pair<>(moduleCode, timeSlotID);
        if (modulePQMap.containsKey(p)){
            modulePQMap.get(p).enqueue(item, priority);
        }else{
            throw new NoSuchElementException("No time slot with module code "+moduleCode+" and ID "+timeSlotID+"exists");
        }
    }

    public Pair<T, S> dequeueFromTimeSlot(String moduleCode, Long timeSlotID){
        Pair<String, Long> p = new Pair<>(moduleCode, timeSlotID);
        if (modulePQMap.containsKey(p)){
            PriorityNode<T, S> top = modulePQMap.get(p).tree.top();
            modulePQMap.get(p).dequeue();
            Pair<T,S> pair=new Pair<>(top.item,top.priority);
            return pair;
        }
        return null;
    }

    void addTimeSlot(String moduleCode, Long timeSlotID){
        addTimeSlot(moduleCode, timeSlotID, "No description");
    }

    public void addTimeSlot(String moduleCode, Long timeSlotID, String timeSlotDescription){
        Pair<String, Long> p = new Pair<>(moduleCode, timeSlotID);
        if (!modulePQMap.containsKey(p)){
            modulePQMap.put(p, new PriorityQueue<T, S>());
            timeSlotDescriptionMap.put(p, timeSlotDescription);
            if (moduleTimeSlotsMap.containsKey(moduleCode)){
                moduleTimeSlotsMap.get(moduleCode).add(timeSlotID);
            }else{
                moduleTimeSlotsMap.put(moduleCode, new ArrayList<>());
                moduleTimeSlotsMap.get(moduleCode).add(timeSlotID);
            }
        }
    }

    public String getTimeSlot(String moduleCode, Long timeSlotID){
        Pair<String, Long> p = new Pair<>(moduleCode, timeSlotID);
        if (modulePQMap.containsKey(p)){
            return timeSlotDescriptionMap.get(p);
        }else{
            return null;
        }
    }

    public Pair<String, Long> getTimeSlotIDFromStr(String timeSlotDescription){
        for (Map.Entry<Pair<String, Long>, String> entry : timeSlotDescriptionMap.entrySet()) {
            if (entry.getValue().equals(timeSlotDescription)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public PriorityQueue<T, S> deleteTimeSlot(String moduleCode, Long timeSlotID){
        Pair<String, Long> p = new Pair<>(moduleCode, timeSlotID);
        if (modulePQMap.containsKey(p)){
            PriorityQueue<T, S> pq = modulePQMap.get(p);
            modulePQMap.remove(p);
            timeSlotDescriptionMap.remove(p);
            moduleTimeSlotsMap.get(moduleCode).remove(timeSlotID);
            return pq;
        }else{
            throw new NoSuchElementException("No time slot with module code "+moduleCode+" and ID "+timeSlotID+"exists");
        }
    }

    public long getPriority(boolean isRequired, int preference, long time){
        return (isRequired?1:0)*(long)Math.pow(10,12) + preference*(long)Math.pow(10,10)+time;
    }

}
