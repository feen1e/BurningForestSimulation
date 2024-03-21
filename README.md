# Burning Forest Simulation  
When using the constructor you must provide two values: size and forestation.

Size `N` must be integer greater than `0`. Your forest will be a chessboard-like map of size `N x N`.  
Forestation must be a double between `0` and `1`. It's the chance of a tree being on a tile.

    BurningForestSimulation name = new BurningForestSimulation(size, forestation);  

---

### **Use `.makeSimulation()` method to begin simulation.**

---

If you do not wish to see the map while the simulation is running use `.printMap = false;` before starting the simulation.

After the simulation is complete you will receive statistics, for example:
    
        All trees: 132.
        Surviving trees: 58.
        Burnt trees: 74.
        Percent of trees burnt: 56,06%.

You can use `.getTreeRatio()` to generate an `int[]` containing the first three values above if needed.

You do not need to use `.mapInitialization()` or `.fireInitialization()` methods, `.makeSimulation()` method calls them for you.

___

`.simulationResults()` method generates or updates a file named `results.txt` with the results of `200` simulations (set of `10` for `20` different forestation values differing by `0.05`) in the following format:

    Forestation Value; Iteration; All Trees; Alive Trees; Burnt Trees; % of Burnt;

Due to the way floating-point numbers are represented precision issues can occur and forestation values may differ by small amounts, like `0.15000000000000002` instead of `0.15`.