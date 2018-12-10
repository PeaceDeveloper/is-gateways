package neurons.define;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioInputStream; 
import javax.sound.sampled.AudioSystem; 
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import org.antlr.grammar.v3.ANTLRv3Parser.exceptionGroup_return;

import br.ufes.inf.lprm.situation.Role;
import jline.internal.Log;
import neurons.core.Robot;
import neurons.sa.scene.MotorNeuron;

//@Publish(host="host", port=4040)
public class Position extends MotorNeuron {
	
	public static void main(String[] args) throws Exception {		
		Position p = new Position();		
		try {
			while(true) {
				long init = System.currentTimeMillis() % 1000;
				p.PlayTheBest();			
				long end = System.currentTimeMillis() % 1000;
				p.SaveMusic(end, init);
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}		
		
	}
	
	Long currentFrame; 
    Clip clip; 
      
    // current status of clip 
    String status; 
      
    AudioInputStream audioInputStream;
    
    static String filePath; 
	
	public static class Music{
		public Music(String id, String nome) {
			this.Id = id;
			this.Nome = nome;
		}
		public String Id;
		public String Nome;
		public int QtdeExecs = 0;
		public long TempoTotalExec = 0;
		public long TempoMedioExec = 0;
	}
	
	static Map<String,Music> musics = new HashMap<String,Music>();
	
	public Position() {
		super();
		//AbstractNeuronsTopology.platform.getConn().assertExchange("sa-data");			
		System.out.println("inicialização**************************************************************** ***************************** ");
		if (musics.isEmpty()) {
			musics.put("A", 
			new Music("A", "/home/tiago/Documents/is-gateways/test/Experimento/playOneSA/musics/A.wav"));
			musics.put("B", 
			new Music("B", "/home/tiago/Documents/is-gateways/test/Experimento/playOneSA/musics/B.wav"));
			musics.put("C", 
			new Music("C", "/home/tiago/Documents/is-gateways/test/Experimento/playOneSA/musics/C.wav"));
		}
	}	
	@Role(label = "f1")
	private Robot robot;

	public void setRobot(Robot robot) {
		this.robot = robot;
	}
	public Robot getRobot() {
		return robot;
	}	
	
	Music music = null;
	
	private void PlayOne(Music bestMusic) throws Exception {		
		music = bestMusic;
		File file = new File(music.Nome);
		// create AudioInputStream object 
        audioInputStream =  
                AudioSystem.getAudioInputStream(file);          
        DataLine.Info info = new DataLine.Info(Clip.class
        		, audioInputStream.getFormat());        
        clip = (Clip)AudioSystem.getLine(info);          
        // open audioInputStream to the clip 
        clip.open(audioInputStream);          
        clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	private void PlayTheBest() throws Exception {
		Optional<Entry<String, Music>> opt = musics.entrySet()
				.stream().filter(a -> a.getValue().QtdeExecs < 3).findFirst();
		Music bestMusic = null;
		if (opt.isPresent()) {
			bestMusic = opt.get().getValue();
		}else {
			
			FileWriter arq = new FileWriter("/home/tiago/Documents/is-gateways/test/Experimento/playOneSA/musics/resultado.txt");
		    PrintWriter gravarArq = new PrintWriter(arq);		 
		    gravarArq.printf("+--Resultado--+%n");			
			for(Entry<String, Music> item : musics.entrySet()) {
				gravarArq.printf(String.format("| A música %s no tempo de %d ms|", item.getValue().Id, item.getValue().TempoMedioExec));
				Music itemMusic = item.getValue();			
				if (bestMusic == null ||						
						bestMusic.TempoMedioExec > itemMusic.TempoMedioExec) {
					bestMusic = itemMusic;
				}			
			}
			gravarArq.printf("+-------------+%n");
		    arq.close();
		}
		this.PlayOne(bestMusic);
	}
	
	private void SaveMusic(long deactivationTimeStamp, long activationTimeStamp)
			throws Exception
	{
		currentFrame = 0L; 
        clip.stop(); 
        clip.close();
        System.out.println("deact " + deactivationTimeStamp);
        System.out.println("act " + activationTimeStamp);
		long milisseconds = Math.abs(activationTimeStamp - deactivationTimeStamp);		
		System.out.println("milisseconds " + milisseconds);
		music.TempoTotalExec = music.TempoTotalExec + milisseconds;
		music.QtdeExecs++;
		System.out.println("execuções ***************************** " + music.QtdeExecs);
		music.TempoMedioExec = music.TempoTotalExec / music.QtdeExecs;		
		musics.put(music.Id, music);
	}	
	
	@Override
	public void setActive() {
		super.setActive();
		try {
			this.PlayTheBest();			
		} catch (Exception e) {			
			e.printStackTrace();
		}		
	}
	@Override
	public void setInactive() {
		super.setInactive();		
		try {			
			this.SaveMusic(this.getDeactivation().getTimestamp(), this.getActivation().getTimestamp());
		} catch (Exception e) {			 
			e.printStackTrace();
		}		
	}		
}