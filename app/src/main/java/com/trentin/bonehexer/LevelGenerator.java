package com.trentin.bonehexer;

import java.util.Random;

public class LevelGenerator {
    //credit to https://gamedevelopment.tutsplus.com/tutorials/generate-random-cave-levels-using-cellular-automata--gamedev-9664
    private int levelWidth, levelHeight, numberOfSteps;
    private float chanceToStartAlive;
    private int deathLimit, birthLimit;

    public LevelGenerator(int level_width, int level_height) {
        levelWidth = level_width;
        levelHeight = level_height;
        numberOfSteps = Utils.randInt(2, 8);
        //numberOfSteps = 2;
        chanceToStartAlive = Utils.randFloat(0.40f, 0.45f);
        //chanceToStartAlive = 0.4f;
        deathLimit = 3;
        //deathLimit = 6;
        birthLimit = Utils.randInt(7, 8);
        //birthLimit = 5;
    }

    public boolean[][] generateMap() {
        boolean[][] cellmap = new boolean[levelWidth][levelHeight];
        cellmap = initialiseMap(cellmap);

        for(int i = 0; i < numberOfSteps; i++){
            cellmap = doSimulationStep(cellmap);
        }
        return cellmap;
    }

    public boolean[][] initialiseMap(boolean[][] map) {
        Random rand = new Random();
        for(int x=0; x < levelWidth; x++) {
            for(int y=0; y< levelHeight; y++) {
                if(rand.nextFloat() < chanceToStartAlive) {
                    map[x][y] = true;
                }
            }
        }
        return map;
    }

    public int countAliveNeighbours(boolean[][] map, int x, int y) {
        int count = 0;
        for(int i=-1; i<2; i++) {
            for(int j=-1; j<2; j++) {
                int neighbour_x = x+i;
                int neighbour_y = y+j;
                if(i == 0 && j == 0) {
                }
                else if(neighbour_x < 0 || neighbour_y < 0 || neighbour_x >= map.length || neighbour_y >= map[0].length) {
                    count++;
                }
                else if(map[neighbour_x][neighbour_y]){
                    count++;
                }
            }
        }
        return count;
    }

    public boolean[][] doSimulationStep(boolean[][] oldMap) {
        boolean[][] newMap = new boolean[levelWidth][levelHeight];
        for(int x = 0; x < oldMap.length; x++) {
            for(int y = 0; y <oldMap[0].length; y++) {
                int nbs = countAliveNeighbours(oldMap, x, y);
                if(oldMap[x][y]){
                    if(nbs < deathLimit) {
                        newMap[x][y] = false;
                    }
                    else{
                        newMap[x][y] = true;
                    }
                }
                else{
                    if(nbs > birthLimit) {
                        newMap[x][y] = true;
                    }
                    else{
                        newMap[x][y] = false;
                    }
                }
            }
        }
        return newMap;
    }
}

