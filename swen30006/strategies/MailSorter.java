/**
 * Author: Shreyash Patodia
 * Student Number: 767336
 * Subject: SWEN30006 Software Modelling and Design.
 * Project: Assignment 1 (Part A)
 * Semester 1, 2017
 * */

/** Package name is strategies */
package strategies;

/** Import all classes from automail */
import automail.*;

/** Importing relevant classes from java libraries */
import java.util.ArrayList;
import exceptions.TubeFullException;
import java.util.Arrays;


/**
 * A MailSorter class that implements Knapsack in order to find the maximum value items that
 * the robot can deliver at each trip. The class implements 2 separate knapsacks for the floors
 * that are below the mail room floors and the rest of the floors repsectively. I did this
 * because the it does not make sense for us to cross the mail room floor without actually
 * stopping at it and collecting more items.
 */
public class MailSorter implements IMailSorter{

    /**
     * The pool of the mail items
     * */
    private MailPool mailPool;


    /**
     * Constructor that tells the sorter which mailPool it is to sort.
     * @param mailPool the mailPool to take items from, and then get the robot to deliver
     * items from.
     */
    public MailSorter(MailPool mailPool) {

        this.mailPool = mailPool;
    }

    /**
     * Function that is called in order to fill the storage tube of the robot in with the
     * highest priority items so that the score can be minimized.
     * @param tube the storage tube to be filled by the sorting algorithm.
     */
    @Override
    public boolean fillStorageTube(StorageTube tube) {

        // System.out.println("Hello! Filling tube at: " + Clock.Time());

        /* Capacity of the tube */
        int maxCapacity = tube.MAXIMUM_CAPACITY;

        /* Total items in the mail pool */
        int totalNumItems = mailPool.getLength();

        /* The floor we are on, by default the mail room floor */
        int referenceFloor = Building.MAILROOM_LOCATION;

        /* The index of the first item with floor greater than or equal to the referenceFloor
         * getIndexForFloor sorts the mailItems so that we can iterate through the mailItems
         * in an ordered manner, also keeps the control of the mailItems to the pool and not
         * to the sorter.
         */
        int indexDivider = this.mailPool.getIndexForFloor(referenceFloor);

        /* These are the items to add to the tube */
        ArrayList<MailItem> itemsToAdd = chooseKnapsackValues( indexDivider, totalNumItems, maxCapacity);

        int count = 0;

        // Get rid of the try catch block. Needs to be gotten rid of. !!!!!!
        while(count < itemsToAdd.size()) {

            MailItem mi = itemsToAdd.get(count);

            try {
                this.mailPool.removeMailItem(mi);
                tube.addItem(mi);

            } catch (TubeFullException e) {
                return true;
            }
            count++;
        }
        // System.out.println("==============================");
        if(!tube.isEmpty()) {
            // System.out.println("Filled the tube " + this.fillingTube);
            // System.out.println("Items delivered being delivered " + this.itemsDelivered);
            return true;
        }

        return false;
    }


    private ArrayList<MailItem> chooseKnapsackValues( int indexDivider, int totalNumItems, int maxCapacity) {
        double valuesTop[][];
        double valuesBottom[][];
        int startIndex = 0;
        double values[][];

        if(indexDivider > -1) {

            valuesTop = Knapsack(indexDivider + 1, totalNumItems, maxCapacity);
            valuesBottom = Knapsack(1, indexDivider, maxCapacity);

            double [] lastTopRow = valuesTop[valuesTop.length - 1];
            double lastTopValue = lastTopRow[lastTopRow.length - 1];

            double [] lastBottomRow = valuesBottom[valuesBottom.length - 1];
            double lastBottomValue = lastBottomRow[lastBottomRow.length - 1];

            if(lastTopValue > lastBottomValue) {
                values = valuesTop;
                startIndex = indexDivider;
            }
            else {
                values = valuesBottom;
            }
        }
        else {
            values = Knapsack(1, totalNumItems, maxCapacity);
        }

        return determineItems(values, startIndex, values.length  - 1, maxCapacity);

    }

