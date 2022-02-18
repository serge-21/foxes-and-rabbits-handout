// followed this tutorial on how to do it https://www.youtube.com/watch?v=MZyvOtxU73w

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.InputStream;


public class Music {
    Clip clip;

    public void setFile(InputStream soundFileName){
        try{
            AudioInputStream sound = AudioSystem.getAudioInputStream(soundFileName);
            clip = AudioSystem.getClip();
            clip.open(sound);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void play(){
        clip.setFramePosition(0);
        clip.start();
        loop();
    }

    public void loop(){
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop(){
        clip.stop();
        clip.close();
    }
}
