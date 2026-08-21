package eus.martico.fileTransfer.ejb.timer;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

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
public class FileWatcherInstantaneo implements Runnable {

    protected static final Logger logger = LoggerFactory.getLogger(FileWatcherInstantaneo.class);
    private static final String salidas_Path = FileTransferConfiguration.getProperty(FileTransferConstantes.DIR_SALIDAS);
    private static final String p12certPath = FileTransferConfiguration.getProperty(FileTransferConstantes.CERT_PATH);
    private static final String p12certPass = FileTransferConfiguration.getProperty(FileTransferConstantes.CERT_PASS);

    @Override
    public void run() {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(salidas_Path);
            
            //Escucha cuando se crean archivos nuevos
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            logger.info("WatchService registrado correctamente en la ruta: " + salidas_Path);

            while (true) {
                WatchKey key = watchService.take(); 	//Espera hasta que ocurra algo
                for(WatchEvent<?> event : key.pollEvents()) {
                    Path fileName = (Path) event.context();
                    
                    //Verifica si es un PDF
                    if(fileName.toString().toLowerCase().endsWith(".pdf")) {
                    	//Construimos la ruta completa real del fichero para poder leerlo
                        Path rutaCompletaDelFichero = path.resolve(fileName);
                        Path fNamePath = rutaCompletaDelFichero.getFileName();
                        String fName = fNamePath.toString();
                        logger.info("¡Nuevo PDF detectado!: " + fName);
                        
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
                        
                        //Importante para poder seguir recibiendo eventos futuros
                        boolean valid = key.reset();
                        if (!valid) {
                            logger.warn("El registro de la carpeta ya no es válido. Saliendo del Watcher.");
                            break;
                        }
                    }
                }
                key.reset();
            }
        } catch (IOException e) {
            logger.error("Error de E/S en el Watcher instantáneo", e);
        } catch (InterruptedException e) {
            logger.info("El hilo del Watcher instantáneo ha sido interrumpido.");
            Thread.currentThread().interrupt();
        }
    }
}
