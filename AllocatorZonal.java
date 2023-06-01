/*
4) Model strefowy
Model strefowy wykorzystuje koncepcję zbioru roboczego, który jest związany z występowaniem zjawiska lokalności odwołań.
Ponownie przyjmujemy Δt, tym razem do wyznaczenia rozmiaru zbioru roboczego (WSS, ang. working set size). WSS jest
liczbą stron, do których proces pi wygenerował odwołania w czasie Δt. Liczbę aktualnie potrzebnych ramek D możemy
wyznaczyć: 𝐷 = Σ𝑊𝑆𝑆𝑖𝑁𝑖 =1
Dopóki D jest mniejsze niż liczba dostępnych w systemie ramek każdy z procesów otrzymuje do wykorzystania tyle ramek,
ile wynosi jego WSS. W momencie przekroczenia liczby dostępnych ramek przez współczynnik D jeden z aktywnych procesów
musi zostać wstrzymany, a uwolnione ramki przekazane do pozostałych procesów (zgodnie z zasadą proporcjonalności).
Strategie wyboru procesu do wstrzymania mogą być różne: zatrzymanie procesu o najmniejszym WSS (ryzyko konieczności
wstrzymania wielu procesów), zatrzymanie procesu o największym WSS (ryzyko zatrzymania dużego, intensywnie pracującego
procesu), zatrzymanie procesu o najniższym priorytecie (konieczność określania priorytetów) czy zatrzymanie procesu o
wymaganej liczbie ramek (wzrost złożoności algorytmu + wprowadzenie pewnej losowości w wyborze procesu).
Równie istotne jest określenie momentu wyznaczania WSS. Obliczanie tej wartości przy każdym odwołaniu powodowałoby
znaczące obciążenie systemu, więc byłoby nieefektywne (a wielu przypadkach zbędne). Rozsądnym wydaje się przyjęcie
pewnej wartości c takiej, że c < Δt. Sugeruję poeksperymentować z konkretnymi wartościami zaczynając od c = ½ Δt.
 */

import java.util.ArrayList;

public class AllocatorZonal extends FrameAllocator
{
    private final AllocatorProportional allocatorProportional;
    private final int deltaT;
    private final int c;

    ArrayList<Process> processArrayList;

    public AllocatorZonal(int totalAmountOfFrames, int processesAmount, int totalAmountOfPages,
                          int c, int deltaT)
    {
        super(totalAmountOfFrames, processesAmount, totalAmountOfPages);
        this.allocatorProportional = new AllocatorProportional(totalAmountOfFrames, processesAmount, totalAmountOfPages);
        this.deltaT = deltaT;
        this.c = c;
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

        int timeAtCheckWorkSpaces = c;
        int time = 0;

        boolean allDone = false;
        while (!allDone)
        {
            allDone = true;
            int framesLeft = totalAmountOfFrames;

            int index = 1;

            for (int i = 0; i < processArrayList.size(); i++)
            {
                Process process = processArrayList.get(i);

                int processFrameAmount = process.lru.getFrameSize();

                if (timeAtCheckWorkSpaces == time)
                {
                    processFrameAmount = GetAmountOfFrames(process);
                }

                // last one - we give it the rest of free frames
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
                    process.lru.DeleteOldReference();

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
                    }
                    else
                    {
                        allDone = false;
                    }
                }

                index++;
            }

            if (timeAtCheckWorkSpaces == time)
            {
                timeAtCheckWorkSpaces += c;
            }

            time++;
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

        return process.lru.GetWorkSpaceAtDeltaTime();
    }

    public int GetAmountOfFramesFirstTime(Process process)
    {
        return allocatorProportional.GetAmountOfFrames(process);
    }

    @Override
    public String toString()
    {
        return "Model strefowy";
    }
}
