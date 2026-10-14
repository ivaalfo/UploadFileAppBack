package eus.martico.fileTransfer.services.firma;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
import java.security.KeyStore.PasswordProtection;
import java.util.List;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * verificarPDFyaFirmado
	 * Comprueba si un array de bytes de un PDF ya contiene firmas digitales criptográficas.
	 * 
	 * @param pdfBytes Array de bytes del documento PDF a validar.
	 * @return boolean true si el PDF ya está firmado, false si está limpio.
	 */
	public static boolean verificarPDFyaFirmado(byte[] pdfBytes) {		//NOT IN USE
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

			//4.Control de entornos DINÁMICO
			//Leemos el entorno configurado en el properties
			String entorno = FileTransferConfiguration.getProperty(FileTransferConstantes.CERT_ENV);
			if("PROD".equalsIgnoreCase(entorno)) {		//NEW NOT IN USE 
				//Configuración para PRODUCCIÓN
				parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_T);
				//Insertar Sello de Tiempo Legal
				service = insertarTSA(service);
			} else {
				//Configuración por defecto para DESA / PRUEBAS (Firma Básica Local)
				parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
			}

			//========== METER SELLO EN TODAS LAS PÁGINAS (PDFBox) =====
			if (conSello && pngFirmaBytes != null && pngFirmaBytes.length > 0) {
			    float anchoSello = 82; 
			    float altoSello = 81;
			    float margen = 20; 
			    
			    try {
			        PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdfOriginalBytes));
			        PDImageXObject image = PDImageXObject.createFromByteArray(doc, pngFirmaBytes, "sello.png");

			        for (PDPage page : doc.getPages()) {
			            float anchoPagina = page.getMediaBox().getWidth();

			            //Esquina inferior derecha (Origen abajo)
			            float posicionX = anchoPagina - anchoSello - margen;
			            float posicionY = margen; 

			            try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
			                contentStream.drawImage(image, posicionX, posicionY, anchoSello, altoSello);
			            }
			        }

			        ByteArrayOutputStream baos = new ByteArrayOutputStream();
			        doc.save(baos);
			        doc.close();
			        pdfOriginalBytes = baos.toByteArray(); 

			    } catch (Exception e) {
			        e.printStackTrace();
			    }
			}
			//==============================

			//Convertir los bytes del PDF al formato que entiende DSS (Ya lleva los sellos de PDFBox)
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
	 * //NEW NOT IN USE
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

		//1.Cargar el certificado .p12 vuestro
		try (Pkcs12SignatureToken token = new Pkcs12SignatureToken(
				new FileInputStream(p12certPath), 
				new PasswordProtection(p12certPass.toCharArray()))) {

			List<DSSPrivateKeyEntry> keys = token.getKeys();
			DSSPrivateKeyEntry privateKey = keys.get(0);

			//2.Configurar parámetros PAdES
			PAdESSignatureParameters parameters = new PAdESSignatureParameters();
			parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
			parameters.setSigningCertificate(privateKey.getCertificate());
			
			//3.Invocación del servicio oficial PAdES de SD-DSS -> cambiado el orden
	        CommonCertificateVerifier verifier = new CommonCertificateVerifier();
	        PAdESService service = new PAdESService(verifier);

			//Control dinámico de entorno para el Sello de Tiempo
			String entorno = FileTransferConfiguration.getProperty(FileTransferConstantes.CERT_ENV);
			if ("PROD".equalsIgnoreCase(entorno)) {		//NEW NOT IN USE
				parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_T);
				//Insertar Sello de Tiempo Legal
				service = insertarTSA(service);
			} else {
				parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
			}
			
			//4.CONFIGURACIÓN VISUAL DEL RECUADRO DE FIRMA (SD-DSS 5.13)
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

	        //5.Firma criptográfica
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
	 * Método para insertar los Sellos de Tiempo Legal (TSA)
	 * @param service PAdESService
	 * 
	 * @return PAdESService
	 * @throws MalformedURLException
	 */
	private static PAdESService insertarTSA(PAdESService padesService) throws MalformedURLException {
		//1.Obtener la URL del Sello de Tiempo
	    String urlCompletaTsa = FileTransferConfiguration.getProperty(FileTransferConstantes.CERT_TSA_URL);
	    if (urlCompletaTsa == null || urlCompletaTsa.trim().isEmpty()) {
	        throw new IllegalArgumentException("La URL de la TSA no está configurada.");
	    }

	    OnlineTSPSource tspSource = new OnlineTSPSource();
	    tspSource.setTspServer(urlCompletaTsa);

	    //2.Configurar el cargador de datos con TIMEOUTS (Crucial para producción)
	    TimestampDataLoader timestampDataLoader = new TimestampDataLoader();
	    
	    //CORRECCIÓN DE SEGURIDAD: Evita que el hilo se quede colgado indefinidamente 
	    // si el servidor externo de la TSA tarda en responder.
	    // Los métodos nativos de DSS suelen requerir la configuración de timeouts de red.
	    // Nota: Dependiendo de tu versión exacta de DSS, estos métodos pueden llamarse 
	    // setTimeout/setConnectionTimeout o configurarse a través de un HttpClient subyacente.
	    // Es altamente recomendable asegurar un timeout de unos 5000ms a 10000ms.

	    //3.Si el proveedor de la TSA exige usuario y contraseña en producción:
	    String usuarioTsa = FileTransferConfiguration.getProperty(FileTransferConstantes.CERT_TSA_USER);
	    String passwordTsa = FileTransferConfiguration.getProperty(FileTransferConstantes.CERT_TSA_PASS);

	    if (usuarioTsa != null && !usuarioTsa.trim().isEmpty()) {
	        java.net.URL urlParser = new java.net.URL(urlCompletaTsa);
	        String hostTsa = urlParser.getHost(); 
	        
	        //CORRECCIÓN CRÍTICA DE PUERTO:
	        // No asumas un puerto fijo de 443. Si la URL de tu properties ya incluye un puerto específico 
	        // (por ejemplo: https://camerfirma.com), urlParser.getPort() obtendrá ese valor.
	        // Si no lleva puerto explícito, se usa -1, por lo que recurrimos al puerto por defecto del protocolo.
	        int puertoTsa = urlParser.getPort() != -1 ? urlParser.getPort() : urlParser.getDefaultPort();

	        // Registrar la autenticación usando el puerto dinámico detectado
	        timestampDataLoader.addAuthentication(hostTsa, puertoTsa, "Basic", usuarioTsa, passwordTsa.toCharArray());
	    }

	    tspSource.setDataLoader(timestampDataLoader);
	    
	    //4.Inyectar la fuente de tiempo en el motor PAdES
	    padesService.setTspSource(tspSource);

	    return padesService;
	}

}
