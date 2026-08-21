package eus.martico.fileTransfer.services.firma;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
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
	 * Image PDF Watermark - ConvertApi
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
	public static byte[] firmarPDFconCertificado(byte[] pdfOriginalBytes, String certPath, String certPass) 
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
			if("PROD".equalsIgnoreCase(entorno)) {		//NO_SE_USA
				//Configuración para PRODUCCIÓN (Sello de Tiempo Legal)
				parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_T);

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
				service.setTspSource(tspSource);

			} else {
				//Configuración por defecto para DESA / PRUEBAS (Firma Básica Local)
				parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
			}

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
	
	//TODO Firmado Incremental Acumulativo
//	public static byte[] firmadoAcumulativo(){
//		// 1. Cargar el PDF que ya contiene la primera firma
//		File pdfExistente = new File("camino/al/cmr_con_firma1.pdf");
//		PDDocument document = PDDocument.load(pdfExistente);
//
//		// 2. Crear un nuevo campo de firma para el Proveedor (Firma 2)
//		PDSignature signature = new PDSignature();
//		signature.setFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
//		signature.setName("Proveedor X");
//		signature.setLocation("Madrid");
//		signature.setReason("Validación de recepción de CMR");
//		signature.setSignDate(Calendar.getInstance());
//
//		// 3. (Opcional) Asociar el PNG del garabato visual a este campo de firma
//		// Aquí configurarías las coordenadas (X, Y) donde quieres que se pinte el PNG en la hoja
//
//		// 4. Registrar la firma en el documento
//		document.addSignature(signature);
//
//		// 5. ¡EL TRUCO CLAVE! Guardar en modo incremental
//		// Esto añade los cambios al final del archivo sin romper la Firma 1
//		FileOutputStream outputStream = new FileOutputStream("camino/al/cmr_con_firma1_y_2.pdf");
//		SigningSupport signingSupport = document.saveIncrementalForExternalSigning(outputStream);
//
//		// 6. Aquí integras tu lógica actual de firmado (la que decías que ya tienes local con el certificado)
//		byte[] cmsSignature = tuMetodoDeFirmadoCriptografico(signingSupport.getContent());
//		signingSupport.setSignature(cmsSignature);
//
//		document.close();
//		outputStream.close();
//	}
	
}
