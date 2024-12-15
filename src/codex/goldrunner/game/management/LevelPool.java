/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.goldrunner.game.management;

import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;

/**
 * A more dynamic version of LevelPackage.
 *
 * @author gary
 */
public class LevelPool {

    LinkedList<LevelData> pool = new LinkedList<>(),
            played = new LinkedList<>();

    public LevelPool() {
    }

    public void add(LevelPackage pack) {
        pool.addAll(pack.getLevels());
    }

    public void add(LevelData data) {
        pool.add(data);
    }

    public void remove(LevelPackage pack) {
        for (LevelData data : pack.getLevels()) {
            pool.remove(data);
        }
    }

    public void remove(LevelData data) {
        pool.remove(data);
    }

    /**
     * Randomly mixes all levels in the pool.
     */
    public void randomize() {
        LinkedList<LevelData> random = new LinkedList<>();
        while (!pool.isEmpty()) {
            LevelData data = pool.get(ThreadLocalRandom.current()
                    .nextInt(pool.size()));
            random.add(data);
            pool.remove(data);
        }
        pool.addAll(random);
    }

    /**
     * Sorts the level pool based on the given sorting function. The sort
     * BiFunction should return true if the first argument is "greater than" the
     * second argument, thus placing the second argument just below the first in
     * the partially-sorted list.
     *
     * @param sort
     */
    public void sort(BiFunction<LevelData, LevelData, Boolean> sort) {
        LinkedList<LevelData> list = new LinkedList<>();
        for (LevelData d1 : pool) {
            if (list.isEmpty()) {
                list.add(d1);
                continue;
            }
            int index = 0;
            for (LevelData d2 : list) {
                if (sort.apply(d1, d2)) {
                    break;
                }
                index++;
            }
            list.add(index, d1);
        }
        pool.clear();
        pool.addAll(list);
    }

    /**
     * Advance through the pool by the asserted amount. All visited levels are
     * removed from the pool.
     *
     * @param amount the number of advances through the pool. An amount of 0
     * will get and remove the very next level.
     * @return The final level visited.
     */
    public LevelData cycle(int amount) {
        assert amount >= 0;
        if (pool.isEmpty()) {
            return null;
        }
        LevelData data = pool.removeFirst();
        if (amount == 0) {
            played.addLast(data);
            return data;
        } else {
            played.addLast(data);
            return cycle(amount - 1);
        }
    }

    public LevelData current() {
        return pool.getFirst();
    }

    public LevelData getAtIndex(int index) {
        return pool.get(index);
    }

    public boolean hasNextLevel() {
        return !pool.isEmpty();
    }

    public int getTotalLevels() {
        return pool.size() + played.size();
    }

    /**
     * Moves all "played" levels back into the main level pool.
     */
    public void refresh() {
        pool.addAll(0, played);
    }

}
