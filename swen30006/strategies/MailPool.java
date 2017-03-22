/**
 * Author: Shreyash Patodia
 * Student Number: 767336
 * Subject: SWEN30006 Software Modelling and Design.
 * Project: Assignment 1 (Part A)
 * Semester 1, 2017
 * */

/** Package name */
package strategies;


/** Importing relevant classes from package automail */
import automail.MailItem;
import automail.IMailPool;

/** Importing java library classes */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Class contains all the mailItems delivered to the building. The class initially chucks all the
 * mail into an ArrayList (due to easy access and insertion) and sorts the mail according to the
 * floor when the robot comes to take some mail to deliver. The mail is not sorted continuously
 * since sorting when every item comes in is more work than required and the only time we need to
 * sort is when the robot comes in to take the items (sorting is called in getIndexForFloor function).
 */
public class MailPool implements IMailPool {

    /**
     * ArrayList of the items in the Mail Pool
     * */
    private ArrayList<MailItem> mailItems;

    /**
     * Constructor that creates the MailPool and initializes mailItems to an empty ArrayList.
     */
    public MailPool(){

        this.mailItems = new ArrayList<>();

    }

    /**
     * Takes a new mail item as parameter and adds it to the ArrayList of already existing items.
     * @param mailItem the mail item being added to the pool.
     */
    @Override
    public void addToPool(MailItem mailItem) {

        // System.out.println("Adding to the pool " + mailItem);
        mailItems.add(mailItem);
        return;
    }

    /**
     * Finds the length of the mailItems ArrayList.
     * @return current length of the mailItems ArrayList.
     */
    public int getLength() {

        return mailItems.size();
    }

    /**
     * FUNCTION IS USED TO ITERATE THROUGH THE ARRAYLIST ONCE IT IS SORTED.
     * Allows user to get the mailItem at a certain index. In this way we can iterate through
     * the mailItems without having to pass all the items to the MailSorter. (which is not in the best
     * interests of abstraction).
     * @param index the index of the mail item to find from the pool.
     * @return the mail item at the given index.
     */
    public MailItem getMailItem(int index) {

        return this.mailItems.get(index);
    }

    /**
     * Checks if the pool is empty
     * @return boolean value indicating whether the pool is empty.
     */
    public boolean isEmptyPool() {

        return mailItems.isEmpty();
    }

    /**
     * Removes the mail item provided as parameter from the mail Pool.
     * @param mailItem the mail item to be removed.
     */
    public void removeMailItem(MailItem mailItem) {

        this.mailItems.remove(mailItem);
        return;
    }

    /**
     * Sorts the list of mail items by their floor (in ascending order).
     */
    private void sortByFloor() {

        FloorComparator comparator = new FloorComparator();
        Collections.sort(this.mailItems, comparator);
        printPool();
        return;
    }

    /**
     * Prints the whole list of mailItems, used mainly for debugging.
     */
    public void printPool() {
        System.out.println("==============================");
        System.out.println("Result of sorting");
        for(MailItem mi : this.mailItems) {
            System.out.println(mi);

        }
        System.out.println("==============================");
    }

    /**
     * Sorts the list, and finds the index of the first item with floor greater
     * than or equal to the floor in the parameter.
     * @param referenceFloor the floor that we need to find the first item of.
     * @return index of the first item with floor greater than or equal to the
     * parameter floor.  -1 otherwise.
     */
    public int getIndexForFloor(int referenceFloor) {

        /* Calling sort here because this is the time we would need the items in
         * a sorted order, and thus, don't need to do the extra work of sorting
         * items as they come in.
         */
        this.sortByFloor();

        for(MailItem mailItem : this.mailItems) {

            if(mailItem.getDestFloor() >= referenceFloor)
            {
                System.out.println("Divider is:" + this.mailItems.indexOf(mailItem));
                return this.mailItems.indexOf(mailItem);

            }
        }
        return -1;
    }

    /**
     * Comparator that is used in order to sort the floor, so that we can sort the
     * mail items by the floor.
     */
    public class FloorComparator implements Comparator<MailItem>
    {
        /**
         * Function is used to compare values when sorting the mail pool items.
         * @param itemOne the first item of the comparison
         * @param itemTwo the second item of the comparison
         * @return -1 if floor of itemOne is less than floor of itemTwo, 1
         * if the floor of itemOne is greater than floor for itemTwo, or if the floors
         * are equal (since itemTwo will have a later arrival time so it makes sense for it
         * to be after itemOne).
         */
        public int compare(MailItem itemOne, MailItem itemTwo)
        {
            int floorOne = itemOne.getDestFloor();
            int floorTwo = itemTwo.getDestFloor();
            if (floorOne < floorTwo) {
                return -1;
            }
            else if(floorOne > floorTwo) {
                return 1;
            }
            return 0;
        }
    }
}
