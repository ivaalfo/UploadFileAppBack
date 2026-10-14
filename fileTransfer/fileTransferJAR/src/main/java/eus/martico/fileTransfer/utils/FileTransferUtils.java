package eus.martico.fileTransfer.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import eus.martico.fileTransfer.config.FileTransferConfiguration;
import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesException;
import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesIDAO;
import eus.martico.fileTransfer.model.pedido.files.PedidoFilesVO;
import eus.martico.fileTransfer.services.MyServiceContext;
import eus.martico.fileTransfer.services.pedido.files.FileSystemStorageServiceImp;
import eus.martico.fileTransfer.services.pedido.files.StorageException;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public class FileTransferUtils {
	
	@Autowired
	PedidoFilesIDAO pedidoFilesDAO;

	//Directorios NO dinámicos
	public final static Path signLocation = Paths.get(FileTransferConfiguration.getProperty(FileTransferConstantes.DIR_SIGN_FILE));
	public final static Path manualLocation = Paths.get(FileTransferConfiguration.getProperty(FileTransferConstantes.MAN_PROV_LOCATION));
	public final static Path FACmatrixLocation = Paths.get(FileTransferConfiguration.getProperty(FileTransferConstantes.DIR_FAC_FILES_DOCPRO));
	
	//Directorios dinámicos
	private final static String year = String.valueOf(LocalDate.now().getYear());	//TO TEST String.valueOf(2028);
	private final static String baseDir = FileTransferConfiguration.getProperty(FileTransferConstantes.DIR_BASE);
	private final static String subfolderCMR = FileTransferConfiguration.getProperty(FileTransferConstantes.DIR_BASE_CMR);
    private final static String subfolderCMRvalidados = FileTransferConfiguration.getProperty(FileTransferConstantes.DIR_BASE_CMR_VALIDATED);
    private final static String subfolderCMRrechazados = FileTransferConfiguration.getProperty(FileTransferConstantes.DIR_BASE_CMR_REJECTED);
    private final static String subfolderFAC = FileTransferConfiguration.getProperty(FileTransferConstantes.DIR_BASE_FAC);
    private final static String subfolderFACrechazadas = FileTransferConfiguration.getProperty(FileTransferConstantes.DIR_BASE_FAC_REJECTED);
    private final static String subfolderOTR = FileTransferConfiguration.getProperty(FileTransferConstantes.DIR_BASE_OTR);
	
	public final static Path CMRrootLocation = Paths.get(baseDir, year, subfolderCMR);
    public final static Path CMRvalidatedLocation  = Paths.get(baseDir, year, subfolderCMRvalidados);
    public final static Path CMRrejectedLocation = Paths.get(baseDir, year, subfolderCMRrechazados);
    public final static Path FACrootLocation = Paths.get(baseDir, year, subfolderFAC);
    public final static Path FACrejectedLocation = Paths.get(baseDir, year, subfolderFACrechazadas);
    public final static Path OTRrootLocation = Paths.get(baseDir, year, subfolderOTR);
	
    protected static final Logger logger = LoggerFactory.getLogger(FileSystemStorageServiceImp.class);
    
	
    /**
     * createDynamicDirs
     * @throws IOException
     */
	public static void createDynamicDirs() throws IOException {
        //Creamos las carpetas solo si no existen
        if(Files.notExists(CMRrootLocation)) {
        	Files.createDirectories(CMRrootLocation);
            logger.info("Directorio autogenerado: {}", CMRrootLocation);
        }
        if(Files.notExists(CMRvalidatedLocation)) {
        	Files.createDirectories(CMRvalidatedLocation);
            logger.info("Directorio autogenerado: {}", CMRvalidatedLocation);
        }
        if(Files.notExists(CMRrejectedLocation)) {
        	Files.createDirectories(CMRrejectedLocation);
            logger.info("Directorio autogenerado: {}", CMRrejectedLocation);
        }
        if(Files.notExists(FACrootLocation)) {
        	Files.createDirectories(FACrootLocation);
            logger.info("Directorio autogenerado: {}", FACrootLocation);
        }
        if(Files.notExists(FACrejectedLocation)) {
        	Files.createDirectories(FACrejectedLocation);
            logger.info("Directorio autogenerado: {}", FACrejectedLocation);
        }
        if(Files.notExists(OTRrootLocation)) {
        	Files.createDirectories(OTRrootLocation);
            logger.info("Directorio autogenerado: {}", OTRrootLocation);
        }
    }

	/**
	 * getFileName
	 * @param _path String
	 * @return String : filename sin extension
	 */
	public static String getFileName(String _path){
		String basename = FilenameUtils.getBaseName(_path);
		logger.info("getFileName :: "+basename);
		return basename;
	}
	
	/**
	 * getExt
	 * @param _filePath String
	 * @return String : extension, no .extension
	 */
	public static String getExt(String _filePath){
		String extension = FilenameUtils.getExtension(_filePath);
		logger.info("getExt :: "+extension);
		return extension;
	}

	/**
	 * renameFile
	 * @param _fileType String
	 * @param _dirDes Path
	 * @param _fileName String
	 * @param _fileExt String
	 */
	public static void renameFile(String _fileType, Path _dirDes, String _fileName, String _fileExt){
		String pathORstr = "";
		Path pathOR = null;
		
		try {
			switch(_fileType){
			case "REJ_CMR":
				pathORstr = _fileName.concat("."+_fileExt);
				pathOR = _dirDes.getParent().getParent().resolve(pathORstr).normalize();
				//COPIAR de origen a destino
				FileSystemUtils.copyRecursively(pathOR, _dirDes);
				//DELETE el original
				deleteFile(pathOR, _fileName);
				break;
			case "REJ_FAC":
				pathORstr = _fileName.concat("."+_fileExt);
				pathOR = _dirDes.getParent().getParent().resolve(pathORstr).normalize();
				//COPIAR de origen a destino
				FileSystemUtils.copyRecursively(pathOR, _dirDes);
				//DELETE el original
				deleteFile(pathOR, _fileName);
				break;
			case "FACMATRIX":
				Path facROOTLocation = Paths.get(baseDir, year, subfolderFAC);
				pathOR = facROOTLocation.resolve(_fileName).normalize();
				//COPIAR de origen a destino sin DELETE el de ORIGEN
				FileSystemUtils.copyRecursively(pathOR, _dirDes);
				break;
			}
			
		} catch (IOException e) {
			logger.error("Failed to rename/store file: " + e.toString());
			throw new StorageException("Failed to rename/store file: " + _fileName, e);
		}
		logger.info("renameFile :: "+_fileName);
	}

	/**
	 * eliminarFicherosRepetidos
	 * @param ficheros MultipartFile[]
	 * @return MultipartFile[]
	 */
	public static MultipartFile[] eliminarFicherosRepetidos(MultipartFile[] ficheros) {
		Set<String> nombresUnicos = new HashSet<String>();
		ArrayList<MultipartFile> ficherosUnicos = new ArrayList<MultipartFile>();

		for (MultipartFile fichero : ficheros) {
			String originFilename = StringUtils.cleanPath(fichero.getOriginalFilename());
			if (!nombresUnicos.contains(originFilename)) {
				nombresUnicos.add(originFilename);
				ficherosUnicos.add(fichero);
			}
		}
		logger.info("eliminarFicherosRepetidos :: FIN");
		return ficherosUnicos.toArray(new MultipartFile[0]);
	}

	/**
	 * determinarEstadoGeneral
	 * @param _pedido PedidoFilesVO
	 * @param _pedidoTrack String
	 * @return estadoGeneral Integer
	 * @throws PedidoFilesException
	 */
	public static Integer determinarEstadoGeneral(PedidoFilesVO _pedido, String _pedidoTrack, String _pedidoExp) throws PedidoFilesException {
		Integer estadoCMR = null;
		Integer estadoFAC = null;
		Integer estadoGeneral = null;

		estadoCMR = _pedido.getEstadoCMR();
		estadoFAC = _pedido.getEstadoFAC();
		logger.info("determinarEstadoGeneral :: TR: {} :: EXP: {}" ,_pedidoTrack, _pedidoExp);
		logger.info("EstadoCMR: {}" ,estadoCMR);
		logger.info("EstadoFAC: {}" ,estadoFAC);

		switch(estadoCMR){
		case 0:
			estadoGeneral = FileTransferConstantes.ESTADO_0_FICH_NO_SUBIDO;
			break;
		case 8:
			estadoGeneral = FileTransferConstantes.ESTADO_0_FICH_NO_SUBIDO;
			break;
		case 1:
			if(estadoFAC.equals(FileTransferConstantes.ESTADO_0_FICH_NO_SUBIDO) || estadoFAC.equals(FileTransferConstantes.ESTADO_8_INIT_GRUP)){
				estadoGeneral = FileTransferConstantes.ESTADO_0_FICH_NO_SUBIDO;
				break;
			}
			if(estadoFAC.equals(FileTransferConstantes.ESTADO_1_FICH_SUBIDO)){
				estadoGeneral = FileTransferConstantes.ESTADO_1_FICH_SUBIDO;
				break;
			}
			if(estadoFAC.equals(FileTransferConstantes.ESTADO_2_FICH_VALIDADO) || estadoFAC.equals(FileTransferConstantes.ESTADO_7_FICH_SIN_DOC)){
				estadoGeneral = FileTransferConstantes.ESTADO_1_FICH_SUBIDO;
				break;
			}
			if(estadoFAC.equals(FileTransferConstantes.ESTADO_3_FICH_RECHAZADO)){
				estadoGeneral = FileTransferConstantes.ESTADO_3_FICH_RECHAZADO;
				break;
			}
		case 2:
			if(estadoFAC.equals(FileTransferConstantes.ESTADO_0_FICH_NO_SUBIDO) || estadoFAC.equals(FileTransferConstantes.ESTADO_8_INIT_GRUP)){
				estadoGeneral = FileTransferConstantes.ESTADO_0_FICH_NO_SUBIDO;
				break;
			}
			if(estadoFAC.equals(FileTransferConstantes.ESTADO_1_FICH_SUBIDO) || estadoFAC.equals(FileTransferConstantes.ESTADO_4_FAC_BLOQUEADA) || estadoFAC.equals(FileTransferConstantes.ESTADO_5_FAC_SIN_CONTABILIZAR)){
				estadoGeneral = FileTransferConstantes.ESTADO_1_FICH_SUBIDO;
				break;
			}
			if(estadoFAC.equals(FileTransferConstantes.ESTADO_2_FICH_VALIDADO) || estadoFAC.equals(FileTransferConstantes.ESTADO_7_FICH_SIN_DOC)){
				estadoGeneral = FileTransferConstantes.ESTADO_2_FICH_VALIDADO;
				break;
			}
			if(estadoFAC.equals(FileTransferConstantes.ESTADO_3_FICH_RECHAZADO)){
				estadoGeneral = FileTransferConstantes.ESTADO_3_FICH_RECHAZADO;
				break;
			}
			if(estadoFAC.equals(FileTransferConstantes.ESTADO_6_FAC_NO_CONTABILIZAR));{
				estadoGeneral = FileTransferConstantes.ESTADO_2_FICH_VALIDADO;
				break;
			}
		case 3:
			estadoGeneral = FileTransferConstantes.ESTADO_3_FICH_RECHAZADO;
			break;
		case 7:
			if(estadoFAC.equals(FileTransferConstantes.ESTADO_0_FICH_NO_SUBIDO) || estadoFAC.equals(FileTransferConstantes.ESTADO_8_INIT_GRUP)){
				estadoGeneral = FileTransferConstantes.ESTADO_0_FICH_NO_SUBIDO;
				break;
			}
			if(estadoFAC.equals(FileTransferConstantes.ESTADO_1_FICH_SUBIDO)){
				estadoGeneral = FileTransferConstantes.ESTADO_1_FICH_SUBIDO;
				break;
			}
			if(estadoFAC.equals(FileTransferConstantes.ESTADO_2_FICH_VALIDADO)){
				estadoGeneral = FileTransferConstantes.ESTADO_2_FICH_VALIDADO;
				break;
			}
			if(estadoFAC.equals(FileTransferConstantes.ESTADO_3_FICH_RECHAZADO)){
				estadoGeneral = FileTransferConstantes.ESTADO_3_FICH_RECHAZADO;
				break;
			}
			if(estadoFAC.equals(FileTransferConstantes.ESTADO_5_FAC_SIN_CONTABILIZAR)){
				estadoGeneral = FileTransferConstantes.ESTADO_7_FICH_SIN_DOC;
			}
			if(estadoFAC.equals(FileTransferConstantes.ESTADO_7_FICH_SIN_DOC)){
				estadoGeneral = FileTransferConstantes.ESTADO_7_FICH_SIN_DOC;
			}
		default:
			break;
		}
		logger.info("Estado General: {}" ,estadoGeneral);
		return estadoGeneral;
	}
	
	/**
	 * getPath
	 * @param _fileType String
	 * @param _track String
	 * @param _completeFname String
	 * @return Path
	 * @throws StorageException
	 */
	public static Path getPath(String _fileType, String _track, String _completeFname) 
			throws StorageException {
		Path resultPath = null;
		String finalFileName = null;
		
		if(_completeFname.contains("..")) {
			//This is a security check
			logger.error("Cannot store file with relative path outside current directory {}", _completeFname);
			throw new StorageException("Cannot store file with relative path outside current directory " +_completeFname);
		}
		
		switch(_fileType){
		case "CMR":			//OK
			//En este caso en _track vendrá la refCarga
			finalFileName = _fileType+"_"+_track+"_"+_completeFname;
			resultPath = CMRrootLocation.resolve(finalFileName).normalize();
			break;
		case "FAC":			//OK
			finalFileName = _fileType+"_"+_track+"_"+_completeFname;
			resultPath = FACrootLocation.resolve(finalFileName).normalize();
			break;
		case "FACMATRIX_A":		//OK
			finalFileName = _completeFname;
			resultPath = FACmatrixLocation.resolve(finalFileName).normalize();
			break;
		case "FACMATRIX_B":		//OK
			finalFileName = "FAC_"+_track+"_"+_completeFname;
			resultPath = FACmatrixLocation.resolve(finalFileName).normalize();
			break;
		case "OTR":			//OK
			finalFileName = _fileType+"_"+_track+"_"+_completeFname;
			resultPath = OTRrootLocation.resolve(finalFileName).normalize();
			break;
		case "MAN_PROV":	//OK
			resultPath = manualLocation.resolve(_completeFname).normalize();
			break;
		}
		return resultPath;
	}
	
	/**
	 * getNewPath
	 * @param _fileType String
	 * @param _completePath Path
	 * @param _completeFname String
	 * @return Path
	 */
	public static Path getNewPath(String _fileType, Path _completePath, String _completeFname){
		Path parentPath = null;
		Path resultPath = null;
		
		switch(_fileType){
		case "VAL_CMR":
			parentPath = _completePath.getParent();
			resultPath = parentPath.resolve(parentPath+"/validados/"+_completeFname).normalize();
			break;
		case "REJ_CMR":
			parentPath = _completePath.getParent();
			resultPath = parentPath.resolve(parentPath+"/rechazados/"+_completeFname).normalize();
			break;
		case "REJ_FAC":
			parentPath = _completePath.getParent();
			resultPath = parentPath.resolve(parentPath+"/rechazadas/"+_completeFname).normalize();
		}
		return resultPath;
	}
	
	/**
	 * getByteFile
	 * @param targetPath Path
	 * @return byte[]
	 * @throws IOException
	 */
//	public static byte[] getByteFile(Path targetPath) throws IOException {
//		byte[] byteFile = Files.readAllBytes(targetPath);
//		return byteFile;
//	}
	
	/**
	 * storeByteFile
	 * @param targetPath Path
	 * @param contenido byte[]
	 * @throws IOException
	 */
	public static void storeByteFile(Path targetPath, byte[] contenido) 
			throws IOException {
		//Asegura que las carpetas existan
		Files.createDirectories(targetPath.getParent());
		//Guarda los bytes directamente en el disco
		Files.write(targetPath, contenido);
	}

	/**
	 * storeFile
	 * @param _path Path
	 * @param _mfile MultipartFile
	 * @throws IOException
	 */
	public static void storeFile(Path _path, MultipartFile _mfile)
			throws IOException {
		String originFileName = "";
		if(_mfile != null){
			originFileName = StringUtils.cleanPath(_mfile.getOriginalFilename());
			
			//Declaramos el InputStream dentro del TRY para que se cierre SOLO
	        try (InputStream inputStream = _mfile.getInputStream()) {
				Files.copy(inputStream, _path, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				logger.error("Failed to store file: "+originFileName+" :: " +e.getMessage());
				throw new StorageException("Failed to store file " +originFileName, e);
			}
		}
		logger.info("storeFile :: "+ originFileName);
	}
	
	/**
	 * deleteFile
	 * @param _oldPath Path
	 * @param _completeFname String
	 * @return boolean
	 */
	public static boolean deleteFile(Path _oldPath, String _completeFname) {
		boolean deleted = false;
		try {
			Path resultPath = null;
			resultPath = _oldPath.resolve(_oldPath).normalize();
			deleted = FileSystemUtils.deleteRecursively(resultPath);
		} catch (IOException e) {
			logger.error("Failed to delete: "+_completeFname+" :: "+e.getMessage());
			throw new StorageException("Failed to delete: "+_completeFname, e);
		}
		logger.info("deleteFile :: "+_completeFname);
		return deleted;
	}
	
	/**
	 * capitalizarPrimeraLetra
	 * @param str String
	 * 
	 * @return String
	 */
	public static String capitalizarPrimeraLetra(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		str = str.toLowerCase();
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	/**
	 * getMediaType
	 * @param fileExt String
	 * 
	 * @return MediaType
	 */
	public static MediaType getMediaType(String fileExt){
		MediaType MediaTypeValue = null ;
		switch (fileExt.toLowerCase()){
		case "pdf":
			MediaTypeValue = MediaType.APPLICATION_PDF;
			break;
		case "jpg":
		case "jpeg":
			MediaTypeValue = MediaType.IMAGE_JPEG;
			break;
		case "png":
		case "tif":
		case "tiff":
			MediaTypeValue = MediaType.IMAGE_PNG;	//MediaType.valueOf("image/tiff");
			break;
		}
		return MediaTypeValue;
	}
	
	/**
	 * validacionAntiMalware
	 * @param _context MyServiceContext
	 * @param _file MultipartFile
	 * @param newFname String
	 * @param tipoDoc String
	 * 
	 * @throws PedidoFilesException
	 */
	public static void validacionAntiMalware(MyServiceContext _context, MultipartFile _file, String newFname, String tipoDoc)
			throws PedidoFilesException {

		if(_context.getUsername() != null && !"Butransa".equals(_context.getUsername().toString())) {
			//1.Extraer estrictamente el nombre final (Previene Path Traversal)
			String safeFname = java.nio.file.Paths.get(newFname).getFileName().toString();

			//2.Controlar archivos sin extensión (Caso 'archivopdf')
			int lastDotIndex = safeFname.lastIndexOf('.');
			if (lastDotIndex <= 0 || lastDotIndex == safeFname.length() - 1) {
				logger.error("Intento de subida de archivo sin extensión: " + safeFname);
				throw new PedidoFilesException("El archivo seleccionado no tiene una extensión válida.");
			}

			//3.Validar el Content Type (MIME Type)
			String incomingMimeType = _file.getContentType();
			if (incomingMimeType == null || incomingMimeType.isEmpty()) {
				throw new PedidoFilesException("No se pudo determinar el tipo de formato del archivo.");
			}

			//Asignar tipos permitidos dinámicamente según el parámetro tipoDoc
			List<String> allowedTypes;
			if ("FAC".equalsIgnoreCase(tipoDoc)) {
				//Facturas: Solo PDF
				allowedTypes = Arrays.asList("application/pdf");
			} else {
				//CMR y OTROS: PDF e Imágenes
				allowedTypes = Arrays.asList("application/pdf", "image/jpeg", "image/png", "image/tiff");
			}

			if (!allowedTypes.contains(incomingMimeType.toLowerCase())) {
				logger.error("Tipo MIME no permitido para " + tipoDoc + ": " + incomingMimeType);
				throw new PedidoFilesException("El formato del archivo no está permitido para este documento.");
			}
			
			//Si el tipo es permitido, verificamos que no esté dañado internamente
			comprobarArchivoDañado(_file, incomingMimeType.toLowerCase());
		}
	}
	
	/**
	 * convertirImagenApdf
	 * @param datosImagen
	 * @param extension
	 * 
	 * @return byte[]
	 * @throws PedidoFilesException
	 * @throws IOException
	 */
	public static byte[] convertirImagenApdf(byte[] datosImagen, String extension) throws PedidoFilesException, IOException {
		try (PDDocument doc = new PDDocument()) {
			PDPage page = new PDPage();
			doc.addPage(page);

			//Crear la imagen para PDFBox
			PDImageXObject image = PDImageXObject.createFromByteArray(doc, datosImagen, "cmr_image." + extension);

			//1.Obtener dimensiones de la página y de la imagen original
			float paginaAncho = page.getMediaBox().getWidth();
			float paginaAlto = page.getMediaBox().getHeight();
			float imagenAnchoOriginal = image.getWidth();
			float imagenAltoOriginal = image.getHeight();

			//2.Calcular márgenes (ej. 20 puntos por cada lado)
			float margen = 10;
			float maxAncho = paginaAncho - (margen * 2);
			float maxAlto = paginaAlto - (margen * 2);

			//3.Calcular la escala óptima manteniendo la proporción
			float escalaAncho = maxAncho / imagenAnchoOriginal;
			float escalaAlto = maxAlto / imagenAltoOriginal;
			float escalaFinal = Math.min(escalaAncho, escalaAlto);		//Escogemos la más restrictiva

			//4.Calcular dimensiones finales escaladas
			float anchoFinal = imagenAnchoOriginal * escalaFinal;
			float altoFinal = imagenAltoOriginal * escalaFinal;

			//5.Centrar la imagen en la página centrándola en los huecos sobrantes
			float x = margen + (maxAncho - anchoFinal) / 2;
			float y = margen + (maxAlto - altoFinal) / 2;

			//6.Dibujar la imagen con las coordenadas corregidas
			try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
				contents.drawImage(image, x, y, anchoFinal, altoFinal);
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			doc.save(out);
			return out.toByteArray();
		}
	}
	

	
	private static void comprobarArchivoDañado(MultipartFile _file, String mimeType) throws PedidoFilesException {
	    if (_file.isEmpty()) {
	        throw new PedidoFilesException("El archivo seleccionado está vacío o corrupto.");
	    }

	    try (InputStream is = _file.getInputStream()) {
	        if ("application/pdf".equals(mimeType)) {
	            //Validar PDF con PDFBox
	            try (PDDocument document = PDDocument.load(is)) {
	                //Si tiene 0 páginas o está estructuralmente roto
	                if (document.getNumberOfPages() == 0) {
	                    throw new PedidoFilesException("El archivo PDF está dañado (no contiene páginas válidas).");
	                }
	            } catch (Exception e) {
	                logger.error("PDF corrupto detectado: " + e.getMessage());
	                throw new PedidoFilesException("El archivo PDF parece estar dañado y no se puede abrir.");
	            }
	        } else if (mimeType.startsWith("image/")) {
	            //Validar Imágenes (jpg, jpeg, png, tiff) con ImageIO
	            try {
	                BufferedImage image = ImageIO.read(is);
	                if (image == null) {
	                	//Si ImageIO null, no pudo parsear los bytes de la imagen (está corrupta)
	                    throw new PedidoFilesException("La imagen seleccionada está dañada o su formato es ilegible.");
	                }
	                //Forzar lectura de dimensiones para asegurar que los bytes internos son legibles
	                image.getWidth(); 
	            } catch (Exception e) {
	                logger.error("Imagen corrupta detectada en anti-malware: " + e.getMessage());
	                throw new PedidoFilesException("La imagen está dañada y no se puede procesar.");
	            }
	        }
	    } catch (IOException e) {
	    	logger.error("Error de E/S al validar estructura: " + e.getMessage());
	        throw new PedidoFilesException("Error al leer el contenido del archivo.");
	    }
	}

}
