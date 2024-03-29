import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;

import org.jfugue.midi.MidiParser;
import org.jfugue.player.Player;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Chord;
import org.jfugue.theory.ChordProgression;
import org.jfugue.theory.Note;
import org.jfugue.midi.MidiFileManager;
import org.jfugue.rhythm.Rhythm;
import org.staccato.StaccatoParserListener;

/**
 * This class plays the first game track
 * @author Austin Liu
 * @version JFugue 5.01
 */
public class GameTrack3 {

    public static void main(String[] args) {
        GameTrack3 gt3 = new GameTrack3();
        Pattern pattern = gt3.getPattern();
	MidiFileManager mymanager = new MidiFileManager();
	
        Player player = new Player();
	File track = new File("track3.mid");
        
        try {
	    mymanager.savePatternToMidi(pattern, track);
        } catch (IOException e) {
             e.printStackTrace();
        }

	// MidiParser parser = new MidiParser();
	// StaccatoParserListener listener = new StaccatoParserListener();
        // parser.addParserListener(listener);
	// parser.parse(MidiSystem.getSequence(track));
        // Pattern staccatoPattern = listener.getPattern();
        // System.out.println(staccatoPattern);

        // player.play(staccatoPattern);
	player.play(pattern);
    }

    public Pattern getPattern() {
	Pattern pattern = new Pattern();
	// Section A
	String voice1a = "A5q G5i F#5q D5i A4q D5i F#5q D5i " +
	    "A5q F#5i D6i C#6i D6i F#6i E6i D6i C#6i D6i E6i " +
	    "A5i B5i C#6i D6q B5i B5q A5i B5q C#6i " +
	    "D6q B5i A5i G5i F#5i F#5i G5i A5i D5q. " ;

	// Section B
	String voice1b = "F#6q G6i A6q F#6i B6q F#6i A6q F#6i " +
	    "D6q F#6i G6q F#6i E6q D6i C#6q B5i " + 
	    "A5q G5i F#5q A5i D6q E6i F#6i E6i D6i " +
	    "E6i D6i B5i A5q F#5i F#5i G5i A5i D5q. ";
	
	Rhythm rhythm = new Rhythm()
	    .addLayer("O..O..O..O..") // This is Layer 0
	    .setLength(16); // Set the length of the rhythm to 16 measures

	Pattern rpattern = rhythm.getPattern();

	Pattern bpattern =
	    new Pattern("Cq. Cq. Cq. Cq. ")
	    .repeat(32);
	
        pattern.add("T180 V0 I[Bass_Drum] " +
		    "X[Volume_Coarse]=0 X[Volume_Fine]=0 " + bpattern);
        pattern.add("V1 I[Violin] X[Volume_Coarse]=192 " +
		    voice1a + voice1a + voice1b + voice1b);
	pattern.add(rpattern);
        return pattern;
    }

    
} 


