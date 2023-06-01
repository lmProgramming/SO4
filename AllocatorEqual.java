public class AllocatorEqual extends FrameAllocator
{
    public AllocatorEqual(int totalAmountOfFrames, int processesAmount, int totalAmountOfPages)
    {
        super(totalAmountOfFrames, processesAmount, totalAmountOfPages);
    }

    @Override
    public int GetAmountOfFrames(Process process)
    {
        return totalAmountOfFrames / processesAmount;
    }

    @Override
    public String toString()
    {
        return "Przydział równy";
    }
}
