//package eus.martico.fileTransfer.control.deca;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//
//import javax.servlet.http.HttpServletResponse;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import eus.martico.fileTransfer.services.pedido.files.StorageFileNotFoundException;
//
//
///**
// * OLD
// * @author Martico S.L. :: ialfonso
// * @version 1.0
// * @see https://www.martico.es/
// *
// */
//@RestController()
//@RequestMapping(value = "/api/v1/descargarDeCA")
//public class DeCADownloadController {
//
//	private static final Logger logger = LoggerFactory.getLogger(DeCADownloadController.class);
//
//	// Ruta física en el disco del servidor donde tu módulo JAR guarda los archivos
//	private static final String RUTA_ALMACENAMIENTO = "C:/Apps/fileTransfer/upload_dir/deca_almacenamiento/";
//
//	//@RequestMapping(value = "/api/v1/descargarDeCA", method = RequestMethod.GET)
//	@RequestMapping(method = RequestMethod.GET, path="/getDeCA")
//	public void descargarDeCA(
//			@RequestParam("id") String fileId, 
//			HttpServletResponse response) throws IOException {
//
//		logger.info("Petición de descarga recibida para el DeCA ID: {}", fileId);
//
//		// Validación de seguridad para el parámetro de entrada (evita ataques Path Traversal)
//		if (fileId == null || !fileId.matches("^[a-zA-Z0-9-]+$")) {
//			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identificador inválido");
//			return;
//		}
//
//		File pdfFile = new File(RUTA_ALMACENAMIENTO + "DeCA-" + fileId + ".pdf");
//
//		if (!pdfFile.exists()) {
//			logger.warn("El archivo solicitado no existe en el disco: {}", pdfFile.getAbsolutePath());
//			response.sendError(HttpServletResponse.SC_NOT_FOUND, "El DeCA solicitado no existe");
//			return;
//		}
//
//		// Configuración de cabeceras HTTP necesarias para la descarga limpia del PDF
//		response.setContentType("application/pdf");
//		response.setContentLength((int) pdfFile.length());
//
//		// 'inline' hace que el navegador intente abrir el PDF en pantalla (ideal para el móvil del inspector).
//		// Si prefieres que se descargue el archivo directamente como un adjunto, cambia 'inline' por 'attachment'
//		response.setHeader("Content-Disposition", "inline; filename=\"DeCA-" + fileId + ".pdf\"");
//
//		// Transferencia de los bytes del disco hacia la respuesta HTTP del navegador
//		try (FileInputStream inStream = new FileInputStream(pdfFile);
//				OutputStream outStream = response.getOutputStream()) {
//
//			byte[] buffer = new byte[4096];
//			int bytesRead;
//			while ((bytesRead = inStream.read(buffer)) != -1) {
//				outStream.write(buffer, 0, bytesRead);
//			}
//			outStream.flush();
//		}
//
//		logger.info("DeCA enviado con éxito al cliente web.");
//	}
//	
//	@ExceptionHandler(StorageFileNotFoundException.class)
//	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
//		return ResponseEntity.notFound().build();
//	}
//
//}