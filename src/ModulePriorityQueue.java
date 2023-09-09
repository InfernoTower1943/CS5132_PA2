import java.util.Map;
import java.util.NoSuchElementException;

public class ModulePriorityQueue<T, S extends Comparable<S>> {
    Map<String, PriorityQueue<T, S>> modulePQMap;

    PriorityQueue<T, S> getPQ(String moduleCode){
        if (modulePQMap.containsKey(moduleCode)){
            return modulePQMap.get(moduleCode);
        }
        return null;
    }

    void enqueueToModule(String moduleCode, T item, S priority){
        if (modulePQMap.containsKey(moduleCode)){
            modulePQMap.get(moduleCode).enqueue(item, priority);
        }else{
            throw new NoSuchElementException("No module with code "+moduleCode+" exists");
        }
    }

    PriorityNode<T, S> dequeueFromModule(String moduleCode){
        if (modulePQMap.containsKey(moduleCode)){
            PriorityNode<T, S> top = modulePQMap.get(moduleCode).tree.top();
            modulePQMap.get(moduleCode).dequeue();
            return top;
        }
        return null;
    }

    void addModule(String moduleCode){
        if (!modulePQMap.containsKey(moduleCode)){
            modulePQMap.put(moduleCode, new PriorityQueue<T, S>());
        }
    }

    PriorityQueue<T, S> deleteModule(String moduleCode){
        if (modulePQMap.containsKey(moduleCode)){
            PriorityQueue<T, S> pq = modulePQMap.get(moduleCode);
            modulePQMap.remove(moduleCode);
            return pq;
        }else{
            throw new NoSuchElementException("No module with code "+moduleCode+" exists");
        }
    }
}
