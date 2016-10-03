package com.wellgo.wad.contentprovider.utils;




import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import static java.nio.file.StandardWatchEventKinds.*;


public class FilesChangeObserver implements Runnable {
	
	private final static Logger logger = LoggerFactory.getLogger(FilesChangeObserver.class);
	
	private IFilesChangeListener listener;
	private WatchService watcher;
	private String watchDirectory;
	private Path pathWatchDirectory;
	
	private HashMap <String, Long> lastFilesModificationMap;
	
	
	//private volatile boolean isActive = true;


	public FilesChangeObserver(IFilesChangeListener listener, String watchDirectory){
		this.listener = listener;
		this.watchDirectory = watchDirectory;
		lastFilesModificationMap = new HashMap<String, Long>();
	}
	
	
	public List<File> init() {
		
		try {
			watcher = FileSystems.getDefault().newWatchService();
			pathWatchDirectory = FileSystems.getDefault().getPath(watchDirectory);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Failed to init, Reason: " + e.getMessage());
			return null;
		}
		
		try {
			WatchKey key = pathWatchDirectory.register(watcher,
	                           ENTRY_CREATE, 
	                           ENTRY_DELETE,
	                           ENTRY_MODIFY);
		} catch (IOException x) {
			logger.error("Failed to register directory <" + watchDirectory + "> watcher, Reason: " + x.getMessage());
			return null;
		}
		
	
	   List<File> files = new ArrayList<File>();
		
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(pathWatchDirectory)) {
            for (Path path : directoryStream) {
            	files.add(path.toFile());
            	logger.debug("File <" + path.toFile().getName() + "> found in watch directory <" + watchDirectory + ">");
            }
        } catch (IOException x) {
        	logger.error("Failed to list directory <" + watchDirectory + "> files, Reason: " + x.getMessage());
        	files = null;
			return files;
        }

		
		logger.info("Successfully initialized");
		return files;
	}
	
	
	/**
	 * 
	 */
	public void run() {
		
		while (!Thread.currentThread().isInterrupted()) {

			// wait for key to be signaled
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				logger.error("Failed to retrieve the watcher event - exits the thread loop, Reason: InterruptedException");
				Thread.currentThread().interrupt();
				break;
			}

			for (WatchEvent<?> event : key.pollEvents()) {

				WatchEvent.Kind<?> kind = event.kind();
				

				// This key is registered only
				// for ENTRY_CREATE events,
				// but an OVERFLOW event can
				// occur regardless if events
				// are lost or discarded.

				if (kind == OVERFLOW) {
					continue;
				}
				
				WatchEvent<Path> ev = (WatchEvent<Path>) event;
				Path filename = ev.context();
				File file = pathWatchDirectory.resolve(filename).toFile();
				logger.info("Received event <" + kind + "> for file: " + file.getName() + ", lastModified=" + file.lastModified());
				
				Long lastModified = lastFilesModificationMap.get(file.getName());
				
				if (lastModified == null || lastModified.longValue() - file.lastModified() > 1000) {
					
					lastFilesModificationMap.put(file.getName(), new Long(file.lastModified()));

					if (kind == ENTRY_MODIFY) {
						listener.onFileModify(file);
					}
					else if (kind == ENTRY_CREATE) {
						listener.onFileCreate(file);
					}
					else if (kind == ENTRY_DELETE) {
						listener.onFileDelete(file);
						lastFilesModificationMap.remove(file.getName());
					}
				}
				
				
				// Reset the key -- this step is critical if you want to
				// receive further watch events. If the key is no longer valid,
				// the directory is inaccessible so exit the loop.
				boolean valid = key.reset();

				if (!valid) {
					logger.error("The directory is inaccessible, exits the thread loop");
					Thread.currentThread().interrupt();
					break;
				}
				
			}

		}
		
		logger.info("Exited the thread loop");
	}
	
	/*
	 public void deactivate() {
		    isActive = false;
	 }
	 */


}
