import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.*;
//Tests version 1 against version 3 with varying grid sizes 

public class TestVer13Grid
{
    // next four constants are relevant to parsing
    public static final int TOKENS_PER_LINE  = 7;
    public static final int POPULATION_INDEX = 4; // zero-based indices
    public static final int LATITUDE_INDEX   = 5;
    public static final int LONGITUDE_INDEX  = 6;
    private static int west, east, north, south;
    private float westL, eastL, northL, southL;
    private static int x;
    private static int y;
    private static int choice;
    private int poprect;
    private double poptotal;
    private Grid finalGrid;
    public GridItem[][] g;
    private boolean first = true;
    private double poptotalfinal;

    // parse the input file into a large array held in a CensusData object
    public static CensusData parse(String filename) {
        CensusData result = new CensusData();
        
        try {
            BufferedReader fileIn = new BufferedReader(new FileReader(filename));
            
            // Skip the first line of the file
            // After that each line has 7 comma-separated numbers (see constants above)
            // We want to skip the first 4, the 5th is the population (an int)
            // and the 6th and 7th are latitude and longitude (floats)
            // If the population is 0, then the line has latitude and longitude of +.,-.
            // which cannot be parsed as floats, so that's a special case
            //   (we could fix this, but noisy data is a fact of life, more fun
            //    to process the real data as provided by the government)
            
            String oneLine = fileIn.readLine(); // skip the first line

            // read each subsequent line and add relevant data to a big array
            while ((oneLine = fileIn.readLine()) != null) {
                String[] tokens = oneLine.split(",");
                if(tokens.length != TOKENS_PER_LINE)
                    throw new NumberFormatException();
                int population = Integer.parseInt(tokens[POPULATION_INDEX]);
                if(population != 0)
                    result.add(population,
                               Float.parseFloat(tokens[LATITUDE_INDEX]),
                               Float.parseFloat(tokens[LONGITUDE_INDEX]));
            }

            fileIn.close();
        } catch(IOException ioe) {
            System.err.println("Error opening/reading/writing input or output file.");
            System.exit(1);
        } catch(NumberFormatException nfe) {
            System.err.println(nfe.toString());
            System.err.println("Error in file format");
            System.exit(1);
        }
        return result;
    }

    private float[] findMaxMin(CensusData cd)
    {
        float maxLat = Math.abs(cd.data[0].latitude);
        float minLat = Math.abs(cd.data[0].latitude);
        float maxLng = Math.abs(cd.data[0].longitude);
        float minLng = Math.abs(cd.data[0].longitude);

        for(int i = 0; i < cd.data_size; i++)
        {
            maxLat = Math.max(maxLat, Math.abs(cd.data[i].latitude));
            minLat = Math.min(minLat, Math.abs(cd.data[i].latitude));
            maxLng = Math.max(maxLng, Math.abs(cd.data[i].longitude));
            minLng = Math.min(minLng, Math.abs(cd.data[i].longitude));

        }
        float [] m = {maxLat, minLat, maxLng, minLng};
        return m;
    }

    // argument 1: file name for input data: pass this to parse
    // argument 2: number of x-dimension buckets
    // argument 3: number of y-dimension buckets
    // argument 4: -v1, -v2, -v3, -v4, or -v5
    public static void main(String[] args) 
    {
        int [][] pop3 = new int [100][100];
        TestVer13Grid pq = new TestVer13Grid();
        CensusData cd = parse("CenPop2010.txt");
        x= 100;
        y = 100;
        for(int i = 0; i < 100; i++)
        {
            west = (int)(Math.random() *50);
            east = (int)(Math.random() *(50)+(50));
            north = (int)(Math.random()*50);
            south = (int)(Math.random()*(50)+(50));
            pq.v1(cd);
            pq.v3(cd);
        }
        long startTime = 0;
        long endTime = 0;
        long preprocessTime = 0;
        long sumTime = 0;
        int queries = 0;
        for(int i = 500; i <= 10000; i += 500)
        {
            x = i;
            y = i;
            pop3 = new int [x][y];
            queries = 0;
            preprocessTime = 0;
            sumTime = 0;
            west = (int)(Math.random() *(x/2));
            east = (int)(Math.random() *(x/2)+(x/2));
            north = (int)(Math.random()*(x/2));
            south = (int)(Math.random()*(x/2)+(x/2));
            startTime = System.currentTimeMillis();
            pop3 = pq.v3(cd);
            endTime = System.currentTimeMillis();
            preprocessTime = endTime-startTime;
            do
            {
                startTime = System.currentTimeMillis();
                pq.v1(cd);
                endTime = System.currentTimeMillis();
                sumTime += (endTime - startTime);
                queries ++;
            }while(sumTime < preprocessTime);
            System.out.println(queries);
            //System.out.println(Math.pow(i,2));
        }
    }

