package org.androiddaisyreader.testapps;

import static org.androiddaisyreader.model.XmlUtilities.obtainEncodingStringFromInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.androiddaisyreader.AudioPlayer;
import org.androiddaisyreader.mock.MockAndroidAudioPlayer;
import org.androiddaisyreader.model.Audio;
import org.androiddaisyreader.model.BookContext;
import org.androiddaisyreader.model.Daisy202Section;
import org.androiddaisyreader.model.FileSystemContext;
import org.androiddaisyreader.model.Part;
import org.androiddaisyreader.model.Daisy202Section.Builder;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class ProcessExternalSmilFile {

	/**
	 * Process and External SMIL file. 
	 * @param args the filename of the SMIL file. If there are several 
	 * arguments, assume they comprise a single composite path and filename.
	 * @throws IOException if there are problems finding or opening the file.
	 */
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			CommandLineUtilities.printUsage("ProcessExternalSmilFile");
			System.exit(1);
		}
		
		StringBuilder filename = new StringBuilder();
		AudioPlayer audioPlayer = new MockAndroidAudioPlayer();
		
		// To help cope with spaces in the filename e.g. on my windows machine.
		for (int i = 0; i < args.length; i++) {
			filename.append(args[i]);
		}
		
		File file = new File(filename.toString());
		
		BookContext bookContext = new FileSystemContext(file.getParent());
		
		Daisy202Section section = new Daisy202Section.Builder()
			.setHref(file.getName())
			.setContext(bookContext)
			.build();
		
		file = null;

		for (Part part : section.getParts()) {
			for (int j = 0; j < part.getSnippets().size(); j++) {

				String text = part.getSnippets().get(j).getText();
				String id = part.getSnippets().get(j).getId();

				if (part.getAudioElements().size() > 0) {
					Audio audio = part.getAudioElements().get(0);
					double duration = audio.getClipEnd() - audio.getClipBegin();
					System.out.printf(" [%s]: %s < Show text for %f seconds => %s\n", 
							id, 
							audio.getAudioFilename(), 
							duration,	
							text);
					audioPlayer.playFileSegment(audio);
				} else {
					System.out.printf(" [%s]: => %s", id, text);
				}
			}
		}
		// TODO 20120207 (jharty): consider checking the section contents here.
		System.out.println("\nparsed file " + args[0] + " without error");
		System.exit(0);

	}

}