# Contributing
All contributions are greatly appreciated.

## Contributing to the Mod
<b>1.</b> To contribute to the mod, start by [forking](https://github.com/KingsMMA/FruitfulUtilities/fork) this repo.  
<b>2.</b> Make your contributions and test for bugs.  
<b>3.</b> Create a pull request and detail your changes.  
<b>4.</b> Make any changes requested by reviewers.

## Contributing to the Upgrade Data
<b>1.</b> To contribute to the upgrade data, start by [forking](https://github.com/KingsMMA/FruitfulData/fork) the [FruitfulData](https://github.com/KingsMMA/FruitfulData) repo.  
<b>2.</b> Make your contributions.  
<b>3.</b> Create a pull request and detail your changes.  
<b>4.</b> Make any changes requested by reviewers.  
<b>5.</b> Upon your pull request being merged, you may choose to implement the data as follows.  This is only required for new upgrades or paths - changes to existing upgrades will automatically be updated.  
<b>6.</b> Follow the steps in the [previous section](https://github.com/KingsMMA/FruitfulUtilities/blob/master/CONTRIBUTING.md#contributing-to-the-mod) to begin contributing the changes to the mod.  

### If you added a new path
<b>1.</b> Load the path in [`PathManager#loadPaths()`](https://github.com/KingsMMA/FruitfulUtilities/blob/master/src/main/java/dev/kingrabbit/fruitfulutilities/pathviewer/PathManager.java#L33).  
<b>2.</b> Save its parent paths in [`PathManager#`](https://github.com/KingsMMA/FruitfulUtilities/blob/master/src/main/java/dev/kingrabbit/fruitfulutilities/pathviewer/PathManager.java#L24).  
<b>3.</b> Create the icon in [`PathScreen#init()`](https://github.com/KingsMMA/FruitfulUtilities/blob/master/src/main/java/dev/kingrabbit/fruitfulutilities/pathviewer/PathScreen.java#L59).  
<b>4.</b> Add a branch to the if statement beginning [here](https://github.com/KingsMMA/FruitfulUtilities/blob/master/src/main/java/dev/kingrabbit/fruitfulutilities/pathviewer/PathScreen.java#L94) in `PathScreen#init()`.  
```java
else if (section.equals("<path_id>")) {
    JsonObject <path_id> = PathManager.paths.get("<path_id>");
}
```
<b>5.</b> Next, add the upgrades as described below:

### If you added a new upgrade
<b>1.</b> Render the upgrade in the appropriate branch of [`PathScreen#init()`](https://github.com/KingsMMA/FruitfulUtilities/blob/master/src/main/java/dev/kingrabbit/fruitfulutilities/pathviewer/PathScreen.java#L94).  
```java
renderUpgrade(matrices, <path_id>.getAsJsonObject("<upgrade_id>"), <gridX>, <gridY>, mouseX, mouseY);
```
<b>2.</b> If required, connect it to any relevant upgrades as such:
```java
connectUpgrades(<gridX1>, <gridY1>, <gridX2>, <gridY2>);
```

### Finally
<b>1.</b> Edit this file and update code references to match their new positions.  
<b>2.</b> In the pull request, send a screenshot of the path viewer with your new path/upgrades.  
<b>3.</b> Request a review in your pull request so a maintainer can review your changes.

## Joining DiamondFire
When launching in a development environment, you will not be logged in.  
It is recommended to use [AuthMe](https://github.com/axieum/authme), which will add a button to the multiplayer screen allowing you to log in.

## Hotswapping
Hotswapping allows you to test your changes without restarting your game.  It is recommended to use [IntelliJ](https://www.jetbrains.com/idea/) as your development environment.
Start by running the `Minecraft Client` task on debug mode.  
After making any changes, build the project by clicking `Build > Build Project` or clicking `Ctrl + F9`.

### Hotswapping Mixins
Mixins can't be hotswapped in a default FabricMC environment.  
For more information, see [here](https://fabricmc.net/wiki/tutorial:mixin_hotswaps).

### Limitations
While hotswaps are a great utility for easily testing changes, there are certain limitations.  

- Methods and field signatures cannot be changed
- Classes cannot be created or deleted
- Performance may be slower than that of a normal environment
