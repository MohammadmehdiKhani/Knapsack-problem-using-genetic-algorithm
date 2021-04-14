import java.util.ArrayList;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);

        String firstLine = scanner.nextLine();
        String[] splitedFirstLine = firstLine.split(" ");
        int maxWeight = Integer.parseInt(splitedFirstLine[0]);
        int itemCount = Integer.parseInt(splitedFirstLine[1]);

        int[] values = new int[itemCount];
        int[] weights = new int[itemCount];
        Helper.PleaseHelp.ReadInputs(values, weights, itemCount);

        KnapSackGeneticAlgorithm knapsack = new KnapSackGeneticAlgorithm(itemCount, values, weights, maxWeight, 200*itemCount, 50, 0.01);
        String Answer = knapsack.findBestIndividual();
        Helper.PleaseHelp.PrintAnswer(Answer);
    }
}

class Helper
{
    static class PleaseHelp
    {
        public static void ReadInputs(int[] values, int[] weights, int itemCount)
        {
            Scanner scanner = new Scanner(System.in);

            for (int i = 0; i < itemCount; i++)
            {
                String line = scanner.nextLine();
                String[] splitedLine = line.split(" ");

                if (splitedLine.length == 2)
                {
                    weights[i] = Integer.parseInt(splitedLine[0]);
                    values[i] = Integer.parseInt(splitedLine[1]);
                } else
                {
                    weights[i] = Integer.parseInt(splitedLine[0]);
                    values[i] = (Integer.parseInt(splitedLine[1])) - ((Integer.parseInt(splitedLine[2]))*(Integer.parseInt(splitedLine[3]))) ;
                }
            }
        }

        public static void PrintAnswer(String Answer)
        {
            for (int i = 0; i < Answer.length(); i++)
                System.out.println(Answer.charAt(i));
        }
    }
}

class KnapSackGeneticAlgorithm
{
    private int ItemsCount;
    private int[] Values;
    private int[] Weights;
    private double MaxWeight;
    private static int Population;
    private static int MaximumGeneration;
    private static double MutationProbability;
    private ArrayList<String> People;
    private double BestFitness;
    private ArrayList<String> SurvivingPeople;

    public KnapSackGeneticAlgorithm(int itemsCount, int[] values, int[] weights, double maxWeight, int population, int maximumGeneration, double mutationProbability)
    {
        ItemsCount = itemsCount;
        Values = values;
        Weights = weights;
        MaxWeight = maxWeight;
        Population = population;
        MaximumGeneration = maximumGeneration;
        MutationProbability = mutationProbability;

        People = new ArrayList<String>();
        SurvivingPeople = new ArrayList<String>();
    }

    public String findBestIndividual()
    {
        generatePeople();

        for (int i = 0; i < MaximumGeneration; i++)       //go forward till reach generation limit
            newGeneration();

        String Answer = getBestChild(People);
        return Answer;
    }

    private void generatePeople()
    {
        String individual;

        for (int i = 0; i < Population; i++)
        {
            individual = "";

            for (int j = 0; j < ItemsCount; j++)
            {
                double chance = Math.random();

                if (chance == 0.5)
                {
                    j--;
                    continue;
                }

                if (chance > 0.5)
                    individual += "1";
                else
                    individual += "0";
            }
            People.add(individual);
        }
    }

    private double getFitness(String individual)
    {
        double fitness = 0;
        double weight = 0;

        for (int i = 0; i < individual.length(); i++)
        {
            if (individual.charAt(i) == '1')
            {
                weight += Weights[i];
                fitness += Values[i];
            }
        }

        if (weight > MaxWeight)
            return -1;
        else
            return fitness;
    }

    private String crossOver(String individual1, String individual2)
    {
        String crossedOver = "";

        for (int i = 0; i < individual1.length(); i++)
        {
            if (Math.random() >= 0.5)
                crossedOver += individual1.charAt(i);
            else
                crossedOver += individual2.charAt(i);
        }

        return crossedOver;
    }

    private String mutate(String candidate)
    {
        String mutated = "";

        for (int i = 0; i < candidate.length(); i++)
        {
            if (Math.random() <= MutationProbability)
            {
                if (candidate.charAt(i) == '0')
                    mutated += '1';

                if (candidate.charAt(i) == '1')
                    mutated += '0';
            } else
            {
                mutated += candidate.charAt(i);
            }
        }
        return mutated;
    }

    private String getBestChild(ArrayList<String> Persons)
    {
        double bestFitness = -1;
        String bestChild = null;

        for (int i = 0; i < Persons.size(); i++)
        {
            double newFitness = getFitness(Persons.get(i));
            if (newFitness != -1)
            {
                if (newFitness >= bestFitness)
                {
                    bestChild = Persons.get(i);
                    bestFitness = newFitness;
                }
            }
        }

        BestFitness = bestFitness;
        return bestChild;
    }

    private void newGeneration()
    {
        for (int i = 0; i < 5; i++)                 //choose 5 person to survive
        {
            String individual = getBestChild(People);
            People.remove(individual);
            SurvivingPeople.add(individual);
        }

        People = null;                              //kill poor people
        People = new ArrayList<String>();

        for (int i = 1; i < Population; i++)
        {
            String firstIndividual = getOneGoodIndividualRandomly(SurvivingPeople);
            String secondIndividual = getOneGoodIndividualRandomly(SurvivingPeople);

            People.add(mutate(crossOver(firstIndividual, secondIndividual)));
        }

        for (int i = 0; i < 5; i++)                 //move Survivors to PEOPLE
        {
            String individual = SurvivingPeople.get(i);
            People.add(individual);
        }

        SurvivingPeople = null;                      //reset SurvivorsList for next generation
        SurvivingPeople = new ArrayList<String>();
    }

    private String getOneGoodIndividualRandomly(ArrayList<String> persons)
    {
        String individual;
        int index = ((int) (Math.random() * persons.size())) % persons.size();
        individual = persons.get(index);

        return individual;
    }
}