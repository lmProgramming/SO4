import java.util.Random;

/*
Postępująca komplikacja zad. 4. Założyć, że:

- w systemie działa pewna ilość (rzędu ~10) procesów.

- każdy korzysta z własnego zbioru stron (zas. lokalności wciąż obowiązuje).

- globalny ciąg odwołań jest wynikiem połączenia sekwencji odwołań generowanych przez poszczególne procesy (każdy generuje ich wiele, nie jedną)

- każdemu system przydziela określoną liczbę ramek. na podstawie następujących metod:

1. Przydział proporcjonalny

2. Przydział równy

3. Sterowanie częstością błędów strony

4. Model strefowy.

- zastępowanie stron odbywa się zgodnie z LRU.

Jak strategie przydziału ramek wpływają na wyniki (ilość błędów strony - globalnie, dla każdego procesu)?

Program powinien wypisywać na ekranie przyjęte założenia symulacji. Mile widziana możliwość ich zmiany przez użytkownika.

Wnioski?

Zakres materiału: jak w zad 3.
 */

public class Main
{
    public static int deltaT = 30;
    public static double e = 0.5f;
    static Random random;
    public static int[] generateRequests(int numberOfPages, int n, int localMaxSize, double localProbability)
    {
        int[] requests = new int[n];

        for (int i = 0; i < n; i++)
        {
            boolean local = i != 0 && random.nextFloat() < localProbability;
            if (local)
            {
                int localRequest = requests[i - 1] + random.nextInt(-localMaxSize, localMaxSize + 1);
                requests[i] = Math.min(numberOfPages - 1, Math.max(0, localRequest));
            }
            else
            {
                requests[i] = random.nextInt(numberOfPages);
            }
        }

        return requests;
    }

    public static Page[] GeneratePages(int n){
        Page[] pages = new Page[n];

        for (int i = 0; i < n; i++) {
            pages[i] = new Page(i);
        }

        return pages;
    }

    public static void main (String[]args)
    {
        random = new Random();

        // SO3
        int frameSize = 50;
        int pagesAmountPerProcessLowerBound = 20;
        int pagesAmountPerProcessUpperBound = 100;
        int requestsAmountPerProcess = 1000;
        int localSize = 1;
        double localProbability = 0.99;

        int processesAmount = 10;
        Process[] processes = new Process[processesAmount];

        int totalAmountOfPages = 0;

        for (int processID = 0; processID < processesAmount; processID++)
        {
            //int numberOfPages = random.nextInt(pagesAmountPerProcessLowerBound, pagesAmountPerProcessUpperBound);
            int numberOfPages = (int) (pagesAmountPerProcessLowerBound + random.nextFloat(0, 1) * random.nextFloat(0, 1) * (pagesAmountPerProcessUpperBound - pagesAmountPerProcessLowerBound));
            totalAmountOfPages += numberOfPages;

            int[] requests = generateRequests(numberOfPages, requestsAmountPerProcess, localSize, localProbability);

            Page[] pages = GeneratePages(numberOfPages);

            processes[processID] = new Process(processID, requests, pages);
        }

        FrameAllocator[] allocators = new FrameAllocator[]
        {
                new AllocatorEqual(frameSize, processesAmount, totalAmountOfPages),
                new AllocatorProportional(frameSize, processesAmount, totalAmountOfPages),
                new AllocatorByError(frameSize, processesAmount, totalAmountOfPages, 10, 20, 25, deltaT),
                new AllocatorZonal(frameSize, processesAmount, totalAmountOfPages, deltaT * 2/3, deltaT),
        };

        for (FrameAllocator allocator : allocators)
        {
            System.out.println("\n" + allocator);
            allocator.RunProcesses(requestsAmountPerProcess, processes);
        }
    }
}