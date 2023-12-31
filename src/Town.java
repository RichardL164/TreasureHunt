/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private boolean dug;

    private boolean isSearched;


    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;

        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
        dug = false;
    }

    public String getLatestNews() {
        return printMessage;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";
        isSearched = false;

        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, you lost your " + item;
            }

            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        shop.enter(hunter, choice);
        printMessage = "You left the shop";
    }


    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (hunter.hasItemInKit("sword")) {
            System.out.println("the brawler, seeing your sword, realizes he picked a losing fight and gives you his gold");
            hunter.changeGold((int) (Math.random() * 10) + 1);
        }
        else {
            if (toughTown) {
                noTroubleChance = 0.66;
            } else {
                noTroubleChance = 0.33;
            }

            if (TreasureHunter.easyMode) {
                noTroubleChance = 0.5;
            }

            if (Math.random() > noTroubleChance) {
                printMessage = "You couldn't find any trouble";
            } else {
                printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n" + Colors.RESET;
                int goldDiff = (int) (Math.random() * 10) + 1;
                if (Math.random() > noTroubleChance) {
                    printMessage += Colors.RED + "Okay, stranger! You proved yer mettle. Here, take my gold." + Colors.RESET;
                    printMessage += Colors.RED + "\nYou won the brawl and receive " + Colors.RESET + Colors.YELLOW + goldDiff + " gold." + Colors.RESET;
                    hunter.changeGold(goldDiff);
                } else {
                    printMessage += Colors.RED + "That'll teach you to go lookin' fer trouble in MY town! Now pay up!" + Colors.RESET;
                    printMessage += Colors.RED + "\nYou lost the brawl and pay " + Colors.YELLOW + goldDiff + " gold." + Colors.RESET;
                    hunter.changeGold(-goldDiff);
                }
            }
        }
    }
    public void digGold() {
        if (!dug){
            if (hunter.hasItemInKit("shovel")) {
                int randNum = (int) (Math.random() * 2) + 1;
                if (randNum == 1) {
                    int nextRandNum = (int) (Math.random() * 20) + 1;
                    System.out.println("You dug up " + nextRandNum + " gold!");
                    hunter.changeGold(nextRandNum);
                } else {
                    System.out.println("You dug but only found dirt.");
                }
                dug = true;
            } else {
                System.out.println("You can't dig for gold without a shovel");
            }
        } else {
            System.out.println("You already dug for gold in this town");
        }
    }

    public String toString() {
        return "This nice little town is surrounded by " + Colors.CYAN + terrain.getTerrainName() + Colors.RESET + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random();
        if (rnd < .17) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < .34) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < .51) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < .68) {
            return new Terrain("Desert", "Water");
        } else if (rnd < .85) {
            return new Terrain("Jungle", "Machete");
        } else {
            return new Terrain("Marsh", "Boots");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        if (TreasureHunter.easyMode) {
            return false;
        }
        return (rand < 0.5);
    }

    public String huntForTreasure() {
        if (isSearched) {
            return "you have already searched this town";
        }
        isSearched = true;
        int treasure = (int) (Math.random() * 3) + 1;
        if (treasure == 1) {
            if (hunter.checkTreasure("crown")) {
                return "You found an crown!";
            }
            return "You already found this.";
        }
        if (treasure == 2) {
            if (hunter.checkTreasure("gem")) {
                return "You found an gem!";
            }
            return "You already found this.";
        }
        if (treasure == 3) {
            if (hunter.checkTreasure("trophy")) {
                return "You found an trophy!";
            }
            return "You already found this.";
        }
        return "You found dust";
    }
}