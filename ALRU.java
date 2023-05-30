import java.util.*;

public class ALRU
{
    public static int run(Page[] pages, int frameSize, int[] requests)
    {
        int errors = 0;

        LinkedList<Page> fifoPages = new LinkedList<>();
        LinkedList<Boolean> fifoParities = new LinkedList<>();

        for (int request : requests) {
            Page requestedPage = pages[request];

            int index = fifoPages.indexOf(requestedPage);
            if (index == -1) {
                if (fifoPages.size() == frameSize)
                {
                    while (fifoParities.element())
                    {
                        //System.out.println(fifoParities.element());
                        //System.out.println(fifoPages.element());

                        fifoParities.remove();
                        fifoParities.add(false);

                        Page firstElement = fifoPages.remove();
                        fifoPages.add(firstElement);
                    }

                    // remove the useless Page
                    fifoPages.remove();
                    fifoParities.remove();
                }

                fifoPages.add(requestedPage);
                fifoParities.add(true);

                errors++;
            }
            else
            {
                fifoParities.set(index, true);
            }

            //System.out.println(fifoPages);
            //System.out.println(fifoParities);
        }

        return errors;
    }
}