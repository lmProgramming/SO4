/*
3) Sterowanie częstością błędów strony
Algorytm sterowania częstością błędów strony jest algorytmem dynamicznego przydziału ramek do procesu. Należy tutaj
przyjąć 2 graniczne progi częstości błędów stron l oraz u, a samą częstość błędów (PPF, ang. page fault frequency) wyznaczymy: 𝑃𝑃𝐹𝑖= 𝑒𝑖Δ𝑡
Gdzie:
• ei - liczba błędów strony generowanych przez proces pi
• Δt – przyjęte okno czasowe dla pomiaru PPF
Na początku działania algorytmu przydzielamy każdemu procesowi ramki z użyciem standardowego algorytmu przydziału
proporcjonalnego. W trakcie działania systemu (symulacji) monitorujemy współczynnik PPF dla każdego procesu, jeżeli
dojdzie do sytuacji, że PPF przekorczy próg u dany proces otrzymuje dodatkową ramkę. W przypadku spadku PPF poniżej
progu l dany proces zwalnia jedną z ramek. W przypadku, gdy nie ma dostępnych wolnych ramek dany proces jest wstrzymywany.
 Do algorytmu sterowania częstością błędów strony można wprowadzić modyfikację polegającą na akceptowaniu przekroczenia
 progu u przez wartość PPF, utrzymaniu aktywności procesu, a wstrzymywanie go dopiero po przekroczeniu pewnej wartości h
  (odpowiednio wysokiej), która określa, kiedy należy proces wstrzymać. W tym wariancie, procesowi przydzielamy dodatkowe
   ramki po przekroczeniu u ale wstrzymujemy dopiero przy przekroczeniu h.
Istotne dla symulacji tego algorytmu jest dopieranie odpowiednich wartości progowych oraz okna czasu.
*/

import java.util.ArrayList;

public class AllocatorByError extends FrameAllocator
{
    private final AllocatorProportional allocatorProportional;
    private final double l;
    private final double u;
    private final double h;
    private final double deltaT;

    ArrayList<Process> processArrayList;

    public AllocatorByError(int totalAmountOfFrames, int processesAmount, int totalAmountOfPages,
                            double l, double u, double h, double deltaT)
    {
        super(totalAmountOfFrames, processesAmount, totalAmountOfPages);
        this.allocatorProportional = new AllocatorProportional(totalAmountOfFrames, processesAmount, totalAmountOfPages);
        this.l = l;
        this.u = u;
        this.h = h;
        this.deltaT = deltaT;
    }

    @Override
    public void RunProcesses(int requestsAmountPerProcess, Process[] processes) {
        processArrayList = new ArrayList<>();

        for (Process process : processes)
        {
            process.ResetLRU();
            int processFrameAmount = GetAmountOfFramesFirstTime(process);
            process.lru.ResizePagesInMemory(processFrameAmount);

            processArrayList.add(process);
        }

        boolean allDone = false;
        while (!allDone)
        {
            allDone = true;
            int framesLeft = totalAmountOfFrames;

            int index = 1;

            for (int i = 0; i < processArrayList.size(); i++)
            {
                Process process = processArrayList.get(i);
                int processFrameAmount = GetAmountOfFrames(process);
                if (index == processesAmount)
                {
                    processFrameAmount = framesLeft;
                }
                framesLeft -= processFrameAmount;

                if (framesLeft < 0 || processFrameAmount <= 0)
                {
                    framesLeft += processFrameAmount;

                    // process suspended
                    allDone = allDone && process.isDone();
                    process.lru.DeleteOldError();

                    suspendedTimesAmount++;
                }
                else
                {
                    boolean done = process.StepAlgorithm(processFrameAmount);
                    if (done)
                    {
                        processArrayList.remove(process);
                        processesAmount -= 1;
                        i -= 1;
                    } else
                    {
                        allDone = false;
                    }
                }

                index++;
            }
        }

        CalculateTotalErrors(processes);
    }

    @Override
    public int GetAmountOfFrames(Process process)
    {
        if (process.isDone())
        {
            return 0;
        }

        double ppf = process.lru.GetErrorsAtDeltaTime() / deltaT;

        int bias = 0;
        if (ppf > h)
        {
            bias = 1;
        }
        else if (ppf > u)
        {
            bias = 1;
        }
        else if (ppf < l)
        {
            bias = -1;
        }

        return allocatorProportional.GetAmountOfFrames(process) + bias;
    }

    public int GetAmountOfFramesFirstTime(Process process) {
        return allocatorProportional.GetAmountOfFrames(process);
    }

    @Override
    public String toString()
    {
        return "Sterowanie częstością błędów strony";
    }
}
