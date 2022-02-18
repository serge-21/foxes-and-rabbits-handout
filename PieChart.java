import java.awt.*;
import java.util.HashMap;
import javax.swing.JPanel;

public class PieChart extends JPanel {
    private int width;
    private int height;
    private HashMap<Color, Counter> stats;

    public void stats(HashMap<Color, Counter> stats) {
        this.stats = stats;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
//
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int total = 0;
        int startAngle = 0;

        // get the cumulative frequency of all colours
        for(Color colour : this.stats.keySet()){
            total += (this.stats.get(colour)).getCount();
        }

        int len = this.stats.keySet().size();
        int start = 0;

        for(Color colour : this.stats.keySet()){
            if(start < len-1 && (this.stats.get(colour)).getCount() > 0){
                int arcAngle = (this.stats.get(colour)).getCount() * 360 / total;
                g.setColor(colour);
                // why for the love of god does this not take radians >:(
                g.fillArc(0, 50, this.width, this.height, startAngle, arcAngle);
                startAngle += arcAngle;
                start++;
            }else{
                // this is necessary because due to all the rounding the last bit will always be white.
                // this way we ensure that all the pie-chart is coloured.
                // just a note: I tried using arc2d class, and it resulted in the same bug.
                // I think this is the best way of making a pie chart.
                g.setColor(colour);
                g.fillArc(0, 50, this.width, this.height, startAngle, 360-startAngle);
            }
        }
        // draw border
        g.setColor(Color.BLACK);
        g.drawArc(0, 50, this.width, this.height, 0, 360);
    }
}
