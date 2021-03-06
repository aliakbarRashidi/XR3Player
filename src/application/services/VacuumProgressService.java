/*
 * 
 */
package application.services;

import java.io.File;

import application.tools.InfoTool;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * Get the progress of Vacuum Operation.
 * 
 * @author GOXR3PLUS
 *
 */
public class VacuumProgressService extends Service<Void> {
	
	/** The basic file. */
	private File basicFile;
	
	/** The journal file. */
	private File journalFile;
	
	/**
	 * Starts the Vacuum Progress Service.
	 *
	 * @param basicFile
	 *        the basic file
	 * @param journalFile
	 *        the journal file
	 */
	public void start(File basicFile , File journalFile) {
		this.basicFile = basicFile;
		this.journalFile = journalFile;
		reset();
		start();
	}
	
	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				
				updateMessage("Hold on(Vacuum running)...");
				
				long bfL = basicFile.length() , jfL;
				// Update Message
				updateMessage("Before:" + InfoTool.getFileSizeEdited(basicFile) + "  After:" + InfoTool.getFileSizeEdited(journalFile));
				
				// Wait until it is created
				while (!journalFile.exists())
					Thread.sleep(50);
				
				// creating process...
				while ( ( jfL = journalFile.length() ) < bfL) {
					
					// Update Message
					updateMessage("Before:" + InfoTool.getFileSizeEdited(basicFile) + "  After:" + InfoTool.getFileSizeEdited(journalFile));
					
					// Update Progress
					updateProgress(jfL, bfL);
					
					// Sleep
					Thread.sleep(50);
				}
				
				// Update Message
				updateMessage("Terminating..");
				
				// System.out.println("Exited Vacuum Progress Service")
				return null;
			}
			
		};
	}
	
}
