import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

public class LRU
{
    public static int run(Page[] pages, int frameSize, int[] requests)
    {
        int errors = 0;

        ArrayList<Page> pagesInMemory = new ArrayList<>();
        ArrayList<Integer> timesPageWaiting = new ArrayList<>();

        for (int request : requests) {
            Page requestedPage = pages[request];

            int indexInMemory = pagesInMemory.indexOf(requestedPage);
            if (indexInMemory == -1) {
                if (pagesInMemory.size() == frameSize)
                {
                    int highestTimeIndex = 0;

                    for (int i = 1; i < pagesInMemory.size(); i++)
                    {
                        if (timesPageWaiting.get(i) > highestTimeIndex){
                            highestTimeIndex = i;
                        }
                    }

                    pagesInMemory.remove(highestTimeIndex);
                    timesPageWaiting.remove(highestTimeIndex);
                }

                pagesInMemory.add(requestedPage);
                timesPageWaiting.add(0);

                errors++;
            }
            else
            {
                timesPageWaiting.replaceAll(integer -> integer + 1);
                timesPageWaiting.set(indexInMemory, 0);
            }
        }

        return errors;
    }
}