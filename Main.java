import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

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
    static Random random;
    public static int[] generateRequests(int numberOfPages, int n, int localMaxSize, double localProbability)
    {
        int[] requests = new int[n];

        for (int i = 0; i < n; i++)
        {
            boolean local = i != 0 && random.nextFloat() < localProbability;
            if (local)
            {
                int localRequest = requests[i - 1] + random.nextInt(-localMaxSize, localMaxSize);
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

        int numberOfPages = 50;

        int[] frameSizes = { 3, 5, 10 };

        int n = 1000;

        Page[] pages = GeneratePages(numberOfPages);

        int localSize = 2;

        double localProbability = 0.95;

        int[] requests = generateRequests(numberOfPages, n, localSize, localProbability);

        boolean testSequence = false;
        if (testSequence)
        {
            requests = new int[] { 0, 1, 2, 3, 0, 1, 4, 0, 1, 2, 3, 4 };
            pages = new Page[] { new Page(0), new Page(1),new Page(2),new Page(3),new Page(4)};
            frameSizes = new int[] { 4 };
        }

        for (int curFrameSize : frameSizes)
        {
            System.out.println("Current frame size: " + curFrameSize);

            int fifoErrors = FIFO.run(pages, curFrameSize, requests);
            System.out.println("FIFO errors: " + fifoErrors);

            int optErrors = OPT.run(pages, curFrameSize, requests);
            System.out.println("OPT errors: " + optErrors);

            int lruErrors = LRU.run(pages, curFrameSize, requests);
            System.out.println("LRU errors: " + lruErrors);

            int alruErrors = ALRU.run(pages, curFrameSize, requests);
            System.out.println("ALRU errors: " + alruErrors);

            int rndErrors = RND.run(pages, curFrameSize, requests);
            System.out.println("RAND errors: " + rndErrors);
        }
    }
}