    private void v1(CensusData cd)
    {
        poprect = 0;
        poptotal = 0;
        float[] maxMin = findMaxMin(cd);
        //finding the latitude/longitude boundaries for given row/columns
        westL = maxMin[2] - ((maxMin[2]-maxMin[3])/x)*(west-1);
        eastL = maxMin[2] - ((maxMin[2]-maxMin[3])/x)*east;
        northL = maxMin[0] -((maxMin[0]-maxMin[1])/y)*(north-1);
        southL =  maxMin[0] -((maxMin[0]-maxMin[1])/y)*south;
        float lt, lg;

        for(int i = 0; i < cd.data_size; i++)
        {
            lt = Math.abs(cd.data[i].latitude);
            lg = Math.abs(cd.data[i].longitude);
            if(lg < westL && lg > eastL && lt < northL && lt > southL)
            {
                poprect += cd.data[i].population;
            }
            poptotal += cd.data[i].population;
        }
    }

    private int[][] v3(CensusData cd)
    {
        poprect = 0;
        poptotal = 0;
        int orig, sub1, sub2, add;
        sub1 = 0;
        sub2 = 0;
        add = 0;

        Rectangle rect = new ForkJoinPool().commonPool().invoke(new Ver2Corners(cd.data, 0, cd.data_size));// finding corners
        //finding the latitude/longitude boundaries for given row/columns
        westL = rect.left - ((rect.left-rect.right)/x)*west;
        eastL = rect.left - ((rect.left-rect.right)/x)*east;
        northL = rect.top-((rect.top-rect.bottom)/y)*north;
        southL =  rect.top -((rect.top-rect.bottom)/y)*south;
        float rowStep = (rect.top - rect.bottom)/x;
        float colStep = (rect.left - rect.right)/y;
        int [][] pop = new int[x][y];
        int row, col;

        for(int i = 0; i < cd.data_size; i++)//making x - y grid
        {
            row = x - 1 - (int)((Math.abs(cd.data[i].latitude) - Math.abs(rect.bottom))/rowStep);
            if(cd.data[i].latitude == rect.top)
                row = 0;
            col = y - 1 - (int)((Math.abs(cd.data[i].longitude) - Math.abs(rect.right))/colStep);
            if(Math.abs(cd.data[i].longitude) == rect.left)
                col = 0;
            pop[row][col] += cd.data[i].population;
            poptotal += cd.data[i].population;
        }

       pop = reGrid(pop);
       poptotalfinal = poptotal;
       //calculating total pop in query 
       v3Process(pop);
       return pop;
    }

    private void v3Process(int [][] pop)
    {
        int orig, sub1, sub2, add;
        sub1 = 0;
        sub2 = 0;
        add = 0;
        if(south != 0 && east != 0)
            orig = pop[south-1][east-1];
        else 
            orig = 0;
        if(north -2 >= 0)
            sub1 = pop[north-2][east-1];
        if(west -2 >= 0)
            sub2 = pop[south-1][west-2];
        if(north -2 >=0 && west -2 >= 0)
            add = pop[north-2][west-2];
        poprect = orig - sub1 - sub2 + add;
    }

    private int [][] reGrid(int [][] pop)//making the smarter grid
    {
         for(int r = 0; r < x; r++)
        {
            for(int c = 0; c < y; c++)
            {
                if(r > 0)
                    pop[r][c] += pop[r-1][c];
                if(c > 0)
                    pop[r][c] += pop[r][c-1];
                if(r > 0 && c> 0)
                    pop[r][c] -= pop[r-1][c-1];     
            }
        }
        return pop;
    }
      class Ver2Corners extends RecursiveTask<Rectangle>//finding corners
    {
        private CensusGroup[] cg;
        private final int THRESHOLD = 50000;
        private int start, end;
        public Ver2Corners(CensusGroup[] cg, int start, int end)
        {
            this.cg = cg;
            this.start = start;
            this.end = end;
        }
        public Rectangle compute()
        {
            if(end - start < THRESHOLD)
            {
                return findMaxMin2(cg);
            }
            else
            {
                int mid = start + (end - start) / 2;
                Ver2Corners lower = new Ver2Corners(cg, start, mid);
                Ver2Corners upper = new Ver2Corners(cg, mid, end);
                lower.fork();
                Rectangle low = lower.join();
                Rectangle up = upper.compute();
                return new Rectangle(Math.max(low.left, up.left), Math.min(low.right, up.right), 
                    Math.max(low.top, up.top), Math.min(low.bottom, up.bottom));
            }
        }

        public Rectangle findMaxMin2(CensusGroup[] cg)//finding max and min lat long of us
        {
            float maxLat = Math.abs(cg[start].latitude);
            float minLat = Math.abs(cg[start].latitude);
            float maxLng = Math.abs(cg[start].longitude);
            float minLng = Math.abs(cg[start].longitude);

            for(int i = start; i < end; i++)
            {
                maxLat = Math.max(maxLat, Math.abs(cg[i].latitude));
                minLat = Math.min(minLat, Math.abs(cg[i].latitude));
                maxLng = Math.max(maxLng, Math.abs(cg[i].longitude));
                minLng = Math.min(minLng, Math.abs(cg[i].longitude));

            }
            return new Rectangle(maxLng, minLng, maxLat, minLat);
        }

    }
}
