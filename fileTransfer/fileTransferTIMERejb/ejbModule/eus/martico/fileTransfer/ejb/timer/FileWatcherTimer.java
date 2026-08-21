package eus.martico.fileTransfer.ejb.timer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eus.martico.fileTransfer.config.FileTransferConfiguration;
import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesException;
import eus.martico.fileTransfer.services.firma.FileSystemCertSignService;
import eus.martico.fileTransfer.services.pedido.files.StorageException;
import eus.martico.fileTransfer.utils.FileTransferUtils;


/**
 * NEW
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@Singleton
@Startup
public class FileWatcherTimer {
	//@Singleton es para tareas programadas en el servidor
	//@Startup obliga al servidor a activar el Timer al arrancar

    protected static final Logger logger = LoggerFactory.getLogger(FileWatcherTimer.class);
    private static final String salidas_Path = FileTransferConfiguration.getProperty(FileTransferConstantes.DIR_SALIDAS);
    private static final String p12certPath = FileTransferConfiguration.getProperty(FileTransferConstantes.CERT_PATH);
    private static final String p12certPass = FileTransferConfiguration.getProperty(FileTransferConstantes.CERT_PASS);
    
    
    //Hilo seguro del servidor para ejecutar tareas en segundo plano
    @Resource(lookup = "java:comp/DefaultManagedExecutorService")
    private ManagedExecutorService executor;
    
    @PostConstruct
    public void initInstantWatcher() {
        logger.info("Iniciando servicio de detección instantánea de PDFs en carpeta 'Salidas'...");
        try {
            //Levantamos el hilo del Watcher al arrancar el servidor
            FileWatcherInstantaneo watcher = new FileWatcherInstantaneo();
            executor.submit(watcher); 
            logger.info("Servicio FileWatcherInstantaneo lanzado con éxito.");
        } catch (Exception e) {
            logger.error("Error al iniciar el FileWatcherInstantaneo", e);
        }
    }

    //Timer: Se ejecuta automáticamente cada 41 minutos
    @Schedule(minute = "*/41", hour = "*", persistent = false)
    public void executeTimerTask() {
    	logger.info("Leyendo directorio de salidas. Temporizador activado a las: " + new Date());
        revisarCarpeta();
    }

    public void revisarCarpeta() {
        File folder = new File(salidas_Path);
        if (!folder.exists() || !folder.isDirectory()) {
            logger.error("La ruta especificada no existe o no es una carpeta: " + salidas_Path);
            return;
        }
        
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        if (files != null && files.length > 0) {
        	logger.info("Iniciando certificación digital by Martico.SL");
        	logger.info("Ficheros encontrados: "+files.length );
        	
            for (File file : files) {
            	String fName = file.getName();
            	logger.info("Procesando PDF: " + fName);
                
            	// =============================================
				// BLOQUE DE CERTIFICACIÓN CRIPTOGRÁFICA (PAdES)
				// =============================================
            	try {
            		byte[] pdfBytes = null;
            		Path pathPdf = Paths.get(salidas_Path, fName);
            		pdfBytes = Files.readAllBytes(pathPdf);
            		
					//Metemos la firma inicial de Martico.SL
					byte[] pdfCertificadoBytes = FileSystemCertSignService.firmarPDFconCertificado(pdfBytes, p12certPath, p12certPass);
					//Guardamosen en ./firmados
					Path pathSignedCMR = Paths.get(salidas_Path+"/firmados/"+fName);
					FileTransferUtils.storeByteFile(pathSignedCMR, pdfCertificadoBytes);
					logger.info("PDF Certificado digitalmente y almacenado con éxito.");
					//Borramos de ./salidas
					try {
						Path oldPath = Paths.get(salidas_Path+"/"+fName);
						FileTransferUtils.deleteFile(oldPath, fName);
						logger.info("CMR borrado de salidas.");
					} catch(StorageException e) {
						logger.error("No se pudo eliminar el original de ./salidas: {}", e.getMessage());
					}
				} catch (PedidoFilesException | IOException ex){
					logger.info("ERROR en la certificación digital automática PAdES: " + ex.getMessage());
				}
				
            }
        }
    }
}
