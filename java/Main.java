import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {

    public enum SolverType {
        SHOTGUN,
        IMPROVE,
        GREEDYSEARCH,
        BESTHEURISTIC
    }

    public static void main(String[] args) {
        String problemNum = "9";
        String folderNameIn = "in";
        String folderName = "submission12";
        String folderOutName = "submission12";


        SolverType solverType = SolverType.GREEDYSEARCH;

        boolean startFromOld = false;
        float probTakeClass = 0.75f;
        ArrayList<Integer> heuristics = new ArrayList<>(Arrays.asList(6));

        System.out.println(folderName);
        System.out.println(problemNum);

        for (int i = 0; i <= 11; i++) {
            heuristics = new ArrayList<>(Arrays.asList(i));
            runProblem(problemNum, folderName, folderNameIn, solverType, folderOutName, probTakeClass,
                    startFromOld, heuristics);
        }

//        runProblem(problemNum, folderName, folderNameIn, solverType, folderOutName, probTakeClass,
//                    startFromOld, heuristics);
    }

    public static void runProblem(String problemNum, String folderName, String folderNameIn,
                                  SolverType solverType,
                                  String folderOutName, float probTakeClass,
                                  boolean startFromOld, ArrayList<Integer> heuristics) {
        /**
         * Read in the input files and parse them
         */
        try {
            BufferedReader br = new BufferedReader(new FileReader("src/" + folderNameIn + "/problem" + problemNum + ".in"));
            float P = Float.parseFloat(br.readLine());
            float M = Float.parseFloat(br.readLine());
            int N = Integer.parseInt(br.readLine());
            int C = Integer.parseInt(br.readLine());

            ArrayList<Item> items = new ArrayList<>();
            ArrayList<ArrayList<Integer>> constraints = new ArrayList<>();

            String line;
            for (int i = 0; i < N; i++) {
                line = br.readLine();
                String[] components = line.split(";");
                Item item = new Item(
                        components[0].replaceAll("^ +", "").replaceAll(" +$", ""),
                        Integer.parseInt(components[1].replaceAll(" ", "")),
                        Float.parseFloat(components[2].replaceAll(" ", "")),
                        Float.parseFloat(components[3].replaceAll(" ", "")),
                        Float.parseFloat(components[4].replaceAll(" ", "")) - Float.parseFloat(components[3].replaceAll(" ", ""))
                );
                if (Float.parseFloat(components[4].replaceAll(" ", "")) - Float.parseFloat(components[3].replaceAll(" ", "")) >= 0) {
                    items.add(item);
                }
            }

            for (int i = 0; i < C; i++) {
                line = br.readLine();
                List<String> constraintList = Arrays.asList(line.split("\\s*,\\s*"));
                ArrayList<Integer> constraint = new ArrayList<>();
                for (String s : constraintList) {
                    constraint.add(Integer.parseInt(s));
                }
                constraints.add(constraint);
            }

            /**
             * Read in previous attempts at this problem
             */

            br = new BufferedReader(new FileReader("src/" + folderName + "/problem" + problemNum + ".out"));

            ArrayList<String> chosenItemNames = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                chosenItemNames.add(line.replaceAll("^ +", "").replaceAll(" +$", ""));
            }

            /**
             * Initialize the solver based on the booleans in the main method
             */

            Solve solver;

            switch (solverType) {
                case SHOTGUN:
                    solver = new Shotgun(P, M, items, constraints, chosenItemNames);
                    break;
                case IMPROVE:
                    solver = new Improve(P, M, items, constraints, chosenItemNames);
                    break;
                case GREEDYSEARCH:
                    solver = new GreedySearch(P, M, items, constraints, chosenItemNames);
                    break;
                case BESTHEURISTIC:
                    solver = new GreedySearch(P, M, items, constraints, chosenItemNames);
                    break;
                default:
                    solver = new Improve(P, M, items, constraints, chosenItemNames);
                    break;
            }

//            if (shotgun) {
//                solver = new Shotgun(P, M, items, constraints, chosenItemNames);
//            } else if (improve) {
//                solver = new Improve(P, M, items, constraints, chosenItemNames);
//            } else if (greedySearch){
//                solver = new GreedySearch(P, M, items, constraints, chosenItemNames);
//            } else {
//                solver = new GreedySearch(P, M, items, constraints, chosenItemNames);
//            }

            /**
             * Add a keyboard interrupt catch (allows me to ctrl c or the equivalent in IntelliJ
             * whenever I want and for it to save the best knapsack whenever I do
             *
             * credits to stackoverflow for this code
             */

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    Knapsack bestKnap = solver.getBestKnap();
                    Set<String> chosenItemNames = bestKnap.getNames();
                    try (PrintWriter out = new PrintWriter("src/" + folderOutName + "/problem" + problemNum + ".out")) {
                        for (String chosenItemName : chosenItemNames) {
                            out.println(chosenItemName);
                        }
                        System.out.println(bestKnap);
//                        System.out.println("Shotgunned " + solver.getShotgunTries() + " times");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            /**
             * Run the solver
             */

            System.out.println("n_items " + items.size());
            System.out.println("n_constraints " + constraints.size());
            switch (solverType) {
                case SHOTGUN:
                    System.out.println("Shotgun");
                    ((Shotgun) solver).shotgun(startFromOld);
                    break;
                case IMPROVE:
                    System.out.println("Improve");
                    ((Improve) solver).improve();
                    break;
                case GREEDYSEARCH:
                    System.out.println("GreedySearch");
                    ((GreedySearch) solver).greedySearch(heuristics, probTakeClass);
                    break;
                case BESTHEURISTIC:
                    System.out.println("GreedyHeuristicSpread");
                    ((GreedySearch) solver).bestHeuristicSpread();
                    break;
                default:
                    System.out.println("Improve");
                    ((Improve) solver).improve();
                    break;
            }


//            if (shotgun) {
//                System.out.println("Shotgun");
//                ((Shotgun) solver).shotgun(startFromOld);
//            } else if (improve) {
//                System.out.println("Improve");
//                ((Improve) solver).improve();
//            } else if (greedySearch) {
//                System.out.println("GreedySearch");
//                ((GreedySearch) solver).greedySearch(heuristics, probTakeClass);
//            } else {
//                System.out.println("GreedyHeuristicSpread");
//                ((GreedySearch) solver).bestHeuristicSpread();
//            }

            /**
             * If the solver completes, then write the results of the best knapsack
             */

            Knapsack bestKnap = solver.getBestKnap();
            Set<String> bestChosenItemNames = bestKnap.getNames();
            try (PrintWriter out = new PrintWriter("src/" + folderOutName + "/problem" + problemNum + ".out")) {
                for (String chosenItemName : bestChosenItemNames) {
                    out.println(chosenItemName);
                }
//                System.out.println("Shotgunned " + solver.getShotgunTries() + " times");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void runProblemTimed(String problemNum, String folderName, String folderNameIn,
                                  boolean shotgun, boolean improve, boolean randomGreedy, boolean greedySearch,
                                  String folderOutName, float probTakeClass,
                                  boolean startFromOld, ArrayList<Integer> heuristics) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("src/" + folderNameIn + "/problem" + problemNum + ".in"));
            float P = Float.parseFloat(br.readLine());
            float M = Float.parseFloat(br.readLine());
            int N = Integer.parseInt(br.readLine());
            int C = Integer.parseInt(br.readLine());

            ArrayList<Item> items = new ArrayList<>();
            ArrayList<ArrayList<Integer>> constraints = new ArrayList<>();

            String line;
            for (int i = 0; i < N; i++) {
                line = br.readLine();
                String[] components = line.split(";");
                Item item = new Item(
                        components[0].replaceAll("^ +", "").replaceAll(" +$", ""),
                        Integer.parseInt(components[1].replaceAll(" ", "")),
                        Float.parseFloat(components[2].replaceAll(" ", "")),
                        Float.parseFloat(components[3].replaceAll(" ", "")),
                        Float.parseFloat(components[4].replaceAll(" ", "")) - Float.parseFloat(components[3].replaceAll(" ", ""))
                );
                if (Float.parseFloat(components[4].replaceAll(" ", "")) - Float.parseFloat(components[3].replaceAll(" ", "")) >= 0) {
                    items.add(item);
                }
            }

            for (int i = 0; i < C; i++) {
                line = br.readLine();
//                System.out.println(line);
//                int start = 0;
//                ArrayList<String> constraintList = new ArrayList<>();
//                for (int j = 0; j < line.length(); j++) {
//                    if (line.charAt(j) == ',') {
//                        constraintList.add(line.substring(start, j));
//                        start = j + 1;
//                    }
//                }
//                constraintList.add(line.substring(start, line.length()));
                List<String> constraintList = Arrays.asList(line.split(","));
                ArrayList<Integer> constraint = new ArrayList<>();
                for (String s : constraintList) {
                    constraint.add(Integer.parseInt(s.replaceAll(" ", "")));
                }
                constraints.add(constraint);

            }

//            br = new BufferedReader(new FileReader("src/" + folderName + "/problem" + problemNum + ".out"));

            ArrayList<String> chosenItemNames = new ArrayList<>();
//            while ((line = br.readLine()) != null) {
//                chosenItemNames.add(line.replaceAll("^ +", "").replaceAll(" +$", ""));
//            }


            Solve solver;
            if (shotgun) {
                solver = new Shotgun(P, M, items, constraints, chosenItemNames);
            } else if (improve) {
                solver = new Improve(P, M, items, constraints, chosenItemNames);
            } else if (greedySearch || randomGreedy){
                solver = new GreedySearch(P, M, items, constraints, chosenItemNames);
            } else {
                solver = new GreedySearch(P, M, items, constraints, chosenItemNames);
            }

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    Knapsack bestKnap = solver.getBestKnap();
                    Set<String> chosenItemNames = bestKnap.getNames();
                    try (PrintWriter out = new PrintWriter("src/" + folderOutName + "/problem" + problemNum + ".out")) {
                        for (String chosenItemName : chosenItemNames) {
                            out.println(chosenItemName);
                        }
                        System.out.println(bestKnap);
//                        System.out.println("Shotgunned " + solver.getShotgunTries() + " times");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            System.out.println("here");



            final Runnable stuffToDo = new Thread() {
                @Override
                public void run() {
    /* Do stuff here. */
                    System.out.println("n_items " + items.size());
                    System.out.println("n_constraints " + constraints.size());
                    if (shotgun) {
                        System.out.println("Shotgun");
                        ((Shotgun) solver).shotgun(startFromOld);
                    } else if (improve) {
                        System.out.println("Improve");
                        ((Improve) solver).improve();
                    } else if (greedySearch) {
                        System.out.println("GreedySearch");
                        ((GreedySearch) solver).greedySearch(heuristics, probTakeClass);
                    } else {
                        System.out.println("GreedyHeursiticSpread");
                        ((GreedySearch) solver).bestHeuristicSpread();
                    }
                }
            };

            final ExecutorService executor = Executors.newSingleThreadExecutor();
            final Future future = executor.submit(stuffToDo);
            executor.shutdown(); // This does not cancel the already-scheduled task.

            try {
                future.get(2, TimeUnit.MINUTES);
            }
            catch (InterruptedException ie) {
  /* Handle the interruption. Or ignore it. */
            }
            catch (ExecutionException ee) {
  /* Handle the error. Or ignore it. */
            }
            catch (TimeoutException te) {
  /* Handle the timeout. Or ignore it. */
                System.out.println("Timed out!");
            }
            executor.shutdownNow();



            Knapsack bestKnap = solver.getBestKnap();
            Set<String> bestChosenItemNames = bestKnap.getNames();
            try (PrintWriter out = new PrintWriter("src/" + folderOutName + "/problem" + problemNum + ".out")) {
                for (String chosenItemName : bestChosenItemNames) {
                    out.println(chosenItemName);
                }
                System.out.println(bestKnap);
//                System.out.println("Shotgunned " + solver.getShotgunTries() + " times");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
