package eus.martico.fileTransfer.services.firma;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore.PasswordProtection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.convertapi.client.Config;
import com.convertapi.client.ConvertApi;
import com.convertapi.client.Param;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.SignatureFieldParameters;
import eu.europa.esig.dss.pades.SignatureImageParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.service.http.commons.TimestampDataLoader;
import eu.europa.esig.dss.service.tsp.OnlineTSPSource;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eus.martico.fileTransfer.config.FileTransferConfiguration;
import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesException;
import eus.martico.fileTransfer.services.pedido.files.FileSystemStorageServiceImp;
import eus.martico.fileTransfer.utils.FileTransferUtils;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public class FileSystemCertSignService {

	protected static final Logger logger = LoggerFactory.getLogger(FileSystemStorageServiceImp.class);

	/**
	 * Image PDF Watermark - ConvertApi		//OLD DELETE
	 * @param _pathOr String
	 * @param _dirDes String
	 * 
	 * @throws IOException
	 * @throws PedidoFilesException 
	 */
	public static void watermarkPDF(String _pathOr, String _dirDes) throws IOException, PedidoFilesException {
		//String tokenFreeTrial = FileTransferConfiguration.getProperty(FileTransferConstantes.CONVERT_API_TK_FREE);
		String tokenProd = FileTransferConfiguration.getProperty(FileTransferConstantes.CONVERT_API_TK_PROD);
		Config.setDefaultApiCredentials(tokenProd);

		String signFileName = FileTransferConfiguration.getProperty(FileTransferConstantes.SIGN_FILE_NAME);
		Path signFilePath = FileTransferUtils.signLocation.resolve(signFileName);

		try {
			ConvertApi.convert("pdf", "image-watermark",
					new Param("File", Paths.get(_pathOr)),							//Paths.get("/path/to/my_file.pdf"))
					new Param("ImageFile", Paths.get(signFilePath.toString())),		//Paths.get("/path/to/selloA.png")
					new Param("Style", "stamp"),
					new Param("GoToLink", "https://www.martico.es/"),
					new Param("PageRotation", "true"),
					new Param("PageBox", "cropbox"),		//mediabox, trimbox, bleedbox, cropbox, 
					new Param("HorizontalAlignment", "right"),
					new Param("VerticalAlignment", "bottom"),
					new Param("Scale", "321")
					).get().saveFilesSync(Paths.get(_dirDes));			//Paths.get("/path/to/result/dir")

			logger.info("watermarkPDF :: "+_pathOr);

		} catch (InterruptedException | ExecutionException e) {
			logger.error("ERROR al sellar el documento: " + e.getMessage());
			e.printStackTrace();
			//Detiene validarCMR y lanza el mensaje hacia el usuario
			throw new PedidoFilesException("El documento no se ha podido sellar por causas ajenas al sistema (Fallo en convertAPI - watermark).");
		}
	}

	/**
	 * verificarPDFyaFirmado
	 * Comprueba si un array de bytes de un PDF ya contiene firmas digitales criptográficas.
	 * 
	 * @param pdfBytes Array de bytes del documento PDF a validar.
	 * @return boolean true si el PDF ya está firmado, false si está limpio.
	 */
	public static boolean verificarPDFyaFirmado(byte[] pdfBytes) {		//NO SE USA  ¿ NEW ?
		if (pdfBytes == null || pdfBytes.length == 0) {
			return false;
		}

		//Leemos el PDF en memoria usando PDFBox
		try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
			//Solicitamos al PDF su lista interna de diccionarios de firma digital
			List<PDSignature> firmas = document.getSignatureDictionaries();

			//Si la lista existe y no está vacía, el documento ya ha sido firmado criptográficamente
			return firmas != null && !firmas.isEmpty();

		} catch (Exception e) {
			//Si el PDF está corrupto o no se puede leer, asumimos false o manejamos el log según tu política
			return false;
		}
	}

	/**
	 * firmarPDFconCertificado
	 * @param pdfOriginalBytes
	 * @param certPath
	 * @param certPass
	 * 
	 * @return byte[]
	 * @throws PedidoFilesException
	 * @throws IOException
	 */
	public static byte[] firmarPDFconCertificado(byte[] pdfOriginalBytes, String certPath, String certPass, Boolean conSello, byte[] pngFirmaBytes) 
			throws PedidoFilesException, IOException {

		//Cargamos el certificado .p12 usando el token de DSS
		try(Pkcs12SignatureToken token = new Pkcs12SignatureToken(
				new FileInputStream(certPath), 
				new PasswordProtection(certPass.toCharArray()))) {

			//1.Obtener la clave privada del almacén
			List<DSSPrivateKeyEntry> keys = token.getKeys();
			if(keys.isEmpty()) {
				throw new IllegalArgumentException("El almacén de certificados está vacío.");
			}
			DSSPrivateKeyEntry privateKey = keys.get(0);

			//2.Configurar los parámetros obligatorios del estándar PAdES
			PAdESSignatureParameters parameters = new PAdESSignatureParameters();
			parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
			parameters.setSigningCertificate(privateKey.getCertificate());

			//3.Crear el motor de firma open-source
			CommonCertificateVerifier verifier = new CommonCertificateVerifier();
			PAdESService service = new PAdESService(verifier);

			//Leer el entorno configurado en el properties
			String entorno = FileTransferConfiguration.getProperty(FileTransferConstantes.CERT_ENV);
			//4.CONTROL DE ENTORNOS DINÁMICO
			if("PROD".equalsIgnoreCase(entorno)) {		//NEW NO SE USA DE MOMENTO 
				//Configuración para PRODUCCIÓN
				parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_T);
				//Insertar Sello de Tiempo Legal
				service = insertarTSA(service);
			} else {
				//Configuración por defecto para DESA / PRUEBAS (Firma Básica Local)
				parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
			}
			
			//============== METER SELLO INICIAL =====
			if (conSello && pngFirmaBytes != null && pngFirmaBytes.length > 0) {
			    SignatureImageParameters imageParameters = new SignatureImageParameters();
			    DSSDocument imageDocument = new InMemoryDocument(pngFirmaBytes);
			    imageParameters.setImage(imageDocument);
			    
			    //Forzamos un cuadrado perfecto para que coincida con los 329x327 píxeles del sello.png
			    int anchoCaja = 60;
			    int altoCaja = 60;
			    
			    int posicionBaseY = 702;
			    int posicionY = posicionBaseY + 45; 
			    
			    //Como ahora la caja mide 60 de ancho en vez de 130, le sumamos un desfase 
			    //de 35 puntos a la X para que el sello quede centrado en el mismo sitio que antes
			    int posicionX = 60 + 35; 

			    SignatureFieldParameters fieldParameters = new SignatureFieldParameters();
			    fieldParameters.setPage(1);				
			    fieldParameters.setOriginX(posicionX);  
			    fieldParameters.setOriginY(posicionY);  
			    fieldParameters.setWidth(anchoCaja);   	
			    fieldParameters.setHeight(altoCaja);   	

			    imageParameters.setFieldParameters(fieldParameters);
			    parameters.setImageParameters(imageParameters);
			}
			//==============================

			//Convertir los bytes del PDF al formato que entiende DSS
			DSSDocument documentToSign = new InMemoryDocument(pdfOriginalBytes);

			//5.Proceso criptográfico de firma
			ToBeSigned dataToSign = service.getDataToSign(documentToSign, parameters);
			SignatureValue signatureValue = token.sign(dataToSign, parameters.getDigestAlgorithm(), privateKey);
			DSSDocument signedDocument = service.signDocument(documentToSign, parameters, signatureValue);

			//6.Extraer los bytes del PDF ya certificado
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			signedDocument.writeTo(baos);
			
			logger.info("firmarPDFconCertificado :: Documento firmado criptográficamente con éxito.");
			return baos.toByteArray();
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ERROR en la certificación digital PAdES: " + e.getMessage());
			//Detiene validarCMR y lanza el mensaje hacia el usuario
			throw new PedidoFilesException("El documento no se ha podido certificar por causas ajenas al sistema (Fallo en la certificación digital PAdES).");
		}
	}
	

	/**
	 * firmadoDSSIncrementalAcumulativo
	 * @param pdfConFirmasPrevias byte[]
	 * @param pngFirmaBytes byte[]
	 * @param p12certPath String
	 * @param p12certPass String
	 * @param numeroFirma int
	 * @param rolFirmante String
	 * 
	 * @return byte[]
	 */
	public static byte[] firmadoDSSIncrementalAcumulativo(
			byte[] pdfConFirmasPrevias, 
			byte[] pngFirmaBytes, 
			String p12certPath, 
			String p12certPass, 
			int numeroFirma, 
			String rolFirmante) throws Exception {

		//1. Cargar el certificado .p12 vuestro
		try (Pkcs12SignatureToken token = new Pkcs12SignatureToken(
				new FileInputStream(p12certPath), 
				new PasswordProtection(p12certPass.toCharArray()))) {

			List<DSSPrivateKeyEntry> keys = token.getKeys();
			DSSPrivateKeyEntry privateKey = keys.get(0);

			//2. Configurar parámetros PAdES
			PAdESSignatureParameters parameters = new PAdESSignatureParameters();
			parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
			parameters.setSigningCertificate(privateKey.getCertificate());
			
			//3. Invocación del servicio oficial PAdES de SD-DSS -> cambiado el orden
	        CommonCertificateVerifier verifier = new CommonCertificateVerifier();
	        PAdESService service = new PAdESService(verifier);

			//Control dinámico de entorno para el Sello de Tiempo
			String entorno = FileTransferConfiguration.getProperty(FileTransferConstantes.CERT_ENV);
			if ("PROD".equalsIgnoreCase(entorno)) {		//NEW NO SE USA DE MOMENTO 
				parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_T);
				// ... Aquí metes tu bloque actual de la TSA con OnlineTSPSource ...
				//Insertar Sello de Tiempo Legal
				service = insertarTSA(service);
			} else {
				parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
			}
			
			//4. CONFIGURACIÓN VISUAL DEL RECUADRO DE FIRMA (SD-DSS 5.13)
			// =========================================================================
			if (pngFirmaBytes != null && pngFirmaBytes.length > 0) {
			    SignatureImageParameters imageParameters = new SignatureImageParameters();
			    DSSDocument imageDocument = new InMemoryDocument(pngFirmaBytes);
			    imageParameters.setImage(imageDocument);
			    
			    //Medidas fijas para encajar en el recuadro del CMR
			    int anchoCaja = 130;
			    int altoCaja = 60;

			    //Base general para la altura que calculamos en el paso anterior
			    int posicionBaseY = 702; 
			    int posicionX = 0;
			    int posicionY = posicionBaseY;

			    if (numeroFirma == 2) {
			        posicionX = 240; 		//Cuadrante central
			        posicionY = posicionBaseY + 45; 
			        
			    } else if (numeroFirma == 3) {
			        posicionX = 420; 		//Cuadrante derecho
			        posicionY = posicionBaseY;
			    }

			    SignatureFieldParameters fieldParameters = new SignatureFieldParameters();
			    fieldParameters.setPage(1);
			    fieldParameters.setOriginX(posicionX);
			    fieldParameters.setOriginY(posicionY);
			    fieldParameters.setWidth(anchoCaja);
			    fieldParameters.setHeight(altoCaja);
			    imageParameters.setFieldParameters(fieldParameters);

			    parameters.setImageParameters(imageParameters);
			}
			// =========================================================================

	        //Convertimos el PDF previo a documento DSS
	        DSSDocument documentToSign = new InMemoryDocument(pdfConFirmasPrevias);

	        //5. Firma criptográfica
	        ToBeSigned dataToSign = service.getDataToSign(documentToSign, parameters);
	        SignatureValue signatureValue = token.sign(dataToSign, parameters.getDigestAlgorithm(), privateKey);
	        
	        //SD-DSS detecta que ya hay firmas y genera los bytes puros incrementales de forma nativa
	        DSSDocument signedDocument = service.signDocument(documentToSign, parameters, signatureValue);

	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        signedDocument.writeTo(baos);
	        
	        return baos.toByteArray();
		}
	}

	/**
	 * insertarTSA
	 * @param service PAdESService
	 * 
	 * @return PAdESService
	 * @throws MalformedURLException
	 */
	private static PAdESService insertarTSA(PAdESService padesService) throws MalformedURLException {
		//Insertar Sello de Tiempo Legal
		String urlCompletaTsa = FileTransferConfiguration.getProperty(FileTransferConstantes.CERT_TSA_URL);
		OnlineTSPSource tspSource = new OnlineTSPSource();
		tspSource.setTspServer(urlCompletaTsa);		//Ejemplo: https://camerfirma.com

		TimestampDataLoader timestampDataLoader = new TimestampDataLoader();

		//Si el proveedor de la TSA exige usuario y contraseña en producción:
		String usuarioTsa = FileTransferConfiguration.getProperty(FileTransferConstantes.CERT_TSA_USER);
		String passwordTsa = FileTransferConfiguration.getProperty(FileTransferConstantes.CERT_TSA_PASS);

		if(usuarioTsa != null && !usuarioTsa.trim().isEmpty()) {
			//EXTRAER EL HOST AUTOMÁTICAMENTE DE LA URL COMPLETA
			java.net.URL urlParser = new java.net.URL(urlCompletaTsa);
			String hostTsa = urlParser.getHost(); 	//Devuelve "camerfirma.com" limpiamente

			//Parámetros: host, puerto (443 para https), esquema, usuario, password
			timestampDataLoader.addAuthentication(hostTsa, 443, "Basic", usuarioTsa, passwordTsa.toCharArray());
		}

		tspSource.setDataLoader(timestampDataLoader);
		//Inyectar la fuente de tiempo
		padesService.setTspSource(tspSource);

		return padesService;
	}

}
