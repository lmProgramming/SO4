import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class LRU {
    private Page[] pages;
    private int frameSize;
    private int[] requests;
    private int errors;
    private int requestsDone;
    private ArrayList<Page> pagesInMemory;
    private ArrayList<Integer> timesPageWaiting;

    Queue<Boolean> errorAtFrame = new LinkedList<>();
    Queue<Integer> references = new LinkedList<>();
    int szamotania = 0;

    public LRU(Page[] pages, int frameSize, int[] requests) {
        this.pages = pages;
        this.frameSize = frameSize;
        this.requests = requests;

        errors = 0;
        requestsDone = 0;
        pagesInMemory = new ArrayList<>();
        timesPageWaiting = new ArrayList<>();
        errorAtFrame = new LinkedList<>();
        references = new LinkedList<>();
    }

    public int getFrameSize() {
        return frameSize;
    }

    public void setFrameSize(int frameSize) {
        this.frameSize = frameSize;
    }

    public int getErrors() {
        return errors;
    }

    public int getSzamotanieErrors() {
        return szamotania;
    }

    public void ResizePagesInMemory(int newFrameSize)
    {
        if (newFrameSize > frameSize)
        {
            pagesInMemory.ensureCapacity(newFrameSize);
        }
        else  if (newFrameSize >= 1)
        {
            pagesInMemory.trimToSize();
        }
        frameSize = newFrameSize;
    }

    public boolean Step(int newFrameSize)
    {
        if (requestsDone == requests.length)
        {
            return true;
        }

        if (newFrameSize != frameSize)
        {
            ResizePagesInMemory(newFrameSize);
        }

        int request = requests[requestsDone];
        Page requestedPage = pages[request];

        int indexInMemory = pagesInMemory.indexOf(requestedPage);
        if (indexInMemory == -1) {
            if (pagesInMemory.size() == frameSize)
            {
                int highestTimeIndex = 0;

                for (int i = 1; i < pagesInMemory.size(); i++) {
                    if (timesPageWaiting.get(i) > timesPageWaiting.get(highestTimeIndex)) {
                        highestTimeIndex = i;
                    }
                }

                pagesInMemory.remove(highestTimeIndex);
                timesPageWaiting.remove(highestTimeIndex);
            }

            pagesInMemory.add(requestedPage);
            timesPageWaiting.add(0);

            errors++;

            errorAtFrame.add(true);
        }
        else
        {
            timesPageWaiting.replaceAll(time -> time + 1);
            timesPageWaiting.set(indexInMemory, 0);

            errorAtFrame.add(false);
        }

        references.add(request);

        if (errorAtFrame.size() > Main.deltaT){
            errorAtFrame.poll();
        }
        if (references.size() > Main.deltaT){
            references.poll();
        }

        if (((float)GetErrorsAtDeltaTime() / (float)Main.deltaT) > Main.e){
            szamotania++;
        }

        requestsDone++;

        return false;
    }

    public void DeleteOldError()
    {
        errorAtFrame.poll();
    }

    public void DeleteOldReference()
    {
        references.poll();
    }

    public int GetErrorsAtDeltaTime()
    {
        int sum = 0;
        for (boolean b : errorAtFrame){
            sum += b ? 1 : 0;
        }
        return sum;
    }

    public int GetWorkSpaceAtDeltaTime()
    {
        ArrayList<Integer> referencesArrayList = new ArrayList<>();
        for (int i : references)
        {
            if (!referencesArrayList.contains(i)){
                referencesArrayList.add(i);
            }
        }
        return referencesArrayList.size();
    }
}