    // Start item is the number of the first item in the knapsack, last is the index of the last item.
    private double[][] Knapsack(int startItem, int lastItem, int maxCapacity) {


        double values[][]= new double[lastItem - startItem  + 2][maxCapacity + 1];
        int times[][] = new int[lastItem - startItem + 2][maxCapacity + 1];
        int locations[][] = new int[lastItem - startItem + 2][maxCapacity + 1];

        for(int itemTimeRow[] : times) {
            Arrays.fill(itemTimeRow, Clock.Time());
        }

        for(int itemLocationArray[] : locations) {
            Arrays.fill(itemLocationArray, Building.MAILROOM_LOCATION);
        }

        for(int col = 0; col <= maxCapacity; col++) {
            values[0][col] = 0;
        }

        for(int row = 0; row <= (lastItem - startItem  + 1); row++) {
            values[row][0] = 0;
        }

        for(int item = 1; item <= (lastItem - startItem + 1); item++) {
            for(int weight = 1; weight <= maxCapacity; weight++) {
                //System.out.println("StartItem = " + startItem + " item = " + item);
                MailItem currentItem = this.mailPool.getMailItem(startItem + item - 2);
                if(currentItem.getSize() > weight) {
                    values[item][weight] = values[item - 1][weight];
                }
                else {
                    double altScore = (values[item - 1][weight - currentItem.getSize()] +
                            (calculateDeliveryScore(currentItem, times[item][weight - currentItem.getSize()],
                                    locations[item - 1][weight - currentItem.getSize()])));
                    double prevScore = values[item - 1][weight];
                    if(prevScore >= altScore) {

                        values[item][weight] = prevScore;
                        times[item][weight] = times[item - 1][weight];
                        locations[item][weight] = locations[item - 1][weight];

                    }
                    else {
                        times[item][weight] = times[item - 1][weight - currentItem.getSize()]  +
                                currentItem.getDestFloor() + 1;
                        values[item][weight] = altScore;
                        locations[item][weight] = currentItem.getDestFloor();
                    }
                }
            }

        }

        return values;
    }

    private ArrayList<MailItem> determineItems(double [][]values, int startIndex, int numItems, int maxCapacity) {
        int capacity = maxCapacity;
        int item = numItems;
        ArrayList<MailItem> itemsToAdd = new ArrayList<>();
        while(capacity > 0 && item > 0) {
            //System.out.println("item = " + item + " capacity = " + capacity);
            if(values[item][capacity] != values[item - 1][capacity]) {
                MailItem mailItem = mailPool.getMailItem(startIndex + item - 1);
                itemsToAdd.add(mailItem);
                capacity = capacity -  mailItem.getSize();
            }
            item = item - 1;
        }

        return itemsToAdd;

    }

    /**
     * Function takes the a mailItem, the current time in the simulation (an overestimate) and a reference floor i.e.
     * the floor the robot was at when considering whether to deliver the mail item passed as parameter so that we
     * can measure the relative distance of the floors, to make sure the robot doesn't have to travel large distances.
     * If the robot ends up travelling too far then it will take it too long to come back when we could have just
     * delivered something else (thus, the distance is factored into the score).
     * @param deliveryItem the item being considered to be delivered.
     * @param simulationTime the overestimated time in the simulation.
     * @param referenceFloor the floor the robot is at when considering the deliveryItem.
     * @return the score of the item, higher means the item is more likely to be selected.
     */
    private static double calculateDeliveryScore(MailItem deliveryItem, int simulationTime, int referenceFloor) {

        // Penalty for longer delivery times
        final double penalty = 1.3;
        // Take (delivery time - arrivalTime)**penalty * priority_weight
        double priority_weight = 0.1;
        // double priority_additive_value = 0.1;
        double scale = 10.0;
            // Determine the priority_weight

        switch(deliveryItem.getPriorityLevel()) {
            case "LOW":
                priority_weight = 1;
                break;
            case "MEDIUM":
                priority_weight = 1.6;
                break;
            case "HIGH":
                priority_weight = 2;
                break;
        }

        double numerator = simulationTime - deliveryItem.getArrivalTime() + Math.pow(priority_weight, 2);
        double denominator = Math.abs(deliveryItem.getDestFloor() - referenceFloor) + 1;
        double score =  ((Math.pow(numerator, penalty)*(priority_weight))
                /(Math.pow(denominator*penalty, penalty*penalty) - 1));

        // System.out.println(score);

        return score;
    }
}
