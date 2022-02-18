import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import javax.swing.JPanel;

public class Histogram extends JPanel {
    private HashMap<Color, Counter> stats;
    private int height;
    private int width;

    public void stats(HashMap<Color, Counter> stats) {
        this.stats = stats;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width;
        int block = 0;

        if(!this.stats.isEmpty()){
            width = this.width / this.stats.size();
        }else{
            width = 4;
        }

        for(Color colour : this.stats.keySet()){
            block++;
            // if population gets out of control set the height to 200. which is 1000/5.
            int height = ((this.stats.get(colour).getCount() < 1000) ? (this.stats.get(colour).getCount() / 5) : 200);
            // draw the actual histogram
            g.setColor(colour);
            g.fillRect(width * block + 40, this.height - height - 40, width, height);

            // draw the border
            g.setColor(Color.BLACK);
            g.drawRect(width * block + 40, this.height - height - 40, width, height);
        }
//        g.setColor(Color.black);
//        g.drawLine(width+40, this.height-260, width+40, this.height);
    }
}
