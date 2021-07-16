package lab11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class Main
{

    public static class Graph
    {
        int[][] arr;
        ArrayList<String> nodes = new ArrayList<>();


        public ArrayList<String> findNeighbours(String x)
        {
            int nodeIndex = -1;

            ArrayList<String> neighbours = new ArrayList<>();
            for (int i = 0; i < nodes.size(); i++)
            {
                if (nodes.get(i).equals(x))
                {
                    nodeIndex = i;
                    break;
                }
            }

            if (nodeIndex != -1)
            {
                for (int j = 0; j < arr[nodeIndex].length; j++)
                {
                    if (arr[nodeIndex][j] == 1)
                    {
                        neighbours.add(nodes.get(j));
                    }
                }
            }
            return neighbours;
        }

        private Map<String, Integer> strToInt = new HashMap<>();
        private Map<Integer, String> intToStr = new HashMap<>();

        private int getIndex(String str)
        {
            return strToInt.get(str);
        }

        public Graph(SortedMap<String, Document> internet)
        {
            int size = internet.size();
            arr = new int[size][size];
            int index = 0;
            for (Map.Entry<String, Document> entry : internet.entrySet())
            {
                strToInt.put(entry.getKey(), index);
                intToStr.put(index, entry.getKey());
                index++;
            }
            for (Map.Entry<String, Document> entry : internet.entrySet())
            {
                Document document = entry.getValue();
                for (Map.Entry<String, Link> link : document.link.entrySet())
                {
                    int x = getIndex(entry.getKey());
                    int y = getIndex(link.getKey());
                    arr[x][y] = link.getValue().weight;
                }
                nodes.add(entry.getKey());
            }

        }

        private String DijkstraSSSP(String startVertexStr)
        {
            int size = arr.length;

            if (!strToInt.containsKey(startVertexStr))
            {
                return null;
            }
            int startVertex = strToInt.get(startVertexStr);


            int[] shortest = new int[size];


            boolean[] added = new boolean[size];


            for (int vertexIndex = 0; vertexIndex < size; vertexIndex++)
            {
                shortest[vertexIndex] = Integer.MAX_VALUE;
                added[vertexIndex] = false;
            }


            shortest[startVertex] = 0;


            int[] parents = new int[size];


            parents[startVertex] = -1;


            for (int i = 1; i < size; i++)
            {


                int nearest = -1;
                int shortestDist = Integer.MAX_VALUE;
                for (int vertexIndex = 0; vertexIndex < size; vertexIndex++)
                {
                    if (!added[vertexIndex] && shortest[vertexIndex] < shortestDist)
                    {
                        nearest = vertexIndex;
                        shortestDist = shortest[vertexIndex];
                    }
                }
                if (nearest == -1)
                {
                    continue;
                }

                added[nearest] = true;

                for (int vertexIndex = 0; vertexIndex < size; vertexIndex++)
                {
                    int edgeDist = arr[nearest][vertexIndex];

                    if (edgeDist > 0 && ((shortestDist + edgeDist) < shortest[vertexIndex]))
                    {
                        parents[vertexIndex] = nearest;
                        shortest[vertexIndex] = shortestDist + edgeDist;
                    }
                }
            }

            return printResult(shortest, parents);
        }

        private String printResult(int[] distances, int[] parents)
        {
            int nVertices = distances.length;
            StringBuilder result = new StringBuilder();

            for (int vertexIndex = 0; vertexIndex < nVertices; vertexIndex++)
            {
                int dist = distances[vertexIndex];
                String vertexValue = intToStr.get(vertexIndex);
                if (dist == Integer.MAX_VALUE)
                {
                    result.append("no path to ").append(vertexValue).append('\n');
                }
                else
                {
                    printVertexPath(vertexIndex, parents, result);
                    result.setLength(result.length() - 2);
                    result.append("=").append(dist);
                    result.append('\n');
                }
            }
            return result.toString();
        }

        private void printVertexPath(int currentVertex, int[] parents, StringBuilder result)
        {

            if (currentVertex == -1)
            {
                return;
            }
            printVertexPath(parents[currentVertex], parents, result);
            result.append(intToStr.get(currentVertex)).append("->");
        }


        public String bfs(String start)
        {
            if (!strToInt.containsKey(start))
            {
                return null;
            }

            StringBuilder result = new StringBuilder();
            Queue<String> queue = new LinkedList<>();
            Set<String> visited = new HashSet<>();
            queue.add(start);
            visited.add(start);
            while (!queue.isEmpty())
            {
                String element = queue.remove();
                result.append(element).append(", ");
                ArrayList<String> neighbours = findNeighbours(element);
                for (int i = 0; i < neighbours.size(); i++)
                {
                    String n = neighbours.get(i);
                    if (n != null && !visited.contains(n))
                    {
                        queue.add(n);
                        visited.add(n);
                    }
                }

            }
            if (result.length() > 0)
            {
                return result.substring(0, result.length() - 2).trim();
            }
            return null;
        }

        private void dfs(StringBuilder builder, Set<String> visited, String node)
        {

            builder.append(node).append(", ");
            ArrayList<String> neighbours = findNeighbours(node);
            visited.add(node);
            for (int i = 0; i < neighbours.size(); i++)
            {
                String n = neighbours.get(i);
                if (n != null && !visited.contains(n))
                {
                    dfs(builder, visited, n);
                }
            }
        }

        public String dfs(String start)
        {
            if (!strToInt.containsKey(start))
            {
                return null;
            }
            StringBuilder result = new StringBuilder();
            dfs(result, new HashSet<>(), start);
            if (result.length() > 0)
            {
                return result.substring(0, result.length() - 2).trim();
            }
            return null;
        }

        public int connectedComponents()
        {
            DisjointSetForest deepForest = new DisjointSetForest(arr.length);
            for (int i = 0; i < arr.length; i++)
            {
                deepForest.makeSet(i);
            }
            for (int x = 0; x < arr.length; x++)
            {
                for (int y = 0; y < arr.length; y++)
                {
                    if (arr[x][y] == 1)
                    {
                        if (deepForest.findSet(x) != deepForest.findSet(y))
                        {
                            deepForest.union(x, y);
                        }
                    }
                }
            }
            return deepForest.countSets();
        }
    }

    public interface IWithName
    {
        String getName();
    }

    // in the constructor there has to be the number
//elements N
// the elements are integer values from 0 to N-1
    public interface DisjointSetDataStructure
    {
        void makeSet(int item);

        int findSet(int item);

        boolean union(int itemA, int itemB);

        int countSets();
    }

    private static class DisjointSetForest implements DisjointSetDataStructure
    {
        int count;

        private class Element
        {
            int rank;
            int parent;
        }

        Element[] arr;

        // Constructor
        public DisjointSetForest(int n)
        {
            arr = new Element[n];
        }

        @Override
        public void makeSet(int x)
        {
            Element e = new Element();
            e.parent = x;
            e.rank = 0;
            arr[x] = e;
            count++;
        }

        private void link(int x, int y)
        {
            Element eX = arr[x];
            Element eY = arr[y];
            if (eX.rank > eY.rank)
            {
                eY.parent = x;
            }
            else
            {
                eX.parent = y;
                if (eX.rank == eY.rank)
                {
                    eY.rank++;
                }
            }


        }

        public int findSet(int x)
        {
            Element e = arr[x];
            if (x != e.parent)
            {
                e.parent = findSet(e.parent);
            }
            return e.parent;
        }

        // Unites the set that includes x and the set
        // that includes x
        @Override
        public boolean union(int x, int y)
        {
            int setX = findSet(x);
            int setY = findSet(y);
            if (setX == setY)
            {
                return false;
            }
            link(setX, setY);
            return true;
        }

        @Override
        public int countSets()
        {
            Set<Integer> uniques = new HashSet<>();
            for (int i = 0; i < arr.length; i++)
            {
                uniques.add(arr[i].parent);
            }
            return uniques.size();
        }

        public String toString()
        {
            StringBuilder result = new StringBuilder("Disjoint sets as forest:\n");
            for (int i = 0; i < arr.length; i++)
            {
                Element e = arr[i];
                result.append(i).append(" -> ").append(e.parent);
                if (i < arr.length - 1)
                {
                    result.append("\n");
                }
            }
            return result.toString();
        }
    }


    public static class Link implements Comparable<Link>
    {
        public String ref;
        public int weight;

        public Link(String ref)
        {
            this.ref = ref;
            weight = 1;
        }

        public Link(String ref, int weight)
        {
            this.ref = ref;
            this.weight = weight;
        }

        @Override
        public boolean equals(Object obj)
        {
            return ((Link) obj).ref.equalsIgnoreCase(ref);
        }

        @Override
        public String toString()
        {
            return ref + "(" + weight + ")";
        }

        @Override
        public int compareTo(Link another)
        {
            return ref.compareTo(another.ref);
        }
    }

    public static class Document implements IWithName
    {
        public String name;
        public SortedMap<String, Link> link;

        public Document(String name)
        {
            this.name = name.toLowerCase();
            link = new TreeMap<>();
        }

        public Document(String name, Scanner scan)
        {
            this.name = name.toLowerCase();
            link = new TreeMap<>();
            load(scan);
        }

        public void load(Scanner scan)
        {
            String marker = "link=";
            String endMarker = "eod";
            String line = scan.nextLine().toLowerCase();
            while (!line.equalsIgnoreCase(endMarker))
            {
                String arr[] = line.split(" ");
                for (String word : arr)
                {
                    if (word.startsWith(marker))
                    {
                        String linkStr = word.substring(marker.length());
                        Link l;
                        if ((l = createLink(linkStr)) != null)
                        {
                            link.put(l.ref, l);
                        }
                    }

                }
                line = scan.nextLine().toLowerCase();
            }

        }

        public static boolean isCorrectId(String id)
        {
            id = id.toLowerCase();
            if (id.length() == 0)
            {
                return false;
            }
            if (id.charAt(0) < 'a' || id.charAt(0) > 'z')
            {
                return false;
            }
            for (int i = 1; i < id.length(); i++)
            {
                if (!(id.charAt(i) >= 'a' && id.charAt(i) <= 'z' || id.charAt(i) >= '0' && id.charAt(i) <= '9' || id.charAt(i) == '_'))
                {
                    return false;
                }
            }
            return true;
        }


        private static Link createIdAndNumber(String id, int n)
        {
            if (!isCorrectId(id))
            {
                return null;
            }
            return new Link(id.toLowerCase(), n);
        }

        // accepted only small letters, capitalic letter, digits nad '_' (but not on the begin)
        public static Link createLink(String link)
        {
            if (link.length() == 0)
            {
                return null;
            }
            int openBracket = link.indexOf('(');
            int closeBracket = link.indexOf(')');
            if (openBracket > 0 && closeBracket > openBracket && closeBracket == link.length() - 1)
            {
                String strNumber = link.substring(openBracket + 1, closeBracket);
                try
                {
                    int number = Integer.parseInt(strNumber);
                    if (number < 1)
                    {
                        return null;
                    }
                    return createIdAndNumber(link.substring(0, openBracket), number);
                }
                catch (NumberFormatException ex)
                {
                    return null;
                }
            }
            return createIdAndNumber(link, 1);
        }

        @Override
        public String toString()
        {
            String retStr = "Document: " + name + "\n ";
            retStr += link;
            return retStr;
        }

        @Override
        public int hashCode()
        {
            return name.hashCode();
        }

        @Override
        public String getName()
        {
            return name;
        }

    }

    static Scanner scan; // for input stream

    //static String input = "#Test for Lab11\n" + "ld x\n" + "link=y(9)\n" + "eod\n" + "ld y\n" + "eod\n" + "ld a\n" + "link=b(2)\n" + "link=c(5)\n" + "eod\n" + "ld c\n" + "link=d(5)\n" + "link=f(6)\n" + "eod\n" + "ld b\n" + "link=d(3)\n" + "link=e(4)\n" + "eod\n" + "ld e\n" + "link=d(3)\n" + "link=f(4)\n" + "link=h(2)\n" + "link=g(8)\n" + "eod\n" + "ld d\n" + "link=e(3)\n" + "link=f(1)\n" + "eod\n" + "ld h\n" + "link=e(2)\n" + "link=g(1)\n" + "eod\n" + "ld g\n" + "link=h(1)\n" + "link=f(7)\n" + "eod\n" + "ld f\n" + "link=d(1)\n" + "link=e(4)\n" + "link=g(7)\n" + "eod\n" + "sssp a\n" + "sssp d\n" + "sssp w\n" + "ha";

    public static void main(String[] args)
    {
        System.out.println("START");
        scan = new Scanner(System.in);
        //scan = new Scanner(input);
        SortedMap<String, Document> sortedMap = new TreeMap<>();
        Document currentDoc = null;
        boolean halt = false;
        while (!halt)
        {
            String line = scan.nextLine();
            // empty line and comment line - read next line
            if (line.length() == 0 || line.charAt(0) == '#')
            {
                continue;
            }
            // copy line to output (it is easier to find a place of a mistake)
            System.out.println("!" + line);
            String word[] = line.split(" ");
            //getdoc name - change document to name
            if (word[0].equalsIgnoreCase("getdoc") && word.length == 2)
            {
                currentDoc = (Document) sortedMap.get(word[1]);
                continue;
            }

            // ld documentName
            if (word[0].equalsIgnoreCase("ld") && word.length == 2)
            {
                if (Document.isCorrectId(word[1]))
                {
                    currentDoc = new Document(word[1], scan);
                    sortedMap.put(currentDoc.name, currentDoc);
                }
                else
                {
                    System.out.println("incorrect ID");
                }
                continue;
            }
            // ha
            if (word[0].equalsIgnoreCase("ha") && word.length == 1)
            {
                halt = true;
                continue;
            }
            // clear
            if (word[0].equalsIgnoreCase("clear") && word.length == 1)
            {
                if (currentDoc != null)
                {
                    currentDoc.link.clear();
                }
                else
                {
                    System.out.println("no current document");
                }
                continue;
            }
            // show
            // it depends of the representation so it will be NOT in tests
            if (word[0].equalsIgnoreCase("show") && word.length == 1)
            {
                if (currentDoc != null)
                {
                    System.out.println(currentDoc.toString());
                }
                else
                {
                    System.out.println("no current document");
                }
                continue;
            }
            // size
            if (word[0].equalsIgnoreCase("size") && word.length == 1)
            {
                if (currentDoc != null)
                {
                    System.out.println(currentDoc.link.size());
                }
                else
                {
                    System.out.println("no current document");
                }
                continue;
            }
            // add str
            if (word[0].equalsIgnoreCase("add") && word.length == 2)
            {
                if (currentDoc != null)
                {
                    Link link = Document.createLink(word[1]);
                    if (link == null)
                    {
                        System.out.println("error");
                    }
                    else
                    {
                        currentDoc.link.put(link.ref, link);
                        System.out.println("true");
                    }
                }
                else
                {
                    System.out.println("no current document");
                }
                continue;
            }
            // get str
            if (word[0].equalsIgnoreCase("get") && word.length == 2)
            {
                if (currentDoc != null)
                {
                    Link l = currentDoc.link.get(word[1]);
                    if (l != null)
                    {
                        System.out.println(l);
                    }
                    else
                    {
                        System.out.println("error");
                    }
                }
                else
                {
                    System.out.println("no current document");
                }
                continue;
            }
            // rem str
            if (word[0].equalsIgnoreCase("rem") && word.length == 2)
            {
                if (currentDoc != null)
                {
                    Link l = currentDoc.link.remove(word[1]);
                    if (l != null)
                    {
                        // write the removed link
                        System.out.println(l);
                    }
                    else
                    {
                        System.out.println("error");
                    }
                }
                else
                {
                    System.out.println("no current document");
                }

                continue;
            }

            // bfs str
            if (word[0].equalsIgnoreCase("bfs") && word.length == 2)
            {
                Graph graph = new Graph(sortedMap);
                String str = graph.bfs(word[1]);
                if (str != null)
                {
                    System.out.println(str);
                }
                else
                {
                    System.out.println("error");
                }
                continue;
            }
            // dfs str
            if (word[0].equalsIgnoreCase("dfs") && word.length == 2)
            {
                Graph graph = new Graph(sortedMap);
                String str = graph.dfs(word[1]);
                if (str != null)
                {
                    System.out.println(str);
                }
                else
                {
                    System.out.println("error");
                }
                continue;
            }
            // cc
            if (word[0].equalsIgnoreCase("cc") && word.length == 1)
            {
                Graph graph = new Graph(sortedMap);
                int str = graph.connectedComponents();
                System.out.println(str);
                continue;
            }
            // graph
            if (word[0].equalsIgnoreCase("graph") && word.length == 1)
            {
                Graph graph = new Graph(sortedMap);
                System.out.println(graph);
                continue;
            }
            // sssp str
            if (word[0].equalsIgnoreCase("sssp") && word.length == 2)
            {
                Graph graph = new Graph(sortedMap);
                String str = graph.DijkstraSSSP(word[1]);

                if (str != null)
                {
                    System.out.print(str);
                }
                else
                {
                    System.out.println("error");
                }
                continue;
            }
            System.out.println("Wrong command");
        }
        System.out.println("END OF EXECUTION");
        scan.close();
    }

}