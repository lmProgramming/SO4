public class Process
{
    int id;

    int[] requests;
    Page[] pages;

    LRU lru;

    boolean suspended;

    public boolean isDone()
    {
        return done;
    }

    public void setDone(boolean done)
    {
        this.done = done;
    }

    boolean done;

    public Process(int id, int[] requests, Page[] pages)
    {
        this.id = id;
        this.requests = requests;
        this.pages = pages;
    }

    public void ResetLRU()
    {
        lru = new LRU(pages, 10, requests);
    }

    public boolean StepAlgorithm(int newFramesAmount)
    {
        if (suspended)
        {
            return false;
        }
        done = lru.Step(newFramesAmount);
        return done;
    }

    public boolean StepAlgorithm()
    {
        return StepAlgorithm(lru.getFrameSize());
    }


    public void Suspend()
    {
        suspended = true;
    }

    public void StopSuspend()
    {
        suspended = false;
    }
}
