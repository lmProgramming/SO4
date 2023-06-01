public class AllocatorProportional extends FrameAllocator
{
    public AllocatorProportional(int totalAmountOfFrames, int processesAmount, int totalAmountOfPages)
    {
        super(totalAmountOfFrames, processesAmount, totalAmountOfPages);
    }

    @Override
    public int GetAmountOfFrames(Process process)
    {
        return (int) Math.max(1, (double) totalAmountOfFrames * (double)process.pages.length / (double)totalAmountOfPages + 0.5f);
    }

    @Override
    public String toString()
    {
        return "Przydział proporcjonalny";
    }
}
