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

    ArrayList<Integer> getTimeSlotIDs(String moduleCode){
        return moduleTimeSlotsMap.get(moduleCode);
    }

    ArrayList<PriorityQueue<T, S>> getModulePQ(String moduleCode){
        ArrayList<PriorityQueue<T, S>> result = new ArrayList<>();
        for (Pair<String, Integer> p : modulePQMap.keySet()){
            if (p.getKey().equals(moduleCode)){
                result.add(modulePQMap.get(p));
            }
        }
        return result;
    }

    PriorityQueue<T, S> getTimeSlotPQ(String moduleCode, Integer timeSlotID){
        Pair<String, Integer> p = new Pair<>(moduleCode, timeSlotID);
        if (modulePQMap.containsKey(p)){
            return modulePQMap.get(p);
        }
        return null;
    }

    void enqueueToTimeSlot(String moduleCode, Integer timeSlotID, T item, S priority){
        Pair<String, Integer> p = new Pair<>(moduleCode, timeSlotID);
        if (modulePQMap.containsKey(p)){
            modulePQMap.get(p).enqueue(item, priority);
        }else{
            throw new NoSuchElementException("No time slot with module code "+moduleCode+" and ID "+timeSlotID+"exists");
        }
    }

    PriorityNode<T, S> dequeueFromTimeSlot(String moduleCode, Integer timeSlotID){
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
        }
    }

    PriorityQueue<T, S> deleteTimeSlot(String moduleCode, Integer timeSlotID){
        Pair<String, Integer> p = new Pair<>(moduleCode, timeSlotID);
        if (modulePQMap.containsKey(p)){
            PriorityQueue<T, S> pq = modulePQMap.get(p);
            modulePQMap.remove(p);
            timeSlotDescriptionMap.remove(p);
            return pq;
        }else{
            throw new NoSuchElementException("No time slot with module code "+moduleCode+" and ID "+timeSlotID+"exists");
        }
    }
}
