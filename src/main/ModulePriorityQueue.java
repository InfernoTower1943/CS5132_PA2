package main;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class ModulePriorityQueue<T, S extends Comparable<S>> {
    Map<Pair<String, Integer>, PriorityQueue<T, S>> modulePQMap;
    Map<Pair<String, Integer>, String> timeSlotDescriptionMap;
    Map<String, ArrayList<Integer>> moduleTimeSlotsMap;

    public ModulePriorityQueue() {
        modulePQMap = new HashMap<>();
        timeSlotDescriptionMap = new HashMap<>();
        moduleTimeSlotsMap = new HashMap<>();
    }

    public ArrayList<Integer> getTimeSlotIDs(String moduleCode){
        return moduleTimeSlotsMap.get(moduleCode);
    }

    public ArrayList<PriorityQueue<T, S>> getModulePQ(String moduleCode){
        ArrayList<PriorityQueue<T, S>> result = new ArrayList<>();
        for (Pair<String, Integer> p : modulePQMap.keySet()){
            if (p.getKey().equals(moduleCode)){
                result.add(modulePQMap.get(p));
            }
        }
        return result;
    }

    public PriorityQueue<T, S> getTimeSlotPQ(String moduleCode, Integer timeSlotID){
        Pair<String, Integer> p = new Pair<>(moduleCode, timeSlotID);
        if (modulePQMap.containsKey(p)){
            return modulePQMap.get(p);
        }
        return null;
    }

    public void enqueueToTimeSlot(String moduleCode, Integer timeSlotID, T item, S priority){
        Pair<String, Integer> p = new Pair<>(moduleCode, timeSlotID);
        if (modulePQMap.containsKey(p)){
            modulePQMap.get(p).enqueue(item, priority);
        }else{
            throw new NoSuchElementException("No time slot with module code "+moduleCode+" and ID "+timeSlotID+"exists");
        }
    }

    public PriorityNode<T, S> dequeueFromTimeSlot(String moduleCode, Integer timeSlotID){
        Pair<String, Integer> p = new Pair<>(moduleCode, timeSlotID);
        if (modulePQMap.containsKey(p)){
            PriorityNode<T, S> top = modulePQMap.get(p).tree.top();
            modulePQMap.get(p).dequeue();
            return top;
        }
        return null;
    }

    void addTimeSlot(String moduleCode, Integer timeSlotID){
        addTimeSlot(moduleCode, timeSlotID, "No description");
    }

    public void addTimeSlot(String moduleCode, Integer timeSlotID, String timeSlotDescription){
        Pair<String, Integer> p = new Pair<>(moduleCode, timeSlotID);
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

    public String getTimeSlot(String moduleCode, Integer timeSlotID){
        Pair<String, Integer> p = new Pair<>(moduleCode, timeSlotID);
        if (modulePQMap.containsKey(p)){
            return timeSlotDescriptionMap.get(p);
        }else{
            return null;
        }
    }

    public Pair<String, Integer> getTimeSlotIDFromStr(String timeSlotDescription){
        for (Map.Entry<Pair<String, Integer>, String> entry : timeSlotDescriptionMap.entrySet()) {
            if (entry.getValue().equals(timeSlotDescription)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public PriorityQueue<T, S> deleteTimeSlot(String moduleCode, Integer timeSlotID){
        Pair<String, Integer> p = new Pair<>(moduleCode, timeSlotID);
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
}
