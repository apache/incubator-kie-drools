Scenario Simulation Model
=========================

**ScenarioSimulationModel** is the class representing the _data-model_ used inside ScenarioSimulation editor.

It consists of four parts:

1. **Simulation**
2. **Background**
3. **Settings**
4. **Imports**

Simulation
----------
This object contains the structure (as **SimulationDesciptor**) and the data (list of **Scenario**) 
to actually run simulations

Background
----------
This object contains the structure (as **SimulationDesciptor**) and the data (list of **BackgroundData**) 
to use for create multiple objects to be used inside **Simulation**

Settings
--------
This object contains the _ScenarioSimulation-specific_ configurations needed 
to actually run simulations

Imports
-------
Data object models imported.

Class diagram
-------------
![](scenariosimulationmodel.png)

[PUML diagram](scenariosimulationmodel.puml)



