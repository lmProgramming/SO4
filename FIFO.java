import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

public class FIFO
{
    public static int run(Page[] pages, int frameSize, int[] requests)
    {
        int errors = 0;

        ArrayList<Page> pagesInMemory = new ArrayList<>();

        int indexToRemove = 0;

        for (int request : requests) {
            Page requestedPage = pages[request];
            if (!pagesInMemory.contains(requestedPage)) {
                if (pagesInMemory.size() == frameSize)
                {
                    pagesInMemory.set(indexToRemove, requestedPage);
                    indexToRemove++;
                    if (indexToRemove >= frameSize)
                    {
                        indexToRemove = 0;
                    }
                }
                else
                {
                    pagesInMemory.add(requestedPage);
                }
                errors++;
            }
        }

        return errors;
    }
}