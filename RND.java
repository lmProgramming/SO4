import java.util.ArrayList;
import java.util.Random;

public class RND
{
    public static int run(Page[] pages, int frameSize, int[] requests)
    {
        int errors = 0;

        Random random = new Random();

        ArrayList<Page> pagesInMemory = new ArrayList<>();

        for (int request : requests) {
            Page requestedPage = pages[request];

            if (!pagesInMemory.contains(requestedPage))
            {
                if (pagesInMemory.size() == frameSize)
                {
                    pagesInMemory.remove(random.nextInt(frameSize));
                }
                pagesInMemory.add(requestedPage);
                errors++;
            }
        }

        return errors;
    }
}
