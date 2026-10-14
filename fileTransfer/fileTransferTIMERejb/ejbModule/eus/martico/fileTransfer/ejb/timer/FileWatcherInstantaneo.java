//package eus.martico.fileTransfer.ejb.timer;
//
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import java.nio.file.FileSystems;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardWatchEventKinds;
//import java.nio.file.WatchEvent;
//import java.nio.file.WatchKey;
//import java.nio.file.WatchService;
//import java.util.UUID;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import eus.martico.fileTransfer.config.FileTransferConfiguration;
//import eus.martico.fileTransfer.config.FileTransferConstantes;
//import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesException;
//import eus.martico.fileTransfer.services.firma.FileSystemCertSignService;
////import eus.martico.fileTransfer.services.firma.SupabaseStorageService;
//import eus.martico.fileTransfer.services.pedido.files.StorageException;
//import eus.martico.fileTransfer.services.qr.QRGeneratorService;
//import eus.martico.fileTransfer.utils.FileTransferUtils;
//
//
///**
// * XXX descartado firmador
// * @author Martico S.L. :: ialfonso
// * @version 1.0
// * @see https://www.martico.es/
// *
// */
//public class FileWatcherInstantaneo implements Runnable {
//
//    protected static final Logger logger = LoggerFactory.getLogger(FileWatcherInstantaneo.class);
//    private static final String salidas_Path = FileTransferConfiguration.getProperty(FileTransferConstantes.DIR_SALIDAS);
//    private static final String p12certPath = FileTransferConfiguration.getProperty(FileTransferConstantes.CERT_PATH);
//    private static final String p12certPass = FileTransferConfiguration.getProperty(FileTransferConstantes.CERT_PASS);
//
//    @Override
//    public void run() {
//        try {
//            WatchService watchService = FileSystems.getDefault().newWatchService();
//            Path path = Paths.get(salidas_Path);
//            
//            //Escucha cuando se crean archivos nuevos
//            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
//            logger.info("WatchService registrado correctamente en la ruta: " + salidas_Path);
//
//            while (true) {
//                WatchKey key = watchService.take(); 	//Espera hasta que ocurra algo
//                for(WatchEvent<?> event : key.pollEvents()) {
//                    Path fileName = (Path) event.context();
//                    
//                    //Verifica si es un PDF
//                    if(fileName.toString().toLowerCase().endsWith(".pdf")) {
//                    	//Construimos la ruta completa real del fichero para poder leerlo
//                        Path rutaCompletaDelFichero = path.resolve(fileName);
//                        String fName = rutaCompletaDelFichero.getFileName().toString();
//                        logger.info("¡Nuevo PDF detectado!: " + fName);
//                        
//                        
//                        /************ PRUEBAS SUPABASE *********/
//                        //URLs PREDICTIVAS
//                        //Generamos el identificador unico para el documento
//                        String tokenUnico = UUID.randomUUID().toString(); 
//                        String nombreArchivoNube = "DeCA-" + tokenUnico + ".pdf";
//                        
//                        //Construimos la URL que tendrá en SUPABASE
//                        String urlFutura = "https://mkqnybyyikzltpxtsyqp.supabase.co/storage/v1/object/public/pruebas_DeCA/" + nombreArchivoNube;
//                        
//                        
//                        BufferedImage qrImage = null;
//                        try {
//                        	//Generamos el QR con el futuro enlace
//                        	//String url = "https://mkqnybyyikzltpxtsyqp.supabase.co/storage/v1/object/public/pruebas_DeCA/prueba_DeCA_QR_AAA.pdf";
//                        	qrImage  = QRGeneratorService.generateQRCodeImage(urlFutura, 100, 100);
//						} catch (Exception ex1) {
//							System.out.println(ex1.getMessage());
//							logger.error("No se pudo generar el QR: {}", ex1.getMessage());
//						}
//                        
//                        
//                        // =============================================
//        				// BLOQUE DE CERTIFICACIÓN CRIPTOGRÁFICA (PAdES)
//        				// =============================================
//                    	try {
//                    		Path pathPdf = Paths.get(salidas_Path, fName);
//                    		byte[] pdfBytesOriginales = Files.readAllBytes(pathPdf);
//                    		byte[] pdfConQRBytes = pdfBytesOriginales;
//                    		
//                    		/************ PRUEBAS QR *********/
//                            if (qrImage != null) {
//                                try {
//                                	//Estampamos el QR
//                                    pdfConQRBytes = FileTransferUtils.estamparQR(pdfBytesOriginales, qrImage);
//                                    logger.info("QR estampado con éxito en la esquina superior derecha.");
//                                } catch (Exception e) {
//                                    logger.error("Error al estampar el QR: {}", e.getMessage());
//                                }
//                            }
//                            
//                            //Cargamos el sello.PNG original de Martico.SL
//                    		String signFileName = FileTransferConfiguration.getProperty(FileTransferConstantes.SIGN_FILE_NAME);
//                    		Path signFilePath = FileTransferUtils.signLocation.resolve(signFileName);
//        					byte[] selloOriginalBytes  = FileTransferUtils.getByteFile(signFilePath);
//							
//                    		//Metemos firma + sello en el doc que ya tiene el QR
//        					byte[] pdfFinalFirmadoBytes = FileSystemCertSignService.firmarPDFconCertificado(
//        							pdfConQRBytes, p12certPath, p12certPass, true, selloOriginalBytes
//        					);
//        					
//        					//SUBIDA de los ficheros a SUPABASE
//        			        //Usamos el SDK de Supabase o una petición HTTP POST/PUT enviando los bytes y nombrando el archivo como 'nombreArchivoNube'
////        			        boolean subidaExitosa = SupabaseStorageService.subirFichero("pruebas_DeCA", nombreArchivoNube, pdfFinalFirmadoBytes);
////        			        
////        			        if (subidaExitosa) {
////        			            logger.info("Archivo completo subido a la nube. El QR ya es funcional.");
////        			        } else {
////        			        	//TO-DO si cualquier punto del proceso falla incluida la subida se interrumpe el flujo
////        			        }
//        					
//        					//Guardamos en en ./firmados
//        					Path pathSignedCMR = Paths.get(salidas_Path+"/firmados/"+fName);
//        					FileTransferUtils.storeByteFile(pathSignedCMR, pdfFinalFirmadoBytes);
//        					logger.info("PDF Certificado digitalmente y almacenado con éxito.");
//        					
//        					//Borramos de ./salidas
//        					try {
//        						Path oldPath = Paths.get(salidas_Path+"/"+fName);
//        						FileTransferUtils.deleteFile(oldPath, fName);
//        						logger.info("CMR borrado de salidas.");
//        					} catch(StorageException e) {
//        						logger.error("No se pudo eliminar el original de ./salidas: {}", e.getMessage());
//        					}
//        					
////        					//NEXT TO-DO UPDATE F3ESTADO en BBDD
////        					int estadoCMR = FileTransferConstantes.ESTADO_FIRMA_MARTICO;
////        					//Tendría que sacar el TR y EXP de algun lado..... el name??? -------> de FTCMR
////        			        this.updateEstadoFirma(track, exp, new Long(estadoCMR));
//        					
//        					
//        					
//        				} catch (PedidoFilesException | IOException ex){
//        					logger.info("ERROR en la certificación digital automática PAdES: " + ex.getMessage());
//        				}
//                        
//                        //Importante para poder seguir recibiendo eventos futuros
//                        boolean valid = key.reset();
//                        if (!valid) {
//                            logger.warn("El registro de la carpeta ya no es válido. Saliendo del Watcher.");
//                            break;
//                        }
//                    }
//                }
//                key.reset();
//            }
//        } catch (IOException e) {
//            logger.error("Error de E/S en el Watcher instantáneo", e);
//        } catch (InterruptedException e) {
//            logger.info("El hilo del Watcher instantáneo ha sido interrumpido.");
//            Thread.currentThread().interrupt();
//        }
//    }
//}
