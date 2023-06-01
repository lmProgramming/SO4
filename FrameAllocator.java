public abstract class FrameAllocator
{
    protected int totalAmountOfFrames;
    protected int processesAmount;
    protected int totalAmountOfPages;

    public int getSuspendedTimesAmount()
    {
        return suspendedTimesAmount;
    }
    protected int suspendedTimesAmount = 0;

    public FrameAllocator(int totalAmountOfFrames, int processesAmount, int totalAmountOfPages)
    {
        this.totalAmountOfFrames = totalAmountOfFrames;
        this.processesAmount = processesAmount;
        this.totalAmountOfPages = totalAmountOfPages;
    }

    public abstract int GetAmountOfFrames(Process process);

    public void RunProcesses(int requestsAmountPerProcess, Process[] processes)
    {
        for (Process process : processes)
        {
            process.ResetLRU();

            int processFrameAmount = GetAmountOfFrames(process);
            process.lru.ResizePagesInMemory(processFrameAmount);
        }

        for (int i = 0; i < requestsAmountPerProcess; i++)
        {
            for (Process process : processes)
            {
                process.StepAlgorithm();
            }
        }

        CalculateTotalErrors(processes);
    }

    public void CalculateTotalErrors(Process[] processes)
    {
        int totalErrors = 0;
        int totalSzamotania = 0;
        int totalSuspended = 0;

        for (Process process : processes)
        {
            int errors = process.lru.getErrors();
            //System.out.println("Frame size: " + process.lru.getFrameSize() + " Errors: " + process.lru.getErrors());
            //System.out.println("Szamotania: " + process.lru.getSzamotanieErrors());
            totalErrors += errors;
            totalSzamotania += process.lru.getSzamotanieErrors();
        }

        System.out.println("Suma błędów: " + totalErrors);
        System.out.println("Ilość szamotań: " + totalSzamotania);
        System.out.println("Ilość zatrzymań: " + suspendedTimesAmount);
    }
}
