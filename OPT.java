import java.util.ArrayList;
import java.util.Arrays;

public class OPT {
    public static int run(Page[] pages, int frameSize, int[] requests) {
        int errors = 0;

        ArrayList<Page> pagesInMemory = new ArrayList<>();

        for (int i = 0; i < requests.length; i++) {
            int request = requests[i];
            boolean pageHit = false;
            Page page = pages[request];

            if (!pagesInMemory.contains(page))
            {
                errors++;

                if (pagesInMemory.size() == frameSize)
                {
                    int pageToReplace = -1;
                    int furthestUse = i;

                    for (int j = 0; j < frameSize; j++) {
                        int nextPageUse = Integer.MAX_VALUE;
                        int curPageRequest = pagesInMemory.get(j).nr;

                        for (int k = i + 1; k < requests.length; k++) {
                            if (requests[k] == curPageRequest) {
                                nextPageUse = k;
                                break;
                            }
                        }

                        if (nextPageUse == Integer.MAX_VALUE) {
                            pageToReplace = j;
                            break;
                        }

                        if (nextPageUse > furthestUse) {
                            furthestUse = nextPageUse;
                            pageToReplace = j;
                        }
                    }

                    pagesInMemory.set(pageToReplace, page);
                }
                else
                {
                    pagesInMemory.add(page);
                }
            }
        }

        return errors;
    }
}