
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.*;

public class PopulationQueryTest4 //tests version 4 compared to version 5 
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
    private int poprect;
    private double poptotal;
    private Grid finalGrid;
    public GridItem[][] g;

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
        PopulationQueryTest4 pq = new PopulationQueryTest4();
        CensusData cd = parse("CenPop2010.txt");
        x = 500;
        y = 500;
        int lowerb = (int)(Math.random()*x);
        west = (int)(Math.random() *lowerb);
        east = (int)(Math.random() *(x-lowerb)+(lowerb));
        north = (int)(Math.random()*lowerb);
        south = (int)(Math.random()*(x -lowerb)+(lowerb));
        for(int i = 0; i < 30; i++)
        {
            lowerb = (int)(Math.random()*x);
            west = (int)(Math.random() *lowerb);
            east = (int)(Math.random() *(x-lowerb)+(lowerb));
            north = (int)(Math.random()*lowerb);
            south = (int)(Math.random()*(x -lowerb)+(lowerb));
            pq.v4(cd);
        }
        System.out.println("Ver. 4");
        long startTime = 0;
        long endTime = 0;
        long sumTime = 0;
        x = 1; 
        y = 1;
        lowerb = 5;
        int inc10 = 1;
        west = (int)(Math.random() *lowerb+1);
        east = (int)(Math.random() *(lowerb -1)+(lowerb +1));
        north = (int)(Math.random()*lowerb+1);
        south = (int)(Math.random()*(lowerb -1)+(lowerb +1));
        for(int i = 1; i <= 3; i++)
        {
            inc10 = inc10 *10;
            for(int b = 1; b < 10; b++)
            {
                x = b*inc10; 
                y = b*inc10;
                /*lowerb = (int)(Math.random()*x);
                west = (int)(Math.random() *lowerb);
                east = (int)(Math.random() *(x-lowerb)+(lowerb));
                north = (int)(Math.random()*lowerb);
                south = (int)(Math.random()*(x -lowerb)+(lowerb));*/
                
                for(int a = 0; a < 20; a++)
                {
                    lowerb = (int)(Math.random()*x);
                    west = (int)(Math.random() *lowerb);
                    east = (int)(Math.random() *(x-lowerb)+(lowerb));
                    north = (int)(Math.random()*lowerb);
                    south = (int)(Math.random()*(x -lowerb)+(lowerb));
                    startTime = System.currentTimeMillis();
                    pq.v4(cd);
                    endTime = System.currentTimeMillis();
                    sumTime += (endTime - startTime);
                }
                
                System.out.println((sumTime)/20.0 );
                if(i == 3 && b == 7)
                    b = 11;
            }
        }
        System.out.println("Ver. 5 ");
        startTime = 0;
        endTime = 0;
        sumTime = 0;
        x = 1; 
        y = 1;
        lowerb = 5;
        inc10 = 1;
        west = (int)(Math.random() *lowerb+1);
        east = (int)(Math.random() *(lowerb -1)+(lowerb +1));
        north = (int)(Math.random()*lowerb+1);
        south = (int)(Math.random()*(lowerb -1)+(lowerb +1));
        for(int i = 1; i <= 2; i++)
        {
            inc10 = inc10 *10;
            for(int b = 1; b < 10; b++)
            {
                x = b*inc10; 
                y = b*inc10;
                /*lowerb = (int)(Math.random()*x);
                west = (int)(Math.random() *lowerb);
                east = (int)(Math.random() *(x-lowerb)+(lowerb));
                north = (int)(Math.random()*lowerb);
                south = (int)(Math.random()*(x -lowerb)+(lowerb));*/
                
                for(int a = 0; a < 20; a++)
                {
                    lowerb = (int)(Math.random()*x);
                    west = (int)(Math.random() *lowerb);
                    east = (int)(Math.random() *(x-lowerb)+(lowerb));
                    north = (int)(Math.random()*lowerb);
                    south = (int)(Math.random()*(x -lowerb)+(lowerb));
                    startTime = System.currentTimeMillis();
                    pq.v5(cd);
                    endTime = System.currentTimeMillis();
                    sumTime += (endTime - startTime);
                }
                
                System.out.println((sumTime)/20.0 );
                if(i == 3 && b == 7)
                    b = 11;
            }
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

    private void v2(CensusData cd)
    {
        poprect = 0;
        poptotal = 0;
        Ver2Corners v = new Ver2Corners(cd.data, 0, cd.data_size);//finding corners
        Rectangle r = new ForkJoinPool().commonPool().invoke(v);
        //finding the latitude/longitude boundaries for given row/columns
        westL = r.left - ((r.left-r.right)/x)*(west-1);
        eastL = r.left - ((r.left-r.right)/x)*east;
        northL = r.top-((r.top-r.bottom)/y)*(north-1);
        southL =  r.top -((r.top-r.bottom)/y)*south;
        Ver2Pop p = new Ver2Pop(cd.data, 0, cd.data_size);
        Integer pop = new ForkJoinPool().commonPool().invoke(p);//adding together all of the population within rectangle query
    }

    private void v3(CensusData cd)
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

       //calculating total pop in query 
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

    private void v4(CensusData cd)
    {
        poprect = 0;
        poptotal = 0;
        int orig, sub1, sub2, add;
        sub1 = 0;
        sub2 = 0;
        add = 0;
        int[][] pop = new int[x][y];
        finalGrid = new Grid(pop, x, y);
        GridStarter gs;
        Rectangle rect = new ForkJoinPool().commonPool().invoke(new Ver2Corners(cd.data, 0, cd.data_size));// finding corners
        //finding the latitude/longitude boundaries for given row/columns
        westL = rect.left - ((rect.left-rect.right)/x)*west;
        eastL = rect.left - ((rect.left-rect.right)/x)*east;
        northL = rect.top-((rect.top-rect.bottom)/y)*north;
        southL =  rect.top -((rect.top-rect.bottom)/y)*south;
        float rowStep = (rect.top - rect.bottom)/x;
        float colStep = (rect.left - rect.right)/y;
        gs =new GridStarter(cd.data, rect, rowStep, colStep);
        finalGrid = new ForkJoinPool().commonPool().invoke(new Ver4Grid(gs, 0, cd.data_size));
        pop = reGrid(finalGrid.getGrid());
       //calculating total pop in query 
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
       /* System.out.println("\nThe population of the rectangle is " + poprect + " and the total population is " + poptotal);
        System.out.printf("The percentage of the population that the rectangle represents is %5.2f\n", (poprect/poptotal*100) );*/

    }

    private void v5(CensusData cd)
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
        GridStarter gs = new GridStarter(cd.data, rect, rowStep, colStep);
        //grid stuff..
        g = new GridItem[x][y];

        for(int i =0; i < x; i++)
        {
            for(int a = 0; a < y; a++)
            {
                g[i][a] = new GridItem(0);
            }
        }
        //Ver5Grid v5 = new Ver5Grid(g, 0, cd.data_size, cd.data_size, gs);
        Ver5Grid [] v5g = new Ver5Grid[4];
        for(int i = 0; i < 4; i++)
        {
            v5g[i] = new Ver5Grid( i*(cd.data_size/4), (i+1)*(cd.data_size/4), cd.data_size, gs);
            v5g[i].start();
        }
        for(int i = 0; i < 4; i++)
        {
            try
            {
                v5g[i].join();
            }
            catch (InterruptedException ex) 
            {
            }
        }
        reGridItem();
        poptotal = g[x-1][y-1].getPop();
      
       //calculating total pop in query 
        if(south != 0 && east != 0)
            orig = g[south-1][east-1].getPop();
        else 
            orig = 0;
        if(north -2 >= 0)
            sub1 = g[north-2][east-1].getPop();
        if(west -2 >= 0)
            sub2 = g[south-1][west-2].getPop();
        if(north -2 >=0 && west -2 >= 0)
            add = g[north-2][west-2].getPop();
        poprect = orig - sub1 - sub2 + add;

    }

    public class Ver5Grid extends Thread
    {
        //private GridItem[][] grid;
        private int start, end, size;
        private GridStarter gs;

        public Ver5Grid(int s, int e, int size, GridStarter gs)
        {
            //grid = g;
            start = s;
            end = e;
            this.size = size;
            this.gs = gs;
        }

        public void run()
        {
            lockGrid(start, end, gs);
        }
        

    }

    private void lockGrid(int start, int end, GridStarter gs)
    {
        int row = 0;
        int col = 0;
        for(int i = start; i < end; i++)
        {
            row = x - 1 - (int)((Math.abs(gs.getCG()[i].latitude) - Math.abs(gs.getRect().bottom))/gs.getRowStep());
            if(gs.getCG()[i].latitude == gs.getRect().top)
                row = 0;
            col = y - 1 - (int)((Math.abs(gs.getCG()[i].longitude) - Math.abs(gs.getRect().right))/gs.getColStep());
            if(Math.abs(gs.getCG()[i].longitude) == gs.getRect().left)
                col = 0;
                g[row][col].getLock().lock();
            try
            {
                g[row][col].addPop(gs.getCG()[i].population);
            }
            finally
            {
                g[row][col].getLock().unlock();
            }
        }
    }

    private void reGridItem()//making the smarter grid
    {
         for(int r = 0; r < x; r++)
        {
            for(int c = 0; c < y; c++)
            {
                if(g[r][c] == null)
                    g[r][c] = new GridItem(0);
                else
                {
                    if(r > 0 && g[r-1][c] != null)
                        g[r][c].addPop(g[r-1][c].getPop());
                    if(c > 0 && g[r][c-1] != null)
                        g[r][c].addPop(g[r][c-1].getPop());
                    if(r > 0 && c> 0 && g[r-1][c-1] != null)
                        g[r][c].subPop(g[r-1][c-1].getPop());  
                }  
            }
        }
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

    class Ver2Pop extends RecursiveTask<Integer>//adds together populations of query and total
    {
        private CensusGroup[] cg;
        private final int THRESHOLD = 50000;
       private int start, end;
        public Ver2Pop(CensusGroup[] cg, int start, int end)
        {
            this.cg = cg;
            this.start = start;
            this.end = end;
        }
        public Integer compute()
        {
            if(end - start < THRESHOLD)
            {
                return findPop();
            }
            else
            {
                int mid = start + (end - start) / 2;
                Ver2Pop lower = new Ver2Pop(cg, start, mid);
                Ver2Pop upper = new Ver2Pop(cg, mid, end);
                lower.fork();
                return Integer.valueOf(lower.join().intValue()+ upper.compute().intValue());
            }
        }

        public Integer findPop()
        {
            for(int i = start; i < end; i++)
            {
                float lt, lg;
                lt = Math.abs(cg[i].latitude);
                lg = Math.abs(cg[i].longitude);
                if(lg < westL && lg > eastL && lt < northL && lt > southL)
                {
                    poprect += cg[i].population;
                }
                poptotal += cg[i].population;
            }
            return Integer.valueOf(poprect);
        }

    }

    class Ver4Grid extends RecursiveTask<Grid>
    {
        private final int THRESHOLD = 50000;
        private int start, end;
        GridStarter gs;
    
        public Ver4Grid(GridStarter gs, int start, int end)
        {
            this.start = start;
            this.end = end;
            this.gs = gs;
        }
        public Grid compute()
        {
            if(end - start < THRESHOLD)
            {
                return(makeGrid(gs, start, end));
            }
            else
            {
                int mid = start + (end - start) / 2;
                Ver4Grid lower = new Ver4Grid(gs, start, mid);
                Ver4Grid upper = new Ver4Grid(gs, mid, end);
                lower.fork();
                Grid low = lower.join();
                Grid up = upper.compute();
                return add(low, up);     
            }
        }
    }
    private Grid makeGrid(GridStarter gs, int start, int end)
    {
        int[][] pop = new int[x][y];
        int row, col;
        for(int i = start; i < end; i++)//making x - y grid
        {
            row = x - 1 - (int)((Math.abs(gs.getCG()[i].latitude) - Math.abs(gs.getRect().bottom))/gs.getRowStep());
            if(gs.getCG()[i].latitude == gs.getRect().top)
                row = 0;
            col = y - 1 - (int)((Math.abs(gs.getCG()[i].longitude) - Math.abs(gs.getRect().right))/gs.getColStep());
            if(Math.abs(gs.getCG()[i].longitude) == gs.getRect().left)
                col = 0;
            pop[row][col] += gs.getCG()[i].population;
            poptotal += gs.getCG()[i].population;
        }

        return new Grid(pop, x, y);
    }

    public Grid add(Grid g1, Grid g2)
    {
        int[][] arrG1 = g1.getGrid();
        int[][] arrG2 = g2.getGrid();
        for(int r = 0; r < g1.getRow(); r++)
            for(int c = 0; c < g1.getCol(); c++)
                arrG1[r][c] += arrG2[r][c];
        return new Grid(arrG1, g1.getRow(), g1.getCol());

    }
    


}
