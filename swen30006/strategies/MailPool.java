package strategies;


/** Importing relevant classes from automail */
import automail.MailItem;
import automail.IMailPool;

/** Importing java libraries */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Sample of what a MailPool could look like.
 * This one tosses the incoming mail on a pile and takes the outgoing mail from the top.
 */
public class MailPool implements IMailPool {

    private ArrayList<MailItem> mailItems;

    public MailPool(){

        this.mailItems = new ArrayList<>();

    }

    @Override
    public void addToPool(MailItem mailItem) {

        System.out.println("Adding to the pool " + mailItem);
        mailItems.add(mailItem);
        return;
    }


    public int getLength() {

        return mailItems.size();
    }

    public MailItem getMailItem(int index) {

        return this.mailItems.get(index);
    }

    public boolean isEmptyPool() {

        return mailItems.isEmpty();
    }

    public void removeMailItem(MailItem mailItem) {


        this.mailItems.remove(mailItem);
        return;
    }

    public void sortByFloor() {

        FloorComparator comparator = new FloorComparator();

        Collections.sort(this.mailItems, comparator);

        printPool();

        return;
    }

    public void printPool() {
        System.out.println("==============================");
        System.out.println("Result of sorting");
        for(MailItem mi : this.mailItems) {
            System.out.println(mi);

        }

        System.out.println("==============================");
    }

    public int getIndexForFloor(int referenceFloor) {


        this.sortByFloor();

        for(MailItem mailItem : this.mailItems) {
            if(mailItem.getDestFloor() >= referenceFloor)
            {
                return this.mailItems.indexOf(mailItem);
            }
        }

        return -1;
    }

    public class FloorComparator implements Comparator<MailItem>
    {
        public FloorComparator() {

        }

        @Override
        public int compare(MailItem itemOne, MailItem itemTwo)
        {
            int floorOne = itemOne.getDestFloor();
            int floorTwo = itemTwo.getDestFloor();
            if (floorOne < floorTwo)
            {
                return -1;
            }
            if (floorTwo > floorOne)
            {
                return 1;
            }
            return 0;
        }
    }
}
