import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.awt.Dimension;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29
 */
public class SimulatorView extends JFrame
{
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private final String DAY = "Daytime: ";
    private final String NUM_OF_DAYS = "Number of days: ";
    private final String TIME_OF_DAY = "";
    private JLabel stepLabel, population, infoLabel, dayLabel, numOfDaysLabel, time;
    private JCheckBox rabbitsCheckBox;
    private JButton music;
    private FieldView fieldView;
    private Histogram histogram;
    private Music mu = new Music();
    private boolean isPlaying = false;
    private PieChart pieChart;
    // A map for storing colors for participants in the simulation
    private Map<Class, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width)
    {
        stats = new FieldStats();
        colors = new LinkedHashMap<>();

        setTitle("Fox and Rabbit Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("      ", JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        fieldView = new FieldView(height, width);
        time = new JLabel(TIME_OF_DAY, JLabel.CENTER);

        // extra stats.
        numOfDaysLabel = new JLabel(NUM_OF_DAYS, JLabel.CENTER);
        dayLabel = new JLabel(DAY, JLabel.CENTER);

        rabbitsCheckBox = new JCheckBox("remove all rabbits from view?");
        music = new JButton("play music bb");

        JPanel infoPane = new JPanel();
        infoPane.setLayout(new FlowLayout());
        infoPane.add(stepLabel);
        infoPane.add(dayLabel);
        infoPane.add(numOfDaysLabel);
        infoPane.add(time);
        infoPane.add(music);

        JPanel checkBoxes = new JPanel(new BorderLayout());
        checkBoxes.add(rabbitsCheckBox, BorderLayout.CENTER);

        Container contents = getContentPane();
        contents.add(infoPane, BorderLayout.NORTH);
        contents.add(checkBoxes, BorderLayout.EAST);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(population, BorderLayout.SOUTH);

        makePieChart(height, width);
        makeHistogram(height, width);
        makeDiagramsVisibile();
        giveButtonsFunctions();
        setLocation(100, 50);
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void makeDiagramsVisibile(){
        JFrame diagrams = new JFrame("Histogram and PieChart");
        diagrams.setSize(1150, 550);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(this.histogram);
        panel.add(this.pieChart);
        diagrams.add(panel);
        diagrams.setVisible(true);
    }

    private void giveButtonsFunctions(){
        rabbitsCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                toggleVisibility(new Rabbit());
            }
        });

        music.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                music();
            }
        });

    }

    private void music(){
        if(!isPlaying){
            mu.setFile(this.getClass().getResourceAsStream("newBeats.wav"));
            mu.play();
            isPlaying = true;
            music.setText("Music on");
        }else{
            mu.stop();
            isPlaying = false;
            music.setText("Music off");
        }
    }

    private void makePieChart(int height, int width) {
        this.pieChart = new PieChart();
        this.pieChart.setSize(height * 4, width * 2);
        this.pieChart.stats(this.getPopulationDetails());
        this.pieChart.repaint();
    }

    private void makeHistogram(int height, int width) {
        this.histogram = new Histogram();
        this.histogram.setSize(height * 2, width * 2);
        this.histogram.stats(this.getPopulationDetails());
        this.histogram.repaint();
    }

    public HashMap<Color, Counter> getPopulationDetails() {
        HashMap<Class, Counter> classStats = this.stats.getPopulation();
        HashMap<Color, Counter> colorStats = new HashMap();

        for(Class c : classStats.keySet()) {
            colorStats.put(this.getColor(c), classStats.get(c));
        }
        return colorStats;
    }
    
    /**
     * Define a color to be used for a given class of animal.
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class animalClass, Color color)
    {
        colors.put(animalClass, color);
    }

    public void toggleVisibility(Entity animal){
        if(rabbitsCheckBox.isSelected()){
            animal.toggleDrawable();
            setColor(animal.getClass(), EMPTY_COLOR);
        }else{
            setColor(animal.getClass(), animal.getColor());
        }
    }

    /**
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text)
    {
        infoLabel.setText(text);
    }

    /**
     * @return The color to be used for a given class of animal.
     */
    private Color getColor(Class animal){
//        return animal.getColor();
        Color col = colors.get(animal);
        if(col == null) {
            // no color defined for this class
            return UNKNOWN_COLOR;
        }
        else {
            return col;
        }
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(int step, Field field, String day, int numOfDays, String currentTime){
        if(!isVisible()) {
            setVisible(true);
        }
            
        stepLabel.setText(STEP_PREFIX + step);
        dayLabel.setText(DAY + day);
        numOfDaysLabel.setText(NUM_OF_DAYS + numOfDays);
        time.setText(TIME_OF_DAY + currentTime);
        stats.reset();
        
        fieldView.preparePaint();

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object animal = field.getObjectAt(row, col);
                if(animal != null) {
                    stats.incrementCount(animal.getClass());
                    fieldView.drawMark(col, row, getColor(animal.getClass()));
                }
                else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }
        stats.countFinished();
        this.histogram.stats(this.getPopulationDetails());
        this.histogram.repaint();
        this.pieChart.stats(this.getPopulationDetails());
        this.pieChart.repaint();

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
        fieldView.repaint();
    }

    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }
    
    /**
     * Provide a graphical view of a rectangular field. This is 
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     * This is rather advanced GUI stuff - you can ignore this 
     * for your project if you like.
     */
    private class FieldView extends JPanel
    {
        private final int GRID_VIEW_SCALING_FACTOR = 6;

        private int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
        private Graphics g;
        private Image fieldImage;

        /**
         * Create a new FieldView component.
         */
        public FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        /**
         * Tell the GUI manager how big we would like to be.
         */
        public Dimension getPreferredSize()
        {
            return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                                 gridHeight * GRID_VIEW_SCALING_FACTOR);
        }

        /**
         * Prepare for a new round of painting. Since the component
         * may be resized, compute the scaling factor again.
         */
        public void preparePaint()
        {
            if(! size.equals(getSize())) {  // if the size has changed...
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if(xScale < 1) {
                    xScale = GRID_VIEW_SCALING_FACTOR;
                }
                yScale = size.height / gridHeight;
                if(yScale < 1) {
                    yScale = GRID_VIEW_SCALING_FACTOR;
                }
            }
        }
        
        /**
         * Paint on grid location on this field in a given color.
         */
        public void drawMark(int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
        }

        /**
         * The field view component needs to be redisplayed. Copy the
         * internal image to screen.
         */
        public void paintComponent(Graphics g)
        {
            if(fieldImage != null) {
                Dimension currentSize = getSize();
                if(size.equals(currentSize)) {
                    g.drawImage(fieldImage, 0, 0, null);
                }
                else {
                    // Rescale the previous image.
                    g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
                }
            }
        }
    }
